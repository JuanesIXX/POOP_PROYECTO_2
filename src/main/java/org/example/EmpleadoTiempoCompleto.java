package org.example;

import java.time.LocalDate;

public class EmpleadoTiempoCompleto extends Empleado {
    private double beneficios;

    // Constructor principal
    public EmpleadoTiempoCompleto(int id, String nombre, String apellido, String cedula,
                                  LocalDate fechaIngreso, double salarioBase) {
        super(id, nombre, apellido, cedula, fechaIngreso, salarioBase);
        this.beneficios = 0.0;
    }

    // Constructor con beneficios
    public EmpleadoTiempoCompleto(int id, String nombre, String apellido, String cedula,
                                  LocalDate fechaIngreso, double salarioBase, double beneficios) {
        super(id, nombre, apellido, cedula, fechaIngreso, salarioBase);
        this.beneficios = beneficios;
    }

    public double getBeneficios() {
        return beneficios;
    }

    public void setBeneficios(double beneficios) {
        this.beneficios = beneficios;
    }

    @Override
    public double calcularSalario() {
        return getSalarioBase() + beneficios;
    }

    @Override
    public String getTipoEmpleado() {
        return "Tiempo Completo";
    }

    @Override
    public String toString() {
        return String.format("Empleado Tiempo Completo: %s %s (ID: %d) - Salario: %.2f - Beneficios: %.2f",
                getNombre(), getApellido(), getId(), getSalarioBase(), beneficios);
    }
}