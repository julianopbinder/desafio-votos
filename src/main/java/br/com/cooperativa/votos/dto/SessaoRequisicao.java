package br.com.cooperativa.votos.dto; 

import io.swagger.v3.oas.annotations.media.Schema; 

@Schema(description = "Dados usados para abrir uma sessão de votação em uma pauta.") 
public record SessaoRequisicao( 
        @Schema(description = "Quantidade de minutos em que a sessão ficará aberta. Se não informar, a API usa 1 minuto por padrão.", example = "5") 
        Integer durationMinutes 
) { 
} 
