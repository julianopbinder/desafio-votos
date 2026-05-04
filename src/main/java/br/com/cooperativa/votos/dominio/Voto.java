package br.com.cooperativa.votos.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "votos",
        uniqueConstraints = @UniqueConstraint(name = "uk_voto_pauta_associado", columnNames = {"pauta_id", "associado_id"}),
        indexes = {
                @Index(name = "idx_votos_pauta_escolha", columnList = "pauta_id, escolha_voto")
        }
)
public class Voto {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 

    @ManyToOne(optional = false) 
    @JoinColumn(name = "pauta_id", nullable = false) 
    private Pauta pauta; 

    @Column(name = "associado_id", nullable = false, length = 80) 
    private String associadoId; 

    @Column(length = 20) 
    private String cpf; 

    @Enumerated(EnumType.STRING) 
    @Column(name = "escolha_voto", nullable = false, length = 3) 
    private EscolhaVoto escolha; 

    @Column(nullable = false) 
    private LocalDateTime votadoEm; 

    protected Voto() { 
    } 

    public Voto(Pauta pauta, String associadoId, String cpf, EscolhaVoto escolha) { 
        this.pauta = pauta; 
        this.associadoId = associadoId; 
        this.cpf = cpf; 
        this.escolha = escolha; 
        this.votadoEm = LocalDateTime.now(); 
    } 

    public Long getId() { 
        return id; 
    } 

    public Pauta getPauta() { 
        return pauta; 
    } 

    public String getAssociadoId() { 
        return associadoId; 
    } 

    public String getCpf() { 
        return cpf; 
    } 

    public EscolhaVoto getEscolha() { 
        return escolha; 
    } 

    public LocalDateTime getVotadoEm() { 
        return votadoEm; 
    } 
} 
