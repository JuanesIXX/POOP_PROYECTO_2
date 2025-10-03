// =============================================================================
// ARCHIVO: EmpleadoFormDialog.java
// UBICACIÓN: org.example.gui.EmpleadoFormDialog.java
// DESCRIPCIÓN: Diálogo para agregar/editar empleados
// =============================================================================
package org.example.gui;

import org.example.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class EmpleadoFormDialog extends JDialog {
    private GestorRRHH gestor;
    private Empleado empleadoActual;
    private boolean confirmado = false;

    private JTextField txtId, txtNombre, txtApellido, txtcedula, txtSalario;
    private JComboBox<String> cbTipo;
    private JButton btnGuardar, btnCancelar;

    public EmpleadoFormDialog(JFrame parent, GestorRRHH gestor, Empleado empleado) {
        super(parent, empleado == null ? "Agregar Empleado" : "Ver/Editar Empleado", true);
        this.gestor = gestor;
        this.empleadoActual = empleado;

        setSize(400, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();

        if (empleado != null) {
            cargarDatos();
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("ID:"), gbc);

        gbc.gridx = 1;
        txtId = new JTextField(15);
        txtId.setEnabled(empleadoActual == null);
        mainPanel.add(txtId, gbc);

        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1;
        txtNombre = new JTextField(15);
        mainPanel.add(txtNombre, gbc);

        // Apellido
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Apellido:"), gbc);

        gbc.gridx = 1;
        txtApellido = new JTextField(15);
        mainPanel.add(txtApellido, gbc);

        // cedula
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("cedula:"), gbc);

        gbc.gridx = 1;
        txtcedula = new JTextField(15);
        mainPanel.add(txtcedula, gbc);

        // Salario
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Salario Base:"), gbc);

        gbc.gridx = 1;
        txtSalario = new JTextField(15);
        mainPanel.add(txtSalario, gbc);

        // Tipo
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(new JLabel("Tipo:"), gbc);

        gbc.gridx = 1;
        cbTipo = new JComboBox<>(new String[]{"Tiempo Completo", "Temporal"});
        mainPanel.add(cbTipo, gbc);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardar());

        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void cargarDatos() {
        txtId.setText(String.valueOf(empleadoActual.getId()));
        txtNombre.setText(empleadoActual.getNombre());
        txtApellido.setText(empleadoActual.getApellido());
        txtcedula.setText(empleadoActual.getcedula());
        txtSalario.setText(String.valueOf(empleadoActual.getSalarioBase()));
        cbTipo.setSelectedItem(empleadoActual.getTipoEmpleado());
    }

    private void guardar() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String cedula = txtcedula.getText().trim();
            double salario = Double.parseDouble(txtSalario.getText().trim());

            if (nombre.isEmpty() || apellido.isEmpty() || cedula.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Todos los campos son obligatorios",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (empleadoActual == null) {
                // Crear nuevo empleado
                Empleado nuevoEmpleado = new EmpleadoTiempoCompleto(
                        id, nombre, apellido, cedula, LocalDate.now(), salario
                );
                gestor.contratarEmpleado(nuevoEmpleado);
            } else {
                // Editar empleado existente
                empleadoActual.setNombre(nombre);
                empleadoActual.setApellido(apellido);
                empleadoActual.setSalarioBase(salario);
            }

            confirmado = true;
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "ID y Salario deben ser números válidos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmado() {
        return confirmado;
    }
}
