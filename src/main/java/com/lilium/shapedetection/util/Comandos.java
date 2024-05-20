package com.lilium.shapedetection.util;

public class Comandos {

    private int tp_comando;
    private String texto;
    private int qtde_repeticao;
    private int segundos_aguardando;

    public Comandos (){


    }

    public Comandos (int tp_comando, String texto , int qtde_repeticao, int segundos_aguardando){

        this.tp_comando = tp_comando;
        this.texto = texto;
        this.qtde_repeticao = qtde_repeticao;
        this.segundos_aguardando = segundos_aguardando;

    }

    public int getTp_comando() {
        return tp_comando;
    }

    public void setTp_comando(int tp_comando) {
        this.tp_comando = tp_comando;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getQtde_repeticao() {
        return qtde_repeticao;
    }

    public void setQtde_repeticao(int qtde_repeticao) {
        this.qtde_repeticao = qtde_repeticao;
    }

    public int getSegundos_aguardando() {
        return segundos_aguardando;
    }

    public void setSegundos_aguardando(int segundos_aguardando) {
        this.segundos_aguardando = segundos_aguardando;
    }

}
