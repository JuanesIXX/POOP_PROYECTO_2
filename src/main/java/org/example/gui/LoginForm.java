
// ARCHIVO: LoginForm.java
// UBICACIÓN: org.example.gui.LoginForm.java
// INSTRUCCIONES: En IntelliJ, crea este archivo con GUI Designer
// =============================================================================
        package org.example.gui;

import org.example.GestorRRHH;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {
    // Componentes de la interfaz (serán creados por GUI Designer)
    private JPanel mainPanel;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnSalir;
    private JLabel lblTitulo;
    private JLabel lblUsuario;
    private JLabel lblPassword;
    private JLabel lblEstado;

    private GestorRRHH gestor;
    private int intentos = 0;

    public LoginForm(GestorRRHH gestor) {
        this.gestor = gestor;

        // Configuración de la ventana
        setTitle("Sistema RRHH - Login");
        setContentPane(createLoginPanel()); // Método que crea los componentes
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Centrar ventana
        setResizable(false);

        // Agregar listeners
        setupListeners();
    }

    // Este método crea manualmente los componentes
    // En GUI Designer, arrastra componentes en lugar de escribir este código
    private JPanel createLoginPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Título
        lblTitulo = new JLabel("Sistema de Gestión RRHH");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(lblTitulo, gbc);

        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Iniciar Sesión");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        mainPanel.add(lblSubtitulo, gbc);

        // Espacio
        gbc.gridy = 2;
        mainPanel.add(Box.createVerticalStrut(20), gbc);

        // Usuario
        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = 0;
        lblUsuario = new JLabel("Usuario:");
        mainPanel.add(lblUsuario, gbc);

        gbc.gridx = 1;
        txtUsuario = new JTextField(15);
        mainPanel.add(txtUsuario, gbc);

        // Contraseña
        gbc.gridy = 4;
        gbc.gridx = 0;
        lblPassword = new JLabel("Contraseña:");
        mainPanel.add(lblPassword, gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        mainPanel.add(txtPassword, gbc);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnLogin = new JButton("Ingresar");
        btnLogin.setPreferredSize(new Dimension(100, 30));
        btnSalir = new JButton("Salir");
        btnSalir.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnSalir);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        // Etiqueta de estado
        lblEstado = new JLabel(" ");
        lblEstado.setForeground(Color.RED);
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 6;
        mainPanel.add(lblEstado, gbc);

        // Info de usuarios
        JLabel lblInfo = new JLabel("<html><center>Usuarios: admin, hr, manager, empleado<br>Contraseña: [usuario]123</center></html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 10));
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 7;
        mainPanel.add(lblInfo, gbc);

        return mainPanel;
    }

    private void setupListeners() {
        // Listener del botón Login
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });

        // Listener del botón Salir
        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Enter en password también hace login
        txtPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });
    }

    private void realizarLogin() {
        System.out.println("==> 3. Se hizo clic en Login. Intentando validar...");
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            System.out.println("==> 4. ¡Login CORRECTO! Intentando abrir el Dashboard...");
            lblEstado.setText("Por favor complete todos los campos");
            return;
        }

        // Validar credenciales
        String rol = validarCredenciales(usuario, password);

        // Dentro de LoginForm, en el método realizarLogin()
        if (rol != null) {
            // Login exitoso
            gestor.setUsuarioActual(usuario);
            lblEstado.setForeground(Color.GREEN);
            lblEstado.setText("Acceso concedido. Bienvenido!");

            // Guardamos las variables para usarlas en el hilo de Swing
            final String rolFinal = rol;
            final String usuarioFinal = usuario;

            // Abrir dashboard DIRECTAMENTE
            SwingUtilities.invokeLater(() -> {
                // Llama a MainDashboard y le pasa los datos que necesita
                MainDashboard dashboard = new MainDashboard(gestor, usuarioFinal, rolFinal);
                dashboard.setVisible(true);
                dispose(); // Cerrar ventana de login
            });
        }

            else {
            // Login fallido
            System.out.println("==> X. Login INCORRECTO.");
            intentos++;
            lblEstado.setForeground(Color.RED);
            lblEstado.setText("Credenciales incorrectas. Intentos: " + intentos + "/3");
            txtPassword.setText("");

            if (intentos >= 3) {
                JOptionPane.showMessageDialog(this,
                        "Demasiados intentos fallidos. El sistema se cerrará.",
                        "Acceso Denegado",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }

    private String validarCredenciales(String usuario, String password) {
        return switch (usuario.toLowerCase()) {
            case "admin" -> "admin123".equals(password) ? "Administrador" : null;
            case "hr" -> "hr123".equals(password) ? "Recursos Humanos" : null;
            case "manager" -> "manager123".equals(password) ? "Manager" : null;
            case "empleado" -> "empleado123".equals(password) ? "Empleado" : null;
            default -> null;
        };
    }
}

