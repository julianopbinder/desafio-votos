package br.com.cooperativa.votos.excecao; 

public class RegraNegocioExcecao extends RuntimeException { 

    public RegraNegocioExcecao(String mensagem) { 
        super(mensagem); 
    } 
} 
