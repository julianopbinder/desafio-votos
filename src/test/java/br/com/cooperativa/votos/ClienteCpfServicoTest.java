package br.com.cooperativa.votos; 

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow; 
import static org.junit.jupiter.api.Assertions.assertEquals; 
import static org.junit.jupiter.api.Assertions.assertThrows; 
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo; 
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound; 
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess; 

import br.com.cooperativa.votos.servico.ClienteCpfServico; 
import org.junit.jupiter.api.BeforeEach; 
import org.junit.jupiter.api.Test; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.boot.test.context.SpringBootTest; 
import org.springframework.http.MediaType; 
import org.springframework.test.context.ActiveProfiles; 
import org.springframework.test.web.client.MockRestServiceServer; 
import org.springframework.web.client.RestTemplate; 

@SpringBootTest(properties = { 
        "integracao.cpf.habilitada=true", 
        "integracao.cpf.url-base=http://servico-cpf/users", 
        "spring.datasource.url=jdbc:h2:mem:cpf-test;DB_CLOSE_DELAY=-1", 
        "spring.jpa.hibernate.ddl-auto=create-drop" 
}) 
@ActiveProfiles("test") 
class ClienteCpfServicoTest { 

    @Autowired 
    private ClienteCpfServico clienteCpfServico; 

    @Autowired 
    private RestTemplate restTemplate; 

    private MockRestServiceServer servidor; 

    @BeforeEach 
    void configurarServidorFalso() { 
        servidor = MockRestServiceServer.createServer(restTemplate); 
    } 

    @Test 
    void devePermitirVotoQuandoServicoRetornaAbleToVote() { 
        servidor.expect(requestTo("http://servico-cpf/users/12345678909")) 
                .andRespond(withSuccess("{\"status\":\"ABLE_TO_VOTE\"}", MediaType.APPLICATION_JSON)); 
        assertDoesNotThrow(() -> clienteCpfServico.validarSePodeVotar("12345678909")); 
        servidor.verify(); 
    } 

    @Test 
    void deveBloquearVotoQuandoServicoRetornaUnableToVote() { 
        servidor.expect(requestTo("http://servico-cpf/users/12345678909")) 
                .andRespond(withSuccess("{\"status\":\"UNABLE_TO_VOTE\"}", MediaType.APPLICATION_JSON)); 
        RuntimeException erro = assertThrows(RuntimeException.class, () -> clienteCpfServico.validarSePodeVotar("12345678909")); 
        assertEquals("Associado não está autorizado a votar pelo serviço externo de CPF", erro.getMessage()); 
        servidor.verify(); 
    } 

    @Test 
    void deveBloquearCpfInvalidoQuandoServicoRetorna404() { 
        servidor.expect(requestTo("http://servico-cpf/users/00000000000")) 
                .andRespond(withResourceNotFound()); 
        RuntimeException erro = assertThrows(RuntimeException.class, () -> clienteCpfServico.validarSePodeVotar("00000000000")); 
        assertEquals("CPF inválido no serviço externo", erro.getMessage()); 
        servidor.verify(); 
    } 
} 
