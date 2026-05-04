package br.com.cooperativa.votos.configuracao; 

import io.swagger.v3.oas.annotations.OpenAPIDefinition; 
import io.swagger.v3.oas.annotations.info.Contact; 
import io.swagger.v3.oas.annotations.info.Info; 
import org.springframework.context.annotation.Configuration; 

@Configuration 
@OpenAPIDefinition( 
        info = @Info( 
                title = "API de Votação em Cooperativa", 
                version = "v1", 
                description = "API REST para cadastrar pautas, abrir sessões, registrar votos únicos por associado, validar CPF opcionalmente e consultar resultados.", 
                contact = @Contact(name = "Desafio Técnico Java") 
        ) 
) 
public class SwaggerConfiguracao { 
} 
