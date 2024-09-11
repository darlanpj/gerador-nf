package br.com.itau.geradornotafiscal.service.impl;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import br.com.itau.geradornotafiscal.port.out.EntregaIntegrationPort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class EntregaService {
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
