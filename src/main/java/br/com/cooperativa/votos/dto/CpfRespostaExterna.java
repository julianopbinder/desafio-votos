package br.com.cooperativa.votos.dto; 

import io.swagger.v3.oas.annotations.media.Schema; 

@Schema(description = "Resposta recebida do serviço externo de validação de CPF informado no bônus do teste.") 
public record CpfRespostaExterna( 
        @Schema(description = "Status retornado pelo serviço externo: ABLE_TO_VOTE ou UNABLE_TO_VOTE.", example = "ABLE_TO_VOTE") 
        String status 
) { 
} 
