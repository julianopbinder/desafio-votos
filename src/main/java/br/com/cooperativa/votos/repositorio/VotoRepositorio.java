package br.com.cooperativa.votos.repositorio; 

import br.com.cooperativa.votos.dominio.EscolhaVoto; 
import br.com.cooperativa.votos.dominio.Voto; 
import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository; 

@Repository 
public interface VotoRepositorio extends JpaRepository<Voto, Long> { 

    boolean existsByPautaIdAndAssociadoId(Long pautaId, String associadoId); 

    long countByPautaIdAndEscolha(Long pautaId, EscolhaVoto escolha); 
} 
