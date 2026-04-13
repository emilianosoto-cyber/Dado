package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Motor del juego: turnos, WIP, terminados e historial
public class LineaProduccion{
    public static final int NUM_ESTACIONES=10;

    private final List<Estacion> estaciones=new ArrayList<>();
    private final Cola<Pieza> colaTerminados=new Cola<>();
    private final Random random=new Random(System.currentTimeMillis());

    private int rondaActual,totalRondas,contadorPiezas;
    private boolean dadosLanzados;

    // Historial para gráficas
    private final List<Integer> historialTerminados=new ArrayList<>();
    private final List<Integer> historialWIP=new ArrayList<>();
    private final List<int[]> historialDados=new ArrayList<>();
    private final List<Integer> historialMovidos=new ArrayList<>();

    public LineaProduccion(){
        String[] nombres={"A","B","C","D","E","F","G","H","I","J"};
        for(String n:nombres) estaciones.add(new Estacion(n,random));
    }

    public void inicializar(int numRondas){
        // Resetea todo para una partida nueva
        colaTerminados.limpiar();
        rondaActual=0;
        totalRondas=numRondas;
        contadorPiezas=0;
        dadosLanzados=false;
        random.setSeed(System.currentTimeMillis());

        historialTerminados.clear();
        historialWIP.clear();
        historialDados.clear();
        historialMovidos.clear();

        for(Estacion e:estaciones) e.reiniciar();

        // Arranque: B..J empiezan con 4 piezas en cola anterior
        for(int i=1;i<NUM_ESTACIONES;i++){
            for(int j=0;j<4;j++) estaciones.get(i).recibir(new Pieza(++contadorPiezas));
            estaciones.get(i).prepararParaMove();
        }
    }

    public void lanzarDados(){
        // Evita tirar dos veces en el mismo turno
        if(haTerminado()||dadosLanzados) return;
        for(Estacion e:estaciones) e.lanzarDado();
        dadosLanzados=true;
    }

    // Un turno: A crea, A->B (hoy), B..J procesan lo anterior
    public void moverPiezas(){
        if(!dadosLanzados) return;

        rondaActual++;

        // Guarda terminados antes de mover para saber cuántos se hicieron este turno
        int antesTerminados=colaTerminados.tamanio();

        // Lo nuevo pasa a anterior (ahora sí se puede procesar)
        for(int k=0;k<NUM_ESTACIONES;k++) estaciones.get(k).prepararParaMove();

        // A crea piezas según su dado
        int nuevas=estaciones.get(0).getUltimoDado();
        while(nuevas-->0) estaciones.get(0).introducirNueva(new Pieza(++contadorPiezas));

        // A manda hoy a B como "nuevo"
        estaciones.get(0).enviarNuevasHacia(estaciones.get(1));

        // B..I procesan hacia la siguiente estación
        for(int i=1;i<=8;i++) estaciones.get(i).procesarHacia(estaciones.get(i+1));

        // J manda a terminados
        estaciones.get(9).procesarHaciaTerminados(colaTerminados);

        // Guarda dados del turno
        int[] dados=new int[NUM_ESTACIONES];
        for(int k=0;k<NUM_ESTACIONES;k++) dados[k]=estaciones.get(k).getUltimoDado();

        // Guarda historial para gráficas
        historialDados.add(dados);
        historialMovidos.add(colaTerminados.tamanio()-antesTerminados);
        historialTerminados.add(colaTerminados.tamanio());
        historialWIP.add(getPiezasEnSistema());

        dadosLanzados=false;
    }

    // WIP: todo lo que está en estaciones (anteriores+nuevas)
    public int getPiezasEnSistema(){
        int total=0;
        for(Estacion e:estaciones) total+=e.getTamanioTotal();
        return total;
    }

    public boolean haTerminado(){
        return rondaActual>=totalRondas;
    }
    public double getThroughput(){
        return rondaActual==0?0:(double)colaTerminados.tamanio()/rondaActual;
    }

    public List<Estacion> getEstaciones(){
        return estaciones;
    }
    public Cola<Pieza> getColaTerminados(){
        return colaTerminados;
    }
    public int getRondaActual(){
        return rondaActual;
    }
    public int getTotalRondas(){
        return totalRondas;
    }

    public List<Integer> getHistorialTerminados(){
        return historialTerminados;
    }
    public List<Integer> getHistorialWIP(){
        return historialWIP;
    }
    public List<int[]> getHistorialDados(){
        return historialDados;
    }
    public List<Integer> getHistorialMovidos(){
        return historialMovidos;
    }
}