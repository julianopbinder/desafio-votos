package br.com.cooperativa.votos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import br.com.cooperativa.votos.dominio.EscolhaVoto;
import br.com.cooperativa.votos.dominio.Pauta;
import br.com.cooperativa.votos.dominio.SessaoVotacao;
import br.com.cooperativa.votos.dominio.Voto;
import br.com.cooperativa.votos.dto.ResultadoResposta;
import br.com.cooperativa.votos.repositorio.PautaRepositorio;
import br.com.cooperativa.votos.repositorio.SessaoRepositorio;
import br.com.cooperativa.votos.repositorio.VotoRepositorio;
import br.com.cooperativa.votos.servico.VotacaoServico;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "integracao.cpf.habilitada=false",
        "spring.datasource.url=jdbc:h2:mem:performance-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@ActiveProfiles("test")
class VotacaoPerformanceTest {

    private static final int TOTAL_DE_VOTOS = 10_000;

    @Autowired
    private VotacaoServico votacaoServico;

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
    void deveApurarResultadoComGrandeVolumeDeVotosSemCarregarTodosEmMemoria() {
        Pauta pauta = pautaRepositorio.save(new Pauta("Pauta de performance", "Validação com grande volume de votos"));
        SessaoVotacao sessao = sessaoRepositorio.save(new SessaoVotacao(pauta, 10));

        List<Voto> votos = new ArrayList<>(TOTAL_DE_VOTOS);
        for (int indice = 0; indice < TOTAL_DE_VOTOS; indice++) {
            EscolhaVoto escolha = indice % 2 == 0 ? EscolhaVoto.SIM : EscolhaVoto.NAO;
            votos.add(new Voto(pauta, "associado-performance-" + indice, "12345678909", escolha));
        }
        votoRepositorio.saveAll(votos);
        votoRepositorio.flush();

        ResultadoResposta resultado = assertTimeoutPreemptively(
                Duration.ofSeconds(5),
                () -> votacaoServico.calcularResultado(pauta.getId())
        );

        assertEquals(TOTAL_DE_VOTOS / 2, resultado.votosSim());
        assertEquals(TOTAL_DE_VOTOS / 2, resultado.votosNao());
        assertEquals(TOTAL_DE_VOTOS, resultado.totalVotos());
        assertEquals("EMPATE", resultado.resultado());

    }
}