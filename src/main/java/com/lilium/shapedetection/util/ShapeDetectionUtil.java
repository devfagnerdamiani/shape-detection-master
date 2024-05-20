package com.lilium.shapedetection.util;


import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Util class for shape detection.
 *
 * @author mirza
 */

public final class ShapeDetectionUtil {
    private static BufferedImage image_capture;
    private static BufferedImage image_selecionada;
    private static Mat mat_;
    private static JPanel panel_1;
    private static JPanel panel_2;
    private static int k;

    // region Constructor

    private ShapeDetectionUtil() {
        // Empty by default
    }
    // endregion

    // region OpenCV
    /**
     * Used to process forwarded {@link Mat} image and return the result.
     *
     * @param mat Image to process.
     * @return Returns processed image.
     */
    public static Mat processImage(final Mat mat) {
        final Mat processed = new Mat(mat.height(), mat.width(), mat.type());
        // Blur an image using a Gaussian filter
        Imgproc.GaussianBlur(mat, processed, new Size(7, 7), 1);

        // Switch from RGB to GRAY
        Imgproc.cvtColor(processed, processed, Imgproc.COLOR_RGB2GRAY);

        // Find edges in an image using the Canny algorithm
        Imgproc.Canny(processed, processed, 200, 25);

        // Dilate an image by using a specific structuring element
        // https://en.wikipedia.org/wiki/Dilation_(morphology)
        Imgproc.dilate(processed, processed, new Mat(), new Point(-1, -1), 1);

        return processed;
    }

    /**
     * Used to mark outer rectangle and its corners.
     *
     * @param processedImage Image used for calculation of contours and corners.
     * @param originalImage Image on which marking is done.
     */
    public static void markOuterContour(final Mat processedImage,
                                        final Mat originalImage) {
        // Find contours of an image
        final List<MatOfPoint> allContours = new ArrayList<>();
        Imgproc.findContours(
                processedImage,
                allContours,
                new Mat(processedImage.size(), processedImage.type()),
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_NONE
        );

        // Filter out noise and display contour area value
        final List<MatOfPoint> filteredContours = allContours.stream()
                .filter(contour -> {
                    final double value = Imgproc.contourArea(contour);
                    final Rect rect = Imgproc.boundingRect(contour);

                    final boolean isNotNoise = value > 1000;

                    if (isNotNoise) {
                        Imgproc.putText (
                                originalImage,
                                "Area: " + (int) value,
                                new Point(rect.x + rect.width, rect.y + rect.height),
                                2,
                                0.5,
                                new Scalar(124, 252, 0),
                                1
                        );

                        MatOfPoint2f dst = new MatOfPoint2f();
                        contour.convertTo(dst, CvType.CV_32F);
                        Imgproc.approxPolyDP(dst, dst, 0.02 * Imgproc.arcLength(dst
                                , true), true);
                        Imgproc.putText (
                                originalImage,
                                "Points: " + dst.toArray().length,
                                new Point(rect.x + rect.width, rect.y + rect.height + 15),
                                2,
                                0.5,
                                new Scalar(124, 252, 0),
                                1
                        );
                    }

                    return isNotNoise;
                }).collect(Collectors.toList());

        // Mark contours
        Imgproc.drawContours(
                originalImage,
                filteredContours,
                -1, // Negative value indicates that we want to draw all of contours
                new Scalar(124, 252, 0), // Green color
                1
        );
    }
    // endregion

    // region UI
    public static void createJFrame(JButton btnAtivarOlhoVirtual,
                                    JButton btnCapturar,
                                    JButton btnLimpar,
                                    JList lista_de_itens ,
                                    final JPanel panel,
                                    JFormattedTextField desce,
                                    JFormattedTextField sobe,
                                    JButton btnPosicionar,
                                    JButton btnModoCaptura,
                                    JButton btnCapturarPdr,
                                    JFormattedTextField dataInicial,
                                    JFormattedTextField dataFinal,
                                    JButton botaoGravaPeriodo,
                                    JFormattedTextField editHora, JButton botaoPrintManual, JButton botaoDeCapturaPi) throws ParseException {




/*
        final JPanel principal = new JPanel();

        principal.setLayout(new BorderLayout());
        principal.add(panel);
        principal.add(btnAtivarOlhoVirtual);
        principal.add(btnLimpar);

        principal.add(lista_de_itens);
        principal.add(btnCapturar);
        principal.add(sobe);
        principal.add(desce);
        principal.add(btnPosicionar);

        principal.setVisible(true);
 */


// /*

        final JFrame window = new JFrame("BOT-360NEWS V_1.2");

        JLabel lbVizualizador = new JLabel("VIZUALIZADOR ");

        panel.setSize(50,50);

        GridLayout grade = new GridLayout(2,3);

//      GroupLayout grade = new GroupLayout();

        window.setSize(new Dimension(800, 600));
        window.setLocationRelativeTo(null);
        window.setResizable(true);

        window.setLayout(grade);

//      panel.setSize(400,400);

        window.add(panel);
        window.add(btnAtivarOlhoVirtual);
        window.add(btnModoCaptura);


        window.add(lista_de_itens);

        window.add(sobe);
        window.add(desce);
        window.add(btnPosicionar);
        window.add(botaoDeCapturaPi);
        window.add(btnCapturarPdr);


        window.add(dataInicial);
        window.add(dataFinal);
        window.add(editHora);
        window.add(botaoGravaPeriodo);
        window.add(btnLimpar);
        window.add(btnCapturar);
        window.add(botaoPrintManual);


// */
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



    }

    public static void drawImage(final Mat mat, final JPanel panel){
        // Get buffered image from mat frame
        final BufferedImage image = ShapeDetectionUtil.convertMatToBufferedImage(mat);

        image_capture = image;
        mat_ = mat;

        // Draw image to panel
        final Graphics graphics = panel.getGraphics();

        graphics.drawImage(image, 0, 0, panel);

//        try{
//
//            Tesseract t = new Tesseract();
//
//            String text_img = t.doOCR(image);
//
//            System.out.println(text_img);
//
//        }catch (Exception e){
//
//            System.out.println(" Erro => " + e);
//
//        }

    }

    public static BufferedImage retorna_imagem(){

        image_selecionada = image_capture;

          return image_selecionada;

    }

    public static void drawImage2(final Mat mat, final JPanel panel, BufferedImage retonada){
        // Get buffered image from mat frame
        final BufferedImage image = retonada;

        // Draw image to panel
        final Graphics graphics = panel.getGraphics();

        graphics.drawImage(image, 0, 0, panel);

//        try{
//
//            Tesseract t = new Tesseract();
//
//            String text_img = t.doOCR(image);
//
//            System.out.println(text_img);
//
//        }catch (Exception e){
//
//            System.out.println(" Erro => " + e);
//
//        }

    }

    // endregion

    // region Helpers
    /**
     * Converts forwarded {@link Mat} to {@link BufferedImage}.
     *
     * @param mat Mat to convert.
     * @return Returns converted BufferedImage.
     */
    public static BufferedImage convertMatToBufferedImage(final Mat mat) {
        // Create buffered image
        final BufferedImage bufferedImage = new BufferedImage(
                mat.width(),
                mat.height(),
                mat.channels() == 1 ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR
        );

        // Write data to image
        final WritableRaster raster = bufferedImage.getRaster();
        final DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        mat.get(0, 0, dataBuffer.getData());

        return bufferedImage;
    }
    // endregion
}