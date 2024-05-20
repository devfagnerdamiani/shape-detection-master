package com.lilium.shapedetection.controllers;

import java.util.Date;

public class dataManipulation {

    Date dataProgramada = new Date();
    boolean status = false;

    String dataTexto = "";

    public dataManipulation(Date dataProgramada, boolean status, String dataTexto) {
        this.dataProgramada = dataProgramada;
        this.status = status;
        this.dataTexto = dataTexto;
    }

    public Date getDataProgramada() {
        return dataProgramada;
    }

    public void setDataProgramada(Date dataProgramada) {
        this.dataProgramada = dataProgramada;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDataTexto() {
        return dataTexto;
    }

    public void setDataTexto(String dataTexto) {
        this.dataTexto = dataTexto;
    }

}