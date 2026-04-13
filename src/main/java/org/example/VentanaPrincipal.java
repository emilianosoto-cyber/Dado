package org.example;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class VentanaPrincipal extends JFrame{
    // Llama al motor del juego
    private LineaProduccion linea;

    // UI principal
    private PanelEstacion[] panelesEstacion;
    private PanelAlmacen panelAlmacen;
    private OverlayColas overlayColas;
    private JLayeredPane layered;

    // Etiquetas arriba
    private JLabel etqTurns,etqWIP,etqTerminados,etqThroughput;

    // Botones
    private JButton btnStart,btnRollMove;

    // Estado para alternar Tirar/Mover
    private enum EstadoRollMove{ESPERANDO,ROLL_HECHO}
    private EstadoRollMove estadoRollMove=EstadoRollMove.ESPERANDO;

    // Configuración del juego
    private static final int RONDAS=20;

    // Colores
    private static final Color PANEL_CTRL=new Color(80,40,35);
    private static final Color COLOR_ROLL=new Color(160,80,40);
    private static final Color COLOR_MOVE=new Color(40,110,50);
    private static final Color COLOR_START=new Color(190,175,155);

    // Dimensiones y posiciones base
    private static final int W=PanelEstacion.PANEL_W,H=PanelEstacion.PANEL_H;
    private static final int SEP=58,SEP_ALMACEN=0,MARGEN_IZQ=12,MARGEN_DER_COLAS=130;
    private static final int[] COLS=new int[5];

    // Fila superior e inferior
    private static final int Y_SUP=40,Y_INF=Y_SUP+H+60;

    // Almacén y tablero
    private static final int X_ALMACEN=MARGEN_IZQ;
    private static final int X0=X_ALMACEN+PanelAlmacen.PANEL_W+SEP_ALMACEN;
    private static final int TABLERO_W=(X0+(5*W+4*SEP)+SEP)+MARGEN_DER_COLAS;
    private static final int TABLERO_H=Y_INF+H+20;

    // Imágenes
    private BufferedImage imgFondo,imgFlecha;

    public VentanaPrincipal(){
        super("The Dice Game - Simulador");
        // Inicializa JavaFX para las gráficas
        new JFXPanel();
        Platform.setImplicitExit(false);

        // Calcula columnas del tablero
        for(int i=0;i<5;i++) COLS[i]=X0+i*(W+SEP);

        cargarImagenes();
        linea=new LineaProduccion();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(crearBarraControl(),BorderLayout.NORTH);
        add(crearTableroConOverlay(),BorderLayout.CENTER);

        setSize(TABLERO_W+18,TABLERO_H+110);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    // Carga fondo y flecha desde resources/images
    private void cargarImagenes(){
        imgFondo=cargarImg("fofo");
        imgFlecha=cargarImg("flecha");}

    private BufferedImage cargarImg(String nombre){
        try{
            return javax.imageio.ImageIO.read(java.util.Objects.requireNonNull(getClass().getResourceAsStream("/images/"+nombre+".png")));}catch(Exception e){return null;
        }
    }

    // Barra superior con botones y valores
    private JPanel crearBarraControl(){
        JPanel fila=new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));
        fila.setBackground(PANEL_CTRL);

        btnStart=crearBoton("Iniciar",COLOR_START,new Color(80,40,35),13);
        btnRollMove=crearBoton("Tirar",COLOR_ROLL,Color.WHITE,13);
        btnRollMove.setEnabled(false);

        btnStart.addActionListener(e->inicializar());
        btnRollMove.addActionListener(e->accionRollMove());

        etqTurns=new JLabel("Turno: 0");etqTurns.setForeground(Color.WHITE);
        etqWIP=new JLabel("WIP: 36");etqWIP.setForeground(new Color(255,200,100));
        etqTerminados=new JLabel("Terminados: 0");etqTerminados.setForeground(new Color(100,220,100));
        etqThroughput=new JLabel("TP: 0.00");etqThroughput.setForeground(new Color(180,220,255));

        fila.add(btnStart);
        fila.add(btnRollMove);
        fila.add(etqTurns);
        fila.add(etqWIP);
        fila.add(etqTerminados);
        fila.add(etqThroughput);

        // Botón para abrir gráficas (solo si ya hay datos)
        JButton btnGraficas=crearBoton("Gráficas",new Color(70,90,140),Color.WHITE,13);
        btnGraficas.addActionListener(e->{
            if(linea.getHistorialDados().isEmpty()){
                JOptionPane.showMessageDialog(this,"Aún no hay datos. Juega al menos 1 turno y luego abre las gráficas.","Sin datos",JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            new VentanaGraficaIndividual(linea.getHistorialTerminados(),linea.getHistorialWIP(),linea.getHistorialDados(),linea.getHistorialMovidos(),0).setVisible(true);
        });
        fila.add(btnGraficas);

        return fila;
    }

    // Botón redondeado
    private JButton crearBoton(String texto,Color bg,Color fg,int size){
        JButton btn=new JButton(texto){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

                Color fondo=!isEnabled()?bg.darker().darker():getModel().isPressed()?bg.darker():getModel().isRollover()?bg.brighter():bg;
                g2.setColor(fondo);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),28,28));

                g2.setColor(fg);
                g2.setFont(getFont());
                FontMetrics fm=g2.getFontMetrics();
                int tx=(getWidth()-fm.stringWidth(getText()))/2;
                int ty=(getHeight()+fm.getAscent()-fm.getDescent())/2;

                g2.drawString(getText(),tx,ty);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g){}
        };

        btn.setFont(new Font("Arial",Font.BOLD,size));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(btn.getFontMetrics(btn.getFont()).stringWidth(texto)+28,34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Tablero + overlay de colas
    private JComponent crearTableroConOverlay(){
        layered=new JLayeredPane();
        layered.setPreferredSize(new Dimension(TABLERO_W,TABLERO_H));
        layered.setOpaque(false);

        JPanel tablero=crearTableroBase();
        overlayColas=new OverlayColas();
        overlayColas.setBounds(0,0,TABLERO_W,TABLERO_H);

        tablero.setBounds(0,0,TABLERO_W,TABLERO_H);
        layered.add(tablero,Integer.valueOf(0));
        layered.add(overlayColas,Integer.valueOf(100));
        return layered;
    }

    // Crea paneles de estaciones y almacén
    private JPanel crearTableroBase(){
        JPanel tablero=new JPanel(null){
            @Override protected void paintComponent(Graphics g){super.paintComponent(g);dibujarFondo(g);}
        };

        tablero.setOpaque(false);
        tablero.setSize(new Dimension(TABLERO_W,TABLERO_H));
        tablero.setPreferredSize(new Dimension(TABLERO_W,TABLERO_H));
        PanelEstacion.reiniciarContador();
        panelesEstacion=new PanelEstacion[LineaProduccion.NUM_ESTACIONES];
        String[] nombres={"A","B","C","D","E","F","G","H","I","J"};
        panelAlmacen=new PanelAlmacen();
        panelAlmacen.setBounds(X_ALMACEN,Y_INF,PanelAlmacen.PANEL_W,PanelAlmacen.PANEL_H);
        tablero.add(panelAlmacen);
        // J se agrega primero para respetar tu layout actual
        panelesEstacion[9]=new PanelEstacion("J");
        panelesEstacion[9].setBounds(COLS[0],Y_INF,W,H);
        tablero.add(panelesEstacion[9]);

        // A..E arriba
        for(int i=0;i<5;i++){
            panelesEstacion[i]=new PanelEstacion(nombres[i]);
            panelesEstacion[i].setBounds(COLS[i],Y_SUP,W,H);
            tablero.add(panelesEstacion[i]);
        }

        // F..I abajo en orden inverso (como tu tablero)
        for(int i=0;i<4;i++){
            int idx=5+i;
            int x=COLS[4-i];
            panelesEstacion[idx]=new PanelEstacion(nombres[idx]);
            panelesEstacion[idx].setBounds(x,Y_INF,W,H);
            tablero.add(panelesEstacion[idx]);
        }

        return tablero;
    }

    // Dibuja fondo y flecha entre A y B
    private void dibujarFondo(Graphics g){
        Graphics2D g2=(Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if(imgFondo!=null) g2.drawImage(imgFondo,0,0,TABLERO_W,TABLERO_H,this);
        else{
            g2.setColor(new Color(220,205,180));g2.fillRect(0,0,TABLERO_W,TABLERO_H);
        }

        // Flecha entre la estación A y B
        if(imgFlecha!=null){
            int xA=COLS[0],xB=COLS[1];
            int gapStart=xA+W,gapEnd=xB,gapW=Math.max(0,gapEnd-gapStart);
            int arrowW=Math.max(10,gapW-6),arrowH=26;
            int x=gapStart+(gapW-arrowW)/2;
            int y=Y_SUP+70;
            g2.drawImage(imgFlecha,x,y,arrowW,arrowH,this);
        }
    }

    // Control de Tirar/Mover
    private void accionRollMove(){
        if(estadoRollMove==EstadoRollMove.ESPERANDO){
            linea.lanzarDados();
            actualizarVista();
            btnRollMove.setText("Mover");
            btnRollMove.setBackground(COLOR_MOVE);
            estadoRollMove=EstadoRollMove.ROLL_HECHO;
        }else{
            linea.moverPiezas();
            actualizarVista();

            // Si ya se jugaron 20 rondas, se muestra resumen final
            if(linea.haTerminado()){
                btnRollMove.setEnabled(false);
                btnStart.setEnabled(true);

                int turnos=linea.getRondaActual();
                int terminados=linea.getColaTerminados().tamanio();
                double tp=linea.getThroughput();
                int wipFinal=linea.getPiezasEnSistema();
                double wipProm=linea.getHistorialWIP().stream().mapToInt(Integer::intValue).average().orElse(0);
                int movidosTotales=linea.getHistorialMovidos().stream().mapToInt(Integer::intValue).sum();

                String msg="El juego finalizó ("+turnos+" rondas)\n\nResultados:\n- Terminados: "+terminados+"\n- Throughput promedio: "+String.format("%.2f",tp)+" / turno\n- WIP final: "+wipFinal+"\n- WIP promedio: "+String.format("%.1f",wipProm)+"\n- Movidos totales: "+movidosTotales+"\n";
                JOptionPane.showMessageDialog(this,msg,"Fin del juego — Resultados",JOptionPane.INFORMATION_MESSAGE);

                btnRollMove.setText("Tirar");
                btnRollMove.setBackground(COLOR_ROLL);
                estadoRollMove=EstadoRollMove.ESPERANDO;
                btnRollMove.repaint();
                return;
            }

            btnRollMove.setText("Tirar");
            btnRollMove.setBackground(COLOR_ROLL);
            estadoRollMove=EstadoRollMove.ESPERANDO;
        }
        btnRollMove.repaint();
    }

    // Reinicia partida
    private void inicializar(){
        linea=new LineaProduccion();
        linea.inicializar(RONDAS);

        estadoRollMove=EstadoRollMove.ESPERANDO;
        btnRollMove.setText("Tirar");
        btnRollMove.setBackground(COLOR_ROLL);
        btnRollMove.setEnabled(true);
        btnStart.setEnabled(false);

        actualizarVista();
    }

    // Actualiza valores y el tablero
    private void actualizarVista(){
        etqTurns.setText("Turno: "+linea.getRondaActual());
        etqWIP.setText("WIP: "+linea.getPiezasEnSistema());

        int term=linea.getColaTerminados().tamanio();
        etqTerminados.setText("Terminados: "+term);
        etqThroughput.setText(String.format("TP: %.2f",linea.getThroughput()));

        if(panelAlmacen!=null) panelAlmacen.actualizar(term);

        List<Estacion> ests=linea.getEstaciones();
        for(int i=0;i<ests.size();i++){
            panelesEstacion[i].setValorDado(ests.get(i).getUltimoDado());
            panelesEstacion[i].repaint();
        }

        if(overlayColas!=null) overlayColas.repaint();
        if(layered!=null){layered.revalidate();layered.repaint();}
    }

    // Overlay: dibuja colas al lado de cada estación
    private class OverlayColas extends JComponent{
        // Grilla fija: 5 de largo y 10 hacia abajo
        private static final int DIAM=9,SEP_P=2,POR_FILA=5,FILAS_MAX=10;
        private static final int Y_COLA_TOP=Y_SUP+92,Y_COLA_BOT=Y_INF+92;
        private static final int Y_NOMBRE_TOP=Y_SUP+H-2,Y_NOMBRE_BOT=Y_INF+H-2;

        OverlayColas(){setOpaque(false);setDoubleBuffered(true);setFocusable(false);}

        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            if(linea==null) return;

            List<Estacion> ests=linea.getEstaciones();
            if(ests==null||ests.size()!=10) return;

            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

            dibujarNombresCentrados(g2);

            int colaW=blockW(),colaH=blockH();

            // A no muestra piezas
            for(int s=1;s<=4;s++) drawQueueFixedGrid(g2,areaDerechaDePanel(COLS[s],Y_COLA_TOP,colaW,colaH),ests.get(s).getPiezasAnteriores(),ests.get(s).getPiezasNuevas());

            // F..J abajo a la derecha
            drawQueueFixedGrid(g2,areaDerechaDePanel(COLS[4],Y_COLA_BOT,colaW,colaH),ests.get(5).getPiezasAnteriores(),ests.get(5).getPiezasNuevas());
            drawQueueFixedGrid(g2,areaDerechaDePanel(COLS[3],Y_COLA_BOT,colaW,colaH),ests.get(6).getPiezasAnteriores(),ests.get(6).getPiezasNuevas());
            drawQueueFixedGrid(g2,areaDerechaDePanel(COLS[2],Y_COLA_BOT,colaW,colaH),ests.get(7).getPiezasAnteriores(),ests.get(7).getPiezasNuevas());
            drawQueueFixedGrid(g2,areaDerechaDePanel(COLS[1],Y_COLA_BOT,colaW,colaH),ests.get(8).getPiezasAnteriores(),ests.get(8).getPiezasNuevas());
            drawQueueFixedGrid(g2,areaDerechaDePanel(COLS[0],Y_COLA_BOT,colaW,colaH),ests.get(9).getPiezasAnteriores(),ests.get(9).getPiezasNuevas());

            g2.dispose();
        }

        // Dibuja letras A..J debajo de cada panel
        private void dibujarNombresCentrados(Graphics2D g2){
            g2.setColor(new Color(80,40,35));
            g2.setFont(new Font("Arial",Font.BOLD,12));
            FontMetrics fm=g2.getFontMetrics();

            drawNombreCentrado(g2,fm,"A",COLS[0],Y_NOMBRE_TOP);
            drawNombreCentrado(g2,fm,"B",COLS[1],Y_NOMBRE_TOP);
            drawNombreCentrado(g2,fm,"C",COLS[2],Y_NOMBRE_TOP);
            drawNombreCentrado(g2,fm,"D",COLS[3],Y_NOMBRE_TOP);
            drawNombreCentrado(g2,fm,"E",COLS[4],Y_NOMBRE_TOP);

            drawNombreCentrado(g2,fm,"J",COLS[0],Y_NOMBRE_BOT);
            drawNombreCentrado(g2,fm,"I",COLS[1],Y_NOMBRE_BOT);
            drawNombreCentrado(g2,fm,"H",COLS[2],Y_NOMBRE_BOT);
            drawNombreCentrado(g2,fm,"G",COLS[3],Y_NOMBRE_BOT);
            drawNombreCentrado(g2,fm,"F",COLS[4],Y_NOMBRE_BOT);
        }

        private void drawNombreCentrado(Graphics2D g2,FontMetrics fm,String nombre,int xPanel,int yBase){
            int x=xPanel+(W-fm.stringWidth(nombre))/2;
            g2.drawString(nombre,x,yBase);
        }

        private int blockW(){return POR_FILA*DIAM+(POR_FILA-1)*SEP_P;}
        private int blockH(){return FILAS_MAX*DIAM+(FILAS_MAX-1)*SEP_P;}

        // Coloca la cola justo a la derecha del panel
        private Rectangle areaDerechaDePanel(int xPanel,int y,int w,int h){
            int x=xPanel+W+2;
            if(x<0) x=0;
            if(x+w>TABLERO_W) x=TABLERO_W-w;
            return new Rectangle(x,y,w,h);
        }

        // Dibuja una puntos fijos sin superponer, y recorta si hay más de 50
        private void drawQueueFixedGrid(Graphics2D g2,Rectangle area,int anteriores,int nuevas){
            int total=anteriores+nuevas;
            if(total<=0) return;

            int maxVisible=POR_FILA*FILAS_MAX;
            int visibles=Math.min(total,maxVisible);

            int i=0;
            while(i<Math.min(anteriores,visibles)){drawDot(g2,area.x,area.y,i,true);i++;}

            int b=0;
            while(i<visibles&&b<nuevas){drawDot(g2,area.x,area.y,i,false);i++;b++;}
        }

        private void drawDot(Graphics2D g2,int baseX,int baseY,int idx,boolean gris){
            int col=idx%POR_FILA,fil=idx/POR_FILA;
            int px=baseX+col*(DIAM+SEP_P);
            int py=baseY+fil*(DIAM+SEP_P);

            if(gris){
                g2.setColor(new Color(150,150,150));
                g2.fillOval(px,py,DIAM,DIAM);
                g2.setColor(new Color(100,100,100));
                g2.drawOval(px,py,DIAM,DIAM);
            }else{
                g2.setColor(new Color(30,100,200));
                g2.fillOval(px,py,DIAM,DIAM);
                g2.setColor(new Color(11, 82, 209));
                g2.drawOval(px,py,DIAM,DIAM);
            }
        }
    }
}