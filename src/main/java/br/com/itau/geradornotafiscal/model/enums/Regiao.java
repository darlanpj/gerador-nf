package br.com.itau.geradornotafiscal.model.enums;


public enum Regiao {
    NORTE(1.08),
    NORDESTE(1.085),
    CENTRO_OESTE(1.07),
    SUDESTE(1.048),
    SUL(1.06);

    private final double valorFrete;
    Regiao(double valorFrete) {
        this.valorFrete = valorFrete;
    }
    public double getValorFrete() {
        return valorFrete;
    }
}
