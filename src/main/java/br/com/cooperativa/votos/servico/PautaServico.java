package br.com.cooperativa.votos.servico; 

import br.com.cooperativa.votos.dominio.Pauta; 
import br.com.cooperativa.votos.excecao.RegraNegocioExcecao; 
import br.com.cooperativa.votos.repositorio.PautaRepositorio; 
import java.util.List; 
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional; 

@Service 
public class PautaServico { 

    private static final Logger log = LoggerFactory.getLogger(PautaServico.class); 

    private final PautaRepositorio repositorio; 

    public PautaServico(PautaRepositorio repositorio) { 
        this.repositorio = repositorio; 
    } 

    @Transactional 
    public Pauta criar(String titulo, String descricao) { 
        log.info("Criando nova pauta com título '{}'", titulo); 
        Pauta pauta = repositorio.save(new Pauta(titulo, descricao)); 
        log.info("Pauta criada com sucesso. id={}", pauta.getId()); 
        return pauta; 
    } 

    public List<Pauta> listarTodas() { 
        log.info("Listando todas as pautas cadastradas"); 
        return repositorio.findAll(); 
    } 

    public Pauta buscarPorId(Long id) { 
        log.info("Buscando pauta por id={}", id); 
        return repositorio.findById(id) 
                .orElseThrow(() -> new RegraNegocioExcecao("Pauta não encontrada")); 
    } 
} 
