package controller;

import app.NotificationManager;
import app.ServiceProvider;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import service.VentasService;
import util.InputValidador;
import util.ValidacionExcepcion;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Controlador para la vista del Dashboard principal (DashboardView.fxml).
 * Gestiona la tabla de ventas y objetivos, así como todas las operaciones CRUD
 * asociadas a estos datos.
 */
public class DashboardController {

    /**
     * Interfaz funcional para encapsular operaciones que pueden lanzar excepciones
     * de validación, simplificando su manejo en los event handlers.
     */
    @FunctionalInterface
    private interface ValidatableOperation {
        void run() throws ValidacionExcepcion;
    }

    //region Constantes
    private static final double MIN_VALOR = 0.0;
    private static final double MAX_VALOR = 9999.99;
    //endregion

    //region Componentes FXML
    @FXML private StackPane rootPane;
    @FXML private TableView<List<String>> ventasTable;
    @FXML private TableColumn<List<String>, String> mesColumn;
    @FXML private TableColumn<List<String>, String> ventaColumn;
    @FXML private TableColumn<List<String>, String> objetivoColumn;
    @FXML private TableColumn<List<String>, String> cumplimientoColumn;
    @FXML private TextField txtVenta;
    @FXML private TextField txtObjetivo;
    @FXML private Button btnUpdate;
    @FXML private Button btnDeleteSelected;
    @FXML private Button btnAddFirst;
    @FXML private Button btnAddLast;
    @FXML private Button btnInsertAfter;
    //endregion

    //region Dependencias
    private final VentasService model;
    //endregion

    /**
     * Constructor que obtiene la instancia del servicio de negocio.
     */
    public DashboardController() {
        this.model = ServiceProvider.getInstance();
    }

    /**
     * Se ejecuta al cargar la vista. Configura la tabla, los listeners de eventos
     * y carga los datos iniciales.
     */
    @FXML
    public void initialize() {
        configureTableColumns();
        setupListeners();
        refreshTable();
    }

    //region Configuración de la UI
    /**
     * Asigna las factorías de valor a cada columna de la tabla para que sepan
     * qué dato mostrar de la lista de strings que representa cada fila.
     */
    private void configureTableColumns() {
        mesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
        ventaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
        objetivoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));
        cumplimientoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3)));
    }

    /**
     * Configura los listeners para la selección de la tabla y los campos de texto,
     * permitiendo una UI reactiva que actualiza el estado de los botones.
     */
    private void setupListeners() {
        // 1. Listener para la selección de filas en la tabla.
        ventasTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateButtonStates();
            if (newSelection != null) {
                // Rellena los campos de texto con los datos de la fila seleccionada.
                txtVenta.setText(newSelection.get(1));
                txtObjetivo.setText(newSelection.get(2));
            }
        });

        // 2. Listeners para los campos de texto para actualizar el estado de los botones en tiempo real.
        txtVenta.textProperty().addListener((obs, oldText, newText) -> updateButtonStates());
        txtObjetivo.textProperty().addListener((obs, oldText, newText) -> updateButtonStates());

        // 3. Listeners para la tecla Enter en los campos de texto como atajo para "Añadir al Final".
        txtVenta.setOnAction(event -> handleAddLast());
        txtObjetivo.setOnAction(event -> handleAddLast());
    }
    //endregion

    //region Lógica de Refresco y Estado
    /**
     * Recarga todos los datos desde el modelo, los procesa y actualiza la tabla.
     * Al final, limpia el formulario y reevalúa el estado de los botones.
     */
    private void refreshTable() {
        List<Double> ventas = model.getVentas();
        List<Double> objetivos = model.getObjetivos();
        int rows = Math.max(ventas.size(), objetivos.size());

        ObservableList<List<String>> data = FXCollections.observableArrayList();
        IntStream.range(0, rows).forEach(i -> {
            double venta = (i < ventas.size()) ? ventas.get(i) : 0.0;
            double objetivo = (i < objetivos.size()) ? objetivos.get(i) : 0.0;
            String cumplimiento = objetivo > 0 ? (venta >= objetivo ? "✓ Cumplido" : "✗ No Cumplido") : "N/A";

            data.add(List.of(
                    String.valueOf(i + 1),
                    String.format("%.2f", venta),
                    String.format("%.2f", objetivo),
                    cumplimiento
            ));
        });

        ventasTable.setItems(data);
        clearForm();
        updateButtonStates();
    }

    /**
     * Método centralizado que gestiona la habilitación/deshabilitación de los
     * botones de acción basándose en el estado actual de la UI (selección,
     * contenido de los campos, etc.).
     */
    private void updateButtonStates() {
        List<String> selectedRow = ventasTable.getSelectionModel().getSelectedItem();
        boolean rowIsSelected = selectedRow != null;
        boolean isFull = model.getVentas().size() >= 12;

        // Lógica para botones de agregación.
        btnAddFirst.setDisable(isFull);
        btnAddLast.setDisable(isFull);
        btnInsertAfter.setDisable(isFull || !rowIsSelected);

        // Lógica para botón de eliminación.
        btnDeleteSelected.setDisable(!rowIsSelected);

        // Lógica para botón de actualización.
        if (!rowIsSelected) {
            btnUpdate.setDisable(true);
            return;
        }

        try {
            // Se activa solo si los valores en los campos de texto son diferentes a los de la fila seleccionada.
            double currentVenta = Double.parseDouble(selectedRow.get(1).replace(",", "."));
            double currentObjetivo = Double.parseDouble(selectedRow.get(2).replace(",", "."));
            double newVenta = Double.parseDouble(txtVenta.getText().replace(",", "."));
            double newObjetivo = Double.parseDouble(txtObjetivo.getText().replace(",", "."));
            boolean hasChanged = currentVenta != newVenta || currentObjetivo != newObjetivo;
            btnUpdate.setDisable(!hasChanged);
        } catch (NumberFormatException | NullPointerException e) {
            // Desactiva el botón si los campos no contienen números válidos.
            btnUpdate.setDisable(true);
        }
    }
    //endregion

    //region Manejadores de Eventos de Botones (CRUD)
    @FXML
    private void handleAddFirst() {
        executeOperation(() -> {
            double venta = InputValidador.parseDoubleInRange(getVentaInput(), "Valor de Venta", MIN_VALOR, MAX_VALOR);
            double objetivo = InputValidador.parseDoubleInRange(getObjetivoInput(), "Valor de Objetivo", MIN_VALOR, MAX_VALOR);
            if (!model.registrarVentaAlInicio(venta) || !model.registrarObjetivoAlInicio(objetivo)) {
                throw new ValidacionExcepcion("Capacidad máxima del repositorio alcanzada.");
            }
        }, "Registro añadido al inicio exitosamente.");
    }

    @FXML
    private void handleAddLast() {
        executeOperation(() -> {
            double venta = InputValidador.parseDoubleInRange(getVentaInput(), "Valor de Venta", MIN_VALOR, MAX_VALOR);
            double objetivo = InputValidador.parseDoubleInRange(getObjetivoInput(), "Valor de Objetivo", MIN_VALOR, MAX_VALOR);
            if (!model.registrarVentaAlFinal(venta) || !model.registrarObjetivoAlFinal(objetivo)) {
                throw new ValidacionExcepcion("Capacidad máxima del repositorio alcanzada.");
            }
        }, "Registro añadido al final exitosamente.");
    }

    @FXML
    private void handleInsertAfter() {
        int selectedIndex = getSelectedRow();
        if (selectedIndex < 0) {
            NotificationManager.showNotification(rootPane, "Por favor, seleccione una fila para poder insertar.", NotificationManager.NotificationType.ERROR);
            return;
        }
        executeOperation(() -> {
            double venta = InputValidador.parseDoubleInRange(getVentaInput(), "Valor de Venta", MIN_VALOR, MAX_VALOR);
            double objetivo = InputValidador.parseDoubleInRange(getObjetivoInput(), "Valor de Objetivo", MIN_VALOR, MAX_VALOR);
            if (!model.registrarDespuesDe(selectedIndex, venta, objetivo)) {
                throw new ValidacionExcepcion("No se pudo insertar. La capacidad máxima podría haber sido alcanzada.");
            }
        }, "Registro insertado correctamente.");
    }

    @FXML
    private void handleUpdate() {
        int selectedIndex = getSelectedRow();
        if (selectedIndex < 0) return;

        executeOperation(() -> {
            double venta = InputValidador.parseDoubleInRange(getVentaInput(), "Valor de Venta", MIN_VALOR, MAX_VALOR);
            double objetivo = InputValidador.parseDoubleInRange(getObjetivoInput(), "Valor de Objetivo", MIN_VALOR, MAX_VALOR);
            model.actualizarVenta(selectedIndex, venta);
            model.actualizarObjetivo(selectedIndex, objetivo);
        }, "Registro del mes " + (selectedIndex + 1) + " actualizado.");
    }

    @FXML
    private void handleDeleteFirst() {
        if (confirm("¿Está seguro de que desea eliminar el primer registro?", "Confirmar Eliminación")) {
            if (model.eliminarPrimerRegistro()) {
                NotificationManager.showNotification(rootPane, "Primer registro eliminado.", NotificationManager.NotificationType.SUCCESS);
                refreshTable();
            } else {
                NotificationManager.showNotification(rootPane, "No se pudo eliminar. El repositorio podría estar vacío.", NotificationManager.NotificationType.ERROR);
            }
        }
    }

    @FXML
    private void handleDeleteLast() {
        if (confirm("¿Está seguro de que desea eliminar el último registro?", "Confirmar Eliminación")) {
            if (model.eliminarUltimoRegistro()) {
                NotificationManager.showNotification(rootPane, "Último registro eliminado.", NotificationManager.NotificationType.SUCCESS);
                refreshTable();
            } else {
                NotificationManager.showNotification(rootPane, "No se pudo eliminar. El repositorio podría estar vacío.", NotificationManager.NotificationType.ERROR);
            }
        }
    }

    @FXML
    private void handleDelete() {
        int selectedIndex = getSelectedRow();
        if (selectedIndex < 0) {
            NotificationManager.showNotification(rootPane, "Debe seleccionar una fila para eliminar.", NotificationManager.NotificationType.ERROR);
            return;
        }

        if (confirm("¿Está seguro de que desea eliminar el registro del mes " + (selectedIndex + 1) + "?", "Confirmar Eliminación")) {
            model.eliminarRegistro(selectedIndex);
            NotificationManager.showNotification(rootPane, "Registro del mes " + (selectedIndex + 1) + " eliminado.", NotificationManager.NotificationType.SUCCESS);

            refreshTable();

            // Lógica para re-seleccionar una fila adyacente después de la eliminación.
            int newIndexToSelect = selectedIndex;
            if (newIndexToSelect >= ventasTable.getItems().size()) {
                newIndexToSelect = ventasTable.getItems().size() - 1; // Selecciona el nuevo último.
            }
            if (newIndexToSelect >= 0) {
                ventasTable.getSelectionModel().select(newIndexToSelect);
            }
        }
    }
    //endregion

    //region Métodos de Ayuda
    /**
     * Ejecuta una operación de negocio, manejando centralizadamente las excepciones
     * y mostrando notificaciones de éxito o error.
     * @param operation      La operación a ejecutar, encapsulada en una lambda.
     * @param successMessage El mensaje a mostrar si la operación tiene éxito.
     */
    private void executeOperation(ValidatableOperation operation, String successMessage) {
        try {
            operation.run();
            refreshTable();
            NotificationManager.showNotification(rootPane, successMessage, NotificationManager.NotificationType.SUCCESS);
        } catch (ValidacionExcepcion | IllegalStateException e) {
            NotificationManager.showNotification(rootPane, e.getMessage(), NotificationManager.NotificationType.ERROR);
        }
    }

    /**
     * Limpia los campos de texto del formulario y la selección de la tabla.
     */
    private void clearForm() {
        txtVenta.clear();
        txtObjetivo.clear();
        ventasTable.getSelectionModel().clearSelection();
        updateButtonStates();
    }

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

    private String getVentaInput() {
        return txtVenta.getText();
    }

    private String getObjetivoInput() {
        return txtObjetivo.getText();
    }

    private int getSelectedRow() {
        return ventasTable.getSelectionModel().getSelectedIndex();
    }
    //endregion
}
