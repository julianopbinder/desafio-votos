package br.com.cooperativa.votos.dto; 

import io.swagger.v3.oas.annotations.media.Schema; 
import jakarta.validation.constraints.NotBlank; 
import jakarta.validation.constraints.Size; 

@Schema(description = "Dados necessários para cadastrar uma nova pauta de votação.") 
public record PautaRequisicao( 
        @Schema(description = "Título curto da pauta que será votada.", example = "Aprovação do novo estatuto") 
        @NotBlank(message = "Título obrigatório") 
        @Size(max = 150, message = "Título deve ter no máximo 150 caracteres") 
        String titulo, 

        @Schema(description = "Descrição detalhada da pauta para orientar os associados.", example = "Votação para aprovar ou rejeitar o novo estatuto da cooperativa.") 
        @NotBlank(message = "Descrição obrigatória") 
        @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres") 
        String descricao 
) { 
} 
