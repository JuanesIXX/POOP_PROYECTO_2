// =============================================================================
// ARCHIVO: CrearEvaluacionDialog.java
// UBICACIÓN: org.example.gui.CrearEvaluacionDialog.java
// =============================================================================
package org.example.gui;

import org.example.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

class CrearEvaluacionDialog extends JDialog {
    private GestorRRHH gestor;
    private List todasEvaluaciones;
    private boolean confirmado = false;

    private JComboBox<String> cbEmpleado;
    private JComboBox<String> cbCriterio;
    private JSpinner spinnerPuntuacion;
    private JTextArea txtComentario;
    private JButton btnGuardar, btnCancelar;

    public CrearEvaluacionDialog(JFrame parent, GestorRRHH gestor, List todasEvaluaciones) {
        super(parent, "Crear Evaluación", true);
        this.gestor = gestor;
        this.todasEvaluaciones = todasEvaluaciones;

        setSize(450, 400);
        setLocationRelativeTo(parent);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Empleado
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Empleado:"), gbc);

        gbc.gridx = 1;
        cbEmpleado = new JComboBox<>();
        for (Empleado emp : gestor.obtenerEmpleados()) {
            cbEmpleado.addItem(emp.getId() + " - " + emp.getNombre() + " " + emp.getApellido());
        }
        mainPanel.add(cbEmpleado, gbc);

        // Criterio
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Criterio:"), gbc);

        gbc.gridx = 1;
        cbCriterio = new JComboBox<>(new String[]{
                "Rendimiento laboral",
                "Trabajo en equipo",
                "Puntualidad",
                "Iniciativa",
                "Comunicación",
                "Liderazgo",
                "Resolución de problemas"
        });
        mainPanel.add(cbCriterio, gbc);

        // Puntuación
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Puntuación (1-10):"), gbc);

        gbc.gridx = 1;
        spinnerPuntuacion = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        mainPanel.add(spinnerPuntuacion, gbc);

        // Comentario
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTH;
        mainPanel.add(new JLabel("Comentario:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtComentario = new JTextArea(5, 20);
        txtComentario.setLineWrap(true);
        txtComentario.setWrapStyleWord(true);
        JScrollPane scrollComentario = new JScrollPane(txtComentario);
        mainPanel.add(scrollComentario, gbc);

        // Botones
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardar());

        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void guardar() {
        try {
            String seleccion = (String) cbEmpleado.getSelectedItem();
            int idEmpleado = Integer.parseInt(seleccion.split(" - ")[0]);
            Empleado empleado = gestor.buscarEmpleado(idEmpleado);

            String criterio = (String) cbCriterio.getSelectedItem();
            int puntuacion = (int) spinnerPuntuacion.getValue();
            String comentario = txtComentario.getText().trim();

            if (comentario.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "El comentario es obligatorio",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crear evaluación (adaptado a tu estructura)
            int nuevoId = todasEvaluaciones.size() + 1;
            Object evaluacion = crearEvaluacionExtendida(nuevoId, empleado, criterio,
                    puntuacion, comentario,
                    LocalDate.now().toString());
            todasEvaluaciones.add(evaluacion);

            confirmado = true;
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al crear evaluación: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Object crearEvaluacionExtendida(int id, Empleado empleado, String criterio,
                                            int puntuacion, String comentario, String fecha) {
        // Usar reflexión para crear la clase interna
        try {
            Class<?> clazz = Class.forName("org.example.gui.EvaluacionesPanel$EvaluacionExtendida");
            return clazz.getDeclaredConstructors()[0].newInstance(
                    id, empleado, criterio, puntuacion, comentario, fecha
            );
        } catch (Exception e) {
            return new Evaluacion(id, criterio, puntuacion, comentario);
        }
    }

    public boolean isConfirmado() {
        return confirmado;
    }
}
