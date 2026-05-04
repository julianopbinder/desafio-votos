package br.com.cooperativa.votos.repositorio; 

import br.com.cooperativa.votos.dominio.Pauta; 
import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository; 

@Repository 
public interface PautaRepositorio extends JpaRepository<Pauta, Long> { 
} 
