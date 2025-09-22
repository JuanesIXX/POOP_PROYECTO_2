package org.example;

import java.time.LocalDate;

public class EvaluacionDesempeno {
    private int id;
    private Empleado empleado;
    private LocalDate fecha;
    private int puntuacion;
    private String comentarios;

    public EvaluacionDesempeno(int id, Empleado empleado, LocalDate fecha,
                               int puntuacion, String comentarios) {
        this.id = id;
        this.empleado = empleado;
        this.fecha = fecha;
        this.puntuacion = puntuacion;
        this.comentarios = comentarios;
    }


}