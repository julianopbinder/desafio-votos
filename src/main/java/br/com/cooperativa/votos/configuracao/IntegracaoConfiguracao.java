package br.com.cooperativa.votos.configuracao; 

import java.time.Duration; 
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.boot.web.client.RestTemplateBuilder; 
import org.springframework.context.annotation.Bean; 
import org.springframework.context.annotation.Configuration; 
import org.springframework.web.client.RestTemplate; 

@Configuration 
public class IntegracaoConfiguracao { 

    @Bean 
    public RestTemplate restTemplate( 
            RestTemplateBuilder builder, 
            @Value("${integracao.cpf.timeout-segundos:3}") Long timeoutSegundos 
    ) { 
        return builder 
                .setConnectTimeout(Duration.ofSeconds(timeoutSegundos)) 
                .setReadTimeout(Duration.ofSeconds(timeoutSegundos)) 
                .build(); 
    } 
} 
