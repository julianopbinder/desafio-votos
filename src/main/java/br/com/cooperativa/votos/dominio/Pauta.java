package br.com.cooperativa.votos.dominio; 

import jakarta.persistence.Column; 
import jakarta.persistence.Entity; 
import jakarta.persistence.GeneratedValue; 
import jakarta.persistence.GenerationType; 
import jakarta.persistence.Id; 
import jakarta.persistence.Table; 
import java.time.LocalDateTime; 

@Entity 
@Table(name = "pautas") 
public class Pauta { 

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 

    @Column(nullable = false, length = 150) 
    private String titulo; 

    @Column(nullable = false, length = 1000) 
    private String descricao; 

    @Column(nullable = false) 
    private LocalDateTime criadaEm; 

    protected Pauta() { 
    } 

    public Pauta(String titulo, String descricao) { 
        this.titulo = titulo; 
        this.descricao = descricao; 
        this.criadaEm = LocalDateTime.now(); 
    } 

    public Long getId() { 
        return id; 
    } 

    public String getTitulo() { 
        return titulo; 
    } 

    public String getDescricao() { 
        return descricao; 
    } 

    public LocalDateTime getCriadaEm() { 
        return criadaEm; 
    } 
} 
