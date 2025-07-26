package service;

import model.MesAsignacion;
import model.Pedido;
import model.TipoEntidad;
import java.util.List;

/**
 * Define el contrato para el servicio de lógica de negocio de la aplicación.
 * <p>
 * Esta interfaz abstrae todas las operaciones de negocio, coordinando las acciones
 * sobre los diferentes repositorios de datos (ventas, objetivos, pedidos, etc.)
 * y proveyendo una API cohesiva para que las capas superiores (controladores)
 * la consuman sin conocer los detalles de la implementación.
 * </p>
 */
public interface VentasService {

    //region Operaciones CRUD en Repositorios Principales
    boolean registrarVentaAlFinal(double valor);
    boolean registrarVentaAlInicio(double valor);
    boolean registrarObjetivoAlFinal(double valor);
    boolean registrarObjetivoAlInicio(double valor);
    boolean registrarDespuesDe(int indice, double valorVenta, double valorObjetivo);
    boolean eliminarPrimerRegistro();
    boolean eliminarUltimoRegistro();
    boolean actualizarVenta(int mes, double valor);
    boolean eliminarVenta(int mes);
    boolean actualizarObjetivo(int mes, double valor);
    boolean eliminarObjetivo(int mes);
    boolean eliminarRegistro(int mes);
    //endregion

    //region Obtención de Datos
    List<Double> getVentas();
    List<Double> getObjetivos();
    double[][] obtenerVentasRegionalMatriz();
    List<MesAsignacion> getMesesDisponiblesParaAsignar();
    List<MesAsignacion> getMesesAsignadosParaLiberar(int region);
    //endregion

    //region Operaciones Avanzadas (Listas)
    void invertirVentas();
    void invertirObjetivos();
    int eliminarDuplicados(TipoEntidad tipoEntidadBase);
    boolean eliminarVentaPorValor(double valor);
    boolean eliminarObjetivoPorValor(double valor);
    int buscarPrimeraVentaMayorOIgual(double umbral);
    int buscarPrimerObjetivoMayorOIgual(double umbral);
    //endregion

    //region Operaciones Regionales
    boolean registrarVentaRegional(int region, int mes, double valor);
    //endregion

    //region Operaciones de la Cola de Pedidos
    void encolarPedido(String descripcion);
    Pedido procesarSiguientePedido();
    Pedido finalizarPedidoEnProceso();
    Pedido cancelarProximoPedido();
    List<Pedido> getPedidosActivos();
    Pedido getPedidoEnProceso();
    List<Pedido> getHistorialPedidos();
    //endregion

    //region Soporte de Características
    /**
     * Verifica si el repositorio de ventas actual soporta operaciones avanzadas de lista.
     * @return {@code true} si las operaciones son soportadas, {@code false} en caso contrario.
     */
    boolean soportaOperacionesAvanzadasVentas();

    /**
     * Verifica si el repositorio de objetivos actual soporta operaciones avanzadas de lista.
     * @return {@code true} si las operaciones son soportadas, {@code false} en caso contrario.
     */
    boolean soportaOperacionesAvanzadasObjetivos();
    //endregion
}
