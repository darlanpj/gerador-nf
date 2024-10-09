package br.com.itau.geradornotafiscal.model;

import br.com.itau.geradornotafiscal.model.enums.RegimeTributacaoPJ;
import br.com.itau.geradornotafiscal.model.enums.TipoPessoa;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Destinatario(String nome,
                           TipoPessoa tipoPessoa,
                           RegimeTributacaoPJ regimeTributacao,
                           List<Documento> documentos,
                           List<Endereco> enderecos
) {
    public Destinatario() {
        this(null, null, null, null, null);
    }
}

