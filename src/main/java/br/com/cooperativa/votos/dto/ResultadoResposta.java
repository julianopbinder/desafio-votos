package br.com.cooperativa.votos.dto; 

import io.swagger.v3.oas.annotations.media.Schema; 

@Schema(description = "Resposta com a contabilização dos votos de uma pauta.") 
public record ResultadoResposta( 
        @Schema(description = "ID da pauta apurada.", example = "1") 
        Long pautaId, 

        @Schema(description = "Título da pauta apurada.", example = "Aprovação do novo estatuto") 
        String titulo, 

        @Schema(description = "Quantidade de votos SIM.", example = "10") 
        long votosSim, 

        @Schema(description = "Quantidade de votos NAO.", example = "3") 
        long votosNao, 

        @Schema(description = "Quantidade total de votos computados.", example = "13") 
        long totalVotos, 

        @Schema(description = "Resultado final da votação: APROVADA, REPROVADA, EMPATE ou SEM VOTOS.", example = "APROVADA") 
        String resultado 
) { 
} 
