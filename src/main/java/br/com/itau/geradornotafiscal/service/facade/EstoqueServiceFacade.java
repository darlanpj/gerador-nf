package br.com.itau.geradornotafiscal.service.facade;

import br.com.itau.geradornotafiscal.model.NotaFiscal;

public interface EstoqueServiceFacade {

    void enviarNotaFiscalParaBaixaEstoque(NotaFiscal notaFiscal);
}
