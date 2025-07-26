package controller;

import app.NotificationManager;
import app.ServiceProvider;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import model.MesAsignacion;
import service.VentasService;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la vista de "Ventas Regionales" (VentasRegionalesView.fxml).
 * <p>
 * Gestiona la visualización de una matriz de ventas por región y mes,
 * y maneja las operaciones de asignación de ventas a una región/mes específico,
 * así como la liberación de dichas asignaciones.
 * </p>
 */
public class VentasRegionalesController {

    //region Componentes FXML
    @FXML private StackPane rootPane;
    @FXML private ComboBox<String> cbRegiones;
    @FXML private ComboBox<MesAsignacion> cbMesesDisponibles;
    @FXML private GridPane matrizGrid;
    //endregion

    //region Dependencias
    private final VentasService model;
    //endregion

    /**
     * Constructor que obtiene la instancia del servicio de negocio.
     */
    public VentasRegionalesController() {
        this.model = ServiceProvider.getInstance();
    }

    /**
     * Se ejecuta al cargar la vista. Configura los controles iniciales y
     * refresca la visualización de la matriz y los ComboBoxes.
     */
    @FXML
    public void initialize() {
        setupControles();
        refreshAll();
    }

    //region Configuración y Refresco de UI
    /**
     * Configura los elementos iniciales de la UI, como poblar el ComboBox de regiones.
     */
    private void setupControles() {
        double[][] matriz = model.obtenerVentasRegionalMatriz();
        for (int i = 0; i < matriz.length; i++) {
            cbRegiones.getItems().add("Región " + (i + 1));
        }
        cbRegiones.getSelectionModel().selectFirst();
    }

    /**
     * Llama a los métodos para refrescar todos los componentes visuales de la vista.
     */
    private void refreshAll() {
        refreshMatriz();
        refreshComboBoxes();
    }

    /**
     * Limpia y vuelve a dibujar la matriz de ventas en el GridPane.
     * Aplica estilos y una animación de aparición escalonada a cada celda.
     */
    private void refreshMatriz() {
        matrizGrid.getChildren().clear();
        double[][] matriz = model.obtenerVentasRegionalMatriz();

        // Añade las cabeceras de los meses (columnas).
        for (int j = 0; j < matriz[0].length; j++) {
            Label header = new Label("Mes " + (j + 1));
            header.getStyleClass().add("matrix-header");
            GridPane.setHalignment(header, HPos.CENTER);
            matrizGrid.add(header, j + 1, 0);
        }

        // Añade las cabeceras de las regiones (filas) y las celdas de datos.
        for (int i = 0; i < matriz.length; i++) {
            Label regionLabel = new Label("Región " + (i + 1));
            regionLabel.getStyleClass().add("matrix-header");
            matrizGrid.add(regionLabel, 0, i + 1);

            for (int j = 0; j < matriz[i].length; j++) {
                double valor = matriz[i][j];
                Label dataLabel = new Label(String.format("%,.2f", valor));

                // Aplica estilos CSS según si la celda tiene un valor asignado o está vacía.
                dataLabel.getStyleClass().add("matrix-cell");
                dataLabel.getStyleClass().add(valor > 0 ? "cell-assigned" : "cell-empty");

                // Configura y ejecuta una animación de desvanecimiento para la celda.
                dataLabel.setOpacity(0);
                FadeTransition ft = new FadeTransition(Duration.millis(500), dataLabel);
                ft.setFromValue(0);
                ft.setToValue(1);
                // El retraso escalonado crea un efecto de barrido visual.
                ft.setDelay(Duration.millis((i * matriz[0].length + j) * 15));
                ft.play();

                // Asegura que la celda se expanda para llenar el espacio disponible.
                GridPane.setHgrow(dataLabel, Priority.ALWAYS);
                dataLabel.setMaxWidth(Double.MAX_VALUE);
                matrizGrid.add(dataLabel, j + 1, i + 1);
            }
        }
    }

    /**
     * Recarga el ComboBox de meses disponibles con los datos actualizados desde el modelo.
     */
    private void refreshComboBoxes() {
        List<MesAsignacion> meses = model.getMesesDisponiblesParaAsignar();
        cbMesesDisponibles.setItems(FXCollections.observableArrayList(meses));
        if (!meses.isEmpty()) {
            cbMesesDisponibles.getSelectionModel().selectFirst();
        }
    }
    //endregion

    //region Manejadores de Eventos
    /**
     * Maneja la acción de asignar una venta (que aún no ha sido asignada a ninguna región)
     * a la región y mes seleccionados.
     */
    @FXML
    private void handleAsignarVenta() {
        int regionIndex = cbRegiones.getSelectionModel().getSelectedIndex();
        MesAsignacion mesSeleccionado = cbMesesDisponibles.getSelectionModel().getSelectedItem();

        if (mesSeleccionado == null) {
            NotificationManager.showNotification(rootPane, "No hay meses disponibles para asignar.", NotificationManager.NotificationType.ERROR);
            return;
        }

        // Obtiene el valor de la venta del mes seleccionado y lo registra en la matriz regional.
        double valorVenta = model.getVentas().get(mesSeleccionado.getIndiceMes());
        model.registrarVentaRegional(regionIndex, mesSeleccionado.getIndiceMes(), valorVenta);

        refreshAll();
        NotificationManager.showNotification(rootPane, "Venta asignada a " + cbRegiones.getValue() + " para el " + mesSeleccionado.toString().split(":")[0], NotificationManager.NotificationType.SUCCESS);
    }

    /**
     * Maneja la acción de liberar una venta de una región, poniendo su valor a cero.
     * Muestra un diálogo para que el usuario elija cuál de las ventas asignadas desea liberar.
     */
    @FXML
    private void handleLiberarVenta() {
        int regionIndex = cbRegiones.getSelectionModel().getSelectedIndex();
        List<MesAsignacion> mesesAsignados = model.getMesesAsignadosParaLiberar(regionIndex);

        if (mesesAsignados.isEmpty()) {
            NotificationManager.showNotification(rootPane, "No hay ventas asignadas para liberar en la " + cbRegiones.getValue() + ".", NotificationManager.NotificationType.INFO);
            return;
        }

        // Muestra un diálogo de elección, ya que una región puede tener varias ventas asignadas.
        ChoiceDialog<MesAsignacion> dialog = new ChoiceDialog<>(mesesAsignados.get(0), mesesAsignados);
        dialog.setTitle("Liberar Venta Regional");
        dialog.setHeaderText("Seleccione la venta que desea liberar de la " + cbRegiones.getValue() + ".");
        dialog.setContentText("Mes asignado:");

        Optional<MesAsignacion> result = dialog.showAndWait();

        // Si el usuario selecciona un mes, registra la venta con valor 0.0 para "liberarla".
        result.ifPresent(mesSeleccionado -> {
            model.registrarVentaRegional(regionIndex, mesSeleccionado.getIndiceMes(), 0.0);
            refreshAll();
            NotificationManager.showNotification(rootPane, "Venta liberada de la región.", NotificationManager.NotificationType.SUCCESS);
        });
    }
    //endregion
}
