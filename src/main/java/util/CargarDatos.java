package util;

import service.VentasService;

/**
 * Clase de utilidad para precargar un conjunto de datos de demostración.
 *
 * <p>Su propósito es facilitar las pruebas y la demostración de la aplicación,
 * evitando la necesidad de introducir datos manualmente cada vez que se inicia.
 * Esta clase no puede ser instanciada.</p>
 */
public final class CargarDatos {

    /**
     * Constructor privado para prevenir la instanciación de esta clase de utilidad.
     */
    private CargarDatos() {}

    /**
     * Popula una instancia de {@link VentasService} con un conjunto predefinido de
     * registros de ventas y sus correspondientes objetivos.
     *
     * @param svc La instancia del servicio de ventas que se va a poblar con
     * los datos de demostración.
     */
    public static void precargarDemo(VentasService svc) {
        double[] ventasDemo = {1200.5, 1500.0, 1700.75, 1100.0, 1800.0, 1800.0};
        double[] objetivosDemo = {1000.0, 1400.0, 1600.0, 1200.0, 1900.0, 1800.0};

        // Itera sobre los arrays de demostración y registra cada par de venta/objetivo
        // en el servicio.
        for (int i = 0; i < ventasDemo.length; i++) {
            svc.registrarVentaAlFinal(ventasDemo[i]);
            svc.registrarObjetivoAlFinal(objetivosDemo[i]);
        }
    }
}
