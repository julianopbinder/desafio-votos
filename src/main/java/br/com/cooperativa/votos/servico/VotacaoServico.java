package br.com.cooperativa.votos.servico; 

import br.com.cooperativa.votos.dominio.EscolhaVoto; 
import br.com.cooperativa.votos.dominio.Pauta; 
import br.com.cooperativa.votos.dominio.SessaoVotacao; 
import br.com.cooperativa.votos.dominio.Voto; 
import br.com.cooperativa.votos.dto.ResultadoResposta; 
import br.com.cooperativa.votos.dto.VotoRequisicao; 
import br.com.cooperativa.votos.excecao.RegraNegocioExcecao; 
import br.com.cooperativa.votos.repositorio.PautaRepositorio; 
import br.com.cooperativa.votos.repositorio.SessaoRepositorio; 
import br.com.cooperativa.votos.repositorio.VotoRepositorio; 
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional; 

@Service 
public class VotacaoServico { 

    private static final Logger log = LoggerFactory.getLogger(VotacaoServico.class); 

    private final PautaRepositorio pautaRepo; 

    private final SessaoRepositorio sessaoRepo; 

    private final VotoRepositorio votoRepo; 

    private final ClienteCpfServico clienteCpfServico; 

    public VotacaoServico(PautaRepositorio pautaRepo, SessaoRepositorio sessaoRepo, VotoRepositorio votoRepo, ClienteCpfServico clienteCpfServico) { 
        this.pautaRepo = pautaRepo; 
        this.sessaoRepo = sessaoRepo; 
        this.votoRepo = votoRepo; 
        this.clienteCpfServico = clienteCpfServico; 
    } 

    @Transactional 
    public SessaoVotacao abrirSessao(Long pautaId, Integer duracao) { 
        log.info("Solicitada abertura de sessão para pautaId={} com duração={} minuto(s)", pautaId, duracao); 
        Pauta pauta = pautaRepo.findById(pautaId) 
                .orElseThrow(() -> new RegraNegocioExcecao("Pauta inexistente")); 
        if (sessaoRepo.findByPautaId(pautaId).isPresent()) { 
            throw new RegraNegocioExcecao("Sessão já existe para esta pauta"); 
        } 
        SessaoVotacao sessao = sessaoRepo.save(new SessaoVotacao(pauta, duracao)); 
        log.info("Sessão aberta com sucesso. sessaoId={}, pautaId={}, inicio={}, fim={}", sessao.getId(), pautaId, sessao.getInicio(), sessao.getFim()); 
        return sessao; 
    } 

    @Transactional 
    public void votar(Long pautaId, VotoRequisicao req) { 
        log.info("Recebido voto para pautaId={}, associadoId={}", pautaId, req.associadoId()); 
        SessaoVotacao sessao = sessaoRepo.findByPautaId(pautaId) 
                .orElseThrow(() -> new RegraNegocioExcecao("Sessão não aberta")); 
        if (!sessao.estaAberta()) { 
            throw new RegraNegocioExcecao("Sessão encerrada"); 
        } 
        if (votoRepo.existsByPautaIdAndAssociadoId(pautaId, req.associadoId())) { 
            throw new RegraNegocioExcecao("Voto já registrado"); 
        } 
        clienteCpfServico.validarSePodeVotar(req.cpf()); 
        votoRepo.save(new Voto(sessao.getPauta(), req.associadoId(), req.cpf(), req.escolha())); 
        log.info("Voto registrado com sucesso. pautaId={}, associadoId={}, escolha={}", pautaId, req.associadoId(), req.escolha()); 
    } 


    public ResultadoResposta calcularResultado(Long pautaId) {
        log.info("Calculando resultado da pautaId={}", pautaId);
        Pauta pauta = pautaRepo.findById(pautaId)
                .orElseThrow(() -> new RegraNegocioExcecao("Pauta inexistente"));
        long sim = votoRepo.countByPautaIdAndEscolha(pautaId, EscolhaVoto.SIM);
        long nao = votoRepo.countByPautaIdAndEscolha(pautaId, EscolhaVoto.NAO);
        long total = sim + nao;
        String resultado = total == 0 ? "SEM VOTOS" : (sim > nao ? "APROVADA" : (nao > sim ? "REPROVADA" : "EMPATE"));
        log.info("Resultado calculado. pautaId={}, sim={}, nao={}, total={}, resultado={}", pautaId, sim, nao, total, resultado);
        return new ResultadoResposta(pautaId, pauta.getTitulo(), sim, nao, total, resultado);
    }
} 
