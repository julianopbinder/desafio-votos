package br.com.cooperativa.votos.dominio; 

import jakarta.persistence.Column; 
import jakarta.persistence.Entity; 
import jakarta.persistence.GeneratedValue; 
import jakarta.persistence.GenerationType; 
import jakarta.persistence.Id; 
import jakarta.persistence.JoinColumn; 
import jakarta.persistence.OneToOne; 
import jakarta.persistence.Table; 
import java.time.LocalDateTime; 

@Entity 
@Table(name = "sessoes_votacao") 
public class SessaoVotacao { 

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 

    @OneToOne(optional = false) 
    @JoinColumn(name = "pauta_id", nullable = false, unique = true) 
    private Pauta pauta; 

    @Column(nullable = false) 
    private LocalDateTime inicio; 

    @Column(nullable = false) 
    private LocalDateTime fim; 

    protected SessaoVotacao() { 
    } 

    public SessaoVotacao(Pauta pauta, Integer duracaoMinutos) { 
        int duracaoAplicada = duracaoMinutos == null || duracaoMinutos <= 0 ? 1 : duracaoMinutos; 
        this.pauta = pauta; 
        this.inicio = LocalDateTime.now(); 
        this.fim = this.inicio.plusMinutes(duracaoAplicada); 
    } 

    public boolean estaAberta() { 
        return LocalDateTime.now().isBefore(fim); 
    } 

    public Long getId() { 
        return id; 
    } 

    public Pauta getPauta() { 
        return pauta; 
    } 

    public LocalDateTime getInicio() { 
        return inicio; 
    } 

    public LocalDateTime getFim() { 
        return fim; 
    } 
} 
