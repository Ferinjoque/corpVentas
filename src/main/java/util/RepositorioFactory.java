package util;

import model.RepositorioVentas;
import model.TipoRepositorio;
import repository.ArrayRepositorio;
import repository.ListaDobleRepositorio;
import repository.ListaSimpleRepositorio;

/**
 * Implementa el patrón de diseño <b>Factory</b> para crear instancias de repositorios.
 *
 * <p>Su responsabilidad es centralizar y abstraer el proceso de creación de las
 * diferentes implementaciones de {@link RepositorioVentas}. El cliente (como la
 * clase {@code ServiceProvider}) solicita un repositorio por su tipo sin necesidad
 * de conocer los detalles de la clase concreta que se está instanciando,
 * facilitando así el intercambio de estructuras de datos en toda la aplicación.
 * </p>
 * <p>Esta clase no puede ser instanciada.</p>
 */
public final class RepositorioFactory {

    /**
     * Constructor privado para prevenir la instanciación de esta clase de utilidad.
     */
    private RepositorioFactory() {}

    /**
     * Crea y devuelve una nueva instancia de {@link RepositorioVentas} según el tipo especificado.
     *
     * @param tipo El {@link TipoRepositorio} que define la implementación deseada
     * (ARRAY, SIMPLE, DOBLE).
     * @param capacidad El tamaño inicial para las implementaciones que lo requieran,
     * como {@code ArrayRepositorio}. Este parámetro se ignora para
     * las implementaciones de listas enlazadas.
     * @return Una nueva instancia que cumple con el contrato de {@code RepositorioVentas}.
     * @throws IllegalArgumentException si se proporciona un tipo de repositorio no soportado.
     */
    public static RepositorioVentas crear(TipoRepositorio tipo, int capacidad) {
        switch (tipo) {
            case ARRAY:
                return new ArrayRepositorio(capacidad);
            case SIMPLE:
                return new ListaSimpleRepositorio();
            case DOBLE:
                return new ListaDobleRepositorio();
            default:
                // Lanza una excepción si se pasa un tipo de enum no contemplado en el switch.
                throw new IllegalArgumentException("Tipo de repositorio desconocido: " + tipo);
        }
    }
}
