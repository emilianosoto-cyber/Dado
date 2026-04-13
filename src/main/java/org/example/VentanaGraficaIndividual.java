package org.example;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;

// Ventana JavaFX para ver una gráfica a la vez
public class VentanaGraficaIndividual extends JFrame{
    // Distribución histórica: % de partidas con >= X terminados (30..70)
    private static final double[] DIST_ACUMULADA={100,100,100,100,100,100,100,99.5,99,98,97,95,92,88,82,73,63,51,40,29,19,12,6,3,1.5,0.8,0.3,0.1,0.05,0.01,0,0,0,0,0,0,0,0,0,0,0};
    // Títulos para navegación
    private static final String[] TITULOS={"Rendimiento","WIP en Sistema","Terminados","Actividad"};

    // Datos del juego
    private final List<Integer> histTerminados,histWIP,histMovidos;
    private final List<int[]> histDados;
    private final int graficaId;

    public VentanaGraficaIndividual(List<Integer> histTerminados,List<Integer> histWIP,List<int[]> histDados,List<Integer> histMovidos,int graficaId){
        super("Gráfica — "+TITULOS[graficaId]);
        this.histTerminados=histTerminados;
        this.histWIP=histWIP;
        this.histDados=histDados;
        this.histMovidos=histMovidos;
        this.graficaId=graficaId;

        setSize(860,580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Panel puente Swing -> JavaFX
        JFXPanel fxPanel=new JFXPanel();
        add(fxPanel,BorderLayout.CENTER);

        // Construye la UI de JavaFX en su hilo
        Platform.runLater(()->{
            Region contenido=crearGrafica(graficaId);
            HBox navBar=crearNavBar();
            VBox root=new VBox(0,contenido,navBar);
            VBox.setVgrow(contenido,Priority.ALWAYS);
            root.setStyle("-fx-background-color: #d4c9b4;");
            Scene scene=new Scene(root,840,550);
            scene.setFill(Color.web("#d4c9b4"));
            fxPanel.setScene(scene);
        });
    }

    // Barra inferior para cambiar de gráfica
    private HBox crearNavBar(){
        HBox nav=new HBox(8);
        nav.setAlignment(Pos.CENTER);
        nav.setPadding(new Insets(6,12,8,12));
        nav.setStyle("-fx-background-color: #3c1c16;");

        for(int i=0;i<TITULOS.length;i++){
            final int idx=i;
            Button btn=new Button(TITULOS[i]);
            btn.setFont(Font.font("Arial",FontWeight.BOLD,11));
            btn.setCursor(javafx.scene.Cursor.HAND);

            // Resalta la gráfica actual
            if(i==graficaId){
                btn.setStyle("-fx-background-color: #a06040;-fx-text-fill: white;-fx-background-radius: 14;-fx-padding: 4 14 4 14;");
            }else{
                btn.setStyle("-fx-background-color: #6e4830;-fx-text-fill: #e0cdb0;-fx-background-radius: 14;-fx-padding: 4 14 4 14;");
                // Abre la otra gráfica y cierra esta
                btn.setOnAction(e->SwingUtilities.invokeLater(()->{new VentanaGraficaIndividual(histTerminados,histWIP,histDados,histMovidos,idx).setVisible(true);dispose();}));
            }
            nav.getChildren().add(btn);
        }
        return nav;
    }

    // Decide qué gráfica crear
    private Region crearGrafica(int id){
        switch(id){
            case 0:return crearGraficaRendimiento();
            case 1:return crearGraficaWIP();
            case 2:return crearGraficaTerminados();
            case 3:return crearGraficaActividad();
            default:return new VBox();
        }
    }

    // Rendimiento: distribución histórica y tu resultado en rojo
    private VBox crearGraficaRendimiento(){
        int terminados=histTerminados.isEmpty()?0:histTerminados.get(histTerminados.size()-1);

        CategoryAxis ejeX=new CategoryAxis();
        NumberAxis ejeY=new NumberAxis(0,100,10);
        ejeX.setLabel("Personas terminadas");
        ejeY.setLabel("% de partidas con ≥ resultado");
        ejeY.setTickLabelFormatter(new NumberAxis.DefaultFormatter(ejeY,null,"%"));

        BarChart<String,Number> grafica=new BarChart<>(ejeX,ejeY);
        grafica.setTitle("Tu rendimiento vs. distribución histórica");
        grafica.setLegendVisible(false);
        grafica.setBarGap(0);
        grafica.setCategoryGap(1);
        grafica.setAnimated(false);
        estilizarGrafica(grafica);

        XYChart.Series<String,Number> serieAzul=new XYChart.Series<>();
        XYChart.Series<String,Number> serieRojo=new XYChart.Series<>();

        for(int i=0;i<=40;i++){
            int val=30+i;
            double pct=i<DIST_ACUMULADA.length?DIST_ACUMULADA[i]:0;
            String lbl=String.valueOf(val);
            if(val==terminados){serieRojo.getData().add(new XYChart.Data<>(lbl,pct));serieAzul.getData().add(new XYChart.Data<>(lbl,0));}
            else{serieAzul.getData().add(new XYChart.Data<>(lbl,pct));serieRojo.getData().add(new XYChart.Data<>(lbl,0));}
        }

        grafica.getData().addAll(serieAzul,serieRojo);

        // Colorea barras una vez que JavaFX creó los nodos
        Platform.runLater(()->{
            serieAzul.getData().forEach(d->d.getNode().setStyle("-fx-bar-fill: #6fa8dc;"));
            serieRojo.getData().forEach(d->d.getNode().setStyle("-fx-bar-fill: #cc2200;"));
        });

        int percentil=calcularPercentil(terminados);
        Label lblInfo=crearEtiquetaInfo("Obtuviste "+terminados+" personas terminadas.  Top "+percentil+"% de todas las partidas.  Esperado: ~53  |  Rango (99%): 44 – 62");

        VBox box=new VBox(8,grafica,lblInfo);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #d4c9b4;");
        VBox.setVgrow(grafica,Priority.ALWAYS);
        return box;
    }

    // WIP: muestra el WIP inicial (0) y luego cada turno
    private VBox crearGraficaWIP(){
        CategoryAxis ejeX=new CategoryAxis();
        NumberAxis ejeY=new NumberAxis(0,100,10);
        ejeX.setLabel("Turno");
        ejeY.setLabel("Personas en sistema (WIP)");

        BarChart<String,Number> grafica=new BarChart<>(ejeX,ejeY);
        grafica.setTitle("Personas en el sistema por turno (WIP)");
        grafica.setLegendVisible(false);
        grafica.setBarGap(2);
        grafica.setCategoryGap(4);
        grafica.setAnimated(false);
        estilizarGrafica(grafica);

        XYChart.Series<String,Number> serie=new XYChart.Series<>();
        serie.getData().add(new XYChart.Data<>("0",36));
        for(int i=0;i<histWIP.size();i++) serie.getData().add(new XYChart.Data<>(String.valueOf(i+1),histWIP.get(i)));
        grafica.getData().add(serie);

        Platform.runLater(()->{
            serie.getData().get(0).getNode().setStyle("-fx-bar-fill: #888888;");
            for(int i=1;i<serie.getData().size();i++) serie.getData().get(i).getNode().setStyle("-fx-bar-fill: #00aaee;");
        });

        double prom=histWIP.stream().mapToInt(Integer::intValue).average().orElse(0);
        Label lblInfo=crearEtiquetaInfo(String.format("WIP promedio: %.1f  |  WIP inicial: 36",prom));

        VBox box=new VBox(8,grafica,lblInfo);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #d4c9b4;");
        VBox.setVgrow(grafica,Priority.ALWAYS);
        return box;
    }

    // Terminados: acumulado por turno y etiqueta al final
    private VBox crearGraficaTerminados(){
        CategoryAxis ejeX=new CategoryAxis();
        NumberAxis ejeY=new NumberAxis(0,80,10);
        ejeX.setLabel("Turno");
        ejeY.setLabel("Personas terminadas (acumulado)");

        BarChart<String,Number> grafica=new BarChart<>(ejeX,ejeY);
        grafica.setTitle("Throughput acumulado — personas terminadas por turno");
        grafica.setLegendVisible(false);
        grafica.setBarGap(2);
        grafica.setCategoryGap(4);
        grafica.setAnimated(false);
        estilizarGrafica(grafica);

        XYChart.Series<String,Number> serie=new XYChart.Series<>();
        for(int i=0;i<histTerminados.size();i++) serie.getData().add(new XYChart.Data<>(String.valueOf(i+1),histTerminados.get(i)));
        grafica.getData().add(serie);

        int ultimo=histTerminados.isEmpty()?0:histTerminados.get(histTerminados.size()-1);

        Platform.runLater(()->{
            serie.getData().forEach(d->d.getNode().setStyle("-fx-bar-fill: #00aaee;"));
            if(!serie.getData().isEmpty()){
                XYChart.Data<String,Number> ult=serie.getData().get(serie.getData().size()-1);
                Label lbl=new Label(String.valueOf(ultimo));
                lbl.setFont(Font.font("Arial",FontWeight.BOLD,11));
                lbl.setTextFill(Color.web("#333333"));
                StackPane sp=(StackPane)ult.getNode();
                sp.getChildren().add(lbl);
                StackPane.setAlignment(lbl,Pos.TOP_CENTER);
            }
        });

        double promTP=histTerminados.isEmpty()?0:(double)ultimo/histTerminados.size();
        Label lblInfo=crearEtiquetaInfo(String.format("Total terminados: %d  |  Promedio por turno: %.2f  |  Esperado: ~53",ultimo,promTP));

        VBox box=new VBox(8,grafica,lblInfo);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #d4c9b4;");
        VBox.setVgrow(grafica,Priority.ALWAYS);
        return box;
    }

    // Actividad: permite filtrar por estación o ver todo junto
    private VBox crearGraficaActividad(){
        String[] estaciones={"A","B","C","D","E","F","G","H","I","J","Todas"};
        ToggleGroup tg=new ToggleGroup();

        HBox selectorBox=new HBox(5);
        selectorBox.setAlignment(Pos.CENTER_LEFT);
        selectorBox.setPadding(new Insets(2,0,6,0));

        Label lblJugador=new Label("Jugador: ");
        lblJugador.setFont(Font.font("Arial",FontWeight.BOLD,13));
        lblJugador.setTextFill(Color.web("#3a1f10"));
        selectorBox.getChildren().add(lblJugador);

        for(String est:estaciones){
            ToggleButton tb=new ToggleButton(est);
            tb.setToggleGroup(tg);
            tb.setStyle("-fx-background-radius: 20;-fx-font-size: 11;-fx-font-weight: bold;-fx-background-color: #9a8060;-fx-text-fill: white;");
            tb.selectedProperty().addListener((obs,o,n)->tb.setStyle("-fx-background-radius: 20;-fx-font-size: 11;-fx-font-weight: bold;-fx-background-color: "+(n?"#3a1f10":"#9a8060")+";-fx-text-fill: white;"));
            if("Todas".equals(est)) tb.setSelected(true);
            selectorBox.getChildren().add(tb);
        }

        CategoryAxis ejeX=new CategoryAxis();
        NumberAxis ejeY=new NumberAxis(0,36,2);
        ejeX.setLabel("Turno");
        ejeY.setLabel("Cantidad");

        BarChart<String,Number> grafica=new BarChart<>(ejeX,ejeY);
        grafica.setTitle("Actividad por jugador — dados lanzados vs. piezas movidas");
        grafica.setBarGap(1);
        grafica.setCategoryGap(3);
        grafica.setAnimated(false);
        estilizarGrafica(grafica);

        Label lblMovido=new Label("■ Movidos");
        lblMovido.setTextFill(Color.web("#00aaee"));
        lblMovido.setFont(Font.font("Arial",FontWeight.BOLD,11));

        Label lblDado=new Label("■ Dado (capacidad)");
        lblDado.setTextFill(Color.web("#888888"));
        lblDado.setFont(Font.font("Arial",FontWeight.BOLD,11));

        Label lblProm=new Label("Promedio: 0.0");
        lblProm.setFont(Font.font("Arial",FontWeight.BOLD,12));
        lblProm.setTextFill(Color.web("#3a1f10"));

        HBox leyenda=new HBox(15,lblMovido,lblDado,lblProm);
        leyenda.setAlignment(Pos.CENTER_RIGHT);

        // Carga inicial con todas las estaciones
        cargarDatosActividad(grafica,-1,lblProm);

        tg.selectedToggleProperty().addListener((obs,o,n)->{
            if(n==null){tg.selectToggle(o);return;}
            String sel=((ToggleButton)n).getText();
            int idx="Todas".equals(sel)?-1:Arrays.asList(estaciones).indexOf(sel);
            cargarDatosActividad(grafica,idx,lblProm);
        });

        VBox box=new VBox(8,selectorBox,leyenda,grafica);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #d4c9b4;");
        VBox.setVgrow(grafica,Priority.ALWAYS);
        return box;
    }

    // Llena la gráfica según estación (-1 = todas)
    private void cargarDatosActividad(BarChart<String,Number> grafica,int estIdx,Label lblProm){
        grafica.getData().clear();

        XYChart.Series<String,Number> serieMovido=new XYChart.Series<>();
        serieMovido.setName("Movidos");

        XYChart.Series<String,Number> serieDado=new XYChart.Series<>();
        serieDado.setName("Dado");

        double sumaMovidos=0;

        for(int r=0;r<histDados.size();r++){
            String turno=String.valueOf(r+1);
            int dado,movido;

            // Todas: suma de 10 dados y el total real movido del sistema
            if(estIdx==-1){
                dado=Arrays.stream(histDados.get(r)).sum();
                movido=r<histMovidos.size()?histMovidos.get(r):0;
            }else{
                // Estación individual: se ve como capacidad (dado)
                dado=estIdx<histDados.get(r).length?histDados.get(r)[estIdx]:0;
                movido=dado;
            }

            serieMovido.getData().add(new XYChart.Data<>(turno,movido));
            serieDado.getData().add(new XYChart.Data<>(turno,dado));
            sumaMovidos+=movido;
        }

        grafica.getData().addAll(serieMovido,serieDado);

        double promedio=histDados.isEmpty()?0:sumaMovidos/histDados.size();
        lblProm.setText(String.format("Promedio: %.1f",promedio));

        Platform.runLater(()->{
            serieMovido.getData().forEach(d->d.getNode().setStyle("-fx-bar-fill: #00aaee;"));
            serieDado.getData().forEach(d->d.getNode().setStyle("-fx-bar-fill: #888888; -fx-opacity: 0.5;"));
        });
    }

    // Aplica el mismo fondo a todas las gráficas
    private void estilizarGrafica(XYChart<?,?> grafica){
        grafica.setStyle("-fx-background-color: #e8e0d0;");
        if(grafica.lookup(".chart-plot-background")!=null) grafica.lookup(".chart-plot-background").setStyle("-fx-background-color: #e8e0d0;");
    }

    // Etiqueta de info en la parte inferior
    private Label crearEtiquetaInfo(String texto){
        Label lbl=new Label(texto);
        lbl.setFont(Font.font("Arial",FontWeight.BOLD,12));
        lbl.setTextFill(Color.web("#3a1f10"));
        lbl.setAlignment(Pos.CENTER);
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setPadding(new Insets(6,16,6,16));
        lbl.setStyle("-fx-background-color: rgba(255,255,255,0.65);-fx-background-radius: 6;-fx-border-color: #9a8060;-fx-border-radius: 6;");
        return lbl;
    }

    // Calcula en qué "top %" cae el resultado final
    private int calcularPercentil(int terminados){
        int idx=terminados-30;
        if(idx<0) return 100;
        if(idx>=DIST_ACUMULADA.length) return 1;
        return (int)Math.round(DIST_ACUMULADA[idx]);
    }
}