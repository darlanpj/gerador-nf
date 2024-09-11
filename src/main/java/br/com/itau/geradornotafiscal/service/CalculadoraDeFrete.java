package br.com.itau.geradornotafiscal.service;

import br.com.itau.geradornotafiscal.model.Endereco;
import br.com.itau.geradornotafiscal.model.Pedido;
import br.com.itau.geradornotafiscal.model.enums.Finalidade;
import br.com.itau.geradornotafiscal.model.enums.Regiao;

public class CalculadoraDeFrete {

    public double calcularFrete(final Pedido pedido) {

        Regiao regiao = pedido.getDestinatario().getEnderecos().stream()
                .filter(endereco -> endereco.getFinalidade() == Finalidade.ENTREGA || endereco.getFinalidade() == Finalidade.COBRANCA_ENTREGA)
                .map(Endereco::getRegiao)
                .findFirst()
                .orElse(null);

        double valorFrete = pedido.getValorFrete();
        double valorFreteComPercentual = getValorFreteComPercentual(regiao, valorFrete);

        return valorFreteComPercentual;
    }

    private double getValorFreteComPercentual(Regiao regiao, double valorFrete) {
        double valorFreteComPercentual = 0;

        if (regiao == Regiao.NORTE) {
            valorFreteComPercentual = valorFrete * Regiao.NORTE.getValorFrete();
        } else if (regiao == Regiao.NORDESTE) {
            valorFreteComPercentual = valorFrete *  Regiao.NORDESTE.getValorFrete();
        } else if (regiao == Regiao.CENTRO_OESTE) {
            valorFreteComPercentual = valorFrete * Regiao.CENTRO_OESTE.getValorFrete();
        } else if (regiao == Regiao.SUDESTE) {
            valorFreteComPercentual = valorFrete *  Regiao.SUDESTE.getValorFrete();
        } else if (regiao == Regiao.SUL) {
            valorFreteComPercentual = valorFrete * Regiao.SUL.getValorFrete();
        }

        return valorFreteComPercentual;
    }
}
