package model;

/**
 * Enumeración que define los tipos de estructuras de datos soportadas para la
 * implementación de los repositorios de la aplicación.
 * <p>
 * Es utilizada por la {@link util.RepositorioFactory} para determinar qué
 * implementación concreta de {@link RepositorioVentas} debe crear. También se usa
 * en la interfaz de usuario para permitir al usuario seleccionar la estructura
 * de datos subyacente en tiempo de ejecución.
 * </p>
 */
public enum TipoRepositorio {
    /**
     * Representa una implementación basada en un array de tamaño fijo.
     * Eficiente para acceso aleatorio (O(1)), pero ineficiente para
     * inserciones y eliminaciones al inicio (O(n)).
     */
    ARRAY("Array"),

    /**
     * Representa una implementación basada en una lista simplemente enlazada.
     * Eficiente para inserciones y eliminaciones al inicio (O(1)), pero
     * ineficiente para acceso aleatorio y operaciones al final (O(n)).
     */
    SIMPLE("Lista Simple"),

    /**
     * Representa una implementación basada en una lista doblemente enlazada.
     * Eficiente para operaciones en ambos extremos (inicio y final) (O(1)),
     * pero ineficiente para acceso aleatorio (O(n)).
     */
    DOBLE("Lista Doble");

    /**
     * El nombre legible del tipo de repositorio, para ser mostrado en la UI.
     */
    private final String displayName;

    /**
     * Constructor privado del enum.
     * @param displayName El nombre amigable para la interfaz de usuario.
     */
    TipoRepositorio(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Devuelve el nombre legible para ser mostrado en la interfaz.
     * Este método es invocado por los componentes de JavaFX para su visualización.
     * @return El nombre para mostrar en la UI (ej. "Lista Doble").
     */
    @Override
    public String toString() {
        return displayName;
    }
}
