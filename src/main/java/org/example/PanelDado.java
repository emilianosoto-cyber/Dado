package org.example;

import javax.swing.*;
import java.awt.*;

// Componente que dibuja el dado de una estación (valor 1–6)
public class PanelDado extends JPanel {

    private int valor;


    // Constructor
    public PanelDado() {
        this.valor = 0;
        setPreferredSize(new Dimension(36, 36));
        setOpaque(false);
    }


    // Actualiza el valor y redibuja
    public void setValor(int valor) {
        this.valor = valor;
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo rojo del dado
        g2.setColor(new Color(200, 30, 30));
        g2.fillRoundRect(1, 1, 34, 34, 8, 8);

        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(1, 1, 34, 34, 8, 8);

        // Puntos blancos según el valor
        if (valor >= 1 && valor <= 6) {
            g2.setColor(Color.WHITE);

            for (int[] p : getPosiciones(valor)) {
                g2.fillOval(p[0] - 3, p[1] - 3, 6, 6);
            }
        }
    }


    // Coordenadas de los puntos según el valor del dado
    private int[][] getPosiciones(int valor) {
        int L = 10, C = 18, R = 26; // columnas: izquierda, centro, derecha
        int T = 10, M = 18, B = 26; // filas: arriba, medio, abajo

        switch (valor) {
            case 1:
                return new int[][]{{C, M}};
            case 2:
                return new int[][]{{L, T}, {R, B}};
            case 3:
                return new int[][]{{L, T}, {C, M}, {R, B}};
            case 4:
                return new int[][]{{L, T}, {R, T}, {L, B}, {R, B}};
            case 5:
                return new int[][]{{L, T}, {R, T}, {C, M}, {L, B}, {R, B}};
            case 6:
                return new int[][]{{L, T}, {R, T}, {L, M}, {R, M}, {L, B}, {R, B}};
            default:
                return new int[][]{};
        }
    }

}