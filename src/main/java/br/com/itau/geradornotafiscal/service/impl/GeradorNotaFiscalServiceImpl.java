package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.*;
import br.com.itau.geradornotafiscal.model.enums.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.model.enums.TipoPessoa;
import br.com.itau.geradornotafiscal.service.CalculadoraAliquotaProduto;
import br.com.itau.geradornotafiscal.service.CalculadoraDeFrete;
import br.com.itau.geradornotafiscal.service.GeradorNotaFiscalService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class GeradorNotaFiscalServiceImpl implements GeradorNotaFiscalService {

	private EstoqueServiceImpl estoqueServiceImpl;
	private RegistroServiceImpl registroServiceImpl;
	private EntregaServiceImpl entregaServiceImpl;

	private FinanceiroServiceImpl financeiroService;

	public GeradorNotaFiscalServiceImpl(EstoqueServiceImpl estoque,
										RegistroServiceImpl registroServiceImpl,
										EntregaServiceImpl entregaServiceImpl,
										FinanceiroServiceImpl financeiroService){
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
				.itens(calculadoraAliquotaProduto.calcularAliquota(tipoPessoa,regimeTributacaoPJ, pedido))
				.destinatario(pedido.getDestinatario())
				.build();

		estoqueServiceImpl.enviarNotaFiscalParaBaixaEstoque(notaFiscal);
		registroServiceImpl.registrarNotaFiscal(notaFiscal);
		entregaServiceImpl.agendarEntrega(notaFiscal);
		financeiroService.enviarNotaFiscalParaContasReceber(notaFiscal);

		return notaFiscal;
	}
}