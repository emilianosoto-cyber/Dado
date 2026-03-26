package org.example;

import java.util.Random;

//Representa una estación de trabajo en la línea de producción.
public class Estacion{

    //iDEntificador de la estación
    private final String nombre;
    private final Cola<Pieza> colaEntrada;
    //acumulado de piezas procesadas por estación
    private int totalProcesado;
    //Resultado del último lanzamiento de dado
    private int ultimoDado;
    //Generador de números aleatorios
    private final Random random;

    //constructor
    public Estacion(String nombre, Random random){
        this.nombre=nombre;
        this.colaEntrada=new Cola<>();
        this.totalProcesado=0;
        this.ultimoDado=0;
        this.random=random;
    }

    //lanza el dado y almacena el resultado.
    public int lanzarDado(){
        ultimoDado = random.nextInt(6) + 1;
        return ultimoDado;
    }

    //Procesa piezas de la cola de entrada y las transfiere a la siguiente cola.
    public int procesar(Cola<Pieza> colaSiguiente){
        int capacidad=ultimoDado;
        int procesadas=0;

        while (procesadas < capacidad && !colaEntrada.estaVacia()){
            Pieza pieza=colaEntrada.desencolar();
            colaSiguiente.encolar(pieza);
            procesadas++;
        }
        totalProcesado += procesadas;
        return procesadas;
    }

    //Agrega una Pieza a la cola de entrada de estación.
    public void agregarPieza(Pieza pieza){
        colaEntrada.encolar(pieza);
    }
    //Tamaño actual de la cola de entrada
    public int getTamanioColaEntrada(){
        return colaEntrada.tamanio();
    }
    //Cola de entrada de la estación
    public Cola<Pieza> getColaEntrada(){
        return colaEntrada;
    }

    //Nombre de la estación
    public String getNombre(){
        return nombre;
    }

    //Total de piezas procesadas por est
    public int getTotalProcesado(){
        return totalProcesado;
    }

    //Resultado del último lanzamiento de dado
    public int getUltimoDado(){
        return ultimoDado;
    }

    //Reinicia la estación a su estado inicial.
    public void reiniciar(){
        colaEntrada.limpiar();
        totalProcesado=0;
        ultimoDado =0;
    }
}