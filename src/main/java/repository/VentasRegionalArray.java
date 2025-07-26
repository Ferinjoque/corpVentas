package repository;

import model.VentasRegional;

/**
 * Implementa la interfaz {@link VentasRegional} utilizando una <b>matriz
 * bidimensional</b> de tipo {@code double} para almacenar los datos de ventas
 * por región y mes.
 */
public class VentasRegionalArray implements VentasRegional {

    /**
     * Matriz interna para almacenar las ventas. La primera dimensión (filas)
     * representa la región y la segunda (columnas), el mes.
     * (mat[region][mes] = valor)
     */
    private final double[][] mat;

    /**
     * Construye un nuevo repositorio, inicializando la matriz con las dimensiones dadas.
     *
     * @param regiones El número de regiones (filas) de la matriz.
     * @param meses    El número de meses (columnas) de la matriz.
     */
    public VentasRegionalArray(int regiones, int meses) {
        this.mat = new double[regiones][meses];
    }

    /**
     * {@inheritDoc}
     * <p>Antes de asignar el valor, este método valida que los índices de región y mes
     * estén dentro de los límites de la matriz para evitar un {@code IndexOutOfBoundsException}.</p>
     * <p><b>Complejidad: O(1)</b></p>
     */
    @Override
    public boolean set(int region, int mes, double valor) {
        if (region < 0 || region >= mat.length || mes < 0 || mes >= mat[0].length) {
            return false; // Índices fuera de los límites.
        }
        mat[region][mes] = valor;
        return true;
    }

    /**
     * {@inheritDoc}
     * <p><b>Algoritmo: Copia profunda de matriz 2D.</b></p>
     * <p>Para proteger la encapsulación, este método crea y devuelve una copia
     * profunda de la matriz. Esto evita que el código externo pueda modificar el
     * estado interno del repositorio directamente a través de la referencia devuelta.</p>
     * <p><b>Complejidad: O(R × M)</b>, donde R es el número de regiones y M el de meses.</p>
     */
    @Override
    public double[][] matriz() {
        // Se crea una nueva matriz con el mismo número de filas.
        double[][] copia = new double[mat.length][];
        for (int i = 0; i < mat.length; i++) {
            // Se clona cada fila (array) individualmente para asegurar una copia profunda.
            copia[i] = mat[i].clone();
        }
        return copia;
    }
}
