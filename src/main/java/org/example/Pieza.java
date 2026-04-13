package org.example;

// Representa una pieza que fluye por la línea de producción
public class Pieza {

    private final int id;

    // Constructor — cada pieza recibe un ID único al entrar al sistema
    public Pieza(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Pieza#" + id;
    }
}