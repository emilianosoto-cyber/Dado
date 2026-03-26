package org.example;

//Registro de los eventros de cada ronda. Almacena los lazamientos, estado de las dolas y las pierzas termunadas
public class RegistroRonda {

    //Número de ronda
    private final int numeroRonda;
    //Resultados de los dados (índice = índice de estación)
    private final int[] dados;
    //Tamaño de la cola de cada estación al FINAL de la ronda
    private final int[] tamaniosCola;
    //Piezas que llegaron a Terminados en una ronda específica
    private final int piezasTerminadasEstRonda;
    //otal acumulado de piezas en Terminados al final de una ronda
    private final int totalTerminados;
    //Tamaño de la cola de entrada al final de la ronda
    private final int tamanioColaEntrada;

    //constructor
    public RegistroRonda(int numeroRonda, int[] dados, int[] tamaniosCola, int piezasTerminadasEstRonda, int totalTerminados, int tamanioColaEntrada){
        this.numeroRonda=numeroRonda;
        this.dados=dados.clone();
        this.tamaniosCola=tamaniosCola.clone();
        this.piezasTerminadasEstRonda=piezasTerminadasEstRonda;
        this.totalTerminados=totalTerminados;
        this.tamanioColaEntrada=tamanioColaEntrada;
    }

    public int   getNumeroRonda(){
        return numeroRonda;
    }
    public int[] getDados(){
        return dados.clone();
    }
    public int[] getTamaniosCola(){
        return tamaniosCola.clone();
    }
    public int   getPiezasTerminadasEstRonda(){
        return piezasTerminadasEstRonda;
    }
    public int   getTotalTerminados(){
        return totalTerminados;
    }
    public int   getTamanioColaEntrada(){
        return tamanioColaEntrada;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Ronda %2d | Dados: [", numeroRonda));

        for (int i = 0; i < dados.length; i++){
            sb.append(dados[i]);
            if (i < dados.length - 1) sb.append(", ");
        }
        sb.append(String.format("] | Colas: [Entrada:%d", tamanioColaEntrada));
        char nombre = 'A';

        for (int t : tamaniosCola){
            sb.append(String.format(", %c:%d", nombre++, t));
        }

        sb.append(String.format("] | Terminadas esta ronda: %d | Total: %d",
                piezasTerminadasEstRonda, totalTerminados));
        return sb.toString();
    }
}