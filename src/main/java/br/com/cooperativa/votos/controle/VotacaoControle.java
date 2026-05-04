package br.com.cooperativa.votos.controle;

import br.com.cooperativa.votos.dominio.Pauta;
import br.com.cooperativa.votos.dominio.SessaoVotacao;
import br.com.cooperativa.votos.dto.PautaRequisicao;
import br.com.cooperativa.votos.dto.ResultadoResposta;
import br.com.cooperativa.votos.dto.SessaoRequisicao;
import br.com.cooperativa.votos.dto.VotoRequisicao;
import br.com.cooperativa.votos.servico.PautaServico;
import br.com.cooperativa.votos.servico.TelaRespostaServico;
import br.com.cooperativa.votos.servico.VotacaoServico;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pautas")
@Tag(name = "votacao", description = "Fluxo simples do teste técnico: pauta, sessão, voto e resultado.")
public class VotacaoControle {

    private final PautaServico pautaServico;
    private final VotacaoServico votacaoServico;
    private final TelaRespostaServico telaRespostaServico;

    public VotacaoControle(PautaServico pautaServico, VotacaoServico votacaoServico, TelaRespostaServico telaRespostaServico) {
        this.pautaServico = pautaServico;
        this.votacaoServico = votacaoServico;
        this.telaRespostaServico = telaRespostaServico;
    }

    @Operation(summary = "Cadastrar pauta", description = "Cria uma pauta e retorna JSON no padrão SELECAO do Anexo 1.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> criarPauta(@RequestBody @Valid PautaRequisicao requisicao) {
        Pauta pauta = pautaServico.criar(requisicao.titulo(), requisicao.descricao());
        return telaRespostaServico.telaPautaCriada(pauta);
    }

    @Operation(summary = "Abrir sessão", description = "Abre a sessão de votação por durationMinutes ou por 1 minuto quando a duração não for informada.")
    @PostMapping("/{pautaId}/sessoes")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> abrirSessao(
            @Parameter(description = "ID da pauta criada.", example = "1") @PathVariable Long pautaId,
            @RequestBody(required = false) SessaoRequisicao requisicao
    ) {
        Integer duracao = requisicao == null ? null : requisicao.durationMinutes();
        SessaoVotacao sessao = votacaoServico.abrirSessao(pautaId, duracao);
        return telaRespostaServico.telaSessaoAberta(sessao);
    }

    @Operation(summary = "Receber voto", description = "Registra voto SIM ou NAO, bloqueia voto duplicado e valida CPF quando a integração externa estiver habilitada.")
    @PostMapping("/{pautaId}/votos")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> votar(
            @Parameter(description = "ID da pauta em votação.", example = "1") @PathVariable Long pautaId,
            @RequestBody @Valid VotoRequisicao requisicao
    ) {
        votacaoServico.votar(pautaId, requisicao);
        return telaRespostaServico.telaVotoRegistrado(pautaId);
    }

    @Operation(summary = "Consultar resultado", description = "Contabiliza votos SIM, votos NAO, total e situação final da pauta.")
    @GetMapping("/{pautaId}/resultado")
    public Map<String, Object> resultado(
            @Parameter(description = "ID da pauta apurada.", example = "1") @PathVariable Long pautaId
    ) {
        return montarTelaResultado(pautaId);
    }

    @Hidden
    @PostMapping("/{pautaId}/resultado")
    public Map<String, Object> resultadoPorAcaoDoAnexo(
            @Parameter(description = "ID da pauta apurada.", example = "1") @PathVariable Long pautaId
    ) {
        return montarTelaResultado(pautaId);
    }

    private Map<String, Object> montarTelaResultado(Long pautaId) {
        ResultadoResposta resultado = votacaoServico.calcularResultado(pautaId);
        return telaRespostaServico.telaResultado(resultado);
    }
}
