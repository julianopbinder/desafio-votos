package br.com.cooperativa.votos.servico; 

import br.com.cooperativa.votos.dto.CpfRespostaExterna; 
import br.com.cooperativa.votos.excecao.RegraNegocioExcecao; 
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.http.ResponseEntity; 
import org.springframework.stereotype.Service; 
import org.springframework.util.StringUtils; 
import org.springframework.web.client.HttpClientErrorException; 
import org.springframework.web.client.RestClientException; 
import org.springframework.web.client.RestTemplate; 

@Service 
public class ClienteCpfServico { 

    private static final Logger log = LoggerFactory.getLogger(ClienteCpfServico.class); 

    private final RestTemplate restTemplate; 

    private final boolean integracaoHabilitada; 

    private final String urlBase; 

    public ClienteCpfServico( 
            RestTemplate restTemplate, 
            @Value("${integracao.cpf.habilitada:false}") boolean integracaoHabilitada, 
            @Value("${integracao.cpf.url-base:https://user-info.herokuapp.com/users}") String urlBase 
    ) { 
        this.restTemplate = restTemplate; 
        this.integracaoHabilitada = integracaoHabilitada; 
        this.urlBase = urlBase; 
    } 

    public void validarSePodeVotar(String cpf) { 
        if (!integracaoHabilitada) { 
            log.info("Integração de CPF desabilitada por configuração; votação seguirá sem chamada externa"); 
            return; 
        } 
        if (!StringUtils.hasText(cpf)) { 
            throw new RegraNegocioExcecao("CPF obrigatório quando a integração externa está habilitada"); 
        } 
        String url = urlBase + "/" + cpf; 
        log.info("Consultando serviço externo de CPF para autorização de voto. cpf={}", cpf); 
        try { 
            ResponseEntity<CpfRespostaExterna> resposta = restTemplate.getForEntity(url, CpfRespostaExterna.class); 
            CpfRespostaExterna corpo = resposta.getBody(); 
            String status = corpo == null ? null : corpo.status(); 
            if (!"ABLE_TO_VOTE".equals(status)) { 
                log.warn("CPF não autorizado para votar. cpf={}, status={}", cpf, status); 
                throw new RegraNegocioExcecao("Associado não está autorizado a votar pelo serviço externo de CPF"); 
            } 
            log.info("CPF autorizado para votar pelo serviço externo. cpf={}", cpf); 
        } catch (HttpClientErrorException.NotFound erro) { 
            log.warn("CPF inválido no serviço externo. cpf={}", cpf); 
            throw new RegraNegocioExcecao("CPF inválido no serviço externo"); 
        } catch (RestClientException erro) { 
            log.error("Falha ao consultar serviço externo de CPF", erro); 
            throw new RegraNegocioExcecao("Não foi possível consultar o serviço externo de CPF"); 
        } 
    } 
} 
