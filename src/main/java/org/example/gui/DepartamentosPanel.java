// =============================================================================
// ARCHIVO: DepartamentosPanel.java
// UBICACIÓN: org.example.gui.DepartamentosPanel.java
// =============================================================================
package org.example.gui;

import org.example.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DepartamentosPanel extends JPanel {
    private GestorRRHH gestor;
    private String usuario;
    private String rol;

    private JTable tableDepartamentos;
    private DefaultTableModel modeloTabla;
    private JButton btnAgregar, btnEliminar, btnAsignarSupervisor, btnRefrescar;

    public DepartamentosPanel(GestorRRHH gestor, String usuario, String rol) {
        this.gestor = gestor;
        this.usuario = usuario;
        this.rol = rol;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        cargarDepartamentos();
    }

    private void initComponents() {
        // Panel superior
        JLabel lblTitulo = new JLabel("Gestión de Departamentos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Nombre", "Descripción", "Supervisor", "Empleados"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableDepartamentos = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tableDepartamentos);
        add(scrollPane, BorderLayout.CENTER);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnAgregar = new JButton("Agregar Departamento");
        btnAgregar.addActionListener(e -> agregarDepartamento());

        btnAsignarSupervisor = new JButton("Asignar Supervisor");
        btnAsignarSupervisor.addActionListener(e -> asignarSupervisor());

        btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarDepartamento());

        btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarDepartamentos());

        buttonPanel.add(btnAgregar);
        buttonPanel.add(btnAsignarSupervisor);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnRefrescar);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void cargarDepartamentos() {
        modeloTabla.setRowCount(0);

        List<Departamento> departamentos = gestor.obtenerDepartamentos();

        for (Departamento dept : departamentos) {
            String supervisor = dept.getSupervisor() != null ?
                    dept.getSupervisor().getNombre() + " " + dept.getSupervisor().getApellido() :
                    "Sin asignar";

            Object[] fila = {
                    dept.getId(),
                    dept.getNombre(),
                    dept.getDescripcion(),
                    supervisor,
                    dept.getCantidadEmpleados()
            };

            modeloTabla.addRow(fila);
        }
    }

    private void agregarDepartamento() {
        JTextField txtId = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtDescripcion = new JTextField();

        Object[] campos = {
                "ID:", txtId,
                "Nombre:", txtNombre,
                "Descripción:", txtDescripcion
        };

        int opcion = JOptionPane.showConfirmDialog(this, campos,
                "Agregar Departamento", JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                String nombre = txtNombre.getText().trim();
                String descripcion = txtDescripcion.getText().trim();

                Departamento dept = new Departamento(id, nombre, descripcion);
                gestor.agregarDepartamento(dept);

                cargarDepartamentos();
                JOptionPane.showMessageDialog(this, "Departamento agregado exitosamente");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void asignarSupervisor() {
        int fila = tableDepartamentos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un departamento");
            return;
        }

        int idDept = (int) modeloTabla.getValueAt(fila, 0);

        // Obtener lista de empleados
        List<Empleado> empleados = gestor.obtenerEmpleados();
        String[] nombresEmpleados = empleados.stream()
                .map(e -> e.getId() + " - " + e.getNombre() + " " + e.getApellido())
                .toArray(String[]::new);

        String seleccion = (String) JOptionPane.showInputDialog(this,
                "Seleccione el supervisor:",
                "Asignar Supervisor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                nombresEmpleados,
                nombresEmpleados[0]);

        if (seleccion != null) {
            int idEmpleado = Integer.parseInt(seleccion.split(" - ")[0]);
            gestor.asignarSupervisor(idDept, idEmpleado);
            cargarDepartamentos();
            JOptionPane.showMessageDialog(this, "Supervisor asignado exitosamente");
        }
    }

    private void eliminarDepartamento() {
        int fila = tableDepartamentos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un departamento");
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este departamento?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            int id = (int) modeloTabla.getValueAt(fila, 0);
            try {
                gestor.eliminarDepartamento(id);
                cargarDepartamentos();
                JOptionPane.showMessageDialog(this, "Departamento eliminado");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

