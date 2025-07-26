package model;

import java.util.List;

/**
 * Define un contrato para operaciones complejas aplicables a repositorios
 * implementados como listas enlazadas.
 * <p>
 * Esta interfaz aplica el <b>Principio de Segregación de Interfaces</b>. Separa
 * funcionalidades que no son eficientes o no tienen sentido en todas las
 * estructuras de datos (como un array de tamaño fijo), permitiendo que el
 * servicio las invoque de forma segura solo cuando el repositorio subyacente
 * declara su compatibilidad.
 * </p>
 *
 * @see RepositorioVentas#soportaOperacionesAvanzadas()
 * @see repository.ListaSimpleRepositorio
 * @see repository.ListaDobleRepositorio
 */
public interface OperacionesAvanzadasLista {

    /**
     * Invierte el orden de todos los elementos en la lista.
     * <p>
     * Esta es una operación "in-place", lo que significa que modifica la
     * estructura de datos existente sin crear una nueva lista.
     * </p>
     */
    void invertir();

    /**
     * Busca el índice del primer elemento en la lista cuyo valor es mayor o
     * igual a un umbral especificado.
     *
     * @param umbral El valor de referencia para la comparación.
     * @return El índice (base 0) del primer elemento que cumple la condición,
     * o -1 si no se encuentra ninguno.
     */
    int buscarPrimeroMayor(double umbral);

    /**
     * Analiza la lista completa e identifica los índices de todos los elementos
     * que son duplicados.
     * <p>
     * Se considera duplicado a cualquier elemento cuyo valor ya ha aparecido
     * en un índice anterior en la lista. La primera aparición de un valor no
     * se considera un duplicado.
     * </p>
     *
     * @return Una {@link List} de enteros con los índices (base 0) de los
     * elementos duplicados. La lista estará vacía si no hay duplicados.
     */
    List<Integer> encontrarIndicesDeDuplicados();
}
