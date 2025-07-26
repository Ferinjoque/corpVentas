package repository;

import model.INodo;
import model.NodoDoble;
import model.OperacionesAvanzadasLista;
import model.RepositorioVentas;
import util.BuscadorListaUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementa las interfaces {@link RepositorioVentas} y {@link OperacionesAvanzadasLista}
 * utilizando una <b>lista doblemente enlazada</b>.
 * <p>
 * Esta implementación mantiene punteros a la cabeza (primer elemento) y la cola
 * (último elemento), lo que permite operaciones de inserción y eliminación en
 * ambos extremos de la lista con una complejidad de tiempo constante O(1).
 * </p>
 */
public class ListaDobleRepositorio implements RepositorioVentas, OperacionesAvanzadasLista {

    private NodoDoble cabeza;
    private NodoDoble cola;
    private int count;

    public ListaDobleRepositorio() {
        this.cabeza = null;
        this.cola = null;
        this.count = 0;
    }

    //region Implementación de RepositorioVentas (CRUD)
    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(1)</b> - La operación es de tiempo constante gracias
     * al puntero {@code cola}.</p>
     */
    @Override
    public boolean agregarAlFinal(double valor) {
        NodoDoble nuevoNodo = new NodoDoble(valor);
        if (cabeza == null) {
            cabeza = cola = nuevoNodo;
        } else {
            cola.setSiguiente(nuevoNodo);
            nuevoNodo.setAnterior(cola);
            cola = nuevoNodo;
        }
        count++;
        return true;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(1)</b> - La operación es de tiempo constante gracias
     * al puntero {@code cabeza}.</p>
     */
    @Override
    public boolean agregarAlInicio(double valor) {
        NodoDoble nuevoNodo = new NodoDoble(valor);
        if (cabeza == null) {
            cabeza = cola = nuevoNodo;
        } else {
            nuevoNodo.setSiguiente(cabeza);
            cabeza.setAnterior(nuevoNodo);
            cabeza = nuevoNodo;
        }
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
        if (indice == count - 1) {
            return agregarAlFinal(valor); // Optimización para el último elemento.
        }

        NodoDoble nuevoNodo = new NodoDoble(valor);
        NodoDoble nodoActual = obtenerNodoEn(indice); // Búsqueda O(n)
        NodoDoble nodoSiguiente = (NodoDoble) nodoActual.getSiguiente();

        // Reenlazar los punteros para insertar el nuevo nodo.
        nuevoNodo.setSiguiente(nodoSiguiente);
        nuevoNodo.setAnterior(nodoActual);
        nodoActual.setSiguiente(nuevoNodo);
        nodoSiguiente.setAnterior(nuevoNodo);

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
     * <p><b>Complejidad: O(n)</b> - En el peor de los casos (nodo en el medio),
     * la búsqueda del nodo es O(n). La eliminación de los extremos es O(1).</p>
     */
    @Override
    public boolean eliminar(int indice) {
        if (indice < 0 || indice >= count) {
            return false;
        }

        if (count == 1) { // Caso: eliminando el único elemento.
            cabeza = cola = null;
        } else if (indice == 0) { // Caso: eliminando la cabeza.
            cabeza = (NodoDoble) cabeza.getSiguiente();
            cabeza.setAnterior(null);
        } else if (indice == count - 1) { // Caso: eliminando la cola.
            cola = cola.getAnterior();
            cola.setSiguiente(null);
        } else { // Caso: eliminando un nodo intermedio.
            NodoDoble nodoAEliminar = obtenerNodoEn(indice);
            nodoAEliminar.getAnterior().setSiguiente(nodoAEliminar.getSiguiente());
            ((NodoDoble) nodoAEliminar.getSiguiente()).setAnterior(nodoAEliminar.getAnterior());
        }
        count--;
        return true;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(1)</b></p>
     */
    @Override
    public boolean eliminarAlInicio() {
        if (count == 0) return false;
        return eliminar(0);
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(1)</b> - Gracias al puntero {@code cola}.</p>
     */
    @Override
    public boolean eliminarAlFinal() {
        if (count == 0) return false;
        return eliminar(count - 1);
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(n)</b> - Requiere buscar el nodo en el índice.</p>
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
     * <p><b>Complejidad: O(n)</b> - Debe recorrer toda la lista.</p>
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
     * <p><b>Complejidad: O(n)</b> - Recorre toda la lista una vez para intercambiar
     * los punteros {@code anterior} y {@code siguiente} de cada nodo.</p>
     */
    @Override
    public void invertir() {
        if (cabeza == null || cabeza.getSiguiente() == null) {
            return; // No hay nada que invertir.
        }

        NodoDoble temp = null;
        NodoDoble actual = cabeza;

        // Intercambia el puntero anterior y siguiente para cada nodo.
        while (actual != null) {
            temp = actual.getAnterior();
            actual.setAnterior((NodoDoble) actual.getSiguiente());
            actual.setSiguiente(temp);
            // Avanza al siguiente nodo, que ahora es el 'anterior' original.
            actual = actual.getAnterior();
        }

        // Intercambia los punteros de cabeza y cola de la lista.
        NodoDoble antiguaCabeza = cabeza;
        cabeza = cola;
        cola = antiguaCabeza;
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
     * Método de ayuda optimizado para encontrar un nodo en un índice específico.
     * <p>
     * Si el índice está en la primera mitad de la lista, busca desde la {@code cabeza}.
     * Si está en la segunda mitad, busca desde la {@code cola} para reducir el
     * número de pasos a un máximo de n/2.
     * </p>
     * @param indice El índice del nodo a obtener.
     * @return El NodoDoble en la posición dada.
     */
    private NodoDoble obtenerNodoEn(int indice) {
        if (indice < count / 2) {
            // Busca desde el principio si el índice está en la primera mitad.
            NodoDoble p = cabeza;
            for (int k = 0; k < indice; k++) {
                p = (NodoDoble) p.getSiguiente();
            }
            return p;
        } else {
            // Busca desde el final si el índice está en la segunda mitad.
            NodoDoble p = cola;
            for (int k = count - 1; k > indice; k--) {
                p = p.getAnterior();
            }
            return p;
        }
    }
    //endregion
}
