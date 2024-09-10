package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.*;
import br.com.itau.geradornotafiscal.model.enums.Finalidade;
import br.com.itau.geradornotafiscal.model.enums.Regiao;
import br.com.itau.geradornotafiscal.model.enums.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.model.enums.TipoPessoa;
import br.com.itau.geradornotafiscal.service.CalculadoraAliquotaProduto;
import br.com.itau.geradornotafiscal.service.CalculadoraDeFrete;
import br.com.itau.geradornotafiscal.service.GeradorNotaFiscalService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GeradorNotaFiscalServiceImpl implements GeradorNotaFiscalService{
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

		new EstoqueService().enviarNotaFiscalParaBaixaEstoque(notaFiscal);
		new RegistroService().registrarNotaFiscal(notaFiscal);
		new EntregaService().agendarEntrega(notaFiscal);
		new FinanceiroService().enviarNotaFiscalParaContasReceber(notaFiscal);

		return notaFiscal;
	}
}