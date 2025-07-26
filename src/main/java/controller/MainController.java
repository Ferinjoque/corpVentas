package controller;

import app.NotificationManager;
import app.ServiceProvider;
import app.ViewLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.TipoRepositorio;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controlador principal de la aplicación. Gestiona la navegación del menú lateral
 * y la carga de las diferentes vistas en el área de contenido principal.
 */
public class MainController {

    //region Componentes FXML
    @FXML private StackPane contentArea;
    @FXML private Button dashboardButton;
    @FXML private Button regionalButton;
    @FXML private Button pedidosButton;
    @FXML private Button analisisButton;
    @FXML private Button advancedOpsButton;
    @FXML private Button performanceButton;
    @FXML private Button structureButton;
    @FXML private Label structureLabel;
    //endregion

    //region Atributos del Controlador
    private List<Button> menuButtons;
    private Button activeViewButton;
    //endregion

    /**
     * Se ejecuta al cargar la vista. Inicializa la lista de botones del menú,
     * actualiza la etiqueta de estado y carga la vista inicial del dashboard.
     */
    @FXML
    public void initialize() {
        this.menuButtons = List.of(dashboardButton, regionalButton, pedidosButton,
                analisisButton, advancedOpsButton, performanceButton, structureButton);
        updateStructureLabel();
        handleDashboardClick();
    }

    //region Lógica de UI
    /**
     * Actualiza la etiqueta en el menú lateral para mostrar la estructura de datos
     * que está actualmente en uso por el servicio.
     */
    private void updateStructureLabel() {
        if (structureLabel != null) {
            structureLabel.setText("Estructura: " + ServiceProvider.getCurrentType().toString());
        }
    }

    /**
     * Gestiona el estado visual de los botones del menú lateral. Elimina el estilo
     * de "seleccionado" de todos los botones y lo aplica solo al botón actual.
     *
     * @param selectedButton El botón que debe marcarse como activo.
     */
    private void updateSelectedButton(Button selectedButton) {
        menuButtons.forEach(button -> button.getStyleClass().remove("sidebar-button-selected"));
        selectedButton.getStyleClass().add("sidebar-button-selected");

        // Guarda una referencia al último botón de VISTA activo, ignorando el botón de "Estructura".
        if (selectedButton != structureButton) {
            this.activeViewButton = selectedButton;
        }
    }

    /**
     * Carga una vista desde un archivo FXML en el panel de contenido principal.
     * Delega la lógica de carga y animación a la clase de utilidad ViewLoader.
     *
     * @param fxmlPath La ruta al archivo FXML que se va a cargar.
     */
    private void loadView(String fxmlPath) {
        ViewLoader.loadView(fxmlPath, contentArea);
    }
    //endregion

    //region Manejadores de Eventos (Handlers)
    @FXML
    private void handleDashboardClick() {
        loadView("/view/DashboardView.fxml");
        updateSelectedButton(dashboardButton);
    }

    @FXML
    private void handleRegionalClick() {
        loadView("/view/VentasRegionalesView.fxml");
        updateSelectedButton(regionalButton);
    }

    @FXML
    private void handlePedidosClick() {
        loadView("/view/PedidosView.fxml");
        updateSelectedButton(pedidosButton);
    }

    @FXML
    private void handleAnalisisClick() {
        loadView("/view/AnalisisDatosView.fxml");
        updateSelectedButton(analisisButton);
    }

    @FXML
    private void handleAdvancedOpsClick() {
        loadView("/view/OperacionesAvanzadasView.fxml");
        updateSelectedButton(advancedOpsButton);
    }

    @FXML
    private void handlePerformanceClick() {
        loadView("/view/PerformanceTestView.fxml");
        updateSelectedButton(performanceButton);
    }

    /**
     * Gestiona el proceso de cambio de la estructura de datos subyacente.
     * Muestra diálogos de confirmación y selección, reinicia el servicio y
     * refresca la interfaz de usuario.
     */
    @FXML
    private void handleStructureClick() {
        updateSelectedButton(structureButton); // Resalta temporalmente el botón de "Estructura".

        // 1. Pide confirmación al usuario, ya que la acción reiniciará los datos.
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION,
                "Se reiniciarán todos los datos con la nueva estructura. ¿Desea continuar?",
                ButtonType.OK, ButtonType.CANCEL);
        confirmationDialog.setTitle("Confirmar Cambio de Estructura");
        confirmationDialog.setHeaderText(null);
        Optional<ButtonType> result = confirmationDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // 2. Si confirma, muestra un diálogo para elegir la nueva estructura.
            ChoiceDialog<TipoRepositorio> choiceDialog = new ChoiceDialog<>(
                    ServiceProvider.getCurrentType(), TipoRepositorio.values());
            choiceDialog.setTitle("Selección de Estructura");
            choiceDialog.setHeaderText("Elija la nueva estructura de datos.");
            choiceDialog.setContentText("Estructura:");
            Optional<TipoRepositorio> choice = choiceDialog.showAndWait();

            // 3. Si elige una nueva estructura, reinicia el servicio y la UI.
            choice.ifPresent(selectedType -> {
                ServiceProvider.reiniciarServicio(selectedType);
                updateStructureLabel();
                handleDashboardClick(); // Vuelve a la vista principal.
                NotificationManager.showNotification(contentArea, "Aplicación reiniciada con estructura: " + selectedType, NotificationManager.NotificationType.SUCCESS);
            });

            // Si el usuario cancela el diálogo de elección, restaura el botón activo.
            if (choice.isEmpty()) {
                updateSelectedButton(activeViewButton);
            }

        } else {
            // Si el usuario cancela el diálogo de confirmación, restaura el botón activo.
            updateSelectedButton(activeViewButton);
        }
    }

    /**
     * Abre la vista de la calculadora en una nueva ventana no modal e independiente.
     */
    @FXML
    private void handleCalculadoraClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CalculadoraView.fxml"));
            Parent root = loader.load();
            Stage calculatorStage = new Stage();
            calculatorStage.setTitle("Calculadora Postfija");
            calculatorStage.setScene(new Scene(root));
            calculatorStage.initModality(Modality.NONE); // Permite interactuar con la ventana principal.
            calculatorStage.setAlwaysOnTop(true);
            calculatorStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //endregion
}
