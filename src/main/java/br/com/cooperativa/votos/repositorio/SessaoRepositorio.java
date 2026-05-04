package br.com.cooperativa.votos.repositorio; 

import br.com.cooperativa.votos.dominio.SessaoVotacao; 
import java.util.Optional; 
import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository; 

@Repository 
public interface SessaoRepositorio extends JpaRepository<SessaoVotacao, Long> { 

    Optional<SessaoVotacao> findByPautaId(Long pautaId); 
} 
