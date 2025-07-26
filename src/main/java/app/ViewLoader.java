package app;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

/**
 * Clase de utilidad para cargar vistas FXML de forma dinámica en un panel.
 * No puede ser instanciada.
 */
public final class ViewLoader {

    /**
     * Constructor privado para prevenir la instanciación de esta clase de utilidad.
     */
    private ViewLoader() {}

    /**
     * Carga una vista desde un archivo FXML y la muestra en un StackPane
     * con una animación de desvanecimiento (fade-in).
     * <p>
     * Este método se encarga de reemplazar el contenido existente del panel
     * por la nueva vista cargada.
     *
     * @param fxmlPath    La ruta del recurso al archivo .fxml que se va a cargar.
     * @param contentArea El StackPane contenedor donde se inyectará la nueva vista.
     */
    public static void loadView(String fxmlPath, StackPane contentArea) {
        try {
            // Carga el nodo raíz de la jerarquía de la interfaz desde el archivo FXML.
            Parent view = FXMLLoader.load(Objects.requireNonNull(ViewLoader.class.getResource(fxmlPath)));

            // Prepara la vista para la animación de entrada.
            view.setOpacity(0);

            // Reemplaza todo el contenido del panel con la nueva vista.
            contentArea.getChildren().setAll(view);

            // Crea y ejecuta la animación de desvanecimiento para una transición suave.
            FadeTransition fadeIn = new FadeTransition(Duration.millis(350), view);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

        } catch (IOException e) {
            // Maneja errores que puedan ocurrir durante la carga del archivo FXML.
            System.err.println("Error al cargar la vista FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
