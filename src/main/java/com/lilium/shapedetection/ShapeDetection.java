package com.lilium.shapedetection;

import com.lilium.shapedetection.controllers.dataManipulation;
import com.lilium.shapedetection.util.*;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ShapeDetection {

    private static boolean chaveExecutaPrint = true;
    private final static boolean chaveCoracao = true;
    private static Robot robot;

    private static boolean dataEncontrada = false;

    private static int midia_selecionada;
    private static int camera_atual = 1;
    private static boolean chaveOlhoDigital = true;
    private static boolean chaveMonitorar = true;
    private static boolean t1 = true;
    private static boolean t2 = true;
    private static boolean atualizaPagina = false;
    private int p_i = 1;
    private BufferedImage imagemDeReferencia;

    private static String enderecoNuvem = "C:\\OneDrive - L2GROUP\\PrintsBot\\";

    private static String enderecoLocal = "C:\\PrintGO\\";

    private static String pasta_atual = "";

    private static int fila = 0;

    private static ArrayList<dataManipulation> lista_de_datas_up = new ArrayList<>();

    private static boolean statusFila = false;

    public static int processoData = 2;

    private static ControleDePastasUtil criaPastas = new ControleDePastasUtil();

    static {

        try {

            robot = new Robot();

        }
        catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static boolean comparando = false;

    private static Mat quadro;
    private static ArrayList<BufferedImage> lista_de_imagens = new ArrayList<>();
    private static ArrayList<PrintGO> lista_de_prints = new ArrayList<>();
    // Create panels
    public static JPanel telaWebCam = new JPanel();
    public static JPanel telaImagemProcessada = new JPanel();
    public static JButton botaoDeCapturaPdr = new JButton("CP = 0");

    public static JButton botaoDeCapturaPi = new JButton("CI = 0");

    public static JButton botaoResetRotina = new JButton("RESET ROTINA");
    public static JButton botaoLigaOlhoDigital = new JButton("MODO: EXECUTAR");
    public static JButton botaoModoCaptura = new JButton("MODO: OLHO VIRTUAL");
    public static JButton botaoLimpaCapturas = new JButton("LIMPAR");
    public static JFormattedTextField editTextDesce = new JFormattedTextField();
    public static JFormattedTextField editTextSobe = new JFormattedTextField();
    public static JButton botaoPercorrePosicao = new JButton("PERCORRE POSICAO");
    public static JButton botaoGravaPeriodo = new JButton("SALVA DATA E HORA");

    public static JButton botaoPrintManual = new JButton("PRINT");

    public static Integer[] lista_item ={1,2,3,4};
    public static JList listaCameraDeCaptura = new JList(lista_item);
    static Map<String, Boolean> datas_de_acao = new HashMap<String, Boolean>();
    static Map<Date, Boolean> datas_de_acao_cal = new HashMap<Date, Boolean>();
    static DateTimeFormatter data_formato = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static MaskFormatter mascaraData;
    public static MaskFormatter mascaraHora;

    static {
        try {
            mascaraData = new MaskFormatter("##/##/####");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            mascaraHora = new MaskFormatter("##:##");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static JFormattedTextField editDataInicial = new JFormattedTextField(mascaraData);
    public static JFormattedTextField editDataFinal = new JFormattedTextField(mascaraData);
    public static JFormattedTextField editHoraExecucao = new JFormattedTextField(mascaraHora);

    public static int atualiazador = 0;

    public static void main (final String args[]) throws ParseException {

        //      criaPastas.cria_pastas();

        // Load OpenCV

        OpenCV.loadShared();

        VideoCapture webCam = new VideoCapture(2); // Escolhe WEBCAM

        botaoResetRotina.addActionListener(e -> resetRotina());
        botaoDeCapturaPi.addActionListener(e -> adcionaPrintImagem(telaWebCam,quadro, botaoResetRotina,editTextDesce,editTextSobe));
        botaoDeCapturaPdr.addActionListener(e -> adicionaPrintPadrao(botaoDeCapturaPdr,editTextDesce,editTextSobe));
        botaoLigaOlhoDigital.addActionListener(e -> alteraStatusOlhoDigital(botaoLigaOlhoDigital));
        botaoModoCaptura.addActionListener(e -> alteraModoDeCaptura(botaoModoCaptura));
        botaoLimpaCapturas.addActionListener(e -> limpaListaDeImagens());

        botaoGravaPeriodo.addActionListener(e -> {
            try {
                criaListaDataHora();

                JOptionPane.showConfirmDialog(null,
                        "Lista de datas e pastas criadas com sucesso !", "Informativo", JOptionPane.DEFAULT_OPTION);

            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        });

        botaoPercorrePosicao.addActionListener(e-> {
            try {

                percorrer(Integer.parseInt(editTextDesce.getText()),Integer.parseInt(editTextSobe.getText()));

                JOptionPane.showConfirmDialog(null,
                        "Percorrer finalizado", "Informativo", JOptionPane.DEFAULT_OPTION);

            } catch (AWTException ex) {
                ex.printStackTrace();
            }
        });

        listaCameraDeCaptura.addListSelectionListener(e -> selecionaCamera(listaCameraDeCaptura));

        botaoPrintManual.addActionListener(e -> fazPrintManual());

        ShapeDetectionUtil.createJFrame(
                botaoLigaOlhoDigital,
                botaoResetRotina,
                botaoLimpaCapturas,
                listaCameraDeCaptura,
                telaWebCam,
                editTextDesce,
                editTextSobe,
                botaoPercorrePosicao,
                botaoModoCaptura,
                botaoDeCapturaPdr,
                editDataInicial,
                editDataFinal,
                botaoGravaPeriodo,
                editHoraExecucao,botaoPrintManual, botaoDeCapturaPi);

        ShapeDetection.startShapeDetection(
                telaWebCam,
                telaImagemProcessada,
                webCam).run();

    }

    private static Runnable startShapeDetection(final JPanel cameraFeed,
                                                final JPanel processedFeed,
                                                final VideoCapture camera) {

        return () -> {

            final Mat frame = new Mat();

            while (chaveCoracao) {
                // Read frame from camera

                camera.read(frame);

                System.out.println("Fila => " + fila);

                // Process frame
                final Mat processed = ShapeDetectionUtil.processImage(frame);

                // Mark outer contour
                //ShapeDetectionUtil.markOuterContour(processed, frame);

                BufferedImage image_atual = ShapeDetectionUtil.convertMatToBufferedImage(processed);

                quadro = processed;

                try {

                    if(chaveOlhoDigital == true){

                        verificaPastaAtual();

                        if(!lista_de_datas_up.isEmpty()){

//                                System.out.println(" TOTAL DE REGISTROS " + lista_de_datas_up.size());

                        }


                        if(chaveMonitorar == true){

                            if(verficicaDataAtualLista() || ((lista_de_prints.stream()
                                    .filter(item-> item.getStatusPrint() == false).count()) > 0)){

                                fila = 1;


                                System.out.println("Total de prints não executados => " + verficicaDataAtualLista() + " | "+
                                        + lista_de_prints.stream().filter(item-> item.getStatusPrint() == false).count());


                                processoData = 0;

                                if ((processoData == 0) || (processoData == 2)){

                                    fila = 2;

//                                            System.out.println("ENTROU NO OU PELO MENOS");

                                    processoData = 0;

                                    if(lista_de_prints.stream().filter(item-> item.getPrintModel() == true).count() > 0){

                                        processaImagemAtual(image_atual);
//                                                System.out.println("######### PROCESSANDO IMAGEM ATUAL ##########");

                                    }else{

                                        processaPrint();

                                    }




                                }else{

                                    System.out.println("DEU RUIM");

                                }

                            }else{

                                if(statusFila == true){

                                    fila = 3;
                                    atualizaDataAtualLista();
//                                            System.out.println("############### ENTROU NO PRINT E ESTA AGUARDANDO ############");

                                }


                                atualiazador++;

                                if(atualiazador == 300){

                                    RobotUtil.Execute_F5(1,1);

                                    atualiazador = 0;

                                    System.out.println("INTERVALO DE ATUALIZACAO => " +atualiazador);

                                }
                                else{

                                    System.out.println("ELSE INTERVALO DE ATUALIZACAO => " +atualiazador);
                                }


                                processoData = 1;


                            }
  /*
                                }else{

                                    System.out.println("DATA DIFERENTE DAS PROGRAMADAS");

                                }

                                */

                        }else{

                        }

                    }else{

                    }

                    if(fila == 2){

                        statusFila = true;

                    }


                } catch (AWTException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Draw current frame
                ShapeDetectionUtil.drawImage(processed, cameraFeed);

            }

        };

    }

    // EXECUTA O MONITORAMENTO DO PRINT

    private static void processaImagemAtual(BufferedImage imagem_atual) throws AWTException, IOException, InterruptedException {
        try{

            PrintGO primeiroRegistro = lista_de_prints.stream().filter(item -> item.getStatusPrint() == false && item.getPrint() != null && item.getPrintModel() == true).findFirst().get();

            camera_atual = primeiroRegistro.getLocalMidia();



            if(comparando == false){

                RobotUtil.Switch_Camera(camera_atual);

                comparando = true;

                primeiroRegistro.getListaDeComandos().stream()
                        .forEach(
                                item -> {
                                    try {

                                        RobotUtil.Execute_Aguardar(1,1);

                                        System.out.println(
                                                item.getTp_comando() + " | "
                                                        + item.getTexto() + " | "
                                                        + item.getQtde_repeticao() + " | "
                                                        + item.getSegundos_aguardando()
                                        );

                                        RobotUtil.Executa_Comandos(
                                                item.getTp_comando(),
                                                item.getTexto(),
                                                item.getQtde_repeticao(),
                                                item.getSegundos_aguardando());

                                    } catch (AWTException e) {

                                        e.printStackTrace();

                                    }
                                });

            }



            if(comparaImagenCapturadaArmazenada(primeiroRegistro.getPrint(),imagem_atual)){

                RobotUtil.Execute_Aguardar(5,1);
                RobotUtil.Faz_Print(pasta_atual);
                lista_de_prints.stream().filter(
                                item -> item.getStatusPrint() == false &&
                                        item.getPrint() != null)
                        .findFirst().get()
                        .setStatusPrint(true);
                chaveExecutaPrint = true;

                comparando = false;

                RobotUtil.Retorna_Topo();

            }

        }catch (Exception e){

        }

    }

    // INICIO - EXECUÇÃO DOS COMANDOS

    private static void processaPrint() throws AWTException, IOException, InterruptedException {


        if(!lista_de_prints.isEmpty()){

            lista_de_prints.stream()
                    .collect(Collectors.toList())
                    .forEach(
                            item -> {

                                if(item.getStatusPrint() == false){

                                    if(item.getPrint() == null){

                                        System.out.println("VALOR DA IMAGEM => " + item.getPrint());

                                        if (!item.getListaDeComandos().isEmpty()
                                                || item.getListaDeComandos() != null) {

                                            RobotUtil.Execute_Aguardar(1, 1);

                                            item.getListaDeComandos().forEach(comand -> {

                                                try {
                                                    RobotUtil.Executa_Comandos(comand.getTp_comando(),comand.getTexto(),comand.getQtde_repeticao(),comand.getSegundos_aguardando());

                                                } catch (AWTException e) {
                                                    System.out.println("" + e);
                                                }


                                            });
                                            RobotUtil.Execute_Aguardar(1, 1);
                                            RobotUtil.Faz_Print(pasta_atual);
                                            item.setStatusPrint(true);
                                            System.out.println("FEZ O COMANDO DE PRINT PADRAO");
                                            RobotUtil.Execute_Aguardar(2, 1);
                                            RobotUtil.Retorna_Topo();


                                        } else {
                                            System.out.println("COMANDOS NULOS");
                                        }

                                    }

                                }




                            });

        }

    }

    static boolean comparaImagenCapturadaArmazenada(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
            for (int x = 0; x < img1.getWidth(); x++) {
                for (int y = 0; y < img1.getHeight(); y++) {
                    if (img1.getRGB(x, y) != img2.getRGB(x, y))
                        return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    // FINAL - EXECUÇÃO DOS COMANDOS

    private static void alteraStatusOlhoDigital(JButton buttonstart){

        if(chaveOlhoDigital == true){

            chaveOlhoDigital = false;
            buttonstart.setText("MODO: CADASTRAR");
            System.out.println("MODO: CADASTRAR | chavOlhoDigital => " + chaveOlhoDigital);

        }

        else{

            chaveOlhoDigital = true;
            buttonstart.setText("MODO: EXECUTAR");
            System.out.println("MODO: EXECUTAR | chavOlhoDigital => " + chaveOlhoDigital);

        }

    }

    private static void alteraModoDeCaptura(JButton buttonstart){

        if(chaveMonitorar == true){

            chaveMonitorar = false;
            buttonstart.setText("MODO: PADRAO");
            System.out.println("MODO: PADRAO | chaveMonitorar => " + chaveMonitorar);

        }

        else{

            chaveMonitorar = true;
            buttonstart.setText("MODO: OLHO VIRTUAL");
            System.out.println("MODO: OLHO VIRTUAL | chaveMonitorar => " + chaveMonitorar);

        }

    }

    private static void selecionaTipoDeCamera(JList lista_de_midia){

        midia_selecionada = (int) lista_de_midia.getSelectedValue();

        RobotUtil.Switch_Camera((int) lista_de_midia.getSelectedValue());

        System.out.println((int) lista_de_midia.getSelectedValue());

    }

    private static void selecionaCamera(JList lista_de_midia){

        camera_atual = (int) lista_de_midia.getSelectedValue();
        RobotUtil.Switch_Camera((int) lista_de_midia.getSelectedValue());
        System.out.println((int) lista_de_midia.getSelectedValue());
    }

    private static void limpaListaDeImagens(){

        lista_de_imagens = new ArrayList<>();
        lista_de_prints = new ArrayList<>();
        t1 = true;
        t2 = true;

        JOptionPane.showConfirmDialog(null,
                "Comandos removidos.", "Informativo", JOptionPane.DEFAULT_OPTION);

    }

    private static void percorrer(int desce, int sobe) throws AWTException {

        System.out.println("Desce: " + desce + " | Sobe: " + sobe);

        RobotUtil.Executa_Comandos(6,"",5,1);
        RobotUtil.Executa_Comandos(14,"",desce,0);
        RobotUtil.Executa_Comandos(13,"",(sobe - sobe)+3,1);
        RobotUtil.Executa_Comandos(13,"",sobe-3,0);

    }

    private static void resetRotina(){

        // chave = false;

        lista_de_prints.stream().forEach(item -> item.setStatusPrint(false));

/*
        JOptionPane.showConfirmDialog(null,
                "Comandos reiniciados.", "Informativo", JOptionPane.DEFAULT_OPTION);
*/

    }

    private static void adcionaPrintImagem(JPanel cameraFeed, Mat frame, JButton bt, JTextField desce, JTextField sobe ){

        // chave = false;


        ArrayList<Comandos> listaDeComandos = new ArrayList<>();


        System.out.println("MODO MONITORAMENTO : DESATIVADO");

        int valorDesce = 0;
        int valorSobe = 0;

        if (desce.getText().isEmpty() || sobe.getText().isEmpty()) {

            System.out.println("VALORES VAZIOS");

            valorDesce = 0;
            valorSobe = 0;

        } else {

            valorDesce = Integer.parseInt(desce.getText());
            valorSobe = Integer.parseInt(sobe.getText());

            System.out.println("VALORES => " + valorDesce + " | " + valorSobe);

        }

        Comandos comandoDescer = new Comandos(14, "", valorDesce, 0);
        Comandos comandoAguardar = new Comandos(6, "", 5, 0);
        Comandos comandoSobeDevagar = new Comandos(13, "", (valorSobe - valorSobe) + 3, 1);
        Comandos comandoSobe = new Comandos(13, "", valorSobe - 3, 0);

        listaDeComandos.add(comandoDescer);
        listaDeComandos.add(comandoAguardar);
        listaDeComandos.add(comandoSobeDevagar);
        listaDeComandos.add(comandoSobe);


        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        BufferedImage image = ShapeDetectionUtil.convertMatToBufferedImage(frame);

        lista_de_imagens.add(image);

        lista_de_prints.add(new PrintGO(image,false,camera_atual,listaDeComandos,true));

        botaoDeCapturaPi.setText("CI = " + lista_de_imagens.size());

        try {
            BufferedImage imagem = image;
            ImageIO.write(
                    imagem,
                    "JPG",
                    new File(
                            enderecoLocal + timeStamp + ".jpg"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void adicionaPrintPadrao(JButton bt, JTextField desce, JTextField sobe ){

        ArrayList<Comandos> listaDeComandos = new ArrayList<>();

        System.out.println("MODO MONITORAMENTO : DESATIVADO");

        System.out.println("MODO OV : DESATIVADO");

        int valorDesce = 0;
        int valorSobe = 0;

        if (desce.getText().isEmpty() || sobe.getText().isEmpty()) {

            System.out.println("VALORES VAZIOS");

            valorDesce = 0;
            valorSobe = 0;

        } else {

            valorDesce = Integer.parseInt(desce.getText());
            valorSobe = Integer.parseInt(sobe.getText());

            System.out.println("VALORES => " + valorDesce + " | " + valorSobe);

        }

        try{

            Comandos comandoDescer = new Comandos(14, "", valorDesce, 0);
            Comandos comandoAguardar = new Comandos(6, "", 5, 0);
            Comandos comandoSobeDevagar = new Comandos(13, "", (valorSobe - valorSobe) + 3, 1);
            Comandos comandoSobe = new Comandos(13, "", valorSobe - 3, 0);

            listaDeComandos.add(comandoDescer);
            listaDeComandos.add(comandoAguardar);
            listaDeComandos.add(comandoSobeDevagar);
            listaDeComandos.add(comandoSobe);

            lista_de_prints.add(new PrintGO(null, false, camera_atual, listaDeComandos, false));

        }catch (Exception e){

            System.out.println("ERRO => " + e);

        }

        bt.setText("CP = " + lista_de_prints.size());

    }

    private static void gestorDeMovivmento(int midia_selecionada){

        if(camera_atual != midia_selecionada){

            if(midia_selecionada == 1){
                camera_atual = midia_selecionada;

                ArrayList<Comandos> nova_lista = new ArrayList<>();
                Comandos novo_comando_1 = new Comandos(17,"",1,1);
                nova_lista.add(novo_comando_1);
                RobotUtil.Tradutor_Comandos(nova_lista);


            }else if(midia_selecionada == 2){

                camera_atual = midia_selecionada;
                ArrayList<Comandos> nova_lista = new ArrayList<>();
                Comandos novo_comando_2 = new Comandos(17,"",1,3);
                Comandos novo_comando_1 = new Comandos(14,"",56,0);
                Comandos novo_comando_3 = new Comandos(13,"",5,1);
                nova_lista.add(novo_comando_1);
                nova_lista.add(novo_comando_2);
                nova_lista.add(novo_comando_3);
                RobotUtil.Tradutor_Comandos(nova_lista);
            }

            else if(midia_selecionada == 3){
                camera_atual = midia_selecionada;

                ArrayList<Comandos> nova_lista = new ArrayList<>();
                Comandos novo_comando_2 = new Comandos(17,"",1,3);
                Comandos novo_comando_1 = new Comandos(14,"",56,0);
                Comandos novo_comando_3 = new Comandos(13,"",5,1);
                nova_lista.add(novo_comando_1);
                nova_lista.add(novo_comando_2);
                nova_lista.add(novo_comando_3);
                RobotUtil.Tradutor_Comandos(nova_lista);

            }

            else if(midia_selecionada == 4){
                camera_atual = midia_selecionada;

                ArrayList<Comandos> nova_lista = new ArrayList<>();
                Comandos novo_comando_2 = new Comandos(17,"",1,3);
                Comandos novo_comando_1 = new Comandos(14,"",56,0);
                Comandos novo_comando_3 = new Comandos(13,"",5,1);
                nova_lista.add(novo_comando_1);
                nova_lista.add(novo_comando_2);
                nova_lista.add(novo_comando_3);
                RobotUtil.Tradutor_Comandos(nova_lista);

            }


        }else{}


    }




    private static void criaListaDataHora() throws ParseException {

        DateFormat df = new SimpleDateFormat ("dd/MM/yyyy");

        SimpleDateFormat formatterAno = new SimpleDateFormat("yyyy");
        SimpleDateFormat formatterMes = new SimpleDateFormat("MM");
        SimpleDateFormat formatterDia = new SimpleDateFormat("dd");


        Date dt1 = df.parse (editDataInicial.getText());
        Date dt2 = df.parse (editDataFinal.getText());
        List<LocalDateTime> lista_de_datas = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime (dt1);

        String resultado = editHoraExecucao.getText();

        String hora = resultado.substring(0,2);
        String minuto = resultado.substring(3,5);


        JOptionPane.showConfirmDialog(null,
                "HORA: " + hora + " | MINUTO: " + minuto , "Informativo", JOptionPane.DEFAULT_OPTION);


        geraListaDeDatas(dt1,dt2).forEach(item -> {



            Calendar dataTrabalhada = Calendar.getInstance();

            dataTrabalhada.set(Integer.valueOf(formatterAno.format(item)),Integer.valueOf(formatterMes.format(item)),Integer.valueOf(formatterDia.format(item)),Integer.parseInt(hora),Integer.parseInt(minuto),10);


            String nome_pasta = cal.get(Calendar.DAY_OF_MONTH) + "_" + (cal.get(Calendar.MONTH)+1) + "_" + cal.get(Calendar.YEAR);


            dataManipulation novoRegistroData = new dataManipulation(dataTrabalhada.getTime(),false,nome_pasta);

            lista_de_datas_up.add(novoRegistroData);

            datas_de_acao_cal.put(dataTrabalhada.getTime(),false);

            System.out.println(Integer.valueOf(formatterAno.format(item))+ " | " + Integer.valueOf(formatterMes.format(item))+ " | " + Integer.valueOf(formatterDia.format(item)));


        });


/*
        for (Date dt = dt1; dt.compareTo (dt2) <= 0; ) {

//            System.out.println (df.format (dt) + " | " + dt.getDate() + " | " + (dt.getMonth() + 1) + " | " + 2023 + " | "  );

//            datas_de_acao.put(LocalDateTime.of(2023,(dt.getMonth() + 1),dt.getDate(),18,0,0).format(data_formato),false);

            Calendar dataTrabalhada = Calendar.getInstance();

            dataTrabalhada.set(2023,(dt.getMonth() + 1),dt.getDate(),Integer.parseInt(hora),Integer.parseInt(minuto),10);

            String nome_pasta = cal.get(Calendar.DAY_OF_MONTH) + "_" + (cal.get(Calendar.MONTH)+1) + "_" + cal.get(Calendar.YEAR);

            cal.add (Calendar.DATE, +1);

            dt = cal.getTime();

            dataManipulation novoRegistroData = new dataManipulation(dataTrabalhada.getTime(),false,nome_pasta);

            lista_de_datas_up.add(novoRegistroData);

            datas_de_acao_cal.put(dataTrabalhada.getTime(),false);

        }
*/
        datas_de_acao_cal.keySet().stream().forEach(key -> {

            Calendar c = Calendar.getInstance();

            c.setTime(key);

            System.out.println(" Dia : " + c.get(Calendar.DAY_OF_MONTH) +
                    " | Mês : " + c.get(Calendar.MONTH) +
                    " | Ano : " + c.get(Calendar.YEAR) +
                    " | Hora : " + c.get(Calendar.HOUR) +
                    " | Minuto : " + c.get(Calendar.MINUTE) +
                    " | Segundos : " + c.get(Calendar.SECOND) +
                    " | Status : " + datas_de_acao_cal.get(key));

            String nome_pasta = c.get(Calendar.DAY_OF_MONTH) + "_" + c.get(Calendar.MONTH) + "_" + c.get(Calendar.YEAR);

            File directory = new File(enderecoLocal + nome_pasta);
            if (!directory.exists()){
                directory.mkdir();
            }

        } );

    }

    private static boolean verficicaDataAtualLista(){

        /*
         *  PEGAR DATA ATUAL - ok
         *  VERIFICA SE A DATA ATUAL ESTA DENTRO DA LISTA DE DATAS
         *  VERIFICA SE OS PRINTS JA FORAM EXECUTADOS NA DATA ATUAL
         */

        Calendar c = Calendar.getInstance();
        Date dataAtual = c.getTime();

        try {

            lista_de_datas_up.stream().forEach(item->{


                String atualD = c.get(Calendar.DAY_OF_MONTH) + "_" + (c.get(Calendar.MONTH)+1) + "_" + c.get(Calendar.YEAR)
                        + "_" + c.get(Calendar.HOUR) + "_"
                        + c.get(Calendar.MINUTE) + "_"
                        + c.get(Calendar.SECOND);

                Calendar nova = Calendar.getInstance();
                nova.setTime(new Date(item.getDataProgramada().getTime()));

                String atualI = nova.get(Calendar.DAY_OF_MONTH) + "_" + (nova.get(Calendar.MONTH)) + "_" + nova.get(Calendar.YEAR)
                        + "_" + nova.get(Calendar.HOUR) + "_"
                        + nova.get(Calendar.MINUTE) + "_"
                        + nova.get(Calendar.SECOND);


                System.out.println(" #### DATA ##### " + atualI.trim() + " | " + atualD.trim() + " | " + item.isStatus());

                dataEncontrada = false;

                if (atualI.trim().equals(atualD.trim()) && item.isStatus() == true){

                    resetRotina();

                    item.setStatus(false);

                    dataEncontrada = true;


                }

            });

        }catch (Exception e){

            System.out.println("ESTA DANDO ERRO EM $$$$$$$$$$$$$$$$$$" +  e);

        }

/*        boolean dataEncontrada = datas_de_acao_cal.keySet().stream().anyMatch(item -> dataAtual.equals(new Date(item.getTime())) && datas_de_acao_cal.get(item) == false);


        if(dataEncontrada == true){

            System.out.println("$$$$$$$$$$$$$$$$$$$$ DATA ENCONTRADA $$$$$$$$$$$$$$$$$$");

            resetRotina();
            resetaDataAtualLista(dataAtual);

        }
*/

        return dataEncontrada;

    }

    private static void verificaPastaAtual(){

        Calendar c = Calendar.getInstance();

        String nome_pasta = c.get(Calendar.DAY_OF_MONTH) + "_" + (c.get(Calendar.MONTH)+1) + "_" + c.get(Calendar.YEAR);

        File directory = new File(enderecoLocal+nome_pasta);
//        File directory = new File(enderecoNuvem+nome_pasta);
        if (!directory.exists()){

//            System.out.println("Não tem o nome " + nome_pasta);

        }else{

            if(!pasta_atual.trim().equals(nome_pasta.trim())){

                RobotUtil.Execute_F5(3,1);
                RobotUtil.Execute_Aguardar(5,1);

                pasta_atual = nome_pasta.trim();

            }else{

//                System.out.println(pasta_atual);

            }



        }



    }


    private static boolean atualizaDataAtualLista(){

//        System.out.println("$$$$$$$$$$$$$$$$$$$$ TESTANDO PELO MENOS $$$$$$$$$$$$$$$$$$");

        /*
         *  PEGAR DATA ATUAL - ok
         *  VERIFICA SE A DATA ATUAL ESTA DENTRO DA LISTA DE DATAS
         *  VERIFICA SE OS PRINTS JA FORAM EXECUTADOS NA DATA ATUAL
         */

        Calendar c = Calendar.getInstance();
        Date dataAtual = c.getTime();

        try {


            lista_de_datas_up.stream().forEach(item->{


                String atualD = c.get(Calendar.DAY_OF_MONTH) + "_" + (c.get(Calendar.MONTH)+1) + "_" + c.get(Calendar.YEAR)
                        + "_" + c.get(Calendar.HOUR) + "_"
                        + c.get(Calendar.MINUTE) + "_"
                        + c.get(Calendar.SECOND);

                Calendar nova = Calendar.getInstance();
                nova.setTime(new Date(item.getDataProgramada().getTime()));

                String atualI = nova.get(Calendar.DAY_OF_MONTH) + "_" + (nova.get(Calendar.MONTH)) + "_" + nova.get(Calendar.YEAR)
                        + "_" + nova.get(Calendar.HOUR) + "_"
                        + nova.get(Calendar.MINUTE) + "_"
                        + nova.get(Calendar.SECOND);


//                System.out.println(atualI.trim() + " | " + atualD.trim() + " | " + item.isStatus());



                if (atualI.trim().equals(atualD.trim())){

                    System.out.println("%%%%%%%% DATA ENCONTRADA %%%%%%%%");

                    item.setStatus(true);

                }

            });

        }catch (Exception e){

            System.out.println("ESTA DANDO ERRO EM $$$$$$$$$$$$$$$$$$" +  e);

        }

/*        boolean dataEncontrada = datas_de_acao_cal.keySet().stream().anyMatch(item -> dataAtual.equals(new Date(item.getTime())) && datas_de_acao_cal.get(item) == false);


        if(dataEncontrada == true){

            System.out.println("$$$$$$$$$$$$$$$$$$$$ DATA ENCONTRADA $$$$$$$$$$$$$$$$$$");

            resetRotina();
            resetaDataAtualLista(dataAtual);

        }
*/

        return dataEncontrada;

    }

    private static void resetaDataAtualLista(Date dataAtual) {

/*
         *  PEGAR DATA ATUAL - ok
         *  VERIFICA SE A DATA ATUAL ESTA DENTRO DA LISTA DE DATAS
         *  VERIFICA SE OS PRINTS JA FORAM EXECUTADOS NA DATA ATUAL
         */

/*

        datas_de_acao_cal.keySet().stream().forEach(item -> {

            if (dataAtual.getDate() == item.getDate()) {

                datas_de_acao_cal.put(item, false);

            }

        });

*/

        lista_de_datas_up.stream().forEach(item-> {


            if(item.getDataProgramada().getTime() == dataAtual.getTime()){

                item.setStatus(false);

                resetRotina();


            }

        });


    }

    private static void fazPrintManual(){

        RobotUtil.Execute_Aguardar(5,1);
        RobotUtil.Faz_Print(pasta_atual);

    }

    public static List<Date> geraListaDeDatas(Date startDate, Date endDate){
        ArrayList<Date> dates = new ArrayList<Date>();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(startDate);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(endDate);

        while(cal1.before(cal2) || cal1.equals(cal2))
        {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }

        return dates;
    }




    /*

    private static boolean atualizaDataAtualLista(){

        */
    /*
     *  PEGAR DATA ATUAL - ok
     *  VERIFICA SE A DATA ATUAL ESTA DENTRO DA LISTA DE DATAS
     *  VERIFICA SE OS PRINTS JA FORAM EXECUTADOS NA DATA ATUAL
     */
/*


        Calendar c = Calendar.getInstance();
        Date dataAtual = c.getTime();
        boolean dataEncontrada_local = false;

        datas_de_acao_cal.keySet().stream().forEach(item ->{

            if(dataAtual.getDate() == item.getDate()){

                datas_de_acao_cal.replace(item,true);

            }

        });

        if(dataEncontrada_local == true){
            System.out.println("========================== DATA IGUAL ENCONTRADA ==========================");
        }


        return dataEncontrada_local;

    }

*/




}