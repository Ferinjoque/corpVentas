package model;

/**
 * Enumeración que define los tipos de entidades de negocio principales.
 * <p>
 * Proporciona una forma segura y legible de distinguir entre los
 * conjuntos de datos de "Ventas" y "Objetivos" a lo largo de la aplicación.
 * Su uso evita el empleo de constantes de cadena ("magic strings"), mejorando
 * la robustez y la mantenibilidad del código.
 * </p>
 */
public enum TipoEntidad {
    /**
     * Representa el conjunto de datos de ventas reales.
     */
    VENTAS("Ventas"),

    /**
     * Representa el conjunto de datos de objetivos de ventas.
     */
    OBJETIVOS("Objetivos");

    /**
     * El nombre legible del tipo de entidad, para ser mostrado en la UI.
     */
    private final String displayName;

    /**
     * Constructor privado del enum.
     * @param displayName El nombre amigable para la interfaz de usuario.
     */
    TipoEntidad(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Devuelve el nombre legible para ser mostrado en la interfaz.
     * Este método es invocado por los componentes de JavaFX para su visualización.
     * @return El nombre para mostrar en la UI (ej. "Ventas").
     */
    @Override
    public String toString() {
        return this.displayName;
    }
}
