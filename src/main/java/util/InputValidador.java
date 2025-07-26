package util;

/**
 * Clase de utilidad que proporciona métodos estáticos para validar y convertir
 * entradas de usuario (Strings) a tipos numéricos, lanzando excepciones
 * personalizadas en caso de error. No puede ser instanciada.
 */
public final class InputValidador {

    /**
     * Constructor privado para prevenir la instanciación de esta clase de utilidad.
     */
    private InputValidador() {}

    /**
     * Valida que una cadena de texto no sea nula, vacía o contenga solo espacios en blanco.
     * @param input La cadena a validar.
     * @param fieldName El nombre descriptivo del campo para usar en el mensaje de error.
     * @return La cadena original sin espacios al principio o al final, si es válida.
     * @throws ValidacionExcepcion si la cadena es nula o está vacía.
     */
    public static String validateNotEmpty(String input, String fieldName) throws ValidacionExcepcion {
        if (input == null || input.trim().isEmpty()) {
            throw new ValidacionExcepcion("El campo '" + fieldName + "' no puede estar vacío.");
        }
        return input.trim();
    }

    /**
     * Convierte una cadena de texto a un {@code double} y verifica que no sea negativo.
     * @param in La cadena de texto a convertir.
     * @return El valor {@code double} convertido.
     * @throws ValidacionExcepcion si la cadena no es un número válido o es un número negativo.
     */
    public static double parseNonNegativeDouble(String in) throws ValidacionExcepcion {
        String validatedInput = validateNotEmpty(in, "valor numérico");
        try {
            double d = Double.parseDouble(validatedInput);
            if (d < 0) {
                throw new ValidacionExcepcion("El valor no puede ser negativo.");
            }
            return d;
        } catch (NumberFormatException e) {
            throw new ValidacionExcepcion("El valor introducido ('" + in + "') no es un número válido.");
        }
    }

    /**
     * Convierte una cadena de texto a un {@code double} y verifica que esté dentro de un rango específico.
     *
     * @param in La cadena de texto a convertir.
     * @param fieldName El nombre descriptivo del campo para los mensajes de error.
     * @param min El valor mínimo permitido (inclusivo).
     * @param max El valor máximo permitido (inclusivo).
     * @return El valor {@code double} convertido.
     * @throws ValidacionExcepcion si la cadena no es un número válido o está fuera del rango especificado.
     */
    public static double parseDoubleInRange(String in, String fieldName, double min, double max) throws ValidacionExcepcion {
        String validatedInput = validateNotEmpty(in, fieldName);
        try {
            double d = Double.parseDouble(validatedInput);
            if (d < min || d > max) {
                throw new ValidacionExcepcion("El " + fieldName + " debe estar entre " + String.format("%,.2f", min) + " y " + String.format("%,.2f", max) + ".");
            }
            return d;
        } catch (NumberFormatException e) {
            throw new ValidacionExcepcion("El valor para " + fieldName + " ('" + in + "') no es un número válido.");
        }
    }
}
