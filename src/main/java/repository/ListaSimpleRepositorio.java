package repository;

import model.INodo;
import model.NodoSimple;
import model.OperacionesAvanzadasLista;
import model.RepositorioVentas;
import util.BuscadorListaUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementa las interfaces {@link RepositorioVentas} y {@link OperacionesAvanzadasLista}
 * utilizando una <b>lista simplemente enlazada</b>.
 * <p>
 * Esta implementación solo mantiene un puntero a la cabeza (primer elemento),
 * lo que hace que las operaciones al inicio de la lista sean muy eficientes (O(1)),
 * pero las operaciones que requieren acceder al final de la lista son ineficientes (O(n))
 * ya que se debe recorrer toda la estructura.
 * </p>
 */
public class ListaSimpleRepositorio implements RepositorioVentas, OperacionesAvanzadasLista {

    /**
     * Puntero al primer nodo de la lista.
     */
    private NodoSimple cabeza;

    /**
     * Número de elementos actualmente en la lista.
     */
    private int count;

    public ListaSimpleRepositorio() {
        this.cabeza = null;
        this.count = 0;
    }

    //region Implementación de RepositorioVentas (CRUD)
    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(n)</b> - Requiere recorrer toda la lista para encontrar
     * el último nodo antes de poder insertar el nuevo elemento.</p>
     */
    @Override
    public boolean agregarAlFinal(double valor) {
        NodoSimple nuevoNodo = new NodoSimple(valor);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            NodoSimple p = cabeza;
            while (p.getSiguiente() != null) {
                p = (NodoSimple) p.getSiguiente();
            }
            p.setSiguiente(nuevoNodo);
        }
        count++;
        return true;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(1)</b> - La operación es de tiempo constante ya que solo
     * implica la actualización del puntero {@code cabeza}.</p>
     */
    @Override
    public boolean agregarAlInicio(double valor) {
        NodoSimple nuevoNodo = new NodoSimple(valor);
        nuevoNodo.setSiguiente(cabeza);
        cabeza = nuevoNodo;
        count++;
        return true;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(n)</b> - La búsqueda del nodo en el índice especificado
     * es O(n), aunque la inserción en sí misma es O(1).</p>
     */
    @Override
    public boolean insertarDespuesDe(int indice, double valor) {
        if (indice < 0 || indice >= count) {
            return false; // Índice inválido.
        }

        NodoSimple nuevoNodo = new NodoSimple(valor);
        NodoSimple nodoActual = obtenerNodoEn(indice); // Búsqueda O(n)

        nuevoNodo.setSiguiente((NodoSimple) nodoActual.getSiguiente());
        nodoActual.setSiguiente(nuevoNodo);

        count++;
        return true;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(n)</b> - Requiere buscar el nodo en el índice especificado.</p>
     */
    @Override
    public boolean actualizar(int indice, double nuevoValor) {
        if (indice < 0 || indice >= count) {
            return false;
        }
        obtenerNodoEn(indice).setValor(nuevoValor);
        return true;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(n)</b> - En el peor de los casos, requiere buscar el nodo
     * anterior al que se va a eliminar.</p>
     */
    @Override
    public boolean eliminar(int indice) {
        if (indice < 0 || indice >= count) {
            return false;
        }
        if (indice == 0) { // Caso especial: eliminar la cabeza (O(1)).
            cabeza = (NodoSimple) cabeza.getSiguiente();
        } else {
            // Se busca el nodo *anterior* al que se va a eliminar (O(n)).
            NodoSimple nodoAnterior = obtenerNodoEn(indice - 1);
            NodoSimple nodoAEliminar = (NodoSimple) nodoAnterior.getSiguiente();
            nodoAnterior.setSiguiente((NodoSimple) nodoAEliminar.getSiguiente());
        }
        count--;
        return true;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(1)</b> - Solo actualiza el puntero {@code cabeza}.</p>
     */
    @Override
    public boolean eliminarAlInicio() {
        if (count == 0) {
            return false;
        }
        cabeza = (NodoSimple) cabeza.getSiguiente();
        count--;
        return true;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(n)</b> - Requiere recorrer la lista hasta el penúltimo
     * elemento para poder desvincular el último.</p>
     */
    @Override
    public boolean eliminarAlFinal() {
        if (count == 0) {
            return false;
        }
        if (count == 1) {
            cabeza = null;
        } else {
            // Se debe recorrer hasta el penúltimo nodo.
            NodoSimple penultimo = obtenerNodoEn(count - 2);
            penultimo.setSiguiente(null);
        }
        count--;
        return true;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(n)</b> - Requiere recorrer la lista para encontrar el nodo.</p>
     */
    @Override
    public double obtener(int indice) {
        if (indice < 0 || indice >= count) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice + ", tamaño actual: " + count);
        }
        return obtenerNodoEn(indice).getValor();
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(n)</b> - Debe recorrer todos los elementos para crear la lista.</p>
     */
    @Override
    public List<Double> obtenerTodos() {
        List<Double> lista = new ArrayList<>();
        INodo p = cabeza;
        while (p != null) {
            lista.add(p.getValor());
            p = p.getSiguiente();
        }
        return lista;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(1)</b></p>
     */
    @Override
    public int tamano() {
        return count;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(n)</b> - Delega a una búsqueda lineal.</p>
     */
    @Override
    public int buscarIndiceDe(double valor) {
        return BuscadorListaUtil.buscarIndiceDe(this.cabeza, valor);
    }

    /**
     * {@inheritDoc}
     * @return Siempre {@code true}, ya que esta implementación soporta todas las operaciones avanzadas.
     */
    @Override
    public boolean soportaOperacionesAvanzadas() {
        return true;
    }
    //endregion

    //region Implementación de OperacionesAvanzadasLista
    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(n)</b> - Recorre toda la lista una vez para reasignar
     * los punteros {@code siguiente} de cada nodo.</p>
     */
    @Override
    public void invertir() {
        NodoSimple prev = null;
        NodoSimple curr = cabeza;
        while (curr != null) {
            NodoSimple nextTemp = (NodoSimple) curr.getSiguiente();
            curr.setSiguiente(prev);
            prev = curr;
            curr = nextTemp;
        }
        cabeza = prev;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(n)</b> - Delega a una búsqueda lineal.</p>
     */
    @Override
    public int buscarPrimeroMayor(double umbral) {
        return BuscadorListaUtil.buscarPrimeroMayor(this.cabeza, umbral);
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(n^2)</b> - Delega a un algoritmo de búsqueda de duplicados anidado.</p>
     */
    @Override
    public List<Integer> encontrarIndicesDeDuplicados() {
        return BuscadorListaUtil.encontrarIndicesDeDuplicados(this.cabeza);
    }
    //endregion

    //region Métodos de Ayuda (Privados)
    /**
     * Método de ayuda que recorre la lista desde la cabeza hasta encontrar el
     * nodo en un índice específico.
     * @param indice El índice del nodo a obtener.
     * @return El {@code NodoSimple} en la posición dada.
     */
    private NodoSimple obtenerNodoEn(int indice) {
        NodoSimple p = cabeza;
        for (int k = 0; k < indice; k++) {
            p = (NodoSimple) p.getSiguiente();
        }
        return p;
    }
    //endregion
}
