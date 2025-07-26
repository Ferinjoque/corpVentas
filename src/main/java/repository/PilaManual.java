package repository;

import model.NodoPila;
import java.util.EmptyStackException;

/**
 * Implementación manual de una Pila genérica usando una lista enlazada.
 * Sigue el principio <b>LIFO (Last-In, First-Out)</b>.
 * <p>
 * Todas las operaciones principales (push, pop, peek) se realizan en tiempo
 * constante O(1) al operar directamente sobre la cima de la pila (la cabeza
 * de la lista).
 * </p>
 *
 * @param <T> El tipo de dato que almacenará la pila.
 */
public class PilaManual<T> {

    /**
     * Puntero al nodo que se encuentra en la cima de la pila (la cabeza de la lista).
     */
    private NodoPila<T> top;

    /**
     * Número de elementos actualmente en la pila.
     */
    private int size;

    public PilaManual() {
        this.top = null;
        this.size = 0;
    }

    /**
     * Inserta un elemento en la cima de la pila (push).
     * <p><b>Complejidad: O(1)</b></p>
     * @param valor El valor a insertar.
     */
    public void push(T valor) {
        NodoPila<T> nuevoNodo = new NodoPila<>(valor);
        if (!isEmpty()) {
            nuevoNodo.setSiguiente(top);
        }
        top = nuevoNodo;
        size++;
    }

    /**
     * Elimina y devuelve el elemento en la cima de la pila (pop).
     * <p><b>Complejidad: O(1)</b></p>
     * @return El elemento eliminado.
     * @throws EmptyStackException si la pila está vacía.
     */
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T valor = top.getValor();
        top = top.getSiguiente();
        size--;
        return valor;
    }

    /**
     * Devuelve el elemento en la cima de la pila sin eliminarlo (peek).
     * <p><b>Complejidad: O(1)</b></p>
     * @return El elemento en la cima.
     * @throws EmptyStackException si la pila está vacía.
     */
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return top.getValor();
    }

    /**
     * Comprueba si la pila está vacía.
     * @return {@code true} si la pila no tiene elementos.
     */
    public boolean isEmpty() {
        return top == null;
    }

    /**
     * Devuelve el número de elementos en la pila.
     * @return El tamaño de la pila.
     */
    public int size() {
        return size;
    }
}
