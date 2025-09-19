package org.example;

import java.util.Base64;

public class SeguridadUtil {
    // Clave simple para encriptación (cambiar en producción)
    private static final String CLAVE = "MiClaveSecreta2024";

    /**
     * Encripta texto usando XOR + Base64
     */
    public static String encriptar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }

        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            char clave = CLAVE.charAt(i % CLAVE.length());
            resultado.append((char) (c ^ clave));
        }

        return Base64.getEncoder().encodeToString(resultado.toString().getBytes());
    }

    /**
     * Desencripta texto
     */
    public static String desencriptar(String textoEncriptado) {
        if (textoEncriptado == null || textoEncriptado.isEmpty()) {
            return textoEncriptado;
        }

        try {
            String texto = new String(Base64.getDecoder().decode(textoEncriptado));
            StringBuilder resultado = new StringBuilder();

            for (int i = 0; i < texto.length(); i++) {
                char c = texto.charAt(i);
                char clave = CLAVE.charAt(i % CLAVE.length());
                resultado.append((char) (c ^ clave));
            }

            return resultado.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al desencriptar datos");
        }
    }

    /**
     * Valida y limpia el input para prevenir inyecciones
     */
    public static String validarInput(String input) {
        if (input == null) return null;

        //  caracteres no permitidos
        String limpio = input.trim()
                .replaceAll("[<>\"'&;]", "")  // Caracteres
                .replaceAll("--", "")         // Comentarios
                .replaceAll("/\\*.*\\*/", ""); // Comentarios

        // SEGURIDAD - SE LIMITA   longitud para prevenir ataques de buffer overflow
        if (limpio.length() > 100) {
            limpio = limpio.substring(0, 100);
        }

        return limpio;
    }

    /**
     *  validar cedula
     */
    public static boolean validarcedula(String cedula) {
        if (cedula == null) return false;
        return cedula.matches("\\d{8} ");
    }

    /**
     * Valida que el salario sea positivo y en rango razonable
     */
    public static boolean validarSalario(double salario) {
        return salario > 0 && salario <= 999999; // Máximo razonable
    }

    /**
     * cedula enmascarada x seguridad
     */
    public static String enmascararcedula(String cedula) {
        if (cedula == null || cedula.length() < 4) return "****";
        return "****" + cedula.substring(cedula.length() - 4);
    }
}
