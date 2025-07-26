package model;

/**
 * Representa un nodo dentro de un Árbol Binario de Búsqueda (ABB).
 * <p>
 * Cada nodo almacena un valor numérico, un contador para la frecuencia de
 * valores duplicados, y las referencias a sus nodos hijos izquierdo y derecho.
 * </p>
 *
 * @see repository.ArbolBinarioBusqueda
 */
public class NodoArbol {

    /**
     * El valor numérico (double) que el nodo almacena. Es final porque no
     * debe cambiar una vez que el nodo es creado.
     */
    private final double valor;

    /**
     * Contador para registrar cuántas veces se ha insertado el mismo valor en el árbol.
     */
    private int frecuencia;

    /**
     * Referencia al subárbol izquierdo, que contiene valores menores que este nodo.
     */
    private NodoArbol izquierdo;

    /**
     * Referencia al subárbol derecho, que contiene valores mayores que este nodo.
     */
    private NodoArbol derecho;

    /**
     * Construye un nuevo nodo de árbol con un valor específico.
     * La frecuencia se inicializa en 1, representando la primera ocurrencia de este valor.
     *
     * @param valor El valor de tipo {@code double} que almacenará el nodo.
     */
    public NodoArbol(double valor) {
        this.valor = valor;
        this.frecuencia = 1;
        this.izquierdo = null;
        this.derecho = null;
    }

    // --- Getters y Setters ---

    public double getValor() {
        return valor;
    }

    public int getFrecuencia() {
        return frecuencia;
    }

    public void incrementarFrecuencia() {
        this.frecuencia++;
    }

    public NodoArbol getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(NodoArbol izquierdo) {
        this.izquierdo = izquierdo;
    }

    public NodoArbol getDerecho() {
        return derecho;
    }

    public void setDerecho(NodoArbol derecho) {
        this.derecho = derecho;
    }
}
