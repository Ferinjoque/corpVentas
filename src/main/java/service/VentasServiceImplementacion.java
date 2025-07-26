package service;

import model.*;
import repository.ColaManual;
import repository.VentasRegionalArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación concreta de la interfaz {@link VentasService}.
 * <p>
 * Orquesta las operaciones entre los diferentes repositorios (ventas, objetivos, regional, etc.)
 * para cumplir con la lógica de negocio requerida por la aplicación. Actúa como el
 * "Modelo" en un patrón de diseño como MVP o MVC.
 * </p>
 */
public class VentasServiceImplementacion implements VentasService {

    //region Atributos y Constructor
    private final RepositorioVentas repoVentas;
    private final RepositorioVentas repoObjetivos;
    private final VentasRegional repoRegional;
    private final ColaManual<Pedido> colaPedidos;
    private Pedido pedidoEnProceso;
    private final List<Pedido> historialPedidos;
    private static final int LIMITE_MESES = 12;

    public VentasServiceImplementacion(RepositorioVentas ventas, RepositorioVentas objetivos) {
        this.repoVentas = ventas;
        this.repoObjetivos = objetivos;
        // Se asume una configuración fija para la matriz regional.
        this.repoRegional = new VentasRegionalArray(3, 12);
        this.colaPedidos = new ColaManual<>();
        this.pedidoEnProceso = null;
        this.historialPedidos = new ArrayList<>();
    }
    //endregion

    //region Operaciones CRUD (Ventas y Objetivos)
    @Override
    public boolean registrarVentaAlFinal(double valor) {
        verificarLimiteMeses(repoVentas);
        return repoVentas.agregarAlFinal(valor);
    }

    @Override
    public boolean registrarVentaAlInicio(double valor) {
        verificarLimiteMeses(repoVentas);
        return repoVentas.agregarAlInicio(valor);
    }

    @Override
    public boolean registrarObjetivoAlFinal(double valor) {
        verificarLimiteMeses(repoObjetivos);
        return repoObjetivos.agregarAlFinal(valor);
    }

    @Override
    public boolean registrarObjetivoAlInicio(double valor) {
        verificarLimiteMeses(repoObjetivos);
        return repoObjetivos.agregarAlInicio(valor);
    }

    @Override
    public boolean registrarDespuesDe(int indice, double valorVenta, double valorObjetivo) {
        // Se valida sobre un solo repositorio, ya que ambos deben estar sincronizados en tamaño.
        verificarLimiteMeses(repoVentas);
        boolean exitoVenta = repoVentas.insertarDespuesDe(indice, valorVenta);
        boolean exitoObjetivo = repoObjetivos.insertarDespuesDe(indice, valorObjetivo);
        return exitoVenta && exitoObjetivo;
    }

    @Override
    public boolean eliminarPrimerRegistro() {
        // Operación atómica: ambas eliminaciones deben tener éxito.
        return repoVentas.eliminarAlInicio() && repoObjetivos.eliminarAlInicio();
    }

    @Override
    public boolean eliminarUltimoRegistro() {
        // Operación atómica: ambas eliminaciones deben tener éxito.
        return repoVentas.eliminarAlFinal() && repoObjetivos.eliminarAlFinal();
    }

    @Override
    public boolean eliminarVenta(int mes) {
        return repoVentas.eliminar(mes);
    }

    @Override
    public boolean eliminarObjetivo(int mes) {
        return repoObjetivos.eliminar(mes);
    }

    @Override
    public boolean eliminarRegistro(int mes) {
        // Asegura que tanto la venta como el objetivo para un mes dado sean eliminados.
        boolean exitoVenta = this.eliminarVenta(mes);
        boolean exitoObjetivo = this.eliminarObjetivo(mes);
        return exitoVenta && exitoObjetivo;
    }

    @Override
    public boolean actualizarVenta(int mes, double valor) {
        boolean exito = repoVentas.actualizar(mes, valor);
        // Efecto secundario: si la venta se actualiza, también se debe actualizar en la
        // matriz regional si estaba asignada, para mantener la consistencia de los datos.
        if (exito) {
            double[][] mat = repoRegional.matriz();
            for (int r = 0; r < mat.length; r++) {
                if (mes < mat[r].length && mat[r][mes] != 0) {
                    repoRegional.set(r, mes, valor);
                }
            }
        }
        return exito;
    }

    @Override
    public boolean actualizarObjetivo(int mes, double valor) {
        return repoObjetivos.actualizar(mes, valor);
    }
    //endregion

    //region Obtención de Datos y Soporte de Características
    @Override
    public List<Double> getVentas() {
        return repoVentas.obtenerTodos();
    }

    @Override
    public List<Double> getObjetivos() {
        return repoObjetivos.obtenerTodos();
    }

    @Override
    public double[][] obtenerVentasRegionalMatriz() {
        return repoRegional.matriz();
    }

    @Override
    public boolean soportaOperacionesAvanzadasVentas() {
        return repoVentas.soportaOperacionesAvanzadas();
    }

    @Override
    public boolean soportaOperacionesAvanzadasObjetivos() {
        return repoObjetivos.soportaOperacionesAvanzadas();
    }
    //endregion

    //region Operaciones Avanzadas (Listas)
    @Override
    public void invertirVentas() {
        invertir(repoVentas);
    }

    @Override
    public void invertirObjetivos() {
        invertir(repoObjetivos);
    }

    @Override
    public boolean eliminarVentaPorValor(double valor) {
        return eliminarPorValor(repoVentas, valor);
    }

    @Override
    public boolean eliminarObjetivoPorValor(double valor) {
        return eliminarPorValor(repoObjetivos, valor);
    }

    @Override
    public int buscarPrimeraVentaMayorOIgual(double umbral) {
        return buscarPrimeroMayorOIgual(repoVentas, umbral);
    }

    @Override
    public int buscarPrimerObjetivoMayorOIgual(double umbral) {
        return buscarPrimeroMayorOIgual(repoObjetivos, umbral);
    }

    @Override
    public int eliminarDuplicados(TipoEntidad tipoEntidadBase) {
        RepositorioVentas repoBase = (tipoEntidadBase == TipoEntidad.VENTAS) ? repoVentas : repoObjetivos;
        return eliminarDuplicadosDesdeBase(repoBase);
    }
    //endregion

    //region Operaciones Regionales
    @Override
    public boolean registrarVentaRegional(int r, int m, double v) {
        return repoRegional.set(r, m, v);
    }

    @Override
    public List<MesAsignacion> getMesesDisponiblesParaAsignar() {
        List<MesAsignacion> disponibles = new ArrayList<>();
        List<Double> ventas = getVentas();
        double[][] mat = obtenerVentasRegionalMatriz();

        for (int m = 0; m < ventas.size(); m++) {
            boolean ocupado = false;
            for (double[] regionData : mat) {
                if (m < regionData.length && regionData[m] != 0) {
                    ocupado = true;
                    break;
                }
            }
            if (!ocupado) {
                String texto = String.format("Mes %d: %.2f", m + 1, ventas.get(m));
                disponibles.add(new MesAsignacion(m, texto));
            }
        }
        return disponibles;
    }

    @Override
    public List<MesAsignacion> getMesesAsignadosParaLiberar(int region) {
        List<MesAsignacion> asignados = new ArrayList<>();
        double[][] mat = obtenerVentasRegionalMatriz();

        if (region >= 0 && region < mat.length) {
            for (int m = 0; m < mat[region].length; m++) {
                if (mat[region][m] != 0) {
                    String texto = String.format("Mes %d (Venta: %.2f)", m + 1, mat[region][m]);
                    asignados.add(new MesAsignacion(m, texto));
                }
            }
        }
        return asignados;
    }
    //endregion

    //region Operaciones de la Cola de Pedidos
    @Override
    public void encolarPedido(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del pedido no puede estar vacía.");
        }
        colaPedidos.encolar(new Pedido(descripcion));
    }

    @Override
    public Pedido procesarSiguientePedido() {
        if (pedidoEnProceso != null) {
            throw new IllegalStateException("Ya hay un pedido en proceso (ID: " + pedidoEnProceso.getId() + "). Debe finalizarlo primero.");
        }
        if (colaPedidos.estaVacia()) {
            return null; // No hay pedidos para procesar.
        }
        pedidoEnProceso = colaPedidos.desencolar();
        pedidoEnProceso.setEstado(EstadoPedido.EN_PROCESO);
        return pedidoEnProceso;
    }

    @Override
    public Pedido finalizarPedidoEnProceso() {
        if (pedidoEnProceso == null) {
            throw new IllegalStateException("No hay ningún pedido en proceso para finalizar.");
        }
        Pedido pedidoFinalizado = pedidoEnProceso;
        pedidoFinalizado.setEstado(EstadoPedido.COMPLETADO);
        historialPedidos.add(pedidoFinalizado);
        pedidoEnProceso = null;
        return pedidoFinalizado;
    }

    @Override
    public Pedido cancelarProximoPedido() {
        Pedido pedidoACancelar;
        if (pedidoEnProceso != null) {
            // Prioridad 1: Cancelar el que está "En Proceso".
            pedidoACancelar = pedidoEnProceso;
            pedidoEnProceso = null;
        } else if (!colaPedidos.estaVacia()) {
            // Prioridad 2: Cancelar el siguiente "Pendiente" de la cola.
            pedidoACancelar = colaPedidos.desencolar();
        } else {
            throw new IllegalStateException("No hay pedidos activos (en proceso o en cola) para cancelar.");
        }

        pedidoACancelar.setEstado(EstadoPedido.CANCELADO);
        historialPedidos.add(pedidoACancelar);
        return pedidoACancelar;
    }

    @Override
    public List<Pedido> getPedidosActivos() {
        List<Pedido> lista = new ArrayList<>();
        if (pedidoEnProceso != null) {
            lista.add(pedidoEnProceso);
        }

        // Para leer la cola sin alterarla, se pasa su contenido a una copia
        // y luego se restaura la cola original.
        ColaManual<Pedido> copia = new ColaManual<>();
        while (!colaPedidos.estaVacia()) {
            Pedido p = colaPedidos.desencolar();
            lista.add(p);
            copia.encolar(p);
        }
        while (!copia.estaVacia()) {
            colaPedidos.encolar(copia.desencolar());
        }

        return lista;
    }

    @Override
    public Pedido getPedidoEnProceso() {
        return pedidoEnProceso;
    }

    @Override
    public List<Pedido> getHistorialPedidos() {
        return new ArrayList<>(historialPedidos); // Devuelve una copia para proteger la encapsulación.
    }
    //endregion

    //region Métodos de Ayuda Privados
    /**
     * Valida que no se exceda el límite de meses permitido en un repositorio.
     * @param repo El repositorio a verificar.
     * @throws IllegalStateException si el repositorio ya está lleno.
     */
    private void verificarLimiteMeses(RepositorioVentas repo) {
        if (repo.tamano() >= LIMITE_MESES) {
            throw new IllegalStateException("No se pueden agregar más de " + LIMITE_MESES + " meses.");
        }
    }

    /**
     * Algoritmo para eliminar duplicados. Encuentra los índices duplicados y los
     * ordena de mayor a menor. La eliminación se realiza en orden descendente para
     * evitar que la eliminación de un elemento afecte los índices de los elementos
     * posteriores que aún no han sido procesados.
     *
     * @param repoBase El repositorio (Ventas u Objetivos) que se usará como referencia.
     * @return El número de registros duplicados que fueron eliminados.
     */
    private int eliminarDuplicadosDesdeBase(RepositorioVentas repoBase) {
        OperacionesAvanzadasLista ops = getAdvancedOps(repoBase, "encontrar duplicados");
        List<Integer> indicesAEliminar = ops.encontrarIndicesDeDuplicados();

        // Optimización: Si no hay duplicados, se retorna inmediatamente.
        if (indicesAEliminar.isEmpty()) {
            return 0;
        }

        // Ordenamiento con Bubble Sort en orden descendente para evitar corrimiento de índices.
        int n = indicesAEliminar.size();
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                // Compara elementos adyacentes
                if (indicesAEliminar.get(j) < indicesAEliminar.get(j + 1)) {
                    // Intercambia si no están en orden descendente
                    Integer temp = indicesAEliminar.get(j);
                    indicesAEliminar.set(j, indicesAEliminar.get(j + 1));
                    indicesAEliminar.set(j + 1, temp);
                    swapped = true;
                }
            }
            // Si no hubo intercambios en una pasada, la lista ya está ordenada.
            if (!swapped) {
                break;
            }
        }

        // Elimina los registros usando los índices ya ordenados.
        for (int indice : indicesAEliminar) {
            eliminarRegistro(indice);
        }

        return indicesAEliminar.size();
    }

    /**
     * Método guardián que verifica si un repositorio soporta operaciones avanzadas
     * antes de intentar castearlo a {@link OperacionesAvanzadasLista}.
     * @throws UnsupportedOperationException si el repositorio no implementa la interfaz.
     */
    private OperacionesAvanzadasLista getAdvancedOps(RepositorioVentas repo, String featureName) {
        if (!repo.soportaOperacionesAvanzadas()) {
            throw new UnsupportedOperationException("La estructura de datos actual no soporta '" + featureName + "'.");
        }
        return (OperacionesAvanzadasLista) repo;
    }

    private void invertir(RepositorioVentas repo) {
        getAdvancedOps(repo, "invertir").invertir();
    }

    private boolean eliminarPorValor(RepositorioVentas repo, double valor) {
        int indice = repo.buscarIndiceDe(valor);
        if (indice != -1) {
            return this.eliminarRegistro(indice);
        }
        return false;
    }

    /**
     * Realiza una búsqueda lineal si el repositorio no ofrece un método optimizado.
     */
    private int buscarPrimeroMayorOIgual(RepositorioVentas repo, double umbral) {
        if (repo.soportaOperacionesAvanzadas()) {
            return ((OperacionesAvanzadasLista) repo).buscarPrimeroMayor(umbral);
        }
        // Fallback a búsqueda lineal para repositorios que no son listas (ej. Array).
        for (int i = 0; i < repo.tamano(); i++) {
            if (repo.obtener(i) >= umbral) {
                return i;
            }
        }
        return -1;
    }
    //endregion
}
