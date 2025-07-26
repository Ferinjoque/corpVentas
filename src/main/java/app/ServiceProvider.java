package app;

import model.TipoRepositorio;
import service.VentasService;
import service.VentasServiceImplementacion;
import util.CargarDatos;
import util.RepositorioFactory;

/**
 * Proporciona una única instancia global (Singleton) del servicio de negocio {@link VentasService}.
 *
 * Esta clase centraliza la creación y el acceso al servicio principal de la
 * aplicación, asegurando que todos los componentes trabajen con la misma
 * instancia. También gestiona el tipo de estructura de datos activa y permite
 * reiniciar el servicio con una nueva configuración.
 */
public class ServiceProvider {

    /**
     * La única instancia del servicio de ventas en toda la aplicación.
     */
    private static VentasService instance;

    /**
     * Almacena el tipo de repositorio (estructura de datos) actualmente en uso.
     */
    private static TipoRepositorio currentType;

    /**
     * Constructor privado para prevenir la instanciación directa y asegurar
     * el patrón Singleton.
     */
    private ServiceProvider() {}

    /**
     * Devuelve la instancia única del servicio de ventas.
     * Si no existe, la crea con una configuración por defecto (Lista Doble) y
     * precarga datos de demostración.
     *
     * @return La instancia singleton de {@link VentasService}.
     */
    public static VentasService getInstance() {
        if (instance == null) {
            currentType = TipoRepositorio.DOBLE; // Estructura por defecto al iniciar.
            instance = new VentasServiceImplementacion(
                    RepositorioFactory.crear(currentType, 12),
                    RepositorioFactory.crear(currentType, 12)
            );
            CargarDatos.precargarDemo(instance);
        }
        return instance;
    }

    /**
     * Devuelve el tipo de repositorio que está siendo utilizado actualmente por el servicio.
     *
     * @return El {@link TipoRepositorio} activo.
     */
    public static TipoRepositorio getCurrentType() {
        return currentType;
    }

    /**
     * Descarta la instancia actual del servicio y crea una nueva con la
     * estructura de datos especificada. Todos los datos anteriores se pierden
     * y se cargan nuevos datos de demostración.
     *
     * @param tipo El nuevo {@link TipoRepositorio} a utilizar.
     */
    public static void reiniciarServicio(TipoRepositorio tipo) {
        currentType = tipo;
        instance = new VentasServiceImplementacion(
                RepositorioFactory.crear(tipo, 12),
                RepositorioFactory.crear(tipo, 12)
        );
        CargarDatos.precargarDemo(instance);
    }
}
