package org.example;

import javax.swing.*;
import java.awt.*;

public class PanelAlmacen extends JPanel{
    // Cuántos terminados hay
    private int cantidad=0;

    // Tamaño de cada punto y su separación
    private static final int DIAM=9,SEP_PIEZA=2,POR_FILA=5;

    // Tamaño fijo del panel
    public static final int PANEL_W=150,PANEL_H=180;

    public PanelAlmacen(){
        setLayout(null);
        setOpaque(false);
        setPreferredSize(new Dimension(PANEL_W,PANEL_H));

        // Etiqueta fija "ALMACÉN"
        JLabel etq=new JLabel("ALMACÉN",SwingConstants.LEFT);
        etq.setFont(new Font("Arial",Font.BOLD,10));
        etq.setForeground(new Color(80,40,35));
        etq.setBounds(4,PANEL_H-16,90,14);
        add(etq);
    }

    // Actualiza el total y repinta
    public void actualizar(int terminados){cantidad=terminados;repaint();}

    @Override protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g;
        // Suaviza círculos
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        int piezasX=6,piezasY=6;
        // Deja espacio abajo para la etiqueta
        int alturaDisponible=PANEL_H-piezasY-20;
        int filasMax=Math.max(1,alturaDisponible/(DIAM+SEP_PIEZA));
        int maxVisible=POR_FILA*filasMax;
        int visibles=Math.min(cantidad,maxVisible);

        // Dibuja los puntos visibles
        for(int i=0;i<visibles;i++){
            int col=i%POR_FILA,fila=i/POR_FILA;
            int x=piezasX+col*(DIAM+SEP_PIEZA);
            int y=piezasY+fila*(DIAM+SEP_PIEZA);
            g2.setColor(new Color(120,120,120));
            g2.fillOval(x,y,DIAM,DIAM);
            g2.setColor(new Color(80,80,80));
            g2.drawOval(x,y,DIAM,DIAM);
        }

        // Si hay más de las que caben, muestra +n
        if(cantidad>maxVisible){
            g2.setColor(new Color(80,40,35));
            g2.setFont(new Font("Arial",Font.BOLD,8));
            g2.drawString("+"+(cantidad-maxVisible),piezasX,piezasY+filasMax*(DIAM+SEP_PIEZA)+9);
        }
    }
}