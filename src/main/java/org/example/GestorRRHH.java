package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GestorRRHH {
    private Map<Integer, Empleado> empleados;
    private Map<Integer, Departamento> departamentos;
    private List<ReporteDesempeno> reportes;
    private int siguienteIdReporte;
    private int siguienteIdEvaluacion; // ✅ NUEVO: Contador para evaluaciones

    //   VARIABLES DE SEGURIDAD
    private String usuarioActual = "SYSTEM";
    private boolean modoSeguro = true;
    private Map<String, Integer> intentosOperacion = new HashMap<>(); // Control de intentos
    private List<String> logAuditoria = new ArrayList<>(); // Log de operaciones

    public GestorRRHH() {
        this.empleados = new HashMap<>();
        this.departamentos = new HashMap<>();
        this.reportes = new ArrayList<>();
        this.siguienteIdReporte = 1;
        this.siguienteIdEvaluacion = 1; // ✅ NUEVO: Inicializar contador de evaluaciones

        //  Log inicial
        registrarOperacion("SYSTEM", "Sistema iniciado", true);
    }

    //  MÉTODOS DE CONTROL DE SEGURIDAD

    /**
     * Establece el usuario actual para auditoría
     */
    public void setUsuarioActual(String usuario) {
        if (usuario == null || usuario.trim().isEmpty()) {
            this.usuarioActual = "SYSTEM";
        } else {
            this.usuarioActual = SeguridadUtil.validarInput(usuario);
        }
        registrarOperacion(this.usuarioActual, "Cambio de usuario activo", true);
    }

    /**
     * Activa/desactiva el modo seguro
     */
    public void setModoSeguro(boolean modoSeguro) {
        this.modoSeguro = modoSeguro;
        registrarOperacion(usuarioActual, "Modo seguro " + (modoSeguro ? "activado" : "desactivado"), true);
    }

    /**
     * Verifica si el usuario tiene permisos para la operación
     */
    private boolean verificarPermisos(String operacion) {
        if (!modoSeguro) {
            return true; // Si modo seguro desactivado, permitir todo
        }

        // Control básico de permisos por rol
        return switch (usuarioActual.toLowerCase()) {
            case "admin", "system" -> true; // Admin y system tienen todos los permisos
            case "hr" -> operacion.contains("empleado") || operacion.contains("reporte") || operacion.contains("evaluacion");
            case "manager" -> operacion.contains("departamento") || operacion.contains("supervisor") || operacion.contains("consultar");
            case "empleado" -> operacion.contains("consultar") || operacion.contains("ver");
            default -> false;
        };
    }

    /**
     * Registra operaciones para auditoría
     */
    private void registrarOperacion(String usuario, String operacion, boolean exitosa) {
        String timestamp = LocalDateTime.now().toString();
        String entrada = String.format("[%s] Usuario: %s | Operación: %s | Estado: %s",
                timestamp, usuario, operacion,
                exitosa ? "EXITOSA" : "FALLIDA");
        logAuditoria.add(entrada);

        // Mantener solo los últimos 100 logs para evitar uso excesivo de memoria
        if (logAuditoria.size() > 100) {
            logAuditoria.remove(0);
        }
    }

    /**
     * Lanza excepción de seguridad si no hay permisos
     */
    private void validarAcceso(String operacion) {
        if (!verificarPermisos(operacion)) {
            registrarOperacion(usuarioActual, "ACCESO DENEGADO: " + operacion, false);
            throw new SecurityException("Acceso denegado para " + usuarioActual + " en operación: " + operacion);
        }
    }

    //

    /**
     * Agregar departamento con validaciones de seguridad
     */
    public void agregarDepartamento(Departamento departamento) {
        validarAcceso("crear_departamento");

        if (departamento == null) {
            throw new IllegalArgumentException("Departamento no puede ser null");
        }

        if (departamentos.containsKey(departamento.getId())) {
            registrarOperacion(usuarioActual, "Intento crear departamento duplicado ID: " + departamento.getId(), false);
            throw new IllegalArgumentException("Ya existe un departamento con ID: " + departamento.getId());
        }

        departamentos.put(departamento.getId(), departamento);
        registrarOperacion(usuarioActual, "Departamento creado: " + departamento.getNombre() + " (ID: " + departamento.getId() + ")", true);
    }

    /**
     * Contratar empleado con validaciones de seguridad
     */
    public void contratarEmpleado(Empleado empleado) {
        validarAcceso("contratar_empleado");

        if (empleado == null) {
            throw new IllegalArgumentException("Empleado no puede ser null");
        }

        if (empleados.containsKey(empleado.getId())) {
            registrarOperacion(usuarioActual, "Intento contratar empleado duplicado ID: " + empleado.getId(), false);
            throw new IllegalArgumentException("Ya existe un empleado con ID: " + empleado.getId());
        }

        //   Validación adicional de datos sensibles
        try {
            //   validación de integridad  desencriptable
            String cedulaTest = empleado.getcedula();
            if (cedulaTest == null || cedulaTest.trim().isEmpty()) {
                throw new IllegalArgumentException("cedula del empleado es inválido");
            }
        } catch (Exception e) {
            registrarOperacion(usuarioActual, "Error validando datos empleado ID: " + empleado.getId(), false);
            throw new RuntimeException("Error en validación de datos sensibles: " + e.getMessage());
        }

        empleados.put(empleado.getId(), empleado);
        registrarOperacion(usuarioActual, "Empleado contratado: " + empleado.getNombre() + " " + empleado.getApellido() + " (ID: " + empleado.getId() + ")", true);
    }

    /**
     * Despedir empleado con auditoría de seguridad
     */
    public void despedirEmpleado(int id) {
        validarAcceso("despedir_empleado");

        Empleado empleado = empleados.get(id);
        if (empleado == null) {
            registrarOperacion(usuarioActual, "Intento despedir empleado inexistente ID: " + id, false);
            throw new IllegalArgumentException("No existe empleado con ID: " + id);
        }

        // Remover del departamento si existe
        if (empleado.getDepartamento() != null) {
            empleado.getDepartamento().getEmpleados().remove(empleado);
        }

        empleados.remove(id);
        registrarOperacion(usuarioActual, "Empleado despedido: " + empleado.getNombre() + " " + empleado.getApellido() + " (ID: " + id + ")", true);
    }

    /**
     * Eliminar departamento con validaciones
     */
    public void eliminarDepartamento(int id) {
        validarAcceso("eliminar_departamento");

        Departamento departamento = departamentos.get(id);
        if (departamento == null) {
            registrarOperacion(usuarioActual, "Intento eliminar departamento inexistente ID: " + id, false);
            throw new IllegalArgumentException("No existe departamento con ID: " + id);
        }

        // Verificar que no tenga empleados asignados
        if (!departamento.getEmpleados().isEmpty()) {
            registrarOperacion(usuarioActual, "Intento eliminar departamento con empleados ID: " + id, false);
            throw new IllegalStateException("No se puede eliminar departamento con empleados asignados");
        }

        departamentos.remove(id);
        registrarOperacion(usuarioActual, "Departamento eliminado: " + departamento.getNombre() + " (ID: " + id + ")", true);
    }

    /**
     * Asignar empleado a departamento
     */
    public void asignarEmpleadoADepartamento(int idEmpleado, int idDepartamento) {
        validarAcceso("asignar_empleado");

        Empleado empleado = empleados.get(idEmpleado);
        Departamento departamento = departamentos.get(idDepartamento);

        if (empleado == null) {
            registrarOperacion(usuarioActual, "Intento asignar empleado inexistente ID: " + idEmpleado, false);
            throw new IllegalArgumentException("No existe empleado con ID: " + idEmpleado);
        }

        if (departamento == null) {
            registrarOperacion(usuarioActual, "Intento asignar a departamento inexistente ID: " + idDepartamento, false);
            throw new IllegalArgumentException("No existe departamento con ID: " + idDepartamento);
        }

        // Remover del departamento anterior si existe
        if (empleado.getDepartamento() != null) {
            empleado.getDepartamento().getEmpleados().remove(empleado);
        }

        departamento.agregarEmpleado(empleado);
        registrarOperacion(usuarioActual, "Empleado " + empleado.getNombre() + " asignado a " + departamento.getNombre(), true);
    }

    /**
     * Asignar supervisor
     */
    public void asignarSupervisor(int idDepartamento, int idSupervisor) {
        validarAcceso("asignar_supervisor");

        Departamento departamento = departamentos.get(idDepartamento);
        Empleado supervisor = empleados.get(idSupervisor);

        if (departamento == null) {
            registrarOperacion(usuarioActual, "Intento asignar supervisor a departamento inexistente ID: " + idDepartamento, false);
            throw new IllegalArgumentException("No existe departamento con ID: " + idDepartamento);
        }

        if (supervisor == null) {
            registrarOperacion(usuarioActual, "Intento asignar supervisor inexistente ID: " + idSupervisor, false);
            throw new IllegalArgumentException("No existe empleado con ID: " + idSupervisor);
        }

        departamento.setSupervisor(supervisor);
        registrarOperacion(usuarioActual, "Supervisor " + supervisor.getNombre() + " asignado a " + departamento.getNombre(), true);
    }

    //   MÉTODOS DE C

    /**
     * Buscar empleado (con control de acceso)
     */
    public Empleado buscarEmpleado(int id) {
        validarAcceso("consultar_empleado");

        Empleado empleado = empleados.get(id);
        if (empleado != null) {
            registrarOperacion(usuarioActual, "Consulta empleado ID: " + id, true);
        }
        return empleado;
    }

    /**
     * Buscar departamento (con control de acceso)
     */
    public Departamento buscarDepartamento(int id) {
        validarAcceso("consultar_departamento");

        Departamento departamento = departamentos.get(id);
        if (departamento != null) {
            registrarOperacion(usuarioActual, "Consulta departamento ID: " + id, true);
        }
        return departamento;
    }

    /**
     * Obtener empleados (con filtro de seguridad)
     */
    public List<Empleado> obtenerEmpleados() {
        validarAcceso("ver_empleados");

        registrarOperacion(usuarioActual, "Consulta lista de empleados (" + empleados.size() + " registros)", true);
        return new ArrayList<>(empleados.values());
    }

    /**
     * Obtener departamentos (con control de acceso)
     */
    public List<Departamento> obtenerDepartamentos() {
        validarAcceso("ver_departamentos");

        registrarOperacion(usuarioActual, "Consulta lista de departamentos (" + departamentos.size() + " registros)", true);
        return new ArrayList<>(departamentos.values());
    }

    //   MÉTODOS DE REPORTES CON SEGURIDAD

    public void guardarReporte(ReporteDesempeno reporte) {
        validarAcceso("guardar_reporte");

        if (reporte == null) {
            throw new IllegalArgumentException("Reporte no puede ser null");
        }

        reportes.add(reporte);
        registrarOperacion(usuarioActual, "Reporte guardado para empleado ID: " + reporte.getEmpleado().getId(), true);
    }

    public List<ReporteDesempeno> obtenerReportesPorEmpleado(int idEmpleado) {
        validarAcceso("consultar_reportes");

        List<ReporteDesempeno> resultado = reportes.stream()
                .filter(r -> r.getEmpleado().getId() == idEmpleado)
                .collect(Collectors.toList());

        registrarOperacion(usuarioActual, "Consulta reportes empleado ID: " + idEmpleado + " (" + resultado.size() + " encontrados)", true);
        return resultado;
    }

    public List<ReporteDesempeno> obtenerReportesPorDepartamento(int idDepartamento) {
        validarAcceso("consultar_reportes");

        List<ReporteDesempeno> resultado = reportes.stream()
                .filter(r -> r.getEmpleado().getDepartamento() != null &&
                        r.getEmpleado().getDepartamento().getId() == idDepartamento)
                .collect(Collectors.toList());

        registrarOperacion(usuarioActual, "Consulta reportes departamento ID: " + idDepartamento + " (" + resultado.size() + " encontrados)", true);
        return resultado;
    }

    public int obtenerSiguienteIdReporte() {
        validarAcceso("generar_reporte");
        return siguienteIdReporte++;
    }

    //   ID EVLAUACION
    public int obtenerSiguienteIdEvaluacion() {
        validarAcceso("crear_evaluacion");

        int idGenerado = siguienteIdEvaluacion++;
        registrarOperacion(usuarioActual, "ID de evaluación generado: " + idGenerado, true);

        return idGenerado;
    }

    //  MÉTODOS DE SEGURIDAD Y AUDITORÍA

    /**
     * Obtiene el log de auditoría
     */
    public List<String> obtenerLogAuditoria() {
        if (!"admin".equals(usuarioActual.toLowerCase()) && !"system".equals(usuarioActual.toLowerCase())) {
            throw new SecurityException("Solo administradores pueden ver el log de auditoría");
        }

        return new ArrayList<>(logAuditoria);
    }

    /**
     *
     */
    public Map<String, Object> obtenerEstadisticasSeguridad() {
        validarAcceso("ver_estadisticas");

        Map<String, Object> stats = new HashMap<>();
        stats.put("empleados_registrados", empleados.size());
        stats.put("departamentos_registrados", departamentos.size());
        stats.put("reportes_generados", reportes.size());
        stats.put("modo_seguro_activo", modoSeguro);
        stats.put("usuario_actual", usuarioActual);
        stats.put("operaciones_auditadas", logAuditoria.size());
        stats.put("siguiente_id_evaluacion", siguienteIdEvaluacion); // ✅ NUEVO: Estadística de evaluaciones

        // Contar empleados con datos encriptados
        long empleadosConcedula = empleados.values().stream()
                .filter(e -> {
                    try {
                        return e.getcedula() != null && !e.getcedula().isEmpty();
                    } catch (Exception ex) {
                        return false;
                    }
                })
                .count();
        stats.put("empleados_con_datos_encriptados", empleadosConcedula);

        registrarOperacion(usuarioActual, "Consulta estadísticas de seguridad", true);
        return stats;
    }

    /**
     * Verifica la integridad de l
     */
    public boolean verificarIntegridadDatos() {
        if (!"admin".equals(usuarioActual.toLowerCase())) {
            throw new SecurityException("Solo administradores pueden verificar integridad de datos");
        }

        try {
            for (Empleado empleado : empleados.values()) {
                // Intentar
                String cedula = empleado.getcedula();
                if (cedula == null || cedula.trim().isEmpty()) {
                    registrarOperacion(usuarioActual, "Error integridad empleado ID: " + empleado.getId(), false);
                    return false;
                }
            }

            registrarOperacion(usuarioActual, "Verificación de integridad completada exitosamente", true);
            return true;

        } catch (Exception e) {
            registrarOperacion(usuarioActual, "Error en verificación de integridad: " + e.getMessage(), false);
            return false;
        }
    }

    /**
     * Limpia el log de audit
     */
    public void limpiarLogAuditoria() {
        if (!"admin".equals(usuarioActual.toLowerCase())) {
            throw new SecurityException("Solo administradores pueden limpiar el log");
        }

        int registrosEliminados = logAuditoria.size();
        logAuditoria.clear();
        registrarOperacion(usuarioActual, "Log de auditoría limpiado (" + registrosEliminados + " registros eliminados)", true);
    }
}