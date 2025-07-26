package model;

/**
 * Representa un nodo para una lista doblemente enlazada.
 * <p>
 * Cada nodo contiene un valor numérico y dos referencias: una al nodo
 * siguiente y otra al nodo anterior en la secuencia. La implementación de
 * la interfaz {@link INodo} asegura la compatibilidad con algoritmos de
 * recorrido genéricos.
 * </p>
 *
 * @see model.INodo
 * @see repository.ListaDobleRepositorio
 */
public class NodoDoble implements INodo {

    /**
     * El valor numérico (la "carga útil" o payload) que almacena el nodo.
     */
    private double valor;

    /**
     * Referencia al siguiente nodo en la lista.
     */
    private NodoDoble siguiente;

    /**
     * Referencia al nodo anterior en la lista.
     */
    private NodoDoble anterior;

    /**
     * Construye un nuevo nodo con un valor específico.
     * Las referencias a los nodos siguiente y anterior se inicializan como {@code null}.
     *
     * @param valor El valor de tipo double que almacenará el nodo.
     */
    public NodoDoble(double valor) {
        this.valor = valor;
        this.siguiente = null;
        this.anterior = null;
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
     * @return El siguiente {@code NodoDoble} en la lista, o {@code null} si es el final.
     */
    @Override
    public NodoDoble getSiguiente() {
        return this.siguiente;
    }

    /**
     * Devuelve una referencia al nodo previo en la secuencia de la lista.
     *
     * @return El {@code NodoDoble} anterior, o {@code null} si este es el primer nodo.
     */
    public NodoDoble getAnterior() {
        return this.anterior;
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
     * @param siguiente El {@code NodoDoble} que será el siguiente.
     */
    public void setSiguiente(NodoDoble siguiente) {
        this.siguiente = siguiente;
    }

    /**
     * Establece la referencia al nodo que precede a este en la lista.
     *
     * @param anterior El {@code NodoDoble} que será el anterior.
     */
    public void setAnterior(NodoDoble anterior) {
        this.anterior = anterior;
    }
}
