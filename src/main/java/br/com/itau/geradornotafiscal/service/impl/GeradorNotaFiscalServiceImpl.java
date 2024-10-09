package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.Destinatario;
import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.model.Pedido;
import br.com.itau.geradornotafiscal.model.enums.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.model.enums.TipoPessoa;
import br.com.itau.geradornotafiscal.service.CalculadoraAliquotaProduto;
import br.com.itau.geradornotafiscal.service.CalculadoraDeFrete;
import br.com.itau.geradornotafiscal.service.GeradorNotaFiscalService;
import br.com.itau.geradornotafiscal.web.controller.GeradorNFController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class GeradorNotaFiscalServiceImpl implements GeradorNotaFiscalService {

    private static final Log LOG = LogFactory.getLog(GeradorNotaFiscalServiceImpl.class);


    private EstoqueServiceImpl estoqueServiceImpl;
    private RegistroServiceImpl registroServiceImpl;
    private EntregaServiceImpl entregaServiceImpl;

    private FinanceiroServiceImpl financeiroService;

    public GeradorNotaFiscalServiceImpl(EstoqueServiceImpl estoque,
                                        RegistroServiceImpl registroServiceImpl,
                                        EntregaServiceImpl entregaServiceImpl,
                                        FinanceiroServiceImpl financeiroService) {
        this.entregaServiceImpl = entregaServiceImpl;
        this.registroServiceImpl = registroServiceImpl;
        this.estoqueServiceImpl = estoque;
        this.financeiroService = financeiroService;
    }

    @Override
    public NotaFiscal gerarNotaFiscal(Pedido pedido) {

        Destinatario destinatario = pedido.getDestinatario();
        TipoPessoa tipoPessoa = destinatario.getTipoPessoa();

        CalculadoraAliquotaProduto calculadoraAliquotaProduto = new CalculadoraAliquotaProduto();
        CalculadoraDeFrete calculadoraDeFrete = new CalculadoraDeFrete();
        RegimeTributacaoPJ regimeTributacaoPJ = pedido.getDestinatario().getRegimeTributacao();

        // Create the NotaFiscal object
        String idNotaFiscal = UUID.randomUUID().toString();

        NotaFiscal notaFiscal = NotaFiscal.builder()
                .idNotaFiscal(idNotaFiscal)
                .data(LocalDateTime.now())
                .valorTotalItens(pedido.getValorTotalItens())
                .valorFrete(calculadoraDeFrete.calcularFrete(pedido))
                .itens(calculadoraAliquotaProduto.calcularAliquota(tipoPessoa, regimeTributacaoPJ, pedido))
                .destinatario(pedido.getDestinatario())
                .build();

        var enviaNota = CompletableFuture.runAsync(() ->
                estoqueServiceImpl.enviarNotaFiscalParaBaixaEstoque(notaFiscal));

        var registraNota = CompletableFuture.runAsync(() ->
                registroServiceImpl.registrarNotaFiscal(notaFiscal));

        var agendaEntrega = CompletableFuture.runAsync(() ->
                entregaServiceImpl.agendarEntrega(notaFiscal));

        var enviaNotaParaFinanceiro = CompletableFuture.runAsync(() ->
                financeiroService.enviarNotaFiscalParaContasReceber(notaFiscal));

        CompletableFuture<Void> allFutures =
                CompletableFuture.allOf(enviaNota, registraNota, agendaEntrega, enviaNotaParaFinanceiro);

        try {
            allFutures.get();
        }  catch (InterruptedException | ExecutionException e) {
            LOG.error("E=Erro no fluxo paralelo ", e);
            throw new RuntimeException(e);
        }

        return notaFiscal;
    }
}