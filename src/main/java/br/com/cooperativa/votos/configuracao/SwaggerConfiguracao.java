package br.com.cooperativa.votos.configuracao;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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

        /**
         * Define explicitamente a URL base exibida pelo Swagger UI.
         *
         * Em ambientes com proxy reverso, como Railway, a aplicação recebe a requisição
         * internamente via HTTP, mesmo quando o usuário acessa a API por HTTPS. Por isso,
         * esta configuração usa a propriedade app.base-url, alimentada pela variável
         * APP_BASE_URL no Railway, para garantir que o Swagger execute chamadas HTTPS.
         */
        @Bean
        public OpenAPI configurarServidorDaDocumentacao(@Value("${app.base-url:http://localhost:8080}" ) String appBaseUrl) {
                String urlBaseSemBarraFinal = appBaseUrl.replaceAll("/+$", "");

                Server servidorPrincipal = new Server()
                        .url(urlBaseSemBarraFinal)
                        .description("Servidor principal da API");

                return new OpenAPI().servers(List.of(servidorPrincipal));
        }
}