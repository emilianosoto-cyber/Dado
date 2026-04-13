package org.example;

import javax.swing.*;
import java.awt.*;

public class PanelEstacion extends JPanel{
    // Lista de imágenes para ir rotando trabajadores
    private static final String[] IMG_NOMBRES={"estacion1","estacion2","estacion3","estacion4","estacion5","estacion6","estacion2","estacion1","estacion3","estacion4"};
    // Contador para escoger la imagen según el orden de creación
    private static int contador=0;
    // Imagen del trabajador
    private java.awt.image.BufferedImage imgTrabajador;

    // Tamaño fijo del panel
    public static final int PANEL_W=150,PANEL_H=180;

    // Tamaño y posición del dado
    private static final int DADO_W=36,DADO_H=36,DADO_X=PANEL_W-DADO_W-4,DADO_Y=4;

    // Tamaño y posición del trabajador
    private static final int TRAB_W=70,TRAB_H=70,TRAB_X=PANEL_W-TRAB_W-2,TRAB_Y=DADO_Y+DADO_H+4;

    // Panel que dibuja el dado
    private final PanelDado panelDado;

    public PanelEstacion(String nombre){
        // Usamos layout absoluto para colocar dado e imagen
        setLayout(null);
        setOpaque(false);
        setPreferredSize(new Dimension(PANEL_W,PANEL_H));

        // Escoge imagen del trabajador
        String imgNombre=IMG_NOMBRES[contador%IMG_NOMBRES.length];
        contador++;

        // Carga la imagen desde resources
        try{imgTrabajador=javax.imageio.ImageIO.read(java.util.Objects.requireNonNull(getClass().getResourceAsStream("/images/"+imgNombre+".png")));}catch(Exception e){imgTrabajador=null;}

        // Crea el dado y lo coloca
        panelDado=new PanelDado();
        panelDado.setBounds(DADO_X,DADO_Y,DADO_W,DADO_H);
        add(panelDado);

        // El nombre de la estación lo dibuja el overlay, no este panel
    }

    // Actualiza el valor del dado
    public void setValorDado(int dado){
        panelDado.setValor(dado);repaint();
    }

    @Override protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g;
        // Suaviza bordes y escalado
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // Dibuja el trabajador
        if(imgTrabajador!=null) g2.drawImage(imgTrabajador,TRAB_X,TRAB_Y,TRAB_W,TRAB_H,this);
    }

    // Reinicia el contador para que las imágenes se asignen en orden al reiniciar el tablero
    public static void reiniciarContador(){
        contador=0;
    }
}