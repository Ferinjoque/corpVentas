package model;

import java.util.List;

/**
 * Define el contrato para una estructura de datos que almacena una colección de valores numéricos.
 * <p>
 * Esta interfaz aplica el <b>Patrón de Diseño Repositorio</b>, que abstrae la
 * implementación de la colección subyacente (como arrays, listas enlazadas, etc.).
 * Esto permite que el resto de la aplicación interactúe con el almacenamiento
 * de datos a través de un conjunto común de operaciones CRUD (Crear, Leer,
 * Actualizar, Eliminar) y otras operaciones de colección, independientemente
 * de la estructura de datos específica que se esté utilizando.
 * </p>
 */
public interface RepositorioVentas {

    /**
     * Añade un nuevo valor al inicio del repositorio.
     * @param valor El valor a agregar.
     * @return {@code true} si la operación fue exitosa, {@code false} si no fue posible (ej. capacidad llena).
     */
    boolean agregarAlInicio(double valor);

    /**
     * Añade un nuevo valor al final del repositorio.
     * @param valor El valor a agregar.
     * @return {@code true} si la operación fue exitosa, {@code false} si no fue posible.
     */
    boolean agregarAlFinal(double valor);

    /**
     * Inserta un nuevo valor después de una posición específica del repositorio.
     * @param indice El índice (base 0) del elemento después del cual se insertará.
     * @param valor El nuevo valor a insertar.
     * @return {@code true} si la operación fue exitosa, {@code false} si no fue posible (ej. capacidad llena o índice inválido).
     */
    boolean insertarDespuesDe(int indice, double valor);

    /**
     * Modifica el valor en una posición específica del repositorio.
     *
     * @param indice El índice (base 0) del elemento a actualizar.
     * @param nuevoValor El nuevo valor que reemplazará al existente.
     * @return {@code true} si el índice es válido y la actualización fue exitosa, {@code false} en caso contrario.
     */
    boolean actualizar(int indice, double nuevoValor);

    /**
     * Elimina el elemento ubicado en una posición específica.
     *
     * @param indice El índice (base 0) del elemento a eliminar.
     * @return {@code true} si el índice es válido y la eliminación fue exitosa, {@code false} en caso contrario.
     */
    boolean eliminar(int indice);

    /**
     * Elimina el primer elemento del repositorio.
     * @return {@code true} si la operación fue exitosa y había al menos un elemento.
     */
    boolean eliminarAlInicio();

    /**
     * Elimina el último elemento del repositorio.
     * @return {@code true} si la operación fue exitosa y había al menos un elemento.
     */
    boolean eliminarAlFinal();

    /**
     * Obtiene el valor almacenado en un índice específico.
     *
     * @param indice El índice (base 0) del elemento a obtener.
     * @return El valor de tipo double en la posición solicitada.
     * @throws IndexOutOfBoundsException si el índice está fuera del rango de elementos.
     */
    double obtener(int indice);

    /**
     * Devuelve una vista de todos los elementos contenidos en el repositorio.
     *
     * @return Una {@link List} de tipo {@code Double} con todos los valores.
     */
    List<Double> obtenerTodos();

    /**
     * Devuelve el número total de elementos actualmente almacenados.
     *
     * @return El tamaño (conteo de elementos) del repositorio.
     */
    int tamano();

    /**
     * Busca la primera ocurrencia de un valor específico dentro del repositorio.
     *
     * @param valor El valor a buscar.
     * @return El índice (base 0) de la primera coincidencia, o -1 si no se encuentra.
     */
    int buscarIndiceDe(double valor);

    /**
     * Verifica si la implementación del repositorio es compatible con operaciones avanzadas.
     * <p>
     * Esto permite al servicio consultar si puede realizar un "cast" seguro a
     * {@link OperacionesAvanzadasLista} para invocar métodos especializados que
     * no son eficientes o aplicables a todas las estructuras de datos.
     * </p>
     * @return {@code true} si la implementación soporta dichas operaciones (ej. es una lista),
     * {@code false} en caso contrario (ej. es un array).
     */
    boolean soportaOperacionesAvanzadas();
}
