package org.example;

import java.util.Random;

// Estación con 2 colas: lo de ayer (gris) y lo que llega hoy (azul)
public class Estacion {
    private final String nombre;
    private final Random random;
    private final Cola<Pieza> colaAnterior;
    private final Cola<Pieza> colaNueva;
    private int ultimoDado;
    private int procesadasUltimaRonda;

    public Estacion(String nombre, Random random){
        this.nombre=nombre;
        this.random=random;
        this.colaAnterior=new Cola<>();
        this.colaNueva=new Cola<>();
    }

    public void reiniciar(){
        colaAnterior.limpiar();
        colaNueva.limpiar();
        ultimoDado=0;
        procesadasUltimaRonda=0;
    }

    public void lanzarDado(){
        ultimoDado=random.nextInt(6)+1;
    }

    // Al iniciar el turno: lo azul pasa a gris (ya se puede procesar)
    public void prepararParaMove(){
        procesadasUltimaRonda=0;
        while(!colaNueva.estaVacia()) colaAnterior.agregarFinal(colaNueva.eliminarInicio());
    }

    // Recibir siempre llega como azul (no se procesa este turno)
    public void recibir(Pieza pieza){
        colaNueva.agregarFinal(pieza);
    }

    // A mete nuevas como azul
    public void introducirNueva(Pieza pieza){
        colaNueva.agregarFinal(pieza);
    }

    // A entrega hoy a B: siguen siendo azules en B
    public void enviarNuevasHacia(Estacion destino){
        while(!colaNueva.estaVacia()){
            Pieza p=colaNueva.eliminarInicio();
            if(p!=null) destino.recibir(p);
        }
    }

    // Mover solo procesa lo gris
    public void procesarHacia(Estacion destino){
        int cantidad=Math.min(ultimoDado,colaAnterior.tamanio());
        procesadasUltimaRonda=0;
        while(procesadasUltimaRonda<cantidad){
            Pieza p=colaAnterior.eliminarInicio();
            if(p==null) break;
            destino.recibir(p);
            procesadasUltimaRonda++;
        }
    }

    // J manda a terminados (también solo lo gris)
    public void procesarHaciaTerminados(Cola<Pieza> terminados){
        int cantidad=Math.min(ultimoDado,colaAnterior.tamanio());
        procesadasUltimaRonda=0;
        while(procesadasUltimaRonda<cantidad){
            Pieza p=colaAnterior.eliminarInicio();
            if(p==null) break;
            terminados.agregarFinal(p);
            procesadasUltimaRonda++;
        }
    }

    public String getNombre(){
        return nombre;
    }

    public int getUltimoDado(){
        return ultimoDado;
    }

    public int getPiezasAnteriores(){
        return colaAnterior.tamanio();
    }
    public int getPiezasNuevas(){
        return colaNueva.tamanio();
    }
    public int getTamanioTotal(){
        return colaAnterior.tamanio()+colaNueva.tamanio();
    }
    public int getProcesadasUltimaRonda(){
        return procesadasUltimaRonda;
    }
}