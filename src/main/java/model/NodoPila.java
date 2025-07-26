package model;

/**
 * Representa un nodo genérico para ser utilizado en una Pila
 * implementada manualmente mediante una lista enlazada simple.
 *
 * @param <T> El tipo de dato que almacenará el nodo.
 * @see repository.PilaManual
 */
public class NodoPila<T> {

    /**
     * El valor o "carga útil" (payload) de tipo genérico que almacena el nodo.
     * Es final para asegurar que el valor no cambie una vez creado el nodo.
     */
    private final T valor;

    /**
     * Referencia al siguiente nodo en la secuencia de la pila (el nodo que está "debajo").
     */
    private NodoPila<T> siguiente;

    /**
     * Construye un nuevo nodo con un valor específico.
     * La referencia al siguiente nodo se inicializa como {@code null}.
     *
     * @param valor El valor de tipo {@code T} que almacenará el nodo.
     */
    public NodoPila(T valor) {
        this.valor = valor;
        this.siguiente = null;
    }

    // --- Getters y Setters ---

    public T getValor() {
        return valor;
    }

    public NodoPila<T> getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoPila<T> siguiente) {
        this.siguiente = siguiente;
    }
}
