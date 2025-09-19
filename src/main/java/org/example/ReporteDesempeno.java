package org.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteDesempeno {
    private int id;
    private Empleado empleado;
    private LocalDate fecha;
    private List<Evaluacion> evaluaciones;
    private String comentarioFinal;
    private double puntuacionPromedio;
    private String periodo;

    public ReporteDesempeno(int id, Empleado empleado, LocalDate fecha, String periodo) {
        this.id = id;
        this.empleado = empleado;
        this.fecha = fecha;
        this.periodo = periodo;
        this.evaluaciones = new ArrayList<>();
    }

    public void agregarEvaluacion(Evaluacion evaluacion) {
        evaluaciones.add(evaluacion);
        calcularPuntuacionPromedio();
    }

    private void calcularPuntuacionPromedio() {
        if (!evaluaciones.isEmpty()) {
            puntuacionPromedio = evaluaciones.stream()
                    .mapToInt(Evaluacion::getPuntuacion)
                    .average()
                    .orElse(0.0);
        }
    }

    public String generarResumenReporte() {
        StringBuilder resumen = new StringBuilder();
        resumen.append("Reporte de Desempeño\n");
        resumen.append("------------------\n");
        resumen.append("Empleado: ").append(empleado.getNombre()).append(" ").append(empleado.getApellido()).append("\n");
        resumen.append("Fecha: ").append(fecha).append("\n");
        resumen.append("Periodo: ").append(periodo).append("\n");
        resumen.append("Puntuación Promedio: ").append(String.format("%.2f", puntuacionPromedio)).append("\n");
        resumen.append("Evaluaciones:\n");

        for (Evaluacion eval : evaluaciones) {
            resumen.append("- ").append(eval.getCriterio())
                    .append(": ").append(eval.getPuntuacion())
                    .append("/10 - ").append(eval.getComentario()).append("\n");
        }

        resumen.append("Comentario Final: ").append(comentarioFinal);
        return resumen.toString();
    }

    // Getters
    public int getId() { return id; }
    public Empleado getEmpleado() { return empleado; }
    public LocalDate getFecha() { return fecha; }
    public List<Evaluacion> getEvaluaciones() { return new ArrayList<>(evaluaciones); }
    public double getPuntuacionPromedio() { return puntuacionPromedio; }
    public String getPeriodo() { return periodo; }
    public String getComentarioFinal() { return comentarioFinal; }

    public void setComentarioFinal(String comentarioFinal) {
        this.comentarioFinal = comentarioFinal;
    }
}