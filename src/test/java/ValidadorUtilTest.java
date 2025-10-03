import org.example.gui.ValidadorUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidadorUtilTest {

    // La etiqueta @Test le dice a JUnit que esto es una prueba.
    @Test
    void prueba_de_una_cedula_valida() {

        //
        String cedulaValida = "1234567890";

        //  .
        boolean resultado = ValidadorUtil.validarCedula(cedulaValida);

        //
        assertTrue(resultado);
    }
}
