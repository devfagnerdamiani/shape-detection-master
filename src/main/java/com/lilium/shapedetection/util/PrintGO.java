package com.lilium.shapedetection.util;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PrintGO {

       public BufferedImage print;
       public boolean statusPrint;
       public ArrayList<Comandos> listaDeComandos = new ArrayList<>();
       public int localMidia;
       public Boolean printModel;

       public PrintGO(BufferedImage print, boolean statusPrint) {
              this.print = print;
              this.statusPrint = statusPrint;
       }

       public PrintGO(BufferedImage print, boolean statusPrint, int local_midia, ArrayList<Comandos> listaDeComandos, Boolean printModel) {
              this.print = print;
              this.localMidia = local_midia;
              this.statusPrint = statusPrint;
              this.listaDeComandos = listaDeComandos;
              this.printModel = printModel;
       }

       public BufferedImage getPrint() {
              return print;
       }

       public void setPrint(BufferedImage print) {
              this.print = print;
       }

       public int getLocalMidia() {
              return localMidia;
       }

       public void setLocalMidia(int print) {
              this.localMidia = localMidia;
       }

       public boolean getStatusPrint() {
              return statusPrint;
       }

       public void setStatusPrint(Boolean statusPrint) {
              this.statusPrint = statusPrint;
       }

       public ArrayList<Comandos> getListaDeComandos() {
              return listaDeComandos;
       }

       public void setListaDeComandos(ArrayList<Comandos> listaDeComandos) {
              this.listaDeComandos = listaDeComandos;
       }

       public ArrayList executa_tarefas(int local_midia){

              ArrayList<Comandos> todos_comandos = new ArrayList<>();

              if(local_midia == 1){

                     RobotUtil.Faz_Print();
                     System.out.println("FEZ O PRINT DO BANNER TOPO");

              }if(local_midia == 2){

                   todos_comandos = monta_lista_de_comandos(local_midia);
                   System.out.println("MONTOU TODOS OS COMANDOS 2 => " + todos_comandos.size());

              }

              if(local_midia == 3){

              todos_comandos = monta_lista_de_comandos(local_midia);
              System.out.println("MONTOU TODOS OS COMANDOS 3 => " + todos_comandos.size());

              }

              if(local_midia == 4){

                     todos_comandos = monta_lista_de_comandos(local_midia);
                     System.out.println("MONTOU TODOS OS COMANDOS 4 => " + todos_comandos.size());

              }

              return todos_comandos;

       }

       public ArrayList monta_lista_de_comandos(int local_midia){

              ArrayList<Comandos> nova_lista = new ArrayList<>();

              switch(local_midia) {

                     case 2:

                            Comandos novo_comando_1 = new Comandos(14,"",62,1);
                            Comandos novo_comando_2 = new Comandos(17,"",1,3);
                            Comandos novo_comando_3 = new Comandos(13,"",5,1);
                            nova_lista.add(novo_comando_1);
                            nova_lista.add(novo_comando_2);
                            nova_lista.add(novo_comando_3);

                     break;
                     case 3:
                            Comandos novo_comando_4 = new Comandos(14,"",57,1);
                            Comandos novo_comando_5 = new Comandos(17,"",1,3);
                            Comandos novo_comando_6 = new Comandos(13,"",5,1);
                            nova_lista.add(novo_comando_4);
                            nova_lista.add(novo_comando_5);
                            nova_lista.add(novo_comando_6);

                            break;
                     case 4:
                            Comandos novo_comando_7 = new Comandos(14,"",57,1);
                            Comandos novo_comando_8 = new Comandos(17,"",1,3);
                            Comandos novo_comando_9 = new Comandos(13,"",5,1);
                            nova_lista.add(novo_comando_7);
                            nova_lista.add(novo_comando_8);
                            nova_lista.add(novo_comando_9);

                            break;

              }

              return nova_lista;

       }

       public boolean isStatusPrint() {
              return statusPrint;
       }

       public void setStatusPrint(boolean statusPrint) {
              this.statusPrint = statusPrint;
       }

       public Boolean getPrintModel() {
              return printModel;
       }

       public void setPrintModel(Boolean printModel) {
              this.printModel = printModel;
       }

}