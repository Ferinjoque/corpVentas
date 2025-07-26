package repository;

import model.NodoCola;
import java.util.NoSuchElementException;

/**
 * Implementación manual de una Cola genérica usando una lista enlazada.
 * Sigue el principio <b>FIFO (First-In, First-Out)</b>.
 * <p>
 * Esta implementación utiliza punteros al frente (cabeza) y al final (cola)
 * de la lista para lograr operaciones de encolar y desencolar en tiempo constante O(1).
 * </p>
 *
 * @param <T> El tipo de dato que almacenará la cola.
 */
public class ColaManual<T> {
    /**
     * Puntero al primer nodo de la cola (la cabeza).
     */
    private NodoCola<T> frente;

    /**
     * Puntero al último nodo de la cola (la cola).
     */
    private NodoCola<T> fin;

    /**
     * Número de elementos actualmente en la cola.
     */
    private int size;

    public ColaManual() {
        this.frente = null;
        this.fin = null;
        this.size = 0;
    }

    /**
     * Inserta un elemento al final de la cola (enqueue).
     * <p><b>Complejidad: O(1)</b> - La operación es de tiempo constante gracias
     * al puntero {@code fin}.</p>
     * @param valor El valor a encolar.
     */
    public void encolar(T valor) {
        NodoCola<T> nuevoNodo = new NodoCola<>(valor);
        if (estaVacia()) {
            frente = nuevoNodo;
        } else {
            fin.setSiguiente(nuevoNodo);
        }
        fin = nuevoNodo;
        size++;
    }

    /**
     * Elimina y devuelve el elemento del frente de la cola (dequeue).
     * <p><b>Complejidad: O(1)</b> - La operación es de tiempo constante gracias
     * al puntero {@code frente}.</p>
     * @return El elemento desencolado.
     * @throws NoSuchElementException si la cola está vacía.
     */
    public T desencolar() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola está vacía.");
        }
        T valor = frente.getValor();
        frente = frente.getSiguiente();
        // Si la cola queda vacía, el puntero 'fin' también debe ser null.
        if (frente == null) {
            fin = null;
        }
        size--;
        return valor;
    }

    /**
     * Comprueba si la cola está vacía.
     * @return {@code true} si la cola no tiene elementos.
     */
    public boolean estaVacia() {
        return frente == null;
    }

    /**
     * Devuelve el número de elementos en la cola.
     * @return El tamaño de la cola.
     */
    public int size() {
        return size;
    }

    /**
     * Genera una representación en String de la cola para visualización y depuración.
     * @return Un String con el contenido de la cola.
     */
    @Override
    public String toString() {
        if (estaVacia()) {
            return "[ Cola Vacía ]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Frente -> ");
        NodoCola<T> actual = frente;
        while (actual != null) {
            sb.append("[").append(actual.getValor()).append("] -> ");
            actual = actual.getSiguiente();
        }
        sb.append("Fin");
        return sb.toString();
    }
}
