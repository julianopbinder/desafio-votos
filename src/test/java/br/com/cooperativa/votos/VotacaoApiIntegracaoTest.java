package br.com.cooperativa.votos;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.cooperativa.votos.repositorio.PautaRepositorio;
import br.com.cooperativa.votos.repositorio.SessaoRepositorio;
import br.com.cooperativa.votos.repositorio.VotoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "integracao.cpf.habilitada=false",
        "spring.datasource.url=jdbc:h2:mem:votos-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.base-url=http://localhost:8080"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VotacaoApiIntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VotoRepositorio votoRepositorio;

    @Autowired
    private SessaoRepositorio sessaoRepositorio;

    @Autowired
    private PautaRepositorio pautaRepositorio;

    @BeforeEach
    void limparBanco() {
        votoRepositorio.deleteAll();
        sessaoRepositorio.deleteAll();
        pautaRepositorio.deleteAll();
    }

    @Test
    void deveExecutarFluxoCompletoDoTesteTecnico() throws Exception {
        Long pautaId = criarPauta("Pauta de teste", "Descrição da pauta de teste");

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/sessoes", pautaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"durationMinutes\":5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo", equalTo("SELECAO")))
                .andExpect(jsonPath("$.titulo", equalTo("Sessão aberta")))
                .andExpect(jsonPath("$.itens[1].titulo", equalTo("Registrar voto")));

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/votos", pautaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\":\"assoc-001\",\"cpf\":\"12345678909\",\"escolha\":\"SIM\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo", equalTo("SELECAO")))
                .andExpect(jsonPath("$.titulo", equalTo("Voto registrado")));

        mockMvc.perform(get("/api/v1/pautas/{pautaId}/resultado", pautaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo", equalTo("FORMULARIO")))
                .andExpect(jsonPath("$.titulo", equalTo("Resultado da votação")))
                .andExpect(jsonPath("$.itens[1].texto", equalTo("Votos SIM: 1")))
                .andExpect(jsonPath("$.itens[2].texto", equalTo("Votos NAO: 0")))
                .andExpect(jsonPath("$.itens[3].texto", equalTo("Total de votos: 1")))
                .andExpect(jsonPath("$.itens[4].texto", equalTo("Resultado: APROVADA")));
    }

    @Test
    void deveConsultarResultadoTambemPorPostParaCompatibilidadeComAnexoUm() throws Exception {
        Long pautaId = criarPauta("Pauta resultado por POST", "Compatibilidade com botão do Anexo 1");
        abrirSessao(pautaId);

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/votos", pautaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\":\"assoc-post-resultado\",\"cpf\":\"12345678909\",\"escolha\":\"SIM\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/resultado", pautaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pautaId\":" + pautaId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo", equalTo("FORMULARIO")))
                .andExpect(jsonPath("$.titulo", equalTo("Resultado da votação")))
                .andExpect(jsonPath("$.itens[1].texto", equalTo("Votos SIM: 1")))
                .andExpect(jsonPath("$.botaoOk.body.pautaId", equalTo(pautaId.intValue())));
    }

    @Test
    void deveUsarDuracaoPadraoQuandoSessaoNaoRecebeCorpo() throws Exception {
        Long pautaId = criarPauta("Pauta duração padrão", "Sessão sem duração informada");

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/sessoes", pautaId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo", equalTo("SELECAO")))
                .andExpect(jsonPath("$.itens[0].texto", containsString("A sessão ficará aberta até")));
    }

    @Test
    void deveImpedirVotoDuplicado() throws Exception {
        Long pautaId = criarPauta("Pauta duplicidade", "Teste de voto duplicado");
        abrirSessao(pautaId);
        String voto = "{\"associadoId\":\"assoc-duplicado\",\"cpf\":\"12345678909\",\"escolha\":\"SIM\"}";

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/votos", pautaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voto))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/votos", pautaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voto))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.tipo", equalTo("FORMULARIO")))
                .andExpect(jsonPath("$.titulo", equalTo("Regra de negócio violada")))
                .andExpect(jsonPath("$.itens[0].texto", equalTo("Voto já registrado")));
    }

    @Test
    void deveBloquearVotoSemSessaoAberta() throws Exception {
        Long pautaId = criarPauta("Pauta sem sessão", "Teste sem sessão");

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/votos", pautaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\":\"assoc-002\",\"cpf\":\"12345678909\",\"escolha\":\"NAO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.tipo", equalTo("FORMULARIO")))
                .andExpect(jsonPath("$.itens[0].texto", equalTo("Sessão não aberta")));
    }

    @Test
    void deveRetornarJsonPadronizadoQuandoEscolhaDoVotoForInvalida() throws Exception {
        Long pautaId = criarPauta("Pauta escolha inválida", "Teste de escolha inválida");
        abrirSessao(pautaId);

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/votos", pautaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\":\"assoc-escolha-invalida\",\"cpf\":\"12345678909\",\"escolha\":\"TALVEZ\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.tipo", equalTo("FORMULARIO")))
                .andExpect(jsonPath("$.titulo", equalTo("Erro de validação")))
                .andExpect(jsonPath("$.itens[0].texto", equalTo("JSON inválido ou valor de campo não aceito. Para o voto, use escolha SIM ou NAO.")));
    }

    private Long criarPauta(String titulo, String descricao) throws Exception {
        String json = "{\"titulo\":\"" + titulo + "\",\"descricao\":\"" + descricao + "\"}";

        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo", equalTo("SELECAO")))
                .andExpect(jsonPath("$.titulo", equalTo("Pauta cadastrada")))
                .andExpect(jsonPath("$.itens[0].titulo", equalTo("Abrir sessão")));

        return pautaRepositorio.findAll().get(0).getId();
    }

    private void abrirSessao(Long pautaId) throws Exception {
        mockMvc.perform(post("/api/v1/pautas/{pautaId}/sessoes", pautaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"durationMinutes\":5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo", equalTo("SELECAO")));
    }
}
