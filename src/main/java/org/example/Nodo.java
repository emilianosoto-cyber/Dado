package org.example;

//Nodo para la cola contiene el dato.
public class Nodo<T>{

    //Dato almacenado en el nodo
    T dato;
    //Referencia al siguiente nodo
    Nodo<T> siguiente;

    //Constructor que inicializa el nodo con el dato proporcionado.
    public Nodo(T dato){
        this.dato=dato;
        this.siguiente=null;
    }
}