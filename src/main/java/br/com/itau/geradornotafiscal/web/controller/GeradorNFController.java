package br.com.itau.geradornotafiscal.web.controller;

import br.com.itau.geradornotafiscal.model.NotaFiscal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.itau.geradornotafiscal.model.Pedido;
import br.com.itau.geradornotafiscal.service.GeradorNotaFiscalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/pedido")
public class GeradorNFController {

    private static final Log LOG = LogFactory.getLog(GeradorNFController.class);
    private GeradorNotaFiscalService notaFiscalService;

    public GeradorNFController(final GeradorNotaFiscalService notaFiscalService) {
        this.notaFiscalService = notaFiscalService;
    }

    @PostMapping("/gerarNotaFiscal")
    public ResponseEntity<NotaFiscal> gerarNotaFiscal(@RequestBody Pedido pedido) {
        // Lógica de processamento do pedido
        // Aqui você pode realizar as operações desejadas com o objeto Pedido

        if (Objects.isNull(pedido)) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        NotaFiscal notaFiscal = notaFiscalService.gerarNotaFiscal(pedido);

        LOG.info("Nota fiscal gerada com sucesso para o pedido: " + pedido.getIdPedido());
        return new ResponseEntity<>(notaFiscal, HttpStatus.CREATED);
    }
}
