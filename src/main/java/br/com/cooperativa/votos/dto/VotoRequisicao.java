package br.com.cooperativa.votos.dto; 

import br.com.cooperativa.votos.dominio.EscolhaVoto; 
import io.swagger.v3.oas.annotations.media.Schema; 
import jakarta.validation.constraints.NotBlank; 
import jakarta.validation.constraints.NotNull; 

@Schema(description = "Dados necessários para registrar o voto de um associado em uma pauta.") 
public record VotoRequisicao( 
        @Schema(description = "Identificador único do associado. É usado para impedir voto duplicado na mesma pauta.", example = "associado-001") 
        @NotBlank(message = "ID do associado obrigatório") 
        String associadoId, 

        @Schema(description = "CPF do associado usado na integração externa do bônus. Se a integração estiver desabilitada, este campo pode ser omitido.", example = "12345678909") 
        String cpf, 

        @Schema(description = "Escolha do voto. Use exatamente SIM ou NAO.", example = "SIM") 
        @NotNull(message = "Escolha do voto obrigatória") 
        EscolhaVoto escolha 
) { 
} 
