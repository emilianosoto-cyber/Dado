package org.example;

// Cola doble
public class Cola<T>{
    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamanio;
    public Cola(){}

    // Inserta por el inicio
    public void insertarInicio(T dato){
        Nodo<T> n=new Nodo<>(dato);
        if(tamanio==0){frente=fin=n;}
        else{
            n.setSiguiente(frente);
            frente.setAnterior(n);
            frente=n;
        }
        tamanio++;
    }

    // Inserta por el final
    public void insertarFinal(T dato){
        Nodo<T> n=new Nodo<>(dato);
        if(tamanio==0){frente=fin=n;}
        else{
            fin.setSiguiente(n);
            n.setAnterior(fin);
            fin=n;
        }
        tamanio++;
    }

    // Elimina por el inicio
    public T eliminarInicio(){
        if(tamanio==0) return null;
        T d=frente.getDato();
        frente=frente.getSiguiente();
        tamanio--;
        if(frente==null) fin=null;
        else frente.setAnterior(null);
        return d;
    }

    // Elimina por el final
    public T eliminarFinal(){
        if(tamanio==0) return null;
        T d=fin.getDato();
        fin=fin.getAnterior();
        tamanio--;
        if(fin==null) frente=null;
        else fin.setSiguiente(null);
        return d;
    }

    // Mira el inicio sin eliminar
    public T inicio(){
        return frente==null?null:frente.getDato();
    }

    // Mira el final sin eliminar
    public T finalCola(){
        return fin==null?null:fin.getDato();
    }

    // Vacía la cola
    public void limpiar(){
        frente=null;
        fin=null;tamanio=0;
    }

    // Regresa el tamaño
    public int tamanio(){
        return tamanio;
    }

    // True si está vacía
    public boolean estaVacia(){
        return tamanio==0;
    }

    // Alias para tu Estacion (no rompe tu código)
    public void agregarFinal(T dato){
        insertarFinal(dato);
    }
}