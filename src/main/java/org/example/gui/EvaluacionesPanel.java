// =============================================================================
// ARCHIVO: EvaluacionesPanel.java
// UBICACIÓN: org.example.gui.EvaluacionesPanel.java
// =============================================================================
package org.example.gui;

import org.example.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class EvaluacionesPanel extends JPanel {
    private GestorRRHH gestor;
    private String usuario;
    private String rol;

    private JTable tableEvaluaciones;
    private DefaultTableModel modeloTabla;
    private JButton btnCrear, btnVerDetalle, btnRefrescar;
    private JComboBox<String> cbEmpleados;
    private List<EvaluacionExtendida> todasEvaluaciones;

    public EvaluacionesPanel(GestorRRHH gestor, String usuario, String rol) {
        this.gestor = gestor;
        this.usuario = usuario;
        this.rol = rol;
        this.todasEvaluaciones = new ArrayList<>();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        cargarEvaluaciones();
    }

    private void initComponents() {
        // Panel superior
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel lblTitulo = new JLabel("Gestión de Evaluaciones");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(lblTitulo, BorderLayout.WEST);

        // Filtro por empleado
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.add(new JLabel("Filtrar por empleado:"));
        cbEmpleados = new JComboBox<>();
        cbEmpleados.addItem("Todos");
        for (Empleado emp : gestor.obtenerEmpleados()) {
            cbEmpleados.addItem(emp.getId() + " - " + emp.getNombre() + " " + emp.getApellido());
        }
        cbEmpleados.addActionListener(e -> filtrarEvaluaciones());
        filterPanel.add(cbEmpleados);
        topPanel.add(filterPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Empleado", "Criterio", "Puntuación", "Comentario", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableEvaluaciones = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tableEvaluaciones);
        add(scrollPane, BorderLayout.CENTER);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnCrear = new JButton("Crear Evaluación");
        btnCrear.addActionListener(e -> crearEvaluacion());

        btnVerDetalle = new JButton("Ver Detalle");
        btnVerDetalle.addActionListener(e -> verDetalle());

        btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarEvaluaciones());

        buttonPanel.add(btnCrear);
        buttonPanel.add(btnVerDetalle);
        buttonPanel.add(btnRefrescar);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void cargarEvaluaciones() {
        modeloTabla.setRowCount(0);

        // Aquí deberías cargar las evaluaciones desde tu gestor
        // Como usamos una lista temporal, mostramos las existentes
        for (EvaluacionExtendida eval : todasEvaluaciones) {
            Object[] fila = {
                    eval.getId(),
                    eval.getEmpleado().getNombre() + " " + eval.getEmpleado().getApellido(),
                    eval.getCriterio(),
                    eval.getPuntuacion() + "/10",
                    eval.getComentario(),
                    eval.getFecha()
            };
            modeloTabla.addRow(fila);
        }
    }

    private void filtrarEvaluaciones() {
        String seleccion = (String) cbEmpleados.getSelectedItem();
        modeloTabla.setRowCount(0);

        if ("Todos".equals(seleccion)) {
            cargarEvaluaciones();
            return;
        }

        int idEmpleado = Integer.parseInt(seleccion.split(" - ")[0]);

        for (EvaluacionExtendida eval : todasEvaluaciones) {
            if (eval.getEmpleado().getId() == idEmpleado) {
                Object[] fila = {
                        eval.getId(),
                        eval.getEmpleado().getNombre() + " " + eval.getEmpleado().getApellido(),
                        eval.getCriterio(),
                        eval.getPuntuacion() + "/10",
                        eval.getComentario(),
                        eval.getFecha()
                };
                modeloTabla.addRow(fila);
            }
        }
    }

    private void crearEvaluacion() {
        CrearEvaluacionDialog dialog = new CrearEvaluacionDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                gestor,
                todasEvaluaciones
        );
        dialog.setVisible(true);

        if (dialog.isConfirmado()) {
            cargarEvaluaciones();
            JOptionPane.showMessageDialog(this,
                    "Evaluación creada exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void verDetalle() {
        int fila = tableEvaluaciones.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una evaluación",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);
        EvaluacionExtendida eval = todasEvaluaciones.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);

        if (eval != null) {
            String detalle = String.format(
                    "=== DETALLE DE EVALUACIÓN ===\n\n" +
                            "ID: %d\n" +
                            "Empleado: %s %s\n" +
                            "Criterio: %s\n" +
                            "Puntuación: %d/10\n" +
                            "Comentario: %s\n" +
                            "Fecha: %s\n\n" +
                            "Clasificación: %s",
                    eval.getId(),
                    eval.getEmpleado().getNombre(),
                    eval.getEmpleado().getApellido(),
                    eval.getCriterio(),
                    eval.getPuntuacion(),
                    eval.getComentario(),
                    eval.getFecha(),
                    eval.getClasificacion()
            );

            JTextArea textArea = new JTextArea(detalle);
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(this,
                    scrollPane,
                    "Detalle de Evaluación",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Clase interna para evaluaciones extendidas
    private static class EvaluacionExtendida extends Evaluacion {
        private Empleado empleado;
        private String fecha;

        public EvaluacionExtendida(int id, Empleado empleado, String criterio,
                                   int puntuacion, String comentario, String fecha) {
            super(id, criterio, puntuacion, comentario);
            this.empleado = empleado;
            this.fecha = fecha;
        }

        public Empleado getEmpleado() {
            return empleado;
        }

        public String getFecha() {
            return fecha;
        }

        public String getClasificacion() {
            int punt = getPuntuacion();
            if (punt >= 9) return "EXCELENTE";
            if (punt >= 7) return "BUENO";
            if (punt >= 5) return "ACEPTABLE";
            return "NECESITA MEJORAR";
        }
    }
}
