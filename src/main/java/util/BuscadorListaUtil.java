package util;

import model.INodo;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Clase de utilidad que provee algoritmos reutilizables para operar sobre listas enlazadas.
 *
 * <p>Los métodos de esta clase son estáticos y operan sobre la interfaz genérica
 * {@link INodo}. Esto permite que la misma lógica de búsqueda se aplique de forma
 * <b>polimórfica</b> a cualquier tipo de lista (simple, doble, etc.) que cumpla
 * con dicho contrato, promoviendo la reutilización de código y evitando la duplicación.</p>
 * <p>Esta clase no puede ser instanciada.</p>
 */
public final class BuscadorListaUtil {

    /**
     * Constructor privado para prevenir la instanciación de esta clase de utilidad.
     */
    private BuscadorListaUtil() {}

    /**
     * Busca el índice de la primera ocurrencia de un valor específico en una lista enlazada.
     * @param cabeza El primer nodo (cabeza) de la lista a recorrer.
     * @param valor  El valor de tipo double a buscar.
     * @return El índice (base 0) del primer nodo que contiene el valor, o -1 si no se encuentra.
     */
    public static int buscarIndiceDe(INodo cabeza, double valor) {
        return buscarIndice(cabeza, nodo -> nodo.getValor() == valor);
    }

    /**
     * Busca el índice del primer nodo en una lista enlazada cuyo valor es mayor o igual a un umbral.
     * @param cabeza El primer nodo (cabeza) de la lista a recorrer.
     * @param umbral El valor de referencia para la comparación.
     * @return El índice (base 0) del primer nodo que cumple la condición, o -1 si no se encuentra.
     */
    public static int buscarPrimeroMayor(INodo cabeza, double umbral) {
        return buscarIndice(cabeza, nodo -> nodo.getValor() >= umbral);
    }

    /**
     * Analiza una lista e identifica los índices de todos los elementos duplicados.
     * <p>
     * <b>Algoritmo:</b> Búsqueda de duplicados por comparación anidada (fuerza bruta).
     * <b>Complejidad: O(n^2)</b>, donde n es el número de nodos en la lista.
     * </p>
     *
     * @param cabeza El primer nodo de la lista a analizar.
     * @return Una {@link List} de enteros con los índices de los elementos duplicados.
     * La lista estará vacía si no se encuentran duplicados.
     */
    public static List<Integer> encontrarIndicesDeDuplicados(INodo cabeza) {
        List<Integer> indicesDeDuplicados = new ArrayList<>();
        INodo nodoActual = cabeza;
        int i = 0;

        while (nodoActual != null) {
            // Compara el nodo actual con todos los nodos que lo preceden.
            INodo nodoAnterior = cabeza;
            int j = 0;
            while (j < i) {
                if (nodoAnterior.getValor() == nodoActual.getValor()) {
                    indicesDeDuplicados.add(i);
                    break; // Duplicado encontrado, no es necesario seguir comparando para este nodo.
                }
                nodoAnterior = nodoAnterior.getSiguiente();
                j++;
            }
            nodoActual = nodoActual.getSiguiente();
            i++;
        }
        return indicesDeDuplicados;
    }

    /**
     * <b>Algoritmo: Búsqueda Lineal Genérica con Predicado.</b>
     * <p>
     * Recorre una lista desde su cabeza y devuelve el índice del primer nodo que
     * satisface una condición funcional dada (un predicado).
     * </p>
     *
     * @param cabeza El primer nodo de la lista a recorrer.
     * @param condicion Un {@link Predicate} que define el criterio de búsqueda a evaluar en cada nodo.
     * @return El índice (base 0) del primer nodo que cumple la condición, o -1 si no se encuentra.
     */
    private static int buscarIndice(INodo cabeza, Predicate<INodo> condicion) {
        INodo p = cabeza;
        int indice = 0;
        while (p != null) {
            if (condicion.test(p)) {
                return indice;
            }
            p = p.getSiguiente();
            indice++;
        }
        return -1; // No se encontró ningún nodo que cumpla la condición.
    }
}
