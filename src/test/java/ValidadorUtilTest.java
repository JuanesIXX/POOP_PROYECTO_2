import org.example.gui.ValidadorUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidadorUtilTest {

    // La etiqueta @Test le dice a JUnit que esto es una prueba.
    @Test
    void prueba_de_una_cedula_valida() {

        // 1. Arrange (Preparar): Preparamos los datos que vamos a usar.
        String cedulaValida = "1234567890";

        // 2. Act (Actuar): Llamamos al método que queremos probar.
        boolean resultado = ValidadorUtil.validarCedula(cedulaValida);

        // 3. Assert (Verificar): Comprobamos si el resultado fue el esperado.
        // Aquí decimos: "Afirmo que el 'resultado' debe ser verdadero".
        assertTrue(resultado);
    }
}
