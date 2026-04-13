package org.example;

// Nodo doble para poder movernos hacia delante y hacia atrás
public class Nodo<T>{
    // Dato guardado
    private T dato;
    // Siguiente nodo
    private Nodo<T> siguiente;
    // Nodo anterior
    private Nodo<T> anterior;

    // Crea el nodo con su dato
    public Nodo(T dato){
        this.dato=dato;
    }

    public T getDato(){
        return dato;
    }
    public void setDato(T dato){
        this.dato=dato;
    }

    public Nodo<T> getSiguiente(){
        return siguiente;
    }
    public void setSiguiente(Nodo<T> siguiente){
        this.siguiente=siguiente;
    }

    public Nodo<T> getAnterior(){
        return anterior;
    }
    public void setAnterior(Nodo<T> anterior){
        this.anterior=anterior;
    }
}