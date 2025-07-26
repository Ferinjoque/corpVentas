package model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Representa la entidad de un Pedido dentro del sistema.
 * <p>
 * Esta clase encapsula la información esencial de un pedido, incluyendo un
 * ID único autoincremental, una descripción textual y su estado actual
 * ({@link EstadoPedido}) dentro del ciclo de vida del procesamiento.
 * </p>
 */
public class Pedido {

    /**
     * Contador estático y seguro para hilos (thread-safe) que genera IDs únicos
     * para cada nuevo pedido creado en la aplicación.
     */
    private static final AtomicInteger contadorId = new AtomicInteger(0);

    /**
     * El identificador numérico único del pedido.
     */
    private final int id;

    /**
     * El texto descriptivo del pedido.
     */
    private final String descripcion;

    /**
     * El estado actual del pedido dentro de su ciclo de vida (ej. PENDIENTE, EN_PROCESO).
     */
    private EstadoPedido estado;

    /**
     * Construye un nuevo pedido con una descripción.
     * Automáticamente le asigna un ID único y establece su estado inicial como PENDIENTE.
     *
     * @param descripcion El texto que describe el contenido o la solicitud del pedido.
     */
    public Pedido(String descripcion) {
        this.id = contadorId.incrementAndGet();
        this.descripcion = descripcion;
        this.estado = EstadoPedido.PENDIENTE; // Todo pedido comienza en estado pendiente.
    }

    // --- Getters y Setters ---

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }
}
