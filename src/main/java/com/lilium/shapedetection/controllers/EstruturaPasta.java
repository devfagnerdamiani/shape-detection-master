package com.lilium.shapedetection.controllers;

public class EstruturaPasta {

    private String endereco;
    private boolean statusCriacao;

    public EstruturaPasta(String endereco, boolean statusCriacao) {
        this.endereco = endereco;
        this.statusCriacao = statusCriacao;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public boolean isStatusCriacao() {
        return statusCriacao;
    }

    public void setStatusCriacao(boolean statusCriacao) {
        this.statusCriacao = statusCriacao;
    }
    

}
