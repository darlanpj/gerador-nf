package br.com.itau.geradornotafiscal.service.facade;

import br.com.itau.geradornotafiscal.model.NotaFiscal;

public interface EntregaServiceFacade {
    void agendarEntrega(NotaFiscal notaFiscal);
}
