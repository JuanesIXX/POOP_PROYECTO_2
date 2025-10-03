package org.example.gui;

public class ValidadorUtil {
    public static boolean validarCedula(String cedula) {
        if (cedula == null) {
            return false;
        }
        return cedula.matches("\\d{10}");
    }
}