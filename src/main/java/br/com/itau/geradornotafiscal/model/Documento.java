package br.com.itau.geradornotafiscal.model;

import br.com.itau.geradornotafiscal.model.enums.TipoDocumento;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Documento(String numero,
                        TipoDocumento tipo
) {}
