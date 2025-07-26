package util;

/**
 * Excepción de tipo "checked" que se lanza para señalar errores específicos
 * de validación en las entradas del usuario.
 *
 * <p>Al ser una excepción personalizada y de tipo "checked", obliga a las capas
 * superiores (como los controladores) a capturar y manejar los errores de
 * validación de forma explícita. Esto permite diferenciar claramente los
 * errores de entrada del usuario de otros errores inesperados en tiempo de
 * ejecución.</p>
 */
public class ValidacionExcepcion extends Exception {

    /**
     * Construye una nueva excepción de validación con un mensaje de error detallado.
     *
     * @param message El mensaje que describe la causa específica del error de validación,
     * destinado a ser mostrado al usuario.
     */
    public ValidacionExcepcion(String message) {
        super(message);
    }
}
