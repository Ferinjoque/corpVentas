package app;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Clase de utilidad para mostrar notificaciones no bloqueantes
 * en la interfaz de usuario. No puede ser instanciada.
 */
public final class NotificationManager {

    /**
     * Define los tipos de notificaciones disponibles, cada uno asociado a una
     * clase de estilo CSS para su personalización visual.
     */
    public enum NotificationType {
        INFO("notification-info"),
        SUCCESS("notification-success"),
        ERROR("notification-error");

        private final String styleClass;

        NotificationType(String styleClass) {
            this.styleClass = styleClass;
        }

        public String getStyleClass() {
            return styleClass;
        }
    }

    /**
     * Constructor privado para prevenir la instanciación de esta clase de utilidad.
     */
    private NotificationManager() {}

    /**
     * Crea y muestra una notificación animada sobre un panel contenedor.
     * La notificación aparece, permanece visible por un momento y luego se desvanece.
     *
     * @param ownerPane El panel contenedor sobre el cual se mostrará la notificación..
     * @param message   El texto que se mostrará en la notificación.
     * @param type      El tipo de notificación (INFO, SUCCESS, ERROR), que determina
     * el estilo CSS que se aplicará.
     */
    public static void showNotification(Pane ownerPane, String message, NotificationType type) {
        if (ownerPane == null) {
            System.err.println("NotificationManager: El panel contenedor (ownerPane) es nulo.");
            return;
        }

        // 1. Crear los componentes de la notificación.
        Label notificationLabel = new Label(message);
        notificationLabel.getStyleClass().addAll("notification-label", type.getStyleClass());
        notificationLabel.setWrapText(true);
        notificationLabel.setMaxWidth(400);

        StackPane notificationPane = new StackPane(notificationLabel);
        notificationPane.setAlignment(Pos.BOTTOM_CENTER);
        notificationPane.setPadding(new Insets(0, 0, 50, 0));
        notificationPane.setMouseTransparent(true); // Permite clics a través de la notificación.

        // 2. Añadir la notificación a la escena.
        ownerPane.getChildren().add(notificationPane);

        // 3. Definir la secuencia de animación.
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), notificationPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition stay = new PauseTransition(Duration.millis(2500));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), notificationPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        // 4. Ejecutar la animación y limpiar al finalizar.
        SequentialTransition sequence = new SequentialTransition(fadeIn, stay, fadeOut);
        sequence.setOnFinished(e -> ownerPane.getChildren().remove(notificationPane));
        sequence.play();
    }
}
