package controller;

import app.NotificationManager;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import model.RepositorioVentas;
import repository.ArrayRepositorio;
import repository.ListaDobleRepositorio;

import java.util.function.Consumer;

/**
 * Controlador para la vista de "Pruebas de Rendimiento" (PerformanceTestView.fxml).
 * <p>
 * Permite comparar el rendimiento de diferentes implementaciones de
 * {@link RepositorioVentas} (Array vs. Lista Doble) para operaciones
 * computacionalmente intensivas, ilustrando las diferencias de complejidad
 * algorítmica (ej. O(1) vs. O(n)).
 * </p>
 */
public class PerformanceTestController {

    /**
     * Enum que define los tipos de pruebas de rendimiento disponibles.
     * Cada tipo tiene un nombre descriptivo que se muestra en la UI.
     */
    private enum TestType {
        ADD_FIRST("Insertar 20,000 al Inicio (O(n) vs O(1))"),
        ADD_LAST("Insertar 50,000 al Final (O(1) vs O(1))"),
        RANDOM_ACCESS("Acceder 50,000 veces a un índice aleatorio (O(1) vs O(n))"),
        DELETE_FIRST("Eliminar 10,000 del Inicio (O(n) vs O(1))");

        private final String displayName;

        TestType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    //region Constantes
    private static final int ARRAY_CAPACITY = 100_000;
    //endregion

    //region Componentes FXML
    @FXML private StackPane rootPane;
    @FXML private ComboBox<TestType> cbOperaciones;
    @FXML private BarChart<String, Number> performanceChart;
    @FXML private Label resultsLabel;
    @FXML private Button btnRunTest;
    @FXML private ProgressIndicator progressIndicator;
    //endregion

    /**
     * Se ejecuta al cargar la vista. Popula el ComboBox de operaciones y
     * aplica ajustes visuales a la gráfica de barras.
     */
    @FXML
    public void initialize() {
        cbOperaciones.setItems(FXCollections.observableArrayList(TestType.values()));
        cbOperaciones.getSelectionModel().selectFirst();

        // Ajustes visuales para la gráfica.
        performanceChart.setCategoryGap(40);
        performanceChart.setBarGap(8);
        performanceChart.setAnimated(false); // Se desactiva para controlar la animación manualmente.
    }

    /**
     * Maneja el evento de clic en el botón "Ejecutar Prueba".
     * Inicia una tarea en segundo plano para no bloquear la UI mientras se
     * ejecutan las pruebas de rendimiento.
     */
    @FXML
    private void handleRunTest() {
        TestType selectedTest = cbOperaciones.getSelectionModel().getSelectedItem();
        if (selectedTest == null) return;

        // Prepara la UI para la ejecución de la prueba.
        progressIndicator.setVisible(true);
        btnRunTest.setDisable(true);
        resultsLabel.setText("Calculando...");

        // Define la tarea que se ejecutará en un hilo separado.
        Task<XYChart.Series<String, Number>> testTask = new Task<>() {
            @Override
            protected XYChart.Series<String, Number> call() {
                // Ejecuta la misma prueba para ambas estructuras de datos.
                long timeArray = executeTest(new ArrayRepositorio(ARRAY_CAPACITY), selectedTest);
                long timeList = executeTest(new ListaDobleRepositorio(), selectedTest);

                // Prepara la serie de datos para la gráfica.
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(selectedTest.toString());
                series.getData().add(new XYChart.Data<>("Array", timeArray));
                series.getData().add(new XYChart.Data<>("Lista Doble", timeList));
                return series;
            }
        };

        // Define qué hacer cuando la tarea finaliza con éxito.
        testTask.setOnSucceeded(event -> {
            XYChart.Series<String, Number> series = testTask.getValue();
            updateChartWithFade(series);

            long timeArray = series.getData().get(0).getYValue().longValue();
            long timeList = series.getData().get(1).getYValue().longValue();

            resultsLabel.setText(String.format("Resultados: Array (%,d ms) vs Lista Doble (%,d ms)", timeArray, timeList));
            NotificationManager.showNotification(rootPane, "Prueba completada exitosamente.", NotificationManager.NotificationType.SUCCESS);

            // Restaura la UI a su estado original.
            progressIndicator.setVisible(false);
            btnRunTest.setDisable(false);
        });

        // Define qué hacer si la tarea falla.
        testTask.setOnFailed(event -> {
            NotificationManager.showNotification(rootPane, "La prueba falló. Revise la consola para más detalles.", NotificationManager.NotificationType.ERROR);
            resultsLabel.setText("Prueba fallida.");
            progressIndicator.setVisible(false);
            btnRunTest.setDisable(false);
            testTask.getException().printStackTrace();
        });

        // Inicia la tarea en un nuevo hilo.
        new Thread(testTask).start();
    }

    /**
     * Actualiza los datos de la gráfica y aplica una animación de desvanecimiento
     * a cada una de las barras para una presentación visual más suave.
     *
     * @param series La nueva serie de datos para mostrar en la gráfica.
     */
    private void updateChartWithFade(XYChart.Series<String, Number> series) {
        performanceChart.getData().setAll(series);

        // Platform.runLater asegura que este código se ejecute después de que JavaFX
        // haya renderizado las barras, permitiendo acceder a sus nodos.
        javafx.application.Platform.runLater(() -> {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node bar = data.getNode();
                if (bar != null) {
                    bar.setOpacity(0);
                    FadeTransition ft = new FadeTransition(Duration.millis(450), bar);
                    ft.setFromValue(0);
                    ft.setToValue(1);
                    ft.play();
                }
            }
        });
    }

    /**
     * Ejecuta una prueba de rendimiento específica sobre un repositorio dado.
     *
     * @param repo El repositorio sobre el cual se ejecutará la prueba.
     * @param test El tipo de prueba a realizar.
     * @return El tiempo transcurrido en milisegundos.
     */
    private long executeTest(RepositorioVentas repo, TestType test) {
        int iterations;

        // Determina el número de iteraciones según el tipo de prueba.
        switch (test) {
            case ADD_FIRST -> iterations = 20_000;
            case DELETE_FIRST -> iterations = 10_000;
            default -> iterations = 50_000;
        }

        // Pre-carga el repositorio con datos si la prueba lo requiere (ej. eliminar, acceder).
        if (test == TestType.DELETE_FIRST) {
            for (int i = 0; i < iterations; i++) repo.agregarAlInicio(i);
        } else if (test == TestType.RANDOM_ACCESS) {
            for (int i = 0; i < iterations; i++) repo.agregarAlFinal(i);
        }

        // Define la operación a medir.
        Consumer<RepositorioVentas> operation = switch (test) {
            case ADD_FIRST -> r -> {
                for (int i = 0; i < iterations; i++) r.agregarAlInicio(i);
            };
            case ADD_LAST -> r -> {
                for (int i = 0; i < iterations; i++) r.agregarAlFinal(i);
            };
            case DELETE_FIRST -> r -> {
                for (int i = 0; i < iterations; i++) r.eliminarAlInicio();
            };
            default -> r -> { // RANDOM_ACCESS
                for (int i = 0; i < iterations; i++) r.obtener((int) (Math.random() * r.tamano()));
            };
        };

        // Mide el tiempo de ejecución de la operación.
        long startTime = System.nanoTime();
        operation.accept(repo);
        long endTime = System.nanoTime();

        return (endTime - startTime) / 1_000_000; // Convierte nanosegundos a milisegundos.
    }
}
