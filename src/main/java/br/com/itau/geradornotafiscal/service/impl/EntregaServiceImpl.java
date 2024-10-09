package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.port.out.EntregaIntegrationPort;
import br.com.itau.geradornotafiscal.service.facade.EntregaServiceFacade;
import org.springframework.stereotype.Service;

@Service
public class EntregaServiceImpl implements EntregaServiceFacade {
    public void agendarEntrega(NotaFiscal notaFiscal) {

            try {
                //Simula o agendamento da entrega
                Thread.sleep(150);
                new EntregaIntegrationPort().criarAgendamentoEntrega(notaFiscal);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

    }
}
