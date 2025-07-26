package model;

/**
 * Representa un Objeto de Transferencia de Datos (DTO) diseñado para poblar
 * componentes de la interfaz de usuario, como un {@code ComboBox}.
 * <p>
 * Esta clase encapsula la información de un mes, separando su identificador
 * de negocio (el {@code indiceMes}) del texto que se muestra al usuario
 * ({@code textoDisplay}). Esto permite que la lógica de la aplicación trabaje
 * con el índice numérico mientras la UI presenta una descripción amigable y contextual.
 * </p>
 */
public class MesAsignacion {

    /**
     * El índice numérico (base 0) del mes, utilizado para la lógica interna.
     */
    private final int indiceMes;

    /**
     * La representación textual del mes para ser mostrada en la interfaz de usuario.
     */
    private final String textoDisplay;

    /**
     * Construye una nueva instancia de asignación de mes.
     *
     * @param indiceMes    El índice base 0 que identifica unívocamente al mes.
     * @param textoDisplay El texto que se mostrará en componentes de la UI como
     * ComboBoxes o listas.
     */
    public MesAsignacion(int indiceMes, String textoDisplay) {
        this.indiceMes = indiceMes;
        this.textoDisplay = textoDisplay;
    }

    /**
     * Devuelve el índice numérico (base 0) asociado a este mes.
     *
     * @return El índice del mes para ser usado en la lógica de negocio.
     */
    public int getIndiceMes() {
        return indiceMes;
    }

    /**
     * Devuelve la representación textual del objeto.
     * <p>
     * Este método es invocado automáticamente por los componentes de JavaFX
     * (como {@code ComboBox} o {@code ChoiceDialog}) para mostrar el objeto
     * de forma legible en la interfaz de usuario.
     * </p>
     *
     * @return El texto de visualización.
     */
    @Override
    public String toString() {
        return textoDisplay;
    }
}
