package model;

/**
 * Define el contrato para la gestión de datos de ventas organizados en una
 * estructura bidimensional de región por mes.
 * <p>
 * Abstrae la lógica de almacenamiento de una matriz, donde los valores de venta
 * se asignan a una coordenada específica {@code [region][mes]}.
 * </p>
 * @see repository.VentasRegionalArray
 */
public interface VentasRegional {

    /**
     * Asigna un valor de venta a una celda específica de la matriz en la
     * coordenada [region][mes].
     * <p>
     * Un valor de 0 se interpreta como una "liberación" o limpieza de la
     * asignación en esa celda, dejándola disponible.
     * </p>
     *
     * @param region El índice de la región (fila).
     * @param mes    El índice del mes (columna).
     * @param valor  El valor de la venta a asignar. Un valor de 0 libera la celda.
     * @return {@code true} si la operación se realizó con éxito dentro de los
     * límites de la matriz; {@code false} en caso contrario.
     */
    boolean set(int region, int mes, double valor);

    /**
     * Devuelve una <b>copia defensiva</b> de la matriz completa de Región × Mes.
     * <p>
     * Al devolver una copia en lugar de la referencia original, se garantiza que la
     * estructura de datos interna no pueda ser modificada por código externo,
     * preservando así la encapsulación del objeto.
     * </p>
     *
     * @return una nueva matriz de tipo {@code double[][]} que es una copia
     * independiente de los datos internos.
     */
    double[][] matriz();
}
