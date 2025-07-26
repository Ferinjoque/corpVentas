package repository;

import model.RepositorioVentas;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementa la interfaz {@link RepositorioVentas} utilizando un <b>array de tamaño fijo</b>
 * como estructura de datos subyacente.
 * <p>
 * Esta implementación es eficiente para el acceso a datos por índice (O(1)), pero
 * es ineficiente para operaciones de inserción o eliminación al principio o en medio
 * de la colección, ya que requieren el desplazamiento de elementos (O(n)).
 * </p>
 */
public class ArrayRepositorio implements RepositorioVentas {

    /**
     * El array interno que almacena los valores de tipo double.
     */
    private final double[] datos;

    /**
     * El número de elementos actualmente almacenados en el array.
     */
    private int count;

    /**
     * Construye un nuevo repositorio basado en un array con una capacidad máxima definida.
     *
     * @param capacidad El tamaño máximo del array.
     * @throws IllegalArgumentException si la capacidad es menor o igual a cero.
     */
    public ArrayRepositorio(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser un número positivo.");
        }
        this.datos = new double[capacidad];
        this.count = 0;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(n)</b> - Requiere desplazar todos los elementos existentes
     * una posición a la derecha para hacer espacio al nuevo elemento.</p>
     */
    @Override
    public boolean agregarAlInicio(double valor) {
        if (count >= datos.length) {
            return false; // Capacidad máxima alcanzada.
        }
        // Desplaza todos los elementos a la derecha.
        for (int i = count; i > 0; i--) {
            datos[i] = datos[i - 1];
        }
        datos[0] = valor;
        count++;
        return true;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(1)</b> - La inserción al final es directa y muy eficiente.</p>
     */
    @Override
    public boolean agregarAlFinal(double valor) {
        if (count >= datos.length) {
            return false; // Capacidad máxima alcanzada.
        }
        datos[count++] = valor;
        return true;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(n)</b> - En el peor de los casos, requiere desplazar
     * casi todos los elementos a la derecha.</p>
     */
    @Override
    public boolean insertarDespuesDe(int indice, double valor) {
        if (count >= datos.length || indice < 0 || indice >= count) {
            return false; // Capacidad máxima o índice inválido.
        }

        // Desplaza los elementos a la derecha desde el final hasta el punto de inserción.
        for (int i = count; i > indice + 1; i--) {
            datos[i] = datos[i - 1];
        }

        datos[indice + 1] = valor;
        count++;
        return true;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(1)</b> - El acceso por índice es directo.</p>
     */
    @Override
    public boolean actualizar(int indice, double nuevoValor) {
        if (indice < 0 || indice >= count) {
            return false;
        }
        datos[indice] = nuevoValor;
        return true;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(n)</b> - Requiere desplazar los elementos a la
     * izquierda para llenar el espacio del elemento eliminado.</p>
     */
    @Override
    public boolean eliminar(int indice) {
        if (indice < 0 || indice >= count) {
            return false;
        }
        // Desplaza los elementos a la izquierda para llenar el vacío.
        for (int i = indice; i < count - 1; i++) {
            datos[i] = datos[i + 1];
        }
        count--;
        datos[count] = 0.0; // Opcional: limpiar el último elemento para evitar datos fantasma.
        return true;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(n)</b> - Hereda la complejidad del método `eliminar(0)`.</p>
     */
    @Override
    public boolean eliminarAlInicio() {
        if (count == 0) {
            return false;
        }
        return eliminar(0);
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(1)</b> - Simplemente se reduce el contador de elementos.</p>
     */
    @Override
    public boolean eliminarAlFinal() {
        if (count == 0) {
            return false;
        }
        count--;
        datos[count] = 0.0; // Opcional: limpiar.
        return true;
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(1)</b> - El acceso por índice es directo.</p>
     */
    @Override
    public double obtener(int indice) {
        if (indice < 0 || indice >= count) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice + ", tamaño actual: " + count);
        }
        return datos[indice];
    }

    /**
     * {@inheritDoc}
     * <p><b>Complejidad: O(n)</b> - Debe recorrer todos los elementos para crear la lista.</p>
     */
    @Override
    public List<Double> obtenerTodos() {
        List<Double> lista = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            lista.add(datos[i]);
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
     * <p><b>Complejidad: O(n)</b> - Realiza una búsqueda lineal, recorriendo
     * los elementos uno por uno en el peor de los casos.</p>
     */
    @Override
    public int buscarIndiceDe(double valor) {
        for (int i = 0; i < count; i++) {
            if (datos[i] == valor) {
                return i;
            }
        }
        return -1; // Valor no encontrado.
    }

    /**
     * {@inheritDoc}
     * @return Siempre {@code false}, ya que un array de tamaño fijo no soporta
     * eficientemente operaciones como la inversión "in-place".
     */
    @Override
    public boolean soportaOperacionesAvanzadas() {
        return false;
    }
}
