package org.example;

import java.util.ArrayList;
import java.util.List;

//Cola simple
public class Cola<T>{

    //Frente de la cola (primer elemento en salir)
    private Nodo<T> frente;
    //Final de la cola (último elemento en entrar)
    private Nodo<T> final_;
    //Número de elementos en la cola.
    private int tamanio;

    //Inicializa una cola vacía.
    public Cola(){
        this.frente=null;
        this.final_=null;
        this.tamanio=0;
    }

    //Agrega un elemento al final
    public void encolar(T elemento){
        Nodo<T> nuevoNodo = new Nodo<>(elemento);

        if (estaVacia()){
            frente=nuevoNodo;
            final_=nuevoNodo;
        } else{
            final_.siguiente=nuevoNodo;
            final_=nuevoNodo;
        }
        tamanio++;
    }

    //Elimina y retorna (desencolar) el dato de frente de la cola
    public T desencolar() {

        if (estaVacia()) {
            return null;
        }
        T dato=frente.dato;
        frente =frente.siguiente;

        if (frente==null){
            final_=null;
        }
        tamanio--;
        return dato;
    }

    //Retorna (sin REMOVER) el elemento del frente de la cola.
    public T verFrente(){
        if (estaVacia()) return null;
        return frente.dato;
    }

    //Indica si la cola está vacía.
    public boolean estaVacia(){
        return tamanio == 0;
    }

    //regresa el número de elementos en la cola.
    public int tamanio() {
        return tamanio;
    }

    //Vacía la cola, eliminando todos los elementos.
    public void limpiar() {
        frente=null;
        final_=null;
        tamanio=0;
    }

    //Retorna una lista sin modificar la cola para la GUI.
    public List<T> aLista(){
        List<T> lista=new ArrayList<>();
        Nodo<T> actual=frente;

        while (actual != null) {
            lista.add(actual.dato);
            actual=actual.siguiente;
        }
        return lista;
    }

    // Es la representación en texto de la cola (frente → final).
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("[");
        Nodo<T> actual = frente;

        while (actual != null){
            sb.append(actual.dato);
            if (actual.siguiente != null) sb.append(", ");
            actual = actual.siguiente;
        }
        sb.append("]");
        return sb.toString();
    }
}