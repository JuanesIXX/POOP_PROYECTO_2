// =============================================================================
// ARCHIVO: EmpleadosPanel.java
// UBICACIÓN: org.example.gui.EmpleadosPanel.java
// DESCRIPCIÓN: Panel CRUD completo para gestión de empleados
// =============================================================================
package org.example.gui;

import org.example.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class EmpleadosPanel extends JPanel {
    private GestorRRHH gestor;
    private String usuario;
    private String rol;

    private JTable tableEmpleados;
    private DefaultTableModel modeloTabla;
    private JButton btnAgregar, btnEditar, btnEliminar, btnRefrescar;
    private JTextField txtBuscar;

    public EmpleadosPanel(GestorRRHH gestor, String usuario, String rol) {
        this.gestor = gestor;
        this.usuario = usuario;
        this.rol = rol;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        cargarEmpleados();
    }

    private void initComponents() {
        // Panel superior con título y búsqueda
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));

        JLabel lblTitulo = new JLabel("Gestión de Empleados");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(lblTitulo, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(20);
        searchPanel.add(txtBuscar);
        topPanel.add(searchPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Panel central con tabla
        String[] columnas = {"ID", "Nombre", "Apellido", "cedula", "Tipo", "Salario", "Departamento"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable directamente
            }
        };

        tableEmpleados = new JTable(modeloTabla);
        tableEmpleados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableEmpleados);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        btnAgregar = new JButton("Agregar Empleado");
        btnAgregar.addActionListener(e -> agregarEmpleado());

        btnEditar = new JButton("Ver/Editar");
        btnEditar.addActionListener(e -> editarEmpleado());

        btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarEmpleado());

        btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarEmpleados());

        buttonPanel.add(btnAgregar);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnRefrescar);

        // Deshabilitar botones según permisos
        if (!tienePermiso("empleado")) {
            btnAgregar.setEnabled(false);
            btnEditar.setEnabled(false);
            btnEliminar.setEnabled(false);
        }

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void cargarEmpleados() {
        modeloTabla.setRowCount(0); // Limpiar tabla

        List<Empleado> empleados = gestor.obtenerEmpleados();

        for (Empleado emp : empleados) {
            String cedula;
            // Mostrar cedula completo solo para admin y hr
            if ("admin".equals(usuario) || "hr".equals(usuario)) {
                cedula = emp.getcedula();
            } else {
                cedula = emp.getCedulaEnmascarado();
            }

            String departamento = emp.getDepartamento() != null ?
                    emp.getDepartamento().getNombre() : "Sin asignar";

            Object[] fila = {
                    emp.getId(),
                    emp.getNombre(),
                    emp.getApellido(),
                    cedula,
                    emp.getTipoEmpleado(),
                    String.format("$%.2f", emp.getSalarioBase()),
                    departamento
            };

            modeloTabla.addRow(fila);
        }
    }

    private void agregarEmpleado() {
        EmpleadoFormDialog dialog = new EmpleadoFormDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                gestor,
                null
        );
        dialog.setVisible(true);

        if (dialog.isConfirmado()) {
            cargarEmpleados();
            JOptionPane.showMessageDialog(this,
                    "Empleado agregado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editarEmpleado() {
        int filaSeleccionada = tableEmpleados.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un empleado",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Empleado empleado = gestor.buscarEmpleado(id);

        if (empleado != null) {
            EmpleadoFormDialog dialog = new EmpleadoFormDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    gestor,
                    empleado
            );
            dialog.setVisible(true);

            if (dialog.isConfirmado()) {
                cargarEmpleados();
            }
        }
    }

    private void eliminarEmpleado() {
        int filaSeleccionada = tableEmpleados.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un empleado",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este empleado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            int id = (int) modeloTabla.getValueAt(filaSeleccionada, 0);

            try {
                gestor.despedirEmpleado(id);
                cargarEmpleados();
                JOptionPane.showMessageDialog(this,
                        "Empleado eliminado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean tienePermiso(String operacion) {
        return switch (usuario.toLowerCase()) {
            case "admin" -> true;
            case "hr" -> operacion.contains("empleado");
            case "manager" -> operacion.contains("departamento");
            default -> false;
        };
    }
}

// 