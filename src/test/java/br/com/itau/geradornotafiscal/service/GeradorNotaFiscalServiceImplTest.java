package br.com.itau.geradornotafiscal.service;

import br.com.itau.geradornotafiscal.model.*;
import br.com.itau.geradornotafiscal.model.enums.Finalidade;
import br.com.itau.geradornotafiscal.model.enums.Regiao;
import br.com.itau.geradornotafiscal.model.enums.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.model.enums.TipoPessoa;
import br.com.itau.geradornotafiscal.service.CalculadoraAliquotaProduto;
import br.com.itau.geradornotafiscal.service.impl.EntregaServiceImpl;
import br.com.itau.geradornotafiscal.service.impl.EstoqueServiceImpl;
import br.com.itau.geradornotafiscal.service.impl.FinanceiroServiceImpl;
import br.com.itau.geradornotafiscal.service.impl.GeradorNotaFiscalServiceImpl;
import br.com.itau.geradornotafiscal.service.impl.RegistroServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeradorNotaFiscalServiceImplTest {

    @InjectMocks
    private GeradorNotaFiscalServiceImpl geradorNotaFiscalService;

    @Mock
    private CalculadoraAliquotaProduto calculadoraAliquotaProduto;

    @Mock
    private EntregaServiceImpl entregaServiceImpl;

    @Mock
    private EstoqueServiceImpl estoqueServiceImpl;

    @Mock
    private FinanceiroServiceImpl financeiroService;

    @Mock
    private RegistroServiceImpl registroServiceImpl;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    public void shouldGenerateNotaFiscalForTipoPessoaFisicaWithValorTotalItensLessThan500() {
        Pedido pedido = criaPedido(400, 100, TipoPessoa.FISICA,
                null, 100, 4);
        NotaFiscal notaFiscal = geradorNotaFiscalService.gerarNotaFiscal(pedido);

        assertEquals(pedido.getValorTotalItens(), notaFiscal.getValorTotalItens());
        assertEquals(1, notaFiscal.getItens().size());
        assertEquals(0, notaFiscal.getItens().get(0).getValorTributoItem());
    }

    @Test
    public void shouldGenerateNotaFiscalForTipoPessoaJuridicaWithRegimeTributacaoLucroPresumidoAndValorTotalItensGreaterThan5000() {
        Pedido pedido = criaPedido(6000, 100, TipoPessoa.JURIDICA,
                RegimeTributacaoPJ.LUCRO_PRESUMIDO, 1000, 6);
        NotaFiscal notaFiscal = geradorNotaFiscalService.gerarNotaFiscal(pedido);

        assertEquals(pedido.getValorTotalItens(), notaFiscal.getValorTotalItens());
        assertEquals(1, notaFiscal.getItens().size());
        assertEquals(0.20 * pedido.getItens().get(0).getValorUnitario(),
                notaFiscal.getItens().get(0).getValorTributoItem());
    }

    @Test
    public void shouldGenerateNotaFiscalForTipoPessoaJuridicaWithRegimeTributacaoLucroReal() {
        Pedido pedido = criaPedido(4800, 200, TipoPessoa.JURIDICA,
                RegimeTributacaoPJ.LUCRO_REAL, 2000, 4);
        NotaFiscal notaFiscal = geradorNotaFiscalService.gerarNotaFiscal(pedido);

        assertEquals(pedido.getValorTotalItens(), notaFiscal.getValorTotalItens());
        assertEquals(1, notaFiscal.getItens().size());
        assertEquals(0.15 * pedido.getItens().get(0).getValorUnitario(),
                notaFiscal.getItens().get(0).getValorTributoItem());
    }

    @Test
    public void shouldGenerateNotaFiscalForTipoPessoaJuridicaWithRegimeTributacaoLucroPresumidoLessThan2000() {
        Pedido pedido = criaPedido(1750, 150, TipoPessoa.JURIDICA,
                RegimeTributacaoPJ.LUCRO_PRESUMIDO, 1750, 1);
        NotaFiscal notaFiscal = geradorNotaFiscalService.gerarNotaFiscal(pedido);

        assertEquals(pedido.getValorTotalItens(), notaFiscal.getValorTotalItens());
        assertEquals(1, notaFiscal.getItens().size());
        assertEquals(0.09 * pedido.getItens().get(0).getValorUnitario(),
                notaFiscal.getItens().get(0).getValorTributoItem());
    }

    @Test
    public void shouldGenerateNotaFiscalForTipoPessoaJuridicaWithRegimeTributacaoSimplesNacionalLessThan5000() {
        Pedido pedido = criaPedido(3500, 150, TipoPessoa.JURIDICA,
                null, 1750, 2);
        NotaFiscal notaFiscal = geradorNotaFiscalService.gerarNotaFiscal(pedido);

        assertEquals(pedido.getValorTotalItens(), notaFiscal.getValorTotalItens());
        assertEquals(1, notaFiscal.getItens().size());
        assertEquals(0.13 * pedido.getItens().get(0).getValorUnitario(),
                notaFiscal.getItens().get(0).getValorTributoItem());
    }


    private Pedido criaPedido(double valorTotalItens, double valorFrete, TipoPessoa tipoPessoa,
                              RegimeTributacaoPJ regimeTributacao, double valorUnitarioItem, int quantidadeItem) {
        Pedido pedido = new Pedido();
        pedido.setValorTotalItens(valorTotalItens);
        pedido.setValorFrete(valorFrete);

        Destinatario destinatario = new Destinatario();
        destinatario.setTipoPessoa(tipoPessoa);
        if (regimeTributacao != null) {
            destinatario.setRegimeTributacao(regimeTributacao);
        }

        Endereco endereco = new Endereco();
        endereco.setFinalidade(Finalidade.ENTREGA);
        endereco.setRegiao(Regiao.SUDESTE);
        destinatario.setEnderecos(Arrays.asList(endereco));

        pedido.setDestinatario(destinatario);

        Item item = new Item();
        item.setValorUnitario(valorUnitarioItem);
        item.setQuantidade(quantidadeItem);
        pedido.setItens(Arrays.asList(item));

        return pedido;
    }
}