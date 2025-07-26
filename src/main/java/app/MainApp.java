package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Punto de entrada principal de la aplicación JavaFX.
 *
 * Esta clase es responsable de inicializar el framework JavaFX, cargar la
 * vista principal desde el archivo FXML y configurar la ventana (Stage) inicial.
 */
public class MainApp extends Application {

    /**
     * Configura e inicia la interfaz de usuario principal de la aplicación.
     *
     * @param stage El escenario principal proporcionado por el framework JavaFX.
     * @throws IOException Si ocurre un error al cargar el archivo MainView.fxml.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Inicializa el proveedor de servicios para asegurar que la lógica de negocio
        // y las estructuras de datos estén disponibles antes de cargar cualquier vista.
        ServiceProvider.getInstance();

        // Carga la vista principal desde el archivo FXML.
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/MainView.fxml")));

        // Crea la escena principal con la vista cargada.
        Scene scene = new Scene(root, 1200, 800);

        // Configura las propiedades de la ventana principal.
        stage.setTitle("CorpVentas Dashboard");
        stage.setMinWidth(1024);
        stage.setMinHeight(768);
        stage.setScene(scene);

        // Muestra la ventana.
        stage.show();
    }

    /**
     * Método principal que lanza la aplicación JavaFX.
     *
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        launch(args);
    }
}
