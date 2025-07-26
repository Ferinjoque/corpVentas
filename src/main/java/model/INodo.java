package model;

/**
 * Define un contrato genérico para los nodos utilizados en estructuras de listas enlazadas.
 * <p>
 * Esta interfaz es importante para aplicar polimorfismo en algoritmos de recorrido
 * y búsqueda. Permite que clases como {@link util.BuscadorListaUtil} operen sobre
 * diferentes implementaciones de nodos (ej. {@link NodoSimple}, {@link NodoDoble})
 * sin acoplarse a una clase concreta.
 * </p>
 *
 * @see NodoSimple
 * @see NodoDoble
 * @see util.BuscadorListaUtil
 */
public interface INodo {

    /**
     * Devuelve el valor numérico (la "carga útil" o payload) almacenado en el nodo.
     *
     * @return El valor de tipo double contenido en el nodo.
     */
    double getValor();

    /**
     * Devuelve una referencia al siguiente nodo en la secuencia de la lista.
     *
     * @return El siguiente nodo que implementa {@code INodo}, o {@code null} si este
     * es el último nodo de la lista.
     */
    INodo getSiguiente();
}
