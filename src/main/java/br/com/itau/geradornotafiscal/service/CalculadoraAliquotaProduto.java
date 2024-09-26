package br.com.itau.geradornotafiscal.service;

import br.com.itau.geradornotafiscal.model.Item;
import br.com.itau.geradornotafiscal.model.ItemNotaFiscal;
import br.com.itau.geradornotafiscal.model.Pedido;
import br.com.itau.geradornotafiscal.model.enums.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.model.enums.TipoPessoa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CalculadoraAliquotaProduto {

    private static final Logger logger = LoggerFactory.getLogger(CalculadoraAliquotaProduto.class);


    public List<ItemNotaFiscal> calcularAliquota(final TipoPessoa pessoa,
                                                 final RegimeTributacaoPJ regimeTributacaoPJ, final Pedido pedido) {

        logger.info("Calculando aliquota para TipoPessoa: {}, RegimeTributacaoPJ: {}, Pedido: {}",
                pessoa, regimeTributacaoPJ, pedido);


        if (pessoa == TipoPessoa.FISICA) {
            return calcularAliquota(pedido.getItens(),
                    calculaAliquotaRequimeTributacaoPF(pedido.getValorTotalItens()));

        } else if (pessoa == TipoPessoa.JURIDICA) {

            return calcularAliquota(pedido.getItens(),
                    calculaAliquotaPeloRequimeTributacaoPJ(regimeTributacaoPJ, pedido));
        }

        return new ArrayList<>();
    }

    private List<ItemNotaFiscal> calcularAliquota(List<Item> items, double aliquotaPercentual) {
        logger.info("Calculando aliquota para os items: {}, porcentagem aliquota: {}", items, aliquotaPercentual);

        final List<ItemNotaFiscal> itemNotaFiscalList = new ArrayList<>();

        if (Objects.nonNull(itemNotaFiscalList)) {

            for (Item item : items) {
                double valorTributo = item.getValorUnitario() * aliquotaPercentual;
                ItemNotaFiscal itemNotaFiscal = ItemNotaFiscal.builder()
                        .idItem(item.getIdItem())
                        .descricao(item.getDescricao())
                        .valorUnitario(item.getValorUnitario())
                        .quantidade(item.getQuantidade())
                        .valorTributoItem(valorTributo)
                        .build();
                itemNotaFiscalList.add(itemNotaFiscal);
                logger.debug("Item processado: {}", itemNotaFiscal);
            }
        }

        return itemNotaFiscalList;
    }

    public final double calculaAliquotaPeloRequimeTributacaoPJ(final RegimeTributacaoPJ regimeTributacaoPJ,
                                                               final Pedido pedido) {
        logger.info("Calculando aliquota para RegimeTributacaoPJ: {}, Pedido: {}", regimeTributacaoPJ, pedido);

        final var valorTotalItens = pedido.getValorTotalItens();

        if (regimeTributacaoPJ == RegimeTributacaoPJ.LUCRO_REAL) {
            return calculaAliquotaLucroReal(valorTotalItens);
        } else if (regimeTributacaoPJ == RegimeTributacaoPJ.LUCRO_PRESUMIDO) {
            return calculaAliquotaLucroPresumido(valorTotalItens);
        }

        return calculaAliquotaSimplesNacional(valorTotalItens);
    }

    public final double calculaAliquotaRequimeTributacaoPF(final double valorTotalItens) {
        double aliquota;

        if (valorTotalItens < 500) {
            aliquota = 0;
        } else if (valorTotalItens <= 2000) {
            aliquota = 0.12;
        } else if (valorTotalItens <= 3500) {
            aliquota = 0.15;
        } else {
            aliquota = 0.17;
        }

        logger.info("Calculando aliquota para Pessoa Fisica com valorTotalItens: {},  aliquota: {}",
                valorTotalItens, aliquota);
        return aliquota;
    }


    private double calculaAliquotaSimplesNacional(final double valorTotalItens) {


        double aliquota;

        if (valorTotalItens < 1000) {
            aliquota = 0.03;
        } else if (valorTotalItens <= 2000) {
            aliquota = 0.07;
        } else if (valorTotalItens <= 5000) {
            aliquota = 0.13;
        } else {
            aliquota = 0.19;
        }

        logger.info("Calculando aliquota para Simples Nacional com valorTotalItens: {}, aliquota: {}",
                valorTotalItens, aliquota);
        return aliquota;
    }

    private double calculaAliquotaLucroReal(final double valorTotalItens) {


        double aliquota;

        if (valorTotalItens < 1000) {
            aliquota = 0.03;
        } else if (valorTotalItens <= 2000) {
            aliquota = 0.09;
        } else if (valorTotalItens <= 5000) {
            aliquota = 0.15;
        } else {
            aliquota = 0.20;
        }

        logger.info("Calculando aliquota para Lucro Real com valorTotalItens: {}, aliquota: {}",
                valorTotalItens, aliquota);
        return aliquota;
    }

    private double calculaAliquotaLucroPresumido(final double valorTotalItens) {

        double aliquota;

        if (valorTotalItens < 1000) {
            aliquota = 0.03;
        } else if (valorTotalItens <= 2000) {
            aliquota = 0.09;
        } else if (valorTotalItens <= 5000) {
            aliquota = 0.16;
        } else {
            aliquota = 0.20;
        }

        logger.info("Calculando aliquota para Lucro Presumido com valorTotalItens: {} , aliquota: {} ",
                valorTotalItens, aliquota);
        return aliquota;
    }
}



