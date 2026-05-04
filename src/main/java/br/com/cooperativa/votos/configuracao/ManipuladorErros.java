package br.com.cooperativa.votos.configuracao; 

import br.com.cooperativa.votos.excecao.RegraNegocioExcecao; 
import java.util.LinkedHashMap; 
import java.util.List; 
import java.util.Map; 
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 
import org.springframework.dao.DataIntegrityViolationException; 
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity; 
import org.springframework.http.converter.HttpMessageNotReadableException; 
import org.springframework.web.bind.MethodArgumentNotValidException; 
import org.springframework.web.bind.annotation.ExceptionHandler; 
import org.springframework.web.bind.annotation.RestControllerAdvice; 

@RestControllerAdvice 
public class ManipuladorErros { 

    private static final Logger log = LoggerFactory.getLogger(ManipuladorErros.class); 

    @ExceptionHandler(RegraNegocioExcecao.class) 
    public ResponseEntity<Map<String, Object>> tratarRegraNegocio(RegraNegocioExcecao erro) { 
        log.warn("Erro de regra de negócio: {}", erro.getMessage()); 
        return resposta(HttpStatus.BAD_REQUEST, "Regra de negócio violada", erro.getMessage()); 
    } 

    @ExceptionHandler(MethodArgumentNotValidException.class) 
    public ResponseEntity<Map<String, Object>> tratarValidacao(MethodArgumentNotValidException erro) { 
        String mensagem = erro.getBindingResult().getFieldErrors().stream() 
                .findFirst() 
                .map(campo -> campo.getDefaultMessage()) 
                .orElse("Dados inválidos"); 
        log.warn("Erro de validação: {}", mensagem); 
        return resposta(HttpStatus.BAD_REQUEST, "Erro de validação", mensagem); 
    } 

    @ExceptionHandler(HttpMessageNotReadableException.class) 
    public ResponseEntity<Map<String, Object>> tratarJsonInvalido(HttpMessageNotReadableException erro) { 
        log.warn("Erro ao interpretar JSON da requisição: {}", erro.getMessage()); 
        return resposta(HttpStatus.BAD_REQUEST, "Erro de validação", "JSON inválido ou valor de campo não aceito. Para o voto, use escolha SIM ou NAO."); 
    } 

    @ExceptionHandler(DataIntegrityViolationException.class) 
    public ResponseEntity<Map<String, Object>> tratarIntegridade(DataIntegrityViolationException erro) { 
        log.warn("Violação de integridade no banco: {}", erro.getMessage()); 
        return resposta(HttpStatus.BAD_REQUEST, "Violação de integridade", "A operação viola uma regra de unicidade ou relacionamento do banco de dados."); 
    } 

    private ResponseEntity<Map<String, Object>> resposta(HttpStatus status, String titulo, String mensagem) { 
        return ResponseEntity.status(status) 
                .body(formularioErro(titulo, mensagem)); 
    } 

    private Map<String, Object> formularioErro(String titulo, String mensagem) { 
        LinkedHashMap<String, Object> tela = new LinkedHashMap<>(); 
        tela.put("tipo", "FORMULARIO"); 
        tela.put("titulo", titulo); 
        tela.put("itens", List.of(texto(mensagem))); 
        tela.put("botaoOk", botao("Voltar", "http://localhost:8080/api/v1/pautas"));
        return tela; 
    } 

    private Map<String, Object> texto(String mensagem) { 
        LinkedHashMap<String, Object> item = new LinkedHashMap<>(); 
        item.put("tipo", "TEXTO"); 
        item.put("texto", mensagem); 
        return item; 
    } 

    private Map<String, Object> botao(String texto, String url) { 
        LinkedHashMap<String, Object> botao = new LinkedHashMap<>(); 
        botao.put("texto", texto); 
        botao.put("url", url); 
        return botao; 
    } 
} 
