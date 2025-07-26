package model;

/**
 * Define los posibles estados de un {@link Pedido} a lo largo de su ciclo de vida.
 * <p>
 * Esta enumeración proporciona una forma segura y legible de gestionar y verificar
 * el estado de un pedido, evitando el uso de constantes de cadena (magic strings)
 * y mejorando la robustez del sistema.
 * </p>
 */
public enum EstadoPedido {
    /**
     * El pedido ha sido creado y está en la cola, esperando ser procesado.
     */
    PENDIENTE("Pendiente"),

    /**
     * El pedido ha sido extraído de la cola y está siendo atendido actualmente.
     */
    EN_PROCESO("En Proceso"),

    /**
     * El pedido ha sido procesado y finalizado con éxito.
     */
    COMPLETADO("Completado"),

    /**
     * El pedido ha sido cancelado antes de ser completado.
     */
    CANCELADO("Cancelado");

    /**
     * El nombre legible del estado para ser mostrado en la interfaz de usuario.
     */
    private final String displayName;

    /**
     * Constructor privado para inicializar cada constante del enum con su nombre de visualización.
     * @param displayName El texto que representará al estado en la UI.
     */
    EstadoPedido(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Devuelve la representación textual del estado.
     * Este método es invocado por los componentes de JavaFX para mostrar el valor.
     *
     * @return El nombre legible del estado (ej. "En Proceso").
     */
    @Override
    public String toString() {
        return displayName;
    }
}
