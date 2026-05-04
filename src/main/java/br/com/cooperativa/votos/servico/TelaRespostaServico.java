package br.com.cooperativa.votos.servico;

import br.com.cooperativa.votos.dominio.Pauta;
import br.com.cooperativa.votos.dominio.SessaoVotacao;
import br.com.cooperativa.votos.dto.ResultadoResposta;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TelaRespostaServico {

    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final String baseUrl;

    public TelaRespostaServico(@Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.baseUrl = removerBarraFinal(baseUrl);
    }

    public Map<String, Object> telaPautaCriada(Pauta pauta) {
        return selecao(
                "Pauta cadastrada",
                List.of(
                        opcao("Abrir sessão", url("/api/v1/pautas/" + pauta.getId() + "/sessoes"), body("durationMinutes", 1)),
                        opcao("Registrar voto", url("/api/v1/pautas/" + pauta.getId() + "/votos"), bodyVotoExemplo()),
                        opcao("Consultar resultado", url("/api/v1/pautas/" + pauta.getId() + "/resultado"), body("pautaId", pauta.getId()))
                )
        );
    }

    public Map<String, Object> telaSessaoAberta(SessaoVotacao sessao) {
        Long pautaId = sessao.getPauta().getId();
        return selecao(
                "Sessão aberta",
                List.of(
                        texto("A sessão ficará aberta até " + sessao.getFim().format(FORMATO_DATA_HORA) + "."),
                        opcao("Registrar voto", url("/api/v1/pautas/" + pautaId + "/votos"), bodyVotoExemplo()),
                        opcao("Consultar resultado", url("/api/v1/pautas/" + pautaId + "/resultado"), body("pautaId", pautaId))
                )
        );
    }

    public Map<String, Object> telaVotoRegistrado(Long pautaId) {
        return selecao(
                "Voto registrado",
                List.of(
                        texto("Voto recebido com sucesso."),
                        opcao("Consultar resultado", url("/api/v1/pautas/" + pautaId + "/resultado"), body("pautaId", pautaId))
                )
        );
    }

    public Map<String, Object> telaResultado(ResultadoResposta resultado) {
        return formulario(
                "Resultado da votação",
                List.of(
                        texto("Pauta: " + resultado.titulo()),
                        texto("Votos SIM: " + resultado.votosSim()),
                        texto("Votos NAO: " + resultado.votosNao()),
                        texto("Total de votos: " + resultado.totalVotos()),
                        texto("Resultado: " + resultado.resultado())
                ),
                botao("Atualizar", url("/api/v1/pautas/" + resultado.pautaId() + "/resultado"), body("pautaId", resultado.pautaId()))
        );
    }

    private Map<String, Object> formulario(String titulo, List<Map<String, Object>> itens, Map<String, Object> botaoOk) {
        LinkedHashMap<String, Object> tela = new LinkedHashMap<>();
        tela.put("tipo", "FORMULARIO");
        tela.put("titulo", titulo);
        tela.put("itens", itens);
        tela.put("botaoOk", botaoOk);
        return tela;
    }

    private Map<String, Object> selecao(String titulo, List<Map<String, Object>> itens) {
        LinkedHashMap<String, Object> tela = new LinkedHashMap<>();
        tela.put("tipo", "SELECAO");
        tela.put("titulo", titulo);
        tela.put("itens", itens);
        return tela;
    }

    private Map<String, Object> texto(String valor) {
        LinkedHashMap<String, Object> item = new LinkedHashMap<>();
        item.put("tipo", "TEXTO");
        item.put("texto", valor);
        return item;
    }

    private Map<String, Object> opcao(String titulo, String url, Map<String, Object> body) {
        LinkedHashMap<String, Object> item = new LinkedHashMap<>();
        item.put("tipo", "OPCAO");
        item.put("titulo", titulo);
        item.put("url", url);
        item.put("body", body == null ? new LinkedHashMap<>() : body);
        return item;
    }

    private Map<String, Object> botao(String titulo, String url, Map<String, Object> body) {
        LinkedHashMap<String, Object> botao = new LinkedHashMap<>();
        botao.put("titulo", titulo);
        botao.put("url", url);
        botao.put("body", body == null ? new LinkedHashMap<>() : body);
        return botao;
    }

    private LinkedHashMap<String, Object> body(String chave, Object valor) {
        LinkedHashMap<String, Object> body = new LinkedHashMap<>();
        body.put(chave, valor);
        return body;
    }

    private LinkedHashMap<String, Object> bodyVotoExemplo() {
        LinkedHashMap<String, Object> body = new LinkedHashMap<>();
        body.put("associadoId", "associado-001");
        body.put("cpf", "12345678909");
        body.put("escolha", "SIM");
        return body;
    }

    private String url(String caminho) {
        return baseUrl + caminho;
    }

    private String removerBarraFinal(String valor) {
        if (valor == null || valor.isBlank()) {
            return "http://localhost:8080";
        }
        return valor.endsWith("/") ? valor.substring(0, valor.length() - 1) : valor;
    }
}
