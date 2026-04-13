package org.example;

// Contiene las estadísticas finales de una simulación completa
public class Estadisticas {

    // Total de piezas que llegaron a Terminados
    private final int totalTerminados;

    // Promedio de piezas terminadas por ronda
    private final double promedioThroughput;

    // Número total de rondas ejecutadas
    private final int totalRondas;

    // WIP restante al finalizar
    private final int wipRestante;

    // Total procesado por cada estación (índice = índice de estación)
    private final int[] totalesPorEstacion;


    // Constructor
    public Estadisticas(int totalTerminados, int totalRondas,
                        int wipRestante, int[] totalesPorEstacion) {

        this.totalTerminados = totalTerminados;
        this.totalRondas = totalRondas;
        this.wipRestante = wipRestante;
        this.totalesPorEstacion = totalesPorEstacion.clone();

        this.promedioThroughput = (totalRondas > 0)
                ? (double) totalTerminados / totalRondas
                : 0.0;
    }


    // Getters
    public int getTotalTerminados() {
        return totalTerminados;
    }

    public double getPromedioThroughput() {
        return promedioThroughput;
    }

    public int getTotalRondas() {
        return totalRondas;
    }

    public int getWipRestante() {
        return wipRestante;
    }

    public int[] getTotalesPorEstacion() {
        return totalesPorEstacion.clone();
    }


    // Resumen legible de las estadísticas
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("========== ESTADÍSTICAS FINALES ==========\n");
        sb.append(String.format("  Rondas ejecutadas   : %d%n", totalRondas));
        sb.append(String.format("  Piezas terminadas   : %d%n", totalTerminados));
        sb.append(String.format("  Throughput promedio : %.2f piezas/ronda%n", promedioThroughput));
        sb.append(String.format("  WIP restante        : %d%n", wipRestante));
        sb.append("  Procesado por estación:\n");

        char nombre = 'A';

        for (int t : totalesPorEstacion) {
            sb.append(String.format("    Estación %c: %d%n", nombre++, t));
        }

        sb.append("==========================================");

        return sb.toString();
    }

}