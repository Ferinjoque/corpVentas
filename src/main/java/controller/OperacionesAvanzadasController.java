package controller;

import app.NotificationManager;
import app.ServiceProvider;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import model.TipoEntidad;
import service.VentasService;
import util.InputValidador;
import util.ValidacionExcepcion;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la vista de "Operaciones Avanzadas" (OperacionesAvanzadasView.fxml).
 * Gestiona las operaciones complejas que solo están disponibles para estructuras de
 * datos de tipo lista, como invertir o eliminar duplicados.
 */
public class OperacionesAvanzadasController {

    //region Constantes
    private static final double MIN_VALOR = 0.0;
    private static final double MAX_VALOR = 9999.99;
    //endregion

    //region Componentes FXML
    @FXML private StackPane rootPane;
    @FXML private ComboBox<TipoEntidad> cbAdvancedTarget;
    @FXML private TextField txtAdvancedValue;
    @FXML private FlowPane listContainer;
    @FXML private Label resultsLabel;
    @FXML private Button btnInvertir;
    @FXML private Button btnEliminarDuplicados;
    @FXML private Button btnEliminarPorValor;
    @FXML private Button btnBuscarMayorIgual;
    //endregion

    //region Dependencias
    private final VentasService model;
    //endregion

    /**
     * Constructor que obtiene la instancia del servicio de negocio.
     */
    public OperacionesAvanzadasController() {
        this.model = ServiceProvider.getInstance();
    }

    /**
     * Se ejecuta al cargar la vista. Determina si la estructura de datos actual
     * soporta operaciones avanzadas y configura la UI correspondientemente.
     */
    @FXML
    public void initialize() {
        // Verifica si la estructura de datos actual (ej. Lista vs. Array) soporta estas operaciones.
        boolean isEnabled = model.soportaOperacionesAvanzadasVentas();

        // Habilita o deshabilita todos los controles de la vista según la capacidad de la estructura.
        cbAdvancedTarget.setDisable(!isEnabled);
        txtAdvancedValue.setDisable(!isEnabled);
        btnInvertir.setDisable(!isEnabled);
        btnEliminarDuplicados.setDisable(!isEnabled);
        btnEliminarPorValor.setDisable(!isEnabled);
        btnBuscarMayorIgual.setDisable(!isEnabled);

        if (isEnabled) {
            // Si las operaciones son soportadas, se configura la funcionalidad completa.
            setupComboBox();
            txtAdvancedValue.setOnAction(event -> handleBuscarMayorIgual());
            refreshVisualList();
        } else {
            // Si no son soportadas, se muestra un mensaje informativo y se oculta la lista.
            resultsLabel.setText("La estructura de datos actual (Array) no soporta operaciones avanzadas.");
            listContainer.setVisible(false);
        }
    }

    /**
     * Configura el ComboBox para seleccionar la entidad (Ventas u Objetivos) sobre la cual operar.
     */
    private void setupComboBox() {
        if (model.soportaOperacionesAvanzadasVentas()) {
            cbAdvancedTarget.getItems().add(TipoEntidad.VENTAS);
        }
        if (model.soportaOperacionesAvanzadasObjetivos()) {
            cbAdvancedTarget.getItems().add(TipoEntidad.OBJETIVOS);
        }
        cbAdvancedTarget.getSelectionModel().selectFirst();
        cbAdvancedTarget.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> refreshVisualList());
    }

    //region Manejadores de Eventos
    @FXML
    private void handleInvertir() {
        TipoEntidad target = getSelectedTarget();
        if (target == null) return;

        if (target == TipoEntidad.VENTAS) model.invertirVentas();
        else model.invertirObjetivos();

        refreshVisualList();
        NotificationManager.showNotification(rootPane, "Lista de " + target + " invertida.", NotificationManager.NotificationType.SUCCESS);
    }

    @FXML
    private void handleEliminarDuplicados() {
        TipoEntidad target = getSelectedTarget();
        if (target == null) return;

        String message = String.format(
                "Se buscarán duplicados en la lista de %s.\n" +
                        "Todos los registros correspondientes (Venta y Objetivo) serán eliminados.\n\n" +
                        "¿Desea continuar?", target);

        if (confirm(message, "Confirmar Eliminación de Duplicados")) {
            int eliminados = model.eliminarDuplicados(target);
            refreshVisualList();

            if (eliminados > 0) {
                NotificationManager.showNotification(rootPane, "Se eliminaron " + eliminados + " registros duplicados.", NotificationManager.NotificationType.SUCCESS);
            } else {
                NotificationManager.showNotification(rootPane, "No se encontraron duplicados para eliminar.", NotificationManager.NotificationType.INFO);
            }
        }
    }

    @FXML
    private void handleEliminarPorValor() {
        TipoEntidad target = getSelectedTarget();
        if (target == null) return;

        try {
            double valor = InputValidador.parseDoubleInRange(txtAdvancedValue.getText(), "Valor", MIN_VALOR, MAX_VALOR);
            String message = String.format(
                    "Se buscará el valor %,.2f.\n" +
                            "Si se encuentra, se eliminará el registro completo (Venta y Objetivo) de ese mes.\n\n" +
                            "¿Está seguro?", valor);

            if (confirm(message, "Confirmar Eliminación por Valor")) {
                boolean exito = (target == TipoEntidad.VENTAS)
                        ? model.eliminarVentaPorValor(valor)
                        : model.eliminarObjetivoPorValor(valor);

                if (exito) {
                    refreshVisualList();
                    NotificationManager.showNotification(rootPane, "Valor " + valor + " eliminado.", NotificationManager.NotificationType.SUCCESS);
                } else {
                    throw new ValidacionExcepcion("El valor " + valor + " no fue encontrado en la lista de " + target.toString().toLowerCase() + ".");
                }
            }
        } catch (ValidacionExcepcion e) {
            NotificationManager.showNotification(rootPane, e.getMessage(), NotificationManager.NotificationType.ERROR);
        }
    }

    @FXML
    private void handleBuscarMayorIgual() {
        TipoEntidad target = getSelectedTarget();
        if (target == null) return;

        try {
            double valor = InputValidador.parseDoubleInRange(txtAdvancedValue.getText(), "Valor de búsqueda", MIN_VALOR, MAX_VALOR);
            int indice = (target == TipoEntidad.VENTAS)
                    ? model.buscarPrimeraVentaMayorOIgual(valor)
                    : model.buscarPrimerObjetivoMayorOIgual(valor);

            if (indice != -1) {
                String mensaje = "El primer valor >= " + valor + " se encontró en la posición " + (indice + 1) + ".";
                NotificationManager.showNotification(rootPane, mensaje, NotificationManager.NotificationType.INFO);
                highlightChip(indice); // Anima el chip encontrado.
            } else {
                NotificationManager.showNotification(rootPane, "No se encontraron valores que cumplan la condición.", NotificationManager.NotificationType.INFO);
            }
        } catch (ValidacionExcepcion e) {
            NotificationManager.showNotification(rootPane, e.getMessage(), NotificationManager.NotificationType.ERROR);
        }
    }
    //endregion

    //region Lógica de UI y Animaciones
    /**
     * Limpia y vuelve a generar la representación visual de la lista de datos
     * como una serie de "chips" animados.
     */
    private void refreshVisualList() {
        TipoEntidad target = getSelectedTarget();
        if (target == null) return;

        resultsLabel.setText("Estado Actual de: " + target.toString());
        listContainer.getChildren().clear();

        List<Double> data = (target == TipoEntidad.VENTAS) ? model.getVentas() : model.getObjetivos();

        for (Double value : data) {
            Label chip = new Label(String.format("%.2f", value));
            chip.getStyleClass().add("list-chip");

            // Anima la aparición de cada chip.
            FadeTransition ft = new FadeTransition(Duration.millis(300), chip);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();

            listContainer.getChildren().add(chip);
        }
    }

    /**
     * Aplica una animación de resaltado y pulso a un "chip" específico en la lista.
     *
     * @param index El índice del chip a animar.
     */
    private void highlightChip(int index) {
        if (index < 0 || index >= listContainer.getChildren().size()) {
            return; // Previene IndexOutOfBoundsException.
        }

        Node chipNode = listContainer.getChildren().get(index);

        // 1. Animación de pulso (crecer y encoger).
        ScaleTransition pulse = new ScaleTransition(Duration.millis(300), chipNode);
        pulse.setByX(0.15);
        pulse.setByY(0.15);
        pulse.setCycleCount(2);
        pulse.setAutoReverse(true);

        // 2. Resaltado temporal mediante una clase CSS.
        chipNode.getStyleClass().add("list-chip-found");
        PauseTransition highlightOff = new PauseTransition(Duration.millis(800));
        highlightOff.setOnFinished(e -> chipNode.getStyleClass().remove("list-chip-found"));

        // Ejecuta ambas animaciones en paralelo.
        pulse.play();
        highlightOff.play();
    }
    //endregion

    //region Métodos de Ayuda
    /**
     * Muestra un diálogo de confirmación estándar.
     * @return true si el usuario presiona OK, false en caso contrario.
     */
    private boolean confirm(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle(title);
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Obtiene la entidad seleccionada (Ventas u Objetivos) del ComboBox.
     * Muestra una notificación de error si no hay ninguna selección.
     *
     * @return La {@link TipoEntidad} seleccionada, o null si no hay selección.
     */
    private TipoEntidad getSelectedTarget() {
        TipoEntidad target = cbAdvancedTarget.getSelectionModel().getSelectedItem();
        if (target == null) {
            NotificationManager.showNotification(rootPane, "Por favor, seleccione una entidad (Ventas u Objetivos) para operar.", NotificationManager.NotificationType.ERROR);
        }
        return target;
    }
    //endregion
}
