package org.example;


//Es la nidad de trabajo que va a estar recorriendo la línea de producción. Se debe registrar en que ronda entró.
public class Pieza {

    //Identificador de la pieza
    private final int id;
    //Ronda en que la pieza entro
    private final int rondaIngreso;

    //Constructor
    public Pieza(int id, int rondaIngreso){
        this.id=id;
        this.rondaIngreso=rondaIngreso;
    }

    //Regresa el Identificador de la pieza.
    public int getId(){
        return id;
    }

    //Reg la ronda en que la pieza ingresó.
    public int getRondaIngreso(){
        return rondaIngreso;
    }

    @Override
    public String toString(){
        return "P" + id;
    }
}