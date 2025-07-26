package repository;

import model.NodoArbol;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de un Árbol Binario de Búsqueda (ABB).
 * <p>
 * Esta estructura de datos mantiene sus elementos ordenados, lo que permite
 * búsquedas, inserciones y eliminaciones eficientes (con una complejidad promedio
 * de O(log n)). Esta implementación maneja valores duplicados incrementando un
 * contador de frecuencia en el nodo existente, en lugar de insertar nodos repetidos.
 * </p>
 */
public class ArbolBinarioBusqueda {

    private NodoArbol raiz;
    private boolean eliminacionExitosa; // Flag para rastrear el resultado de la eliminación recursiva.

    public ArbolBinarioBusqueda() {
        this.raiz = null;
    }

    /**
     * Clase interna estática que encapsula los resultados de una búsqueda detallada.
     * Funciona como un DTO para devolver múltiples datos sobre el nodo encontrado.
     */
    public static class ResultadoBusqueda {
        public final NodoArbol nodoEncontrado;
        public final NodoArbol nodoPadre;
        public final int nivel; // Nivel del nodo (la raíz es nivel 0).
        public final String posicion; // "Raíz", "Hijo Izquierdo" o "Hijo Derecho".

        public ResultadoBusqueda(NodoArbol nodoEncontrado, NodoArbol nodoPadre, int nivel, String posicion) {
            this.nodoEncontrado = nodoEncontrado;
            this.nodoPadre = nodoPadre;
            this.nivel = nivel;
            this.posicion = posicion;
        }
    }

    //region API Pública
    public NodoArbol obtenerRaiz() {
        return this.raiz;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    /**
     * Punto de entrada público para insertar un nuevo valor en el árbol.
     * @param valor El valor a insertar.
     */
    public void insertar(double valor) {
        raiz = insertarRecursivo(raiz, valor);
    }

    /**
     * Realiza un recorrido In-Orden del árbol (Izquierdo, Raíz, Derecho),
     * lo que resulta en una lista ordenada de los valores.
     * @return Una lista de strings representando los nodos en orden ascendente.
     */
    public List<String> recorridoInorden() {
        List<String> resultado = new ArrayList<>();
        inordenRecursivo(raiz, resultado);
        return resultado;
    }

    /**
     * Realiza un recorrido Pre-Orden del árbol (Raíz, Izquierdo, Derecho).
     * @return Una lista de strings representando los nodos en pre-orden.
     */
    public List<String> recorridoPreorden() {
        List<String> resultado = new ArrayList<>();
        preordenRecursivo(raiz, resultado);
        return resultado;
    }

    /**
     * Realiza un recorrido Post-Orden del árbol (Izquierdo, Derecho, Raíz).
     * @return Una lista de strings representando los nodos en post-orden.
     */
    public List<String> recorridoPostorden() {
        List<String> resultado = new ArrayList<>();
        postordenRecursivo(raiz, resultado);
        return resultado;
    }

    /**
     * Busca un nodo en el árbol y devuelve un objeto con detalles sobre su posición y contexto.
     * @param valor El valor a buscar.
     * @return Un objeto {@link ResultadoBusqueda} si se encuentra, o {@code null} si no.
     */
    public ResultadoBusqueda buscarConDetalles(double valor) {
        NodoArbol actual = raiz;
        NodoArbol padre = null;
        int nivel = 0;

        while (actual != null) {
            if (valor == actual.getValor()) {
                String posicion = (padre == null) ? "Raíz del árbol"
                        : (padre.getIzquierdo() == actual ? "Hijo Izquierdo" : "Hijo Derecho");
                return new ResultadoBusqueda(actual, padre, nivel, posicion);
            }

            padre = actual;
            if (valor < actual.getValor()) {
                actual = actual.getIzquierdo();
            } else {
                actual = actual.getDerecho();
            }
            nivel++;
        }
        return null; // El valor no se encontró.
    }

    /**
     * Punto de entrada público para eliminar un valor del árbol.
     * @param valor El valor a eliminar.
     * @return {@code true} si el valor fue encontrado y eliminado, {@code false} en caso contrario.
     */
    public boolean eliminar(double valor) {
        eliminacionExitosa = false; // Resetea el flag antes de la operación.
        raiz = eliminarRecursivo(raiz, valor);
        return eliminacionExitosa;
    }
    //endregion

    //region Implementaciones Recursivas
    private NodoArbol insertarRecursivo(NodoArbol nodo, double valor) {
        if (nodo == null) {
            return new NodoArbol(valor);
        }

        if (valor < nodo.getValor()) {
            nodo.setIzquierdo(insertarRecursivo(nodo.getIzquierdo(), valor));
        } else if (valor > nodo.getValor()) {
            nodo.setDerecho(insertarRecursivo(nodo.getDerecho(), valor));
        } else {
            // El valor ya existe, se incrementa la frecuencia en lugar de insertar un duplicado.
            nodo.incrementarFrecuencia();
        }
        return nodo;
    }

    private NodoArbol eliminarRecursivo(NodoArbol nodo, double valor) {
        if (nodo == null) {
            return null; // El valor no está en el árbol.
        }

        // Busca el nodo a eliminar.
        if (valor < nodo.getValor()) {
            nodo.setIzquierdo(eliminarRecursivo(nodo.getIzquierdo(), valor));
        } else if (valor > nodo.getValor()) {
            nodo.setDerecho(eliminarRecursivo(nodo.getDerecho(), valor));
        } else {
            // Nodo encontrado.
            eliminacionExitosa = true;

            // Caso 1: El nodo es una hoja (sin hijos).
            if (nodo.getIzquierdo() == null && nodo.getDerecho() == null) {
                return null;
            }
            // Caso 2: El nodo tiene un solo hijo.
            if (nodo.getDerecho() == null) {
                return nodo.getIzquierdo();
            }
            if (nodo.getIzquierdo() == null) {
                return nodo.getDerecho();
            }
            // Caso 3: El nodo tiene dos hijos.
            // Se busca el sucesor in-orden (el menor valor en el subárbol derecho).
            NodoArbol sucesor = encontrarSucesor(nodo.getDerecho());
            // Se crea un nuevo nodo con el valor del sucesor para reemplazar el nodo actual.
            NodoArbol nuevoNodo = new NodoArbol(sucesor.getValor());
            nuevoNodo.setIzquierdo(nodo.getIzquierdo());
            // Se elimina el sucesor de su posición original en el subárbol derecho.
            nuevoNodo.setDerecho(eliminarRecursivo(nodo.getDerecho(), sucesor.getValor()));
            return nuevoNodo;
        }
        return nodo;
    }

    private void inordenRecursivo(NodoArbol nodo, List<String> resultado) {
        if (nodo != null) {
            inordenRecursivo(nodo.getIzquierdo(), resultado);
            resultado.add(nodo.getValor() + " (frec: " + nodo.getFrecuencia() + ")");
            inordenRecursivo(nodo.getDerecho(), resultado);
        }
    }

    private void preordenRecursivo(NodoArbol nodo, List<String> resultado) {
        if (nodo != null) {
            resultado.add(nodo.getValor() + " (frec: " + nodo.getFrecuencia() + ")");
            preordenRecursivo(nodo.getIzquierdo(), resultado);
            preordenRecursivo(nodo.getDerecho(), resultado);
        }
    }

    private void postordenRecursivo(NodoArbol nodo, List<String> resultado) {
        if (nodo != null) {
            postordenRecursivo(nodo.getIzquierdo(), resultado);
            postordenRecursivo(nodo.getDerecho(), resultado);
            resultado.add(nodo.getValor() + " (frec: " + nodo.getFrecuencia() + ")");
        }
    }
    //endregion

    //region Métodos de Ayuda
    /**
     * Encuentra el sucesor in-orden de un nodo (el nodo con el menor valor
     * en un subárbol), que siempre se encuentra en el extremo izquierdo del subárbol.
     * @param nodo Raíz del subárbol derecho donde buscar.
     * @return El nodo sucesor.
     */
    private NodoArbol encontrarSucesor(NodoArbol nodo) {
        while (nodo.getIzquierdo() != null) {
            nodo = nodo.getIzquierdo();
        }
        return nodo;
    }
    //endregion
}
