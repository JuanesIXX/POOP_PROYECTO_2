// =============================================================================
// ARCHIVO: ReportesPanel.java
// UBICACIÓN: org.example.gui.ReportesPanel.java
// =============================================================================
package org.example.gui;

import org.example.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ReportesPanel extends JPanel {
    private GestorRRHH gestor;
    private GeneradorReportes generador;
    private String usuario;
    private String rol;

    private JTextArea txtReporte;
    private JComboBox<String> cbTipoReporte;
    private JComboBox<String> cbSeleccion;
    private JButton btnGenerar;

    public ReportesPanel(GestorRRHH gestor, String usuario, String rol) {
        this.gestor = gestor;
        this.generador = new GeneradorReportes(gestor);
        this.usuario = usuario;
        this.rol = rol;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
    }

    private void initComponents() {
        // Panel superior
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel lblTitulo = new JLabel("Generador de Reportes");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(lblTitulo, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

        // Panel de configuración
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(BorderFactory.createTitledBorder("Configuración del Reporte"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tipo de reporte
        gbc.gridx = 0;
        gbc.gridy = 0;
        configPanel.add(new JLabel("Tipo de Reporte:"), gbc);

        gbc.gridx = 1;
        cbTipoReporte = new JComboBox<>(new String[]{
                "Reporte Individual",
                "Reporte Departamental",
                "Reporte General"
        });
        cbTipoReporte.addActionListener(e -> actualizarSeleccion());
        configPanel.add(cbTipoReporte, gbc);

        // Selección (empleado o departamento)
        gbc.gridx = 0;
        gbc.gridy = 1;
        configPanel.add(new JLabel("Seleccionar:"), gbc);

        gbc.gridx = 1;
        cbSeleccion = new JComboBox<>();
        configPanel.add(cbSeleccion, gbc);

        // Botón generar
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        btnGenerar = new JButton("Generar Reporte");
        btnGenerar.addActionListener(e -> generarReporte());
        configPanel.add(btnGenerar, gbc);

        add(configPanel, BorderLayout.NORTH);

        // Área de texto para mostrar el reporte
        txtReporte = new JTextArea();
        txtReporte.setEditable(false);
        txtReporte.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtReporte);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Reporte Generado"));
        add(scrollPane, BorderLayout.CENTER);

        // Inicializar
        actualizarSeleccion();
    }

    private void actualizarSeleccion() {
        cbSeleccion.removeAllItems();
        String tipo = (String) cbTipoReporte.getSelectedItem();

        if ("Reporte Individual".equals(tipo)) {
            List<Empleado> empleados = gestor.obtenerEmpleados();
            for (Empleado emp : empleados) {
                cbSeleccion.addItem(emp.getId() + " - " + emp.getNombre() + " " + emp.getApellido());
            }
            cbSeleccion.setEnabled(true);
        } else if ("Reporte Departamental".equals(tipo)) {
            List<Departamento> departamentos = gestor.obtenerDepartamentos();
            for (Departamento dept : departamentos) {
                cbSeleccion.addItem(dept.getId() + " - " + dept.getNombre());
            }
            cbSeleccion.setEnabled(true);
        } else {
            cbSeleccion.addItem("No aplica");
            cbSeleccion.setEnabled(false);
        }
    }

    private void generarReporte() {
        String tipo = (String) cbTipoReporte.getSelectedItem();
        StringBuilder reporte = new StringBuilder();

        try {
            if ("Reporte Individual".equals(tipo)) {
                String seleccion = (String) cbSeleccion.getSelectedItem();
                int idEmpleado = Integer.parseInt(seleccion.split(" - ")[0]);

                Empleado empleado = gestor.buscarEmpleado(idEmpleado);
                if (empleado != null) {
                    reporte.append(generarReporteIndividual(empleado));
                }

            } else if ("Reporte Departamental".equals(tipo)) {
                String seleccion = (String) cbSeleccion.getSelectedItem();
                int idDepartamento = Integer.parseInt(seleccion.split(" - ")[0]);

                Departamento departamento = gestor.buscarDepartamento(idDepartamento);
                if (departamento != null) {
                    reporte.append(generarReporteDepartamental(departamento));
                }

            } else {
                reporte.append(generarReporteGeneral());
            }

            txtReporte.setText(reporte.toString());
            txtReporte.setCaretPosition(0);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar reporte: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generarReporteIndividual(Empleado empleado) {
        StringBuilder sb = new StringBuilder();
        sb.append("=" .repeat(60)).append("\n");
        sb.append("REPORTE INDIVIDUAL DE EMPLEADO\n");
        sb.append("=".repeat(60)).append("\n\n");

        sb.append("DATOS PERSONALES:\n");
        sb.append(String.format("ID: %d\n", empleado.getId()));
        sb.append(String.format("Nombre: %s %s\n", empleado.getNombre(), empleado.getApellido()));

        // Mostrar cédula según permisos
        if ("admin".equals(usuario) || "hr".equals(usuario)) {
            sb.append(String.format("Cédula: %s\n", empleado.getCedula()));
        } else {
            sb.append(String.format("Cédula: %s\n", empleado.getCedulaEnmascarado()));
        }

        sb.append(String.format("Tipo: %s\n", empleado.getTipoEmpleado()));
        sb.append(String.format("Fecha de Contratación: %s\n", empleado.getFechaContratacion()));
        sb.append(String.format("Salario Base: $%.2f\n", empleado.getSalarioBase()));
        sb.append(String.format("Salario Total: $%.2f\n\n", empleado.calcularSalario()));

        if (empleado.getDepartamento() != null) {
            sb.append("DEPARTAMENTO:\n");
            sb.append(String.format("Nombre: %s\n", empleado.getDepartamento().getNombre()));
            if (empleado.getDepartamento().getSupervisor() != null) {
                Empleado supervisor = empleado.getDepartamento().getSupervisor();
                sb.append(String.format("Supervisor: %s %s\n\n",
                        supervisor.getNombre(), supervisor.getApellido()));
            }
        }

        sb.append("=".repeat(60)).append("\n");
        sb.append("Reporte generado por: ").append(usuario).append(" (").append(rol).append(")\n");
        sb.append("=".repeat(60)).append("\n");

        return sb.toString();
    }

    private String generarReporteDepartamental(Departamento departamento) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("REPORTE DEPARTAMENTAL\n");
        sb.append("=".repeat(60)).append("\n\n");

        sb.append("INFORMACIÓN DEL DEPARTAMENTO:\n");
        sb.append(String.format("ID: %d\n", departamento.getId()));
        sb.append(String.format("Nombre: %s\n", departamento.getNombre()));
        sb.append(String.format("Descripción: %s\n", departamento.getDescripcion()));
        sb.append(String.format("Cantidad de Empleados: %d\n\n", departamento.getCantidadEmpleados()));

        if (departamento.getSupervisor() != null) {
            Empleado supervisor = departamento.getSupervisor();
            sb.append("SUPERVISOR:\n");
            sb.append(String.format("%s %s (ID: %d)\n\n",
                    supervisor.getNombre(), supervisor.getApellido(), supervisor.getId()));
        }

        List<Empleado> empleados = departamento.getEmpleados();
        if (!empleados.isEmpty()) {
            sb.append("EMPLEADOS DEL DEPARTAMENTO:\n");
            sb.append("-".repeat(60)).append("\n");

            double totalSalarios = 0;
            for (Empleado emp : empleados) {
                sb.append(String.format("• %s %s (ID: %d)\n",
                        emp.getNombre(), emp.getApellido(), emp.getId()));
                sb.append(String.format("  Tipo: %s | Salario: $%.2f\n",
                        emp.getTipoEmpleado(), emp.calcularSalario()));
                totalSalarios += emp.calcularSalario();
            }

            sb.append("\nRESUMEN FINANCIERO:\n");
            sb.append(String.format("Total en Salarios: $%.2f\n", totalSalarios));
            sb.append(String.format("Promedio Salarial: $%.2f\n", totalSalarios / empleados.size()));
        }

        sb.append("\n").append("=".repeat(60)).append("\n");
        sb.append("Reporte generado por: ").append(usuario).append(" (").append(rol).append(")\n");
        sb.append("=".repeat(60)).append("\n");

        return sb.toString();
    }

    private String generarReporteGeneral() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("REPORTE GENERAL DE LA EMPRESA\n");
        sb.append("=".repeat(60)).append("\n\n");

        List<Empleado> empleados = gestor.obtenerEmpleados();
        List<Departamento> departamentos = gestor.obtenerDepartamentos();

        sb.append("ESTADÍSTICAS GENERALES:\n");
        sb.append(String.format("Total de Empleados: %d\n", empleados.size()));
        sb.append(String.format("Total de Departamentos: %d\n\n", departamentos.size()));

        if (!empleados.isEmpty()) {
            double totalSalarios = empleados.stream()
                    .mapToDouble(Empleado::calcularSalario)
                    .sum();

            sb.append("INFORMACIÓN SALARIAL:\n");
            sb.append(String.format("Nómina Total: $%.2f\n", totalSalarios));
            sb.append(String.format("Promedio Salarial: $%.2f\n\n", totalSalarios / empleados.size()));
        }

        sb.append("RESUMEN POR DEPARTAMENTOS:\n");
        sb.append("-".repeat(60)).append("\n");

        for (Departamento dept : departamentos) {
            sb.append(String.format("• %s: %d empleado(s)",
                    dept.getNombre(), dept.getCantidadEmpleados()));

            if (dept.getSupervisor() != null) {
                sb.append(String.format(" | Supervisor: %s %s",
                        dept.getSupervisor().getNombre(),
                        dept.getSupervisor().getApellido()));
            }
            sb.append("\n");
        }

        sb.append("\n").append("=".repeat(60)).append("\n");
        sb.append("Reporte generado por: ").append(usuario).append(" (").append(rol).append(")\n");
        sb.append("=".repeat(60)).append("\n");

        return sb.toString();
    }
}