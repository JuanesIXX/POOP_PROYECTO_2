
package org.example;

import java.time.LocalDate;

public abstract class Empleado {
    private int id;
    private String nombre;
    private String apellido;
    private String cedulaEncriptado;  //  criptado
    private LocalDate fechaContratacion;
    private double salarioBase;
    protected Departamento departamento;

    public Empleado(int id, String nombre, String apellido, String cedula,
                    LocalDate fechaContratacion, double salarioBase) {

        //   NUEVAS VALIDACIONES DE SEGURIDAD
        if (id <= 0) {
            throw new IllegalArgumentException("ID debe ser positivo");
        }

        if (!SeguridadUtil.validarcedula(cedula)) {
            throw new IllegalArgumentException("Formato de cedula inválido (debe ser 10 dígitos  )");
        }

        if (!SeguridadUtil.validarSalario(salarioBase)) {
            throw new IllegalArgumentException("Salario debe ser positivo y menor a 999,999");
        }

        this.id = id;
        this.nombre = SeguridadUtil.validarInput(nombre);    // ✅ Input sanitizado
        this.apellido = SeguridadUtil.validarInput(apellido); // ✅ Input sanitizado
        this.fechaContratacion = fechaContratacion;
        this.salarioBase = salarioBase;

        // criptado
        setcedula(cedula);
    }

    // Métodos abstract
    public abstract double calcularSalario();
    public abstract String getTipoEmpleado();

    //  GETTERS SEGUROS
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    /**
     * ✅
     */
    public String getcedula() {
        return SeguridadUtil.desencriptar(cedulaEncriptado);
    }

    /**
     * criptado pt2
     */
    public String getcedulaEnmascarado() {
        String cedula = getcedula();
        return SeguridadUtil.enmascararcedula(cedula);
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }

    public double getSalarioBase() {
        return salarioBase;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    // SETTERS CO seguridad
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre no puede ser nulo o vacío");
        }
        this.nombre = SeguridadUtil.validarInput(nombre);
    }

    public void setApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("Apellido no puede ser nulo o vacío");
        }
        this.apellido = SeguridadUtil.validarInput(apellido);
    }

    /**
     * ✅
     */
    public void setcedula(String cedula) {
        if (!SeguridadUtil.validarcedula(cedula)) {
            throw new IllegalArgumentException("Formato de Cedula inválido (  )");
        }
        this.cedulaEncriptado = SeguridadUtil.encriptar(cedula);
    }

    public void setFechaContratacion(LocalDate fechaContratacion) {
        if (fechaContratacion == null) {
            throw new IllegalArgumentException("Fecha de contratación no puede ser nula");
        }
        if (fechaContratacion.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Fecha de contratación no puede ser futura");
        }
        this.fechaContratacion = fechaContratacion;
    }

    public void setSalarioBase(double salarioBase) {
        if (!SeguridadUtil.validarSalario(salarioBase)) {
            throw new IllegalArgumentException("Salario debe ser positivo y menor a 999,999");
        }
        this.salarioBase = salarioBase;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    //  ----------------------
    @Override
    public String toString() {
        return "Empleado{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", cedula='" + getcedulaEnmascarado() + '\'' +  //
                ", fechaContratacion=" + fechaContratacion +
                ", salarioBase=" + salarioBase +
                ", tipo='" + getTipoEmpleado() + '\'' +
                ", departamento=" + (departamento != null ? departamento.getNombre() : "No asignado") +
                '}';
    }

    /**
     *
     */
    public String toStringCompleto() {
        return "Empleado{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", cedula='" + getcedula() + '\'' +  //
                ", fechaContratacion=" + fechaContratacion +
                ", salarioBase=" + salarioBase +
                ", tipo='" + getTipoEmpleado() + '\'' +
                ", departamento=" + (departamento != null ? departamento.getNombre() : "No asignado") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Empleado)) return false;
        Empleado empleado = (Empleado) o;
        return id == empleado.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}