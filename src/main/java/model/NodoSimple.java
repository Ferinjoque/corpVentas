package model;

/**
 * Representa un nodo para una lista simplemente enlazada.
 * <p>
 * Cada nodo contiene un valor numérico y una única referencia al siguiente nodo
 * en la secuencia. La implementación de la interfaz {@link INodo} asegura la
 * compatibilidad con algoritmos genéricos que operan sobre listas.
 * </p>
 *
 * @see model.INodo
 * @see repository.ListaSimpleRepositorio
 */
public class NodoSimple implements INodo {

    /**
     * El valor numérico (la "carga útil" o payload) que almacena el nodo.
     */
    private double valor;

    /**
     * Referencia al siguiente nodo en la secuencia de la lista.
     */
    private NodoSimple siguiente;

    /**
     * Construye un nuevo nodo con un valor específico.
     * La referencia al siguiente nodo se inicializa como {@code null}.
     *
     * @param valor El valor de tipo double que almacenará el nodo.
     */
    public NodoSimple(double valor) {
        this.valor = valor;
        this.siguiente = null;
    }

    // --- Getters ---

    /**
     * {@inheritDoc}
     */
    @Override
    public double getValor() {
        return this.valor;
    }

    /**
     * {@inheritDoc}
     *
     * @return El siguiente {@code NodoSimple} en la lista, o {@code null} si es el final.
     */
    @Override
    public NodoSimple getSiguiente() {
        return this.siguiente;
    }

    // --- Setters ---

    /**
     * Actualiza el valor almacenado en el nodo.
     *
     * @param valor El nuevo valor de tipo double.
     */
    public void setValor(double valor) {
        this.valor = valor;
    }

    /**
     * Establece la referencia al nodo que seguirá a este en la lista.
     *
     * @param siguiente El {@code NodoSimple} que será el siguiente.
     */
    public void setSiguiente(NodoSimple siguiente) {
        this.siguiente = siguiente;
    }
}
