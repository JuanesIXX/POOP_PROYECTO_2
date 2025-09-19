package org.example;

import java.time.LocalDate;

public class EmpleadoTemporal extends Empleado {
    private LocalDate fechaFinContrato;
    private String tipoContrato;

    public EmpleadoTemporal(int id, String nombre, String apellido, String cedula,
                            LocalDate fechaContratacion, double salarioBase,
                            LocalDate fechaFinContrato, String tipoContrato) {
        super(id, nombre, apellido, cedula, fechaContratacion, salarioBase);
        this.fechaFinContrato = fechaFinContrato;
        this.tipoContrato = tipoContrato;
    }

    @Override
    public double calcularSalario() {
        return getSalarioBase(); // Los temporales no tienen bonificaciones
    }

    @Override
    public String getTipoEmpleado() {
        return "Temporal";
    }

    public LocalDate getFechaFinContrato() {
        return fechaFinContrato;
    }

    public void setFechaFinContrato(LocalDate fechaFinContrato) {
        this.fechaFinContrato = fechaFinContrato;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    @Override
    public String toString() {
        return super.toString() + " [Fin Contrato=" + fechaFinContrato +
                ", Tipo Contrato=" + tipoContrato + "]";
    }
}