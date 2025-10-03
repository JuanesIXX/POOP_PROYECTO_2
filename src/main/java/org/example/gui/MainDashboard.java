
package org.example.gui;

import org.example.GestorRRHH;
import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {
    private GestorRRHH gestor;
    private String usuario;
    private String rol;

    private JPanel contentPanel;
    private JLabel lblUsuario;
    private JLabel lblRol;

    public MainDashboard(GestorRRHH gestor, String usuario, String rol) {
        System.out.println("==> 5. El constructor de MainDashboard se está ejecutando. ");
        this.gestor = gestor;
        this.usuario = usuario;
        this.rol = rol;

        setTitle("Sistema RRHH - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        // Layout principal: BorderLayout
        setLayout(new BorderLayout());

        // Panel superior con info del usuario
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Panel izquierdo con menú
        JPanel menuPanel = createMenuPanel();
        add(menuPanel, BorderLayout.WEST);

        // Panel central para contenido dinámico
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(contentPanel, BorderLayout.CENTER);

        // Mostrar panel de inicio
        mostrarPanelInicio();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(63, 81, 181)); // Color azul
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitulo = new JLabel("Sistema de Gestión de Recursos Humanos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        JPanel userInfoPanel = new JPanel(new GridLayout(2, 1));
        userInfoPanel.setOpaque(false);

        lblUsuario = new JLabel("Usuario: " + usuario);
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setHorizontalAlignment(SwingConstants.RIGHT);

        lblRol = new JLabel("Rol: " + rol);
        lblRol.setForeground(Color.WHITE);
        lblRol.setHorizontalAlignment(SwingConstants.RIGHT);

        userInfoPanel.add(lblUsuario);
        userInfoPanel.add(lblRol);

        panel.add(lblTitulo, BorderLayout.WEST);
        panel.add(userInfoPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(200, 0));

        // Título del menú
        JLabel lblMenu = new JLabel("MENÚ PRINCIPAL");
        lblMenu.setFont(new Font("Arial", Font.BOLD, 14));
        lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblMenu);
        panel.add(Box.createVerticalStrut(20));

        // Botones del menú
        addMenuButton(panel, "Inicio", e -> mostrarPanelInicio());
        addMenuButton(panel, "Empleados", e -> mostrarPanelEmpleados());
        addMenuButton(panel, "Departamentos", e -> mostrarPanelDepartamentos());
        addMenuButton(panel, "Evaluaciones", e -> mostrarPanelEvaluaciones());
        addMenuButton(panel, "Reportes", e -> mostrarPanelReportes());

        panel.add(Box.createVerticalGlue()); // Espacio flexible

        // Botón de cerrar sesión
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCerrarSesion.setMaximumSize(new Dimension(180, 40));
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        panel.add(btnCerrarSesion);

        return panel;
    }

    private void addMenuButton(JPanel panel, String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.addActionListener(action);
        panel.add(button);
        panel.add(Box.createVerticalStrut(10));
    }

    private void mostrarPanelInicio() {
        contentPanel.removeAll();

        JPanel inicioPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblBienvenida = new JLabel("Bienvenido al Sistema de Gestión RRHH");
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        inicioPanel.add(lblBienvenida, gbc);

        JLabel lblInfo = new JLabel("<html><center>Utilice el menú lateral para navegar<br>por las diferentes secciones del sistema</center></html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 1;
        inicioPanel.add(lblInfo, gbc);

        // Estadísticas rápidas
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        statsPanel.add(createStatCard("Empleados", String.valueOf(gestor.obtenerEmpleados().size())));
        statsPanel.add(createStatCard("Departamentos", String.valueOf(gestor.obtenerDepartamentos().size())));
        statsPanel.add(createStatCard("Rol Actual", rol));

        gbc.gridy = 2;
        inicioPanel.add(statsPanel, gbc);

        contentPanel.add(inicioPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createStatCard(String titulo, String valor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.BOLD, 28));
        lblValor.setHorizontalAlignment(SwingConstants.CENTER);
        lblValor.setForeground(new Color(63, 81, 181));

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }

    private void mostrarPanelEmpleados() {
        contentPanel.removeAll();
        EmpleadosPanel empleadosPanel = new EmpleadosPanel(gestor, usuario, rol);
        contentPanel.add(empleadosPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void mostrarPanelDepartamentos() {
        contentPanel.removeAll();
        DepartamentosPanel deptosPanel = new DepartamentosPanel(gestor, usuario, rol);
        contentPanel.add(deptosPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void mostrarPanelEvaluaciones() {
        contentPanel.removeAll();
        EvaluacionesPanel evalPanel = new EvaluacionesPanel(gestor, usuario, rol);
        contentPanel.add(evalPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void mostrarPanelReportes() {
        contentPanel.removeAll();
        ReportesPanel reportesPanel = new ReportesPanel(gestor, usuario, rol);
        contentPanel.add(reportesPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea cerrar sesión?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginForm loginForm = new LoginForm(gestor);
                loginForm.setVisible(true);
            });
        }
    }
}

// =============================================================================
// CONTINÚA EN EL SIGUIENTE MENSAJE CON LOS PANELES ESPECÍFICOS
// =============================================================================