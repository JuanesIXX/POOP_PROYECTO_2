package org.example;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneradorReportes {
    private final GestorRRHH gestorRRHH;

    public GeneradorReportes(GestorRRHH gestorRRHH) {
        this.gestorRRHH = gestorRRHH;
    }

    public ReporteDesempeno generarReporteIndividual(Empleado empleado, String periodo) {
        int nuevoId = gestorRRHH.obtenerSiguienteIdReporte();
        ReporteDesempeno reporte = new ReporteDesempeno(nuevoId, empleado, LocalDate.now(), periodo);

        // Agregar evaluaciones estándar
        reporte.agregarEvaluacion(new Evaluacion(1, "Productividad", 0, ""));
        reporte.agregarEvaluacion(new Evaluacion(2, "Trabajo en Equipo", 0, ""));
        reporte.agregarEvaluacion(new Evaluacion(3, "Puntualidad", 0, ""));
        reporte.agregarEvaluacion(new Evaluacion(4, "Calidad de Trabajo", 0, ""));

        return reporte;
    }

    public String generarReporteDepartamento(Departamento departamento, String periodo) {
        List<ReporteDesempeno> reportesDepartamento = gestorRRHH.obtenerReportesPorDepartamento(departamento.getId());

        StringBuilder reporteDepartamental = new StringBuilder();
        reporteDepartamental.append("Reporte Departamental: ").append(departamento.getNombre()).append("\n");
        reporteDepartamental.append("Periodo: ").append(periodo).append("\n");
        reporteDepartamental.append("Fecha: ").append(LocalDate.now()).append("\n\n");

        // Estadísticas generales
        double promedioGeneral = reportesDepartamento.stream()
                .mapToDouble(ReporteDesempeno::getPuntuacionPromedio)
                .average()
                .orElse(0.0);

        reporteDepartamental.append("Estadísticas Generales:\n");
        reporteDepartamental.append("- Número de empleados: ").append(departamento.getCantidadEmpleados()).append("\n");
        reporteDepartamental.append("- Promedio general: ").append(String.format("%.2f", promedioGeneral)).append("\n\n");

        // Resumen por empleado
        reporteDepartamental.append("Resumen por Empleado:\n");
        for (Empleado empleado : departamento.getEmpleados()) {
            List<ReporteDesempeno> reportesEmpleado = gestorRRHH.obtenerReportesPorEmpleado(empleado.getId());
            if (!reportesEmpleado.isEmpty()) {
                double promedioEmpleado = reportesEmpleado.stream()
                        .mapToDouble(ReporteDesempeno::getPuntuacionPromedio)
                        .average()
                        .orElse(0.0);

                reporteDepartamental.append("- ").append(empleado.getNombre())
                        .append(" ").append(empleado.getApellido())
                        .append(": ").append(String.format("%.2f", promedioEmpleado))
                        .append("\n");
            }
        }

        return reporteDepartamental.toString();
    }
}