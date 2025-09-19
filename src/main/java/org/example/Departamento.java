package org.example;

import java.util.ArrayList;
import java.util.List;

public class Departamento {
    private int id;
    private String nombre;
    private String descripcion;
    private Empleado supervisor;
    private List<Empleado> empleados;

    public Departamento(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.empleados = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Empleado getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Empleado supervisor) {
        this.supervisor = supervisor;
    }

    public List<Empleado> getEmpleados() {
        return new ArrayList<>(empleados);
    }

    public void agregarEmpleado(Empleado empleado) {
        if (!empleados.contains(empleado)) {
            empleados.add(empleado);
            empleado.setDepartamento(this);
        }
    }

    public int getCantidadEmpleados() {
        return empleados.size();
    }

    @Override
    public String toString() {
        return String.format("Departamento: %s (ID: %d) - Supervisor: %s - Empleados: %d",
                nombre, id,
                supervisor != null ? supervisor.getNombre() : "No asignado",
                empleados.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Departamento that = (Departamento) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}