package org.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private static GestorRRHH gestor;
    private static GeneradorReportes generador;
    private static Scanner scanner;

    // Variables de seguridad
    private static String usuarioActual = null;
    private static String rolUsuario = null;
    private static boolean sistemaSeguro = true;

    // Lista para almacenar evaluaciones
    private static List<Evaluacion> evaluacionesCreadas = new ArrayList<>();

    public static void main(String[] args) {
        inicializarSistema();

        if (iniciarSesion()) {
            mostrarMenuPrincipal();
        } else {
            System.out.println("Acceso denegado. El sistema se cerrará.");
        }

        scanner.close();
    }

    private static void inicializarSistema() {
        gestor = new GestorRRHH();
        generador = new GeneradorReportes(gestor);
        scanner = new Scanner(System.in);
        crearDatosDePrueba();
    }

    private static boolean iniciarSesion() {
        System.out.println("\nSISTEMA DE ACCESO SEGURO");
        System.out.println("Usuarios disponibles:");
        System.out.println("- admin (contraseña: admin123) - Acceso completo");
        System.out.println("- hr (contraseña: hr123) - Gestión de empleados");
        System.out.println("- manager (contraseña: manager123) - Solo departamentos");
        System.out.println("- empleado (contraseña: emp123) - Solo consulta\n");

        int intentos = 0;
        while (intentos < 3) {
            try {
                System.out.print("Usuario: ");
                String usuario = scanner.nextLine().trim();

                System.out.print("Contraseña: ");
                String password = scanner.nextLine().trim();

                if (autenticarUsuario(usuario, password)) {
                    usuarioActual = usuario;
                    System.out.println("Acceso concedido. Bienvenido " + usuario + " (" + rolUsuario + ")");

                    gestor.setUsuarioActual(usuarioActual);
                    gestor.setModoSeguro(sistemaSeguro);

                    return true;
                } else {
                    intentos++;
                    System.out.println("Credenciales incorrectas. Intentos restantes: " + (3 - intentos));
                }
            } catch (Exception e) {
                System.out.println("Error en el login: " + e.getMessage());
                intentos++;
            }
        }

        return false;
    }

    private static boolean autenticarUsuario(String usuario, String password) {
        return switch (usuario.toLowerCase()) {
            case "admin" -> {
                if ("admin123".equals(password)) {
                    rolUsuario = "Administrador";
                    yield true;
                }
                yield false;
            }
            case "hr" -> {
                if ("hr123".equals(password)) {
                    rolUsuario = "Recursos Humanos";
                    yield true;
                }
                yield false;
            }
            case "manager" -> {
                if ("manager123".equals(password)) {
                    rolUsuario = "Manager";
                    yield true;
                }
                yield false;
            }
            case "empleado" -> {
                if ("emp123".equals(password)) {
                    rolUsuario = "Empleado";
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    private static boolean tienePermiso(String operacion) {
        return switch (usuarioActual.toLowerCase()) {
            case "admin" -> true;
            case "hr" -> operacion.contains("empleado") || operacion.contains("evaluacion") || operacion.contains("reporte");
            case "manager" -> operacion.contains("departamento") || operacion.contains("supervisor") || operacion.contains("reporte");
            case "empleado" -> operacion.contains("ver") || operacion.contains("listar");
            default -> false;
        };
    }

    private static boolean verificarAcceso(String operacion, String mensaje) {
        if (!tienePermiso(operacion)) {
            System.out.println("Acceso denegado: " + mensaje);
            System.out.println("Su rol (" + rolUsuario + ") no tiene permisos para esta operación.");
            return false;
        }
        return true;
    }

    private static void mostrarMenuPrincipal() {
        while (true) {
            System.out.println("\nSistema de Gestión de RRHH - CompuWork");
            System.out.println("Usuario: " + usuarioActual + " (" + rolUsuario + ")");
            System.out.println("Modo seguro: " + (sistemaSeguro ? "Activado" : "Desactivado"));
            System.out.println("\nMENÚ PRINCIPAL:");
            System.out.println("1. Gestionar Departamentos");
            System.out.println("2. Gestionar Empleados");
            System.out.println("3. Gestionar Evaluaciones");
            System.out.println("4. Ver Reportes");
            System.out.println("5. Configuración de Seguridad");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1: menuDepartamentos(); break;
                    case 2: menuEmpleados(); break;
                    case 3: menuEvaluaciones(); break;
                    case 4: menuReportes(); break;
                    case 5: menuSeguridad(); break;
                    case 0:
                        System.out.println("Cerrando sesión de " + usuarioActual);
                        System.out.println("¡Gracias por usar el sistema!");
                        return;
                    default:
                        System.out.println("Opción no válida");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un número válido");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void menuSeguridad() {
        if (!verificarAcceso("admin", "Solo administradores pueden cambiar configuración")) {
            return;
        }

        while (true) {
            System.out.println("\nConfiguración de Seguridad");
            System.out.println("1. " + (sistemaSeguro ? "Desactivar" : "Activar") + " modo seguro");
            System.out.println("2. Ver estadísticas de seguridad");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1:
                        sistemaSeguro = !sistemaSeguro;
                        gestor.setModoSeguro(sistemaSeguro);
                        System.out.println("Modo seguro " + (sistemaSeguro ? "activado" : "desactivado"));
                        break;
                    case 2:
                        mostrarEstadisticasSeguridad();
                        break;
                    case 0: return;
                    default: System.out.println("Opción no válida");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void mostrarEstadisticasSeguridad() {
        System.out.println("\nEstadísticas de Seguridad");
        System.out.println("Modo seguro: " + (sistemaSeguro ? "Activado" : "Desactivado"));
        System.out.println("Usuario actual: " + usuarioActual);
        System.out.println("Rol: " + rolUsuario);
        System.out.println("Empleados con datos encriptados: " + gestor.obtenerEmpleados().size());
        System.out.println("Departamentos registrados: " + gestor.obtenerDepartamentos().size());
        System.out.println("Evaluaciones creadas: " + evaluacionesCreadas.size());
    }

    private static void menuDepartamentos() {
        while (true) {
            System.out.println("\nGestión de Departamentos");
            System.out.println("1. Crear Departamento");
            System.out.println("2. Listar Departamentos");
            System.out.println("3. Asignar Supervisor");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1: crearDepartamento(); break;
                    case 2: listarDepartamentos(); break;
                    case 3: asignarSupervisor(); break;
                    case 0: return;
                    default: System.out.println("Opción no válida");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void menuEmpleados() {
        while (true) {
            System.out.println("\nGestión de Empleados");
            System.out.println("1. Contratar Empleado Tiempo Completo");
            System.out.println("2. Contratar Empleado Temporal");
            System.out.println("3. Listar Empleados");
            System.out.println("4. Asignar Empleado a Departamento");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1: contratarEmpleadoTiempoCompleto(); break;
                    case 2: contratarEmpleadoTemporal(); break;
                    case 3: listarEmpleados(); break;
                    case 4: asignarEmpleadoADepartamento(); break;
                    case 0: return;
                    default: System.out.println("Opción no válida");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void menuEvaluaciones() {
        while (true) {
            System.out.println("\nGestión de Evaluaciones");
            System.out.println("1. Crear Evaluación");
            System.out.println("2. Ver Evaluaciones por Empleado");
            System.out.println("3. Listar Todas las Evaluaciones");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1: crearEvaluacion(); break;
                    case 2: verEvaluacionesPorEmpleado(); break;
                    case 3: listarTodasLasEvaluaciones(); break;
                    case 0: return;
                    default: System.out.println("Opción no válida");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void menuReportes() {
        while (true) {
            System.out.println("\nReportes");
            System.out.println("1. Ver Reporte Individual");
            System.out.println("2. Ver Reporte Departamental");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1: verReporteIndividual(); break;
                    case 2: verReporteDepartamental(); break;
                    case 0: return;
                    default: System.out.println("Opción no válida");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    //   SEGURIDAD
    private static void crearDepartamento() {
        if (!verificarAcceso("departamento", "No tiene permisos para crear departamentos")) {
            return;
        }

        try {
            System.out.print("ID del departamento: ");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Nombre del departamento: ");
            String nombre = scanner.nextLine();

            System.out.print("Descripción: ");
            String descripcion = scanner.nextLine();

            Departamento departamento = new Departamento(id, nombre, descripcion);
            gestor.agregarDepartamento(departamento);
            System.out.println("Departamento creado exitosamente");
        } catch (Exception e) {
            System.out.println("Error al crear departamento: " + e.getMessage());
        }
    }

    private static void listarDepartamentos() {
        if (!verificarAcceso("ver", "No tiene permisos para ver departamentos")) {
            return;
        }

        try {
            List<Departamento> departamentos = gestor.obtenerDepartamentos();
            if (departamentos.isEmpty()) {
                System.out.println("No hay departamentos registrados");
                return;
            }

            System.out.println("\nLista de Departamentos");
            for (Departamento depto : departamentos) {
                System.out.println(depto);
            }
        } catch (Exception e) {
            System.out.println("Error al listar departamentos: " + e.getMessage());
        }
    }

    private static void asignarSupervisor() {
        if (!verificarAcceso("supervisor", "No tiene permisos para asignar supervisores")) {
            return;
        }

        try {
            System.out.print("ID del departamento: ");
            int idDepto = Integer.parseInt(scanner.nextLine());

            System.out.print("ID del empleado supervisor: ");
            int idSupervisor = Integer.parseInt(scanner.nextLine());

            gestor.asignarSupervisor(idDepto, idSupervisor);
            System.out.println("Supervisor asignado exitosamente");
        } catch (Exception e) {
            System.out.println("Error al asignar supervisor: " + e.getMessage());
        }
    }

    private static void contratarEmpleadoTiempoCompleto() {
        if (!verificarAcceso("empleado", "No tiene permisos para contratar empleados")) {
            return;
        }

        try {
            System.out.print("ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();

            System.out.print("Apellido: ");
            String apellido = scanner.nextLine();

            System.out.print("Cedula (formato: 12345678): ");
            String cedula = scanner.nextLine();

            System.out.print("Salario base: ");
            double salario = Double.parseDouble(scanner.nextLine());

            EmpleadoTiempoCompleto empleado = new EmpleadoTiempoCompleto(
                    id, nombre, apellido, cedula, LocalDate.now(), salario
            );

            gestor.contratarEmpleado(empleado);
            System.out.println("Empleado contratado exitosamente");
            System.out.println("cedula encriptado automáticamente por seguridad");

        } catch (Exception e) {
            System.out.println("Error al contratar empleado: " + e.getMessage());
        }
    }

    private static void contratarEmpleadoTemporal() {
        if (!verificarAcceso("empleado", "No tiene permisos para contratar empleados")) {
            return;
        }

        try {
            System.out.print("ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();

            System.out.print("Apellido: ");
            String apellido = scanner.nextLine();

            System.out.print("cedula (formato: 12345678): ");
            String cedula = scanner.nextLine();

            System.out.print("Salario base: ");
            double salario = Double.parseDouble(scanner.nextLine());

            System.out.print("Fecha fin contrato (YYYY-MM-DD): ");
            LocalDate fechaFin = LocalDate.parse(scanner.nextLine());

            System.out.print("Tipo contrato: ");
            String tipoContrato = scanner.nextLine();

            EmpleadoTemporal empleado = new EmpleadoTemporal(
                    id, nombre, apellido, cedula, LocalDate.now(),
                    salario, fechaFin, tipoContrato
            );

            gestor.contratarEmpleado(empleado);
            System.out.println("Empleado temporal contratado exitosamente");
            System.out.println("cedula encriptado automáticamente por seguridad");

        } catch (Exception e) {
            System.out.println("Error al contratar empleado: " + e.getMessage());
        }
    }

    private static void listarEmpleados() {
        if (!verificarAcceso("ver", "No tiene permisos para ver empleados")) {
            return;
        }

        try {
            List<Empleado> empleados = gestor.obtenerEmpleados();
            if (empleados.isEmpty()) {
                System.out.println("No hay empleados registrados");
                return;
            }

            System.out.println("\nLista de Empleados");
            for (Empleado emp : empleados) {
                if ("admin".equals(usuarioActual) || "hr".equals(usuarioActual)) {
                    System.out.println(emp.toStringCompleto()); //
                } else {
                    System.out.println(emp.toString()); //  cedula oculto
                }
            }

            System.out.println("\nDatos sensibles protegidos según su nivel de acceso");

        } catch (Exception e) {
            System.out.println("Error al listar empleados: " + e.getMessage());
        }
    }

    private static void asignarEmpleadoADepartamento() {
        if (!verificarAcceso("empleado", "No tiene permisos para asignar empleados")) {
            return;
        }

        try {
            System.out.print("ID del empleado: ");
            int idEmpleado = Integer.parseInt(scanner.nextLine());

            System.out.print("ID del departamento: ");
            int idDepartamento = Integer.parseInt(scanner.nextLine());

            gestor.asignarEmpleadoADepartamento(idEmpleado, idDepartamento);
            System.out.println("Empleado asignado exitosamente");
        } catch (Exception e) {
            System.out.println("Error al asignar empleado: " + e.getMessage());
        }
    }

    //  EVALUACIONES
    private static void crearEvaluacion() {
        if (!verificarAcceso("evaluacion", "No tiene permisos para crear evaluaciones")) {
            return;
        }

        try {
            List<Empleado> empleados = gestor.obtenerEmpleados();
            if (empleados.isEmpty()) {
                System.out.println("No hay empleados registrados");
                return;
            }

            System.out.println("\nEmpleados Disponibles");
            for (int i = 0; i < empleados.size(); i++) {
                Empleado emp = empleados.get(i);
                System.out.println((i + 1) + ". " + emp.getNombre() + " " + emp.getApellido() +
                        " (ID: " + emp.getId() + ")");
            }

            System.out.print("\nSeleccione empleado a evaluar: ");
            int seleccion = Integer.parseInt(scanner.nextLine());

            if (seleccion < 1 || seleccion > empleados.size()) {
                System.out.println("Selección inválida");
                return;
            }

            Empleado empleado = empleados.get(seleccion - 1);

            System.out.print("Criterio de evaluación: ");
            String criterio = scanner.nextLine();

            System.out.print("Puntuación (1-10): ");
            int puntuacion = Integer.parseInt(scanner.nextLine());

            if (puntuacion < 1 || puntuacion > 10) {
                System.out.println("La puntuación debe estar entre 1 y 10");
                return;
            }

            System.out.print("Comentario: ");
            String comentario = scanner.nextLine();

            //  evaluación con referencia al empleado
            int idEvaluacion = gestor.obtenerSiguienteIdEvaluacion();
            EvaluacionEmpleado evaluacion = new EvaluacionEmpleado(idEvaluacion, criterio, puntuacion, comentario, empleado.getId());

            evaluacionesCreadas.add(evaluacion);

            System.out.println("Evaluación creada exitosamente");
            System.out.println("Datos procesados con validaciones de seguridad");
            System.out.println("Empleado: " + empleado.getNombre() + " " + empleado.getApellido());
            System.out.println("Criterio: " + criterio);
            System.out.println("Puntuación: " + puntuacion + "/10");

        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese valores numéricos válidos");
        } catch (Exception e) {
            System.out.println("Error al crear evaluación: " + e.getMessage());
        }
    }

    //  EVALUACIONES Parte 2
    private static void verEvaluacionesPorEmpleado() {
        if (!verificarAcceso("ver", "No tiene permisos para ver evaluaciones")) {
            return;
        }

        try {
            List<Empleado> empleados = gestor.obtenerEmpleados();
            if (empleados.isEmpty()) {
                System.out.println("No hay empleados registrados");
                return;
            }

            System.out.println("\nEmpleados Disponibles");
            for (int i = 0; i < empleados.size(); i++) {
                Empleado emp = empleados.get(i);
                System.out.println((i + 1) + ". " + emp.getNombre() + " " + emp.getApellido() +
                        " (ID: " + emp.getId() + ")");
            }

            System.out.print("\nSeleccione empleado: ");
            int seleccion = Integer.parseInt(scanner.nextLine());

            if (seleccion < 1 || seleccion > empleados.size()) {
                System.out.println("Selección inválida");
                return;
            }

            Empleado empleado = empleados.get(seleccion - 1);

            System.out.println("\nEvaluaciones de " + empleado.getNombre() + " " + empleado.getApellido());

            //  imprimir información detallada del empleado
            if ("admin".equals(usuarioActual) || "hr".equals(usuarioActual)) {
                System.out.println("ID Empleado: " + empleado.getId());
                System.out.println("Fecha contratación: " + empleado.getFechaContratacion());
                System.out.println("Salario: $" + String.format("%.2f", empleado.getSalarioBase()));
                System.out.println("Departamento: " +
                        (empleado.getDepartamento() != null ?
                                empleado.getDepartamento().getNombre() : "Sin asignar"));
            }

            // Filtrar
            List<EvaluacionEmpleado> evaluacionesEmpleado = evaluacionesCreadas.stream()
                    .filter(eval -> eval instanceof EvaluacionEmpleado &&
                            ((EvaluacionEmpleado) eval).getIdEmpleado() == empleado.getId())
                    .map(eval -> (EvaluacionEmpleado) eval)
                    .collect(Collectors.toList());

            if (evaluacionesEmpleado.isEmpty()) {
                System.out.println("\nNo hay evaluaciones registradas para este empleado");
                System.out.println("Use la opción 'Crear Evaluación' para agregar evaluaciones");
            } else {
                System.out.println("\nHistorial de Evaluaciones");
                double sumaTotal = 0;
                for (int i = 0; i < evaluacionesEmpleado.size(); i++) {
                    EvaluacionEmpleado eval = evaluacionesEmpleado.get(i);
                    System.out.println((i + 1) + ". " + eval.getCriterio() + ": " + eval.getPuntuacion() + "/10");
                    if (!eval.getComentario().isEmpty()) {
                        System.out.println("   Comentario: " + eval.getComentario());
                    }
                    sumaTotal += eval.getPuntuacion();
                }

                // Estadísticas
                double promedio = sumaTotal / evaluacionesEmpleado.size();
                System.out.println("\nResumen de Desempeño");
                System.out.println("• Total evaluaciones: " + evaluacionesEmpleado.size());
                System.out.println("• Promedio general: " + String.format("%.1f", promedio) + "/10");

                if ("admin".equals(usuarioActual) || "hr".equals(usuarioActual)) {
                    System.out.println("\nAnálisis de Rendimiento:");
                    if (promedio >= 8.0) {
                        System.out.println("• Rendimiento EXCELENTE");
                        System.out.println("• Considerar para promoción");
                    } else if (promedio >= 6.0) {
                        System.out.println("• Rendimiento SATISFACTORIO");
                        System.out.println("• Mantener seguimiento regular");
                    } else {
                        System.out.println("• Rendimiento NECESITA MEJORA");
                        System.out.println("• Implementar plan de desarrollo");
                    }
                } else {
                    System.out.println("\nDetalles adicionales disponibles solo para Admin/HR");
                }
            }

            System.out.println("\nConsulta completada con validaciones de seguridad");

        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese un número válido");
        } catch (Exception e) {
            System.out.println("Error al consultar evaluaciones: " + e.getMessage());
        }
    }

    private static void listarTodasLasEvaluaciones() {
        if (!verificarAcceso("ver", "No tiene permisos para ver evaluaciones")) {
            return;
        }

        try {
            if (evaluacionesCreadas.isEmpty()) {
                System.out.println("No hay evaluaciones registradas en el sistema");
                System.out.println("Use la opción 'Crear Evaluación' para agregar evaluaciones");
                return;
            }

            System.out.println("\nTodas las Evaluaciones del Sistema");
            System.out.println("Total de evaluaciones: " + evaluacionesCreadas.size());
            System.out.println();

            for (Evaluacion eval : evaluacionesCreadas) {
                if (eval instanceof EvaluacionEmpleado) {
                    EvaluacionEmpleado evalEmp = (EvaluacionEmpleado) eval;
                    Empleado empleado = gestor.buscarEmpleado(evalEmp.getIdEmpleado());

                    if (empleado != null) {
                        System.out.println("Evaluación ID: " + eval.getId());
                        System.out.println("Empleado: " + empleado.getNombre() + " " + empleado.getApellido() + " (ID: " + empleado.getId() + ")");
                        System.out.println("Criterio: " + eval.getCriterio());
                        System.out.println("Puntuación: " + eval.getPuntuacion() + "/10");
                        System.out.println("Comentario: " + eval.getComentario());
                        System.out.println("------------------------");
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar evaluaciones: " + e.getMessage());
        }
    }

    //   REPORTES
    private static void verReporteIndividual() {
        if (!verificarAcceso("reporte", "No tiene permisos para ver reportes")) {
            return;
        }

        try {
            List<Empleado> empleados = gestor.obtenerEmpleados();
            if (empleados.isEmpty()) {
                System.out.println("No hay empleados para generar reportes");
                return;
            }

            System.out.println("\nEmpleados Disponibles");
            for (int i = 0; i < empleados.size(); i++) {
                Empleado emp = empleados.get(i);
                System.out.println((i + 1) + ". " + emp.getNombre() + " " + emp.getApellido() +
                        " (ID: " + emp.getId() + ")");
            }

            System.out.print("\nSeleccione el número del empleado: ");
            int seleccion = Integer.parseInt(scanner.nextLine());

            if (seleccion < 1 || seleccion > empleados.size()) {
                System.out.println("Selección inválida");
                return;
            }

            Empleado empleadoSeleccionado = empleados.get(seleccion - 1);

            System.out.print("Periodo del reporte (ej: Q1-2024): ");
            String periodo = scanner.nextLine();

            System.out.println("\nGenerando reporte con validaciones de seguridad...");

            ReporteDesempeno reporte = generador.generarReporteIndividual(empleadoSeleccionado, periodo);

            List<Evaluacion> evaluaciones = reporte.getEvaluaciones();
            for (int i = 0; i < evaluaciones.size(); i++) {
                Evaluacion eval = evaluaciones.get(i);
                System.out.print("Puntuación para " + eval.getCriterio() + " (1-10): ");
                try {
                    int puntuacion = Integer.parseInt(scanner.nextLine());
                    if (puntuacion >= 1 && puntuacion <= 10) {
                        eval.setPuntuacion(puntuacion);

                        System.out.print("Comentario (opcional): ");
                        String comentario = scanner.nextLine();
                        eval.setComentario(comentario);
                    } else {
                        System.out.println("Puntuación inválida, asignando 5 por defecto");
                        eval.setPuntuacion(5);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Valor inválido, asignando 5 por defecto");
                    eval.setPuntuacion(5);
                }
            }

            System.out.print("Comentario final del reporte: ");
            String comentarioFinal = scanner.nextLine();
            reporte.setComentarioFinal(comentarioFinal);

            System.out.println("\n" + "=".repeat(50));
            System.out.println("REPORTE GENERADO EXITOSAMENTE");
            System.out.println("=".repeat(50));

            if ("admin".equals(usuarioActual) || "hr".equals(usuarioActual)) {
                System.out.println(reporte.generarResumenReporte());
            } else {
                System.out.println("Reporte Individual - Datos Limitados");
                System.out.println("Empleado: " + empleadoSeleccionado.getNombre() + " " + empleadoSeleccionado.getApellido());
                System.out.println("Periodo: " + periodo);
                System.out.println("Puntuación Promedio: " + String.format("%.2f", reporte.getPuntuacionPromedio()));
                System.out.println("Detalles completos disponibles solo para Admin/HR");
            }

            System.out.println("\nReporte procesado con controles de seguridad aplicados");

        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese un número válido");
        } catch (Exception e) {
            System.out.println("Error al generar reporte: " + e.getMessage());
        }
    }

    private static void verReporteDepartamental() {
        if (!verificarAcceso("reporte", "No tiene permisos para ver reportes")) {
            return;
        }

        try {
            List<Departamento> departamentos = gestor.obtenerDepartamentos();
            if (departamentos.isEmpty()) {
                System.out.println("No hay departamentos registrados");
                return;
            }

            System.out.println("\nDepartamentos Disponibles");
            for (int i = 0; i < departamentos.size(); i++) {
                Departamento dept = departamentos.get(i);
                System.out.println((i + 1) + ". " + dept.getNombre() +
                        " (ID: " + dept.getId() + ") - " +
                        dept.getCantidadEmpleados() + " empleados");
            }

            System.out.print("\nSeleccione el número del departamento: ");
            int seleccion = Integer.parseInt(scanner.nextLine());

            if (seleccion < 1 || seleccion > departamentos.size()) {
                System.out.println("Selección inválida");
                return;
            }

            Departamento departamentoSeleccionado = departamentos.get(seleccion - 1);

            System.out.print("Periodo del reporte (ej: Q1-2024): ");
            String periodo = scanner.nextLine();

            System.out.println("\nGenerando reporte departamental con validaciones de seguridad...");

            String reporteDepartamental = generador.generarReporteDepartamento(departamentoSeleccionado, periodo);

            System.out.println("\n" + "=".repeat(60));
            System.out.println("REPORTE DEPARTAMENTAL GENERADO");
            System.out.println("=".repeat(60));

            if ("admin".equals(usuarioActual) || "manager".equals(usuarioActual) || "hr".equals(usuarioActual)) {
                System.out.println(reporteDepartamental);

                System.out.println("\nEstadísticas Adicionales");
                System.out.println("Acceso completo autorizado para: " + rolUsuario);
                System.out.println("Total empleados: " + departamentoSeleccionado.getCantidadEmpleados());
                System.out.println("Supervisor: " +
                        (departamentoSeleccionado.getSupervisor() != null ?
                                departamentoSeleccionado.getSupervisor().getNombre() + " " +
                                        departamentoSeleccionado.getSupervisor().getApellido() : "No asignado"));

            } else {
                System.out.println("Reporte Departamental - Vista Limitada");
                System.out.println("Departamento: " + departamentoSeleccionado.getNombre());
                System.out.println("Periodo: " + periodo);
                System.out.println("Total empleados: " + departamentoSeleccionado.getCantidadEmpleados());
                System.out.println("Detalles completos disponibles solo para Admin/Manager/HR");
            }

            System.out.println("\nReporte departamental procesado con controles de seguridad");

        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese un número válido");
        } catch (Exception e) {
            System.out.println("Error al generar reporte departamental: " + e.getMessage());
        }
    }

    private static void crearDatosDePrueba() {
        try {
            Departamento it = new Departamento(1, "Tecnología", "Departamento de sistemas");
            Departamento rrhh = new Departamento(2, "Recursos Humanos", "Gestión de personal");
            Departamento ventas = new Departamento(3, "Ventas", "Departamento comercial");

            gestor.agregarDepartamento(it);
            gestor.agregarDepartamento(rrhh);
            gestor.agregarDepartamento(ventas);

            EmpleadoTiempoCompleto admin = new EmpleadoTiempoCompleto(
                    1, "Ana", "García", "12345678A", LocalDate.of(2023, 1, 15), 3500.0
            );

            EmpleadoTiempoCompleto manager = new EmpleadoTiempoCompleto(
                    2, "Carlos", "López", "87654321B", LocalDate.of(2023, 3, 20), 3000.0
            );

            EmpleadoTemporal temporal = new EmpleadoTemporal(
                    3, "María", "Rodríguez", "11111111C", LocalDate.of(2023, 6, 1),
                    2500.0, LocalDate.of(2024, 12, 31), "Proyecto Especial"
            );

            gestor.contratarEmpleado(admin);
            gestor.contratarEmpleado(manager);
            gestor.contratarEmpleado(temporal);

            gestor.asignarEmpleadoADepartamento(1, 1);
            gestor.asignarEmpleadoADepartamento(2, 2);
            gestor.asignarEmpleadoADepartamento(3, 3);

            gestor.asignarSupervisor(1, 1);
            gestor.asignarSupervisor(2, 2);

            System.out.println("Datos de prueba creados con seguridad implementada");
            System.out.println("3 Departamentos y 3 Empleados listos para pruebas");

        } catch (Exception e) {
            System.out.println("Error al crear datos de prueba: " + e.getMessage());
        }
    }

    // EVALUACION EMPLEADO PArte 3
    static class EvaluacionEmpleado extends Evaluacion {
        private int idEmpleado;

        public EvaluacionEmpleado(int id, String criterio, int puntuacion, String comentario, int idEmpleado) {
            super(id, criterio, puntuacion, comentario);
            this.idEmpleado = idEmpleado;
        }

        public int getIdEmpleado() {
            return idEmpleado;
        }
    }
}