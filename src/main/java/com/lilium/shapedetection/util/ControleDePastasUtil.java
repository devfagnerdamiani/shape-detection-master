package com.lilium.shapedetection.util;

import com.lilium.shapedetection.controllers.EstruturaPasta;

import java.io.File;
import java.util.ArrayList;

public class ControleDePastasUtil {

  private File file;
  private ArrayList<EstruturaPasta> listaDePastas = new ArrayList<>();

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public ArrayList<EstruturaPasta> getListaDePastas() {
    return listaDePastas;
  }

  public void setListaDePastas(ArrayList<EstruturaPasta> listaDePastas) {
    this.listaDePastas = listaDePastas;
  }

  public void cria_pastas(){

    monta_lista_de_pastas();

    getListaDePastas().stream().forEach(item -> {

      file = new File(item.getEndereco());

      if(file.exists()){
        item.setStatusCriacao(true);
      }else{
        file.mkdir();
        item.setStatusCriacao(true);
      }

    });

  }

  public void monta_lista_de_pastas(){

  EstruturaPasta pasta_1 =  new EstruturaPasta("C:\\Users\\Eduardo\\OneDrive - L2INOVA\\prints\\imagem_de_referencia",
            false);

      getListaDePastas().add(pasta_1);

      setListaDePastas(getListaDePastas());

  }

}