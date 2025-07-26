package controller;

import app.NotificationManager;
import app.ServiceProvider;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import model.Pedido;
import service.VentasService;
import util.InputValidador;
import util.ValidacionExcepcion;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para la vista de "Gestión de Pedidos" (PedidosView.fxml).
 * <p>
 * Maneja la lógica de la interfaz para la cola de pedidos, incluyendo
 * el registro de nuevos pedidos, la gestión de su ciclo de vida (procesar,
 * finalizar, cancelar) y la visualización del historial.
 * </p>
 */
public class PedidosController {

    //region Componentes FXML
    @FXML private StackPane rootPane;
    @FXML private TextField txtDescripcion;
    @FXML private TableView<Pedido> pedidosTable;
    @FXML private TableColumn<Pedido, String> idColumn;
    @FXML private TableColumn<Pedido, String> descripcionColumn;
    @FXML private TableColumn<Pedido, String> estadoColumn;
    @FXML private Button btnProcesar;
    @FXML private Button btnFinalizar;
    @FXML private Button btnCancelar;
    //endregion

    //region Dependencias
    private final VentasService model;
    //endregion

    /**
     * Constructor que obtiene la instancia del servicio de negocio.
     */
    public PedidosController() {
        this.model = ServiceProvider.getInstance();
    }

    /**
     * Se ejecuta al cargar la vista. Configura la tabla, los listeners
     * y refresca la vista con los datos iniciales.
     */
    @FXML
    public void initialize() {
        txtDescripcion.setOnAction(event -> handleRegistrarPedido());
        configureTableColumns();
        refreshView();
    }

    //region Configuración de la UI
    /**
     * Configura las columnas de la tabla, incluyendo una `CellFactory` personalizada
     * para la columna "Estado" que aplica estilos CSS dinámicos según el estado del pedido.
     */
    private void configureTableColumns() {
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        descripcionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescripcion()));
        estadoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEstado().toString()));

        // Asigna una fábrica de celdas para colorear la fila según el estado del pedido.
        estadoColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                // Limpia estilos previos para evitar artefactos visuales al reciclar celdas.
                getStyleClass().removeAll("status-en-proceso", "status-pendiente", "status-completado", "status-cancelado");

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    // Obtiene el objeto Pedido completo para la fila actual.
                    Pedido pedido = getTableView().getItems().get(getIndex());
                    // Aplica la clase CSS correspondiente al estado del pedido.
                    switch (pedido.getEstado()) {
                        case EN_PROCESO -> getStyleClass().add("status-en-proceso");
                        case COMPLETADO -> getStyleClass().add("status-completado");
                        case CANCELADO -> getStyleClass().add("status-cancelado");
                        default -> getStyleClass().add("status-pendiente");
                    }
                }
            }
        });
    }

    /**
     * Actualiza todos los componentes de la vista para reflejar el estado actual del modelo.
     * Recarga la tabla de pedidos y ajusta la habilitación de los botones de acción.
     */
    private void refreshView() {
        // Recarga la tabla con los pedidos activos (en proceso y en cola).
        pedidosTable.setItems(FXCollections.observableArrayList(model.getPedidosActivos()));

        // Actualiza el estado de los botones basándose en la lógica de negocio.
        boolean hayPedidoEnProceso = model.getPedidoEnProceso() != null;
        boolean hayPedidosEnCola = !model.getPedidosActivos().isEmpty() && !hayPedidoEnProceso;

        btnProcesar.setDisable(hayPedidoEnProceso || !hayPedidosEnCola);
        btnFinalizar.setDisable(!hayPedidoEnProceso);
        btnCancelar.setDisable(model.getPedidosActivos().isEmpty());

        pedidosTable.refresh(); // Fuerza el redibujado de las celdas.
    }
    //endregion

    //region Manejadores de Eventos
    @FXML
    private void handleRegistrarPedido() {
        try {
            String descripcion = InputValidador.validateNotEmpty(txtDescripcion.getText(), "Descripción del Pedido");
            model.encolarPedido(descripcion);
            txtDescripcion.clear();
            refreshView();
            NotificationManager.showNotification(rootPane, "Nuevo pedido encolado.", NotificationManager.NotificationType.SUCCESS);
        } catch (ValidacionExcepcion | IllegalArgumentException e) {
            NotificationManager.showNotification(rootPane, e.getMessage(), NotificationManager.NotificationType.ERROR);
        }
    }

    @FXML
    private void handleProcesarSiguiente() {
        try {
            Pedido procesado = model.procesarSiguientePedido();
            if (procesado != null) {
                refreshView();
                NotificationManager.showNotification(rootPane, "Procesando pedido ID: " + procesado.getId(), NotificationManager.NotificationType.INFO);
            } else {
                NotificationManager.showNotification(rootPane, "No hay pedidos pendientes en la cola.", NotificationManager.NotificationType.INFO);
            }
        } catch (IllegalStateException e) {
            NotificationManager.showNotification(rootPane, e.getMessage(), NotificationManager.NotificationType.ERROR);
        }
    }

    @FXML
    private void handleFinalizarPedido() {
        try {
            Pedido finalizado = model.finalizarPedidoEnProceso();
            refreshView();
            NotificationManager.showNotification(rootPane, "Pedido ID: " + finalizado.getId() + " finalizado y movido al historial.", NotificationManager.NotificationType.SUCCESS);
        } catch (IllegalStateException e) {
            NotificationManager.showNotification(rootPane, e.getMessage(), NotificationManager.NotificationType.ERROR);
        }
    }

    @FXML
    private void handleCancelarProximo() {
        try {
            Pedido cancelado = model.cancelarProximoPedido();
            refreshView();
            NotificationManager.showNotification(rootPane, "Pedido ID: " + cancelado.getId() + " ha sido cancelado.", NotificationManager.NotificationType.SUCCESS);
        } catch (IllegalStateException e) {
            NotificationManager.showNotification(rootPane, e.getMessage(), NotificationManager.NotificationType.ERROR);
        }
    }

    /**
     * Muestra el historial de pedidos (completados y cancelados) en un diálogo expandible.
     */
    @FXML
    private void handleMostrarHistorial() {
        List<Pedido> historial = model.getHistorialPedidos();
        if (historial.isEmpty()) {
            showInfo("Historial de Pedidos", "El historial de pedidos está vacío.");
            return;
        }

        // Formatea la lista de pedidos en un solo String para mostrarlo.
        String textoHistorial = historial.stream()
                .map(p -> String.format("ID: %d | Estado: %s | Descripción: %s",
                        p.getId(), p.getEstado(), p.getDescripcion()))
                .collect(Collectors.joining("\n"));

        // Crea un diálogo de información con un TextArea expandible.
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Historial de Pedidos");
        alert.setHeaderText("Registro de todos los pedidos completados y cancelados.");

        TextArea textArea = new TextArea(textoHistorial);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.getDialogPane().setExpanded(true);
        alert.showAndWait();
    }
    //endregion

    //region Métodos de Ayuda
    /**
     * Muestra un diálogo de información simple.
     * @param title   El título de la ventana del diálogo.
     * @param message El mensaje a mostrar.
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    //endregion
}
