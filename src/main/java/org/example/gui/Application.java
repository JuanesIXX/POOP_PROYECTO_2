// =============================================================================
// ARCHIVO: Application.java (Clase Principal para iniciar la GUI)
// UBICACIÓN: org.example.gui.Application.java
// =============================================================================
package org.example.gui;

import org.example.GestorRRHH;
import javax.swing.*;

public class Application {
    public static void main(String[] args) {
        System.out.println("==> 1. El programa ha iniciado en main().");
        // Establecer look and feel del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Inicializar gestor
        GestorRRHH gestor = new GestorRRHH();

        // Lanzar ventana de login en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            System.out.println("==> 2. Creando el LoginForm.");
            LoginForm loginForm = new LoginForm(gestor);
            loginForm.setVisible(true);
        });
    }
}

// 