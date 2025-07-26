package controller;

import app.NotificationManager;
import app.ServiceProvider;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.NodoArbol;
import repository.ArbolBinarioBusqueda;
import service.VentasService;
import util.InputValidador;
import util.ValidacionExcepcion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador para la vista de "Análisis de Datos", que gestiona la visualización
 * y la interacción con un Árbol Binario de Búsqueda (ABB) de los datos de ventas.
 */
public class AnalisisDatosController {

    //region Constantes de Configuración Visual
    private static final double NODE_RADIUS = 28;
    private static final double VERT_SPACING = 90;
    private static final double HORIZ_SPACING = 110;
    private static final double CANVAS_MARGIN = 50;
    //endregion

    //region Componentes FXML
    @FXML private StackPane rootPane;
    @FXML private Pane canvas;
    @FXML private ScrollPane scrollPane;
    @FXML private TextField txtValor;
    @FXML private TextArea resultsArea;
    @FXML private Button btnGenerar, btnInorden, btnPreorden,
            btnPostorden, btnBuscar, btnEliminar;
    //endregion

    //region Atributos del Controlador
    private final VentasService ventasModel;
    private ArbolBinarioBusqueda arbolDeVentas;
    private List<Button> controlButtons;
    /** Mapea un valor del árbol a su nodo de UI correspondiente (StackPane). */
    private final Map<Double, StackPane> nodoUI = new HashMap<>();
    //endregion

    /**
     * Constructor que inicializa las dependencias del modelo.
     */
    public AnalisisDatosController() {
        this.ventasModel = ServiceProvider.getInstance();
        this.arbolDeVentas = new ArbolBinarioBusqueda();
    }

    /**
     * Se ejecuta al cargar la vista. Configura los componentes iniciales y los listeners.
     */
    @FXML
    public void initialize() {
        controlButtons = List.of(btnGenerar, btnInorden, btnPreorden,
                btnPostorden, btnBuscar, btnEliminar);

        resultsArea.setText("Genere un árbol para comenzar el análisis.");
        resultsArea.setWrapText(true);

        // Permite buscar al presionar Enter en el campo de texto.
        txtValor.setOnAction(event -> handleBuscar());

        // Configura el ScrollPane para que las barras aparezcan solo cuando sea necesario.
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    }

    //region Manejadores de Eventos (Handlers)
    /**
     * Genera un nuevo ABB a partir de los datos de ventas actuales y lo dibuja.
     */
    @FXML
    private void handleGenerarArbol() {
        List<Double> ventas = ventasModel.getVentas();
        if (ventas.isEmpty()) {
            NotificationManager.showNotification(rootPane, "No hay datos de ventas para generar el árbol.", NotificationManager.NotificationType.ERROR);
            return;
        }

        arbolDeVentas = new ArbolBinarioBusqueda();
        ventas.forEach(arbolDeVentas::insertar);

        resultsArea.setText("Árbol generado con " + ventas.size() + " registros.");
        drawTree(true);
        NotificationManager.showNotification(rootPane, "Árbol Binario de Búsqueda generado exitosamente.", NotificationManager.NotificationType.SUCCESS);
    }

    /**
     * Inicia la animación de recorrido In-Orden.
     */
    @FXML
    private void handleRecorridoInorden() {
        playRecorrido("In-Orden", toDouble(arbolDeVentas.recorridoInorden()));
    }

    /**
     * Inicia la animación de recorrido Pre-Orden.
     */
    @FXML
    private void handleRecorridoPreorden() {
        playRecorrido("Pre-Orden", toDouble(arbolDeVentas.recorridoPreorden()));
    }

    /**
     * Inicia la animación de recorrido Post-Orden.
     */
    @FXML
    private void handleRecorridoPostorden() {
        playRecorrido("Post-Orden", toDouble(arbolDeVentas.recorridoPostorden()));
    }

    /**
     * Busca un valor en el árbol, muestra un informe detallado y resalta el nodo si lo encuentra.
     */
    @FXML
    private void handleBuscar() {
        if (isArbolInvalido()) return;
        try {
            double valorBuscado = InputValidador.parseNonNegativeDouble(txtValor.getText());
            ArbolBinarioBusqueda.ResultadoBusqueda res = arbolDeVentas.buscarConDetalles(valorBuscado);

            drawTree(false); // Limpia resaltados previos.

            if (res != null) {
                highlightFoundNode(nodoUI.get(valorBuscado));
                resultsArea.setText(generarInformeBusqueda(res));
                NotificationManager.showNotification(rootPane, "Valor encontrado en el árbol.", NotificationManager.NotificationType.INFO);
            } else {
                resultsArea.setText("--- Resultado de la Búsqueda ---\nEstado: NO ENCONTRADO.");
                NotificationManager.showNotification(rootPane, "Valor " + valorBuscado + " no encontrado.", NotificationManager.NotificationType.ERROR);
            }
        } catch (ValidacionExcepcion ex) {
            NotificationManager.showNotification(rootPane, ex.getMessage(), NotificationManager.NotificationType.ERROR);
        }
    }

    /**
     * Elimina un valor del árbol y actualiza la visualización.
     */
    @FXML
    private void handleEliminar() {
        if (isArbolInvalido()) return;
        try {
            double valorAEliminar = InputValidador.parseNonNegativeDouble(txtValor.getText());
            boolean exito = arbolDeVentas.eliminar(valorAEliminar);

            if (exito) {
                txtValor.clear();
                drawTree(true);
                resultsArea.setText("Valor " + valorAEliminar + " eliminado del árbol.");
                NotificationManager.showNotification(rootPane, "Nodo eliminado.", NotificationManager.NotificationType.SUCCESS);
            } else {
                NotificationManager.showNotification(rootPane, "El valor " + valorAEliminar + " no existe en el árbol.", NotificationManager.NotificationType.ERROR);
            }
        } catch (ValidacionExcepcion ex) {
            NotificationManager.showNotification(rootPane, ex.getMessage(), NotificationManager.NotificationType.ERROR);
        }
    }
    //endregion

    //region Lógica de Dibujo del Árbol
    /**
     * Orquesta el proceso de dibujado del árbol en el canvas.
     * Limpia el canvas, calcula las posiciones de los nodos y luego los renderiza.
     * @param animate Indica si la aparición de los nodos debe ser animada.
     */
    private void drawTree(boolean animate) {
        canvas.getChildren().clear();
        nodoUI.clear();
        if (arbolDeVentas.estaVacio()) return;

        // 1. Calcular posiciones de nodos usando un recorrido in-order.
        Map<NodoArbol, Point2D> posiciones = new HashMap<>();
        int[] columnaActual = {0};
        computePos(arbolDeVentas.obtenerRaiz(), 0, posiciones, columnaActual);

        // 2. Determinar el tamaño necesario para el canvas.
        double widthNeeded = columnaActual[0] * HORIZ_SPACING + CANVAS_MARGIN * 2;
        double heightNeeded = (depth(arbolDeVentas.obtenerRaiz()) + 1) * VERT_SPACING + CANVAS_MARGIN * 2;
        canvas.setPrefSize(widthNeeded, heightNeeded);

        // 3. Centrar el árbol si cabe en el viewport.
        double viewportW = getViewportWidthSafe();
        double shiftX = Math.max((viewportW - widthNeeded) / 2, 0);

        // 4. Renderizar nodos y conectores.
        render(arbolDeVentas.obtenerRaiz(), null, posiciones, animate, shiftX);
    }

    /**
     * Calcula recursivamente la posición (x, y) de cada nodo.
     * El eje Y se basa en la profundidad y el eje X en el orden in-order.
     */
    private void computePos(NodoArbol nodo, int depth, Map<NodoArbol, Point2D> pos, int[] col) {
        if (nodo == null) return;
        computePos(nodo.getIzquierdo(), depth + 1, pos, col);

        double x = col[0] * HORIZ_SPACING + CANVAS_MARGIN;
        double y = depth * VERT_SPACING + CANVAS_MARGIN;
        pos.put(nodo, new Point2D(x, y));
        col[0]++;

        computePos(nodo.getDerecho(), depth + 1, pos, col);
    }

    /**
     * Dibuja recursivamente los nodos y las líneas que los conectan en el canvas.
     */
    private void render(NodoArbol nodo, NodoArbol parent, Map<NodoArbol, Point2D> pos, boolean animate, double shiftX) {
        if (nodo == null) return;

        Point2D p = pos.get(nodo);
        double x = p.getX() + shiftX, y = p.getY();

        // Dibuja la línea conectora desde el padre (si existe).
        if (parent != null) {
            Point2D pp = pos.get(parent);
            double x0 = pp.getX() + shiftX, y0 = pp.getY();
            double dx = x - x0, dy = y - y0;
            double dist = Math.hypot(dx, dy);
            double offX = (dx / dist) * NODE_RADIUS;
            double offY = (dy / dist) * NODE_RADIUS;

            Line line = new Line(x0 + offX, y0 + offY, x - offX, y - offY);
            line.getStyleClass().add("tree-connector");
            canvas.getChildren().add(line);
        }

        // Crea y posiciona el nodo visual (círculo + texto).
        Label label = fitLabel(String.format("%.2f", nodo.getValor()));
        Circle mainCircle = createMainCircle();
        StackPane pane = new StackPane(new Circle(NODE_RADIUS, Color.WHITE), mainCircle, label);
        pane.getStyleClass().add("tree-node-pane");
        pane.setLayoutX(x - NODE_RADIUS);
        pane.setLayoutY(y - NODE_RADIUS);

        // Añade efecto hover para interactividad.
        pane.setOnMouseEntered(event -> mainCircle.getStyleClass().add("tree-node-circle-hover"));
        pane.setOnMouseExited(event -> mainCircle.getStyleClass().remove("tree-node-circle-hover"));

        nodoUI.put(nodo.getValor(), pane);
        canvas.getChildren().add(pane);

        // Anima la aparición del nodo si se solicita.
        if (animate) {
            pane.setScaleX(0);
            pane.setScaleY(0);
            ScaleTransition st = new ScaleTransition(Duration.millis(280), pane);
            st.setToX(1);
            st.setToY(1);
            st.play();
        }

        // Continúa el renderizado para los hijos.
        render(nodo.getIzquierdo(), nodo, pos, animate, shiftX);
        render(nodo.getDerecho(), nodo, pos, animate, shiftX);
    }
    //endregion

    //region Animaciones y Efectos Visuales
    /**
     * Ejecuta una secuencia de animaciones para visualizar un recorrido del árbol.
     * Para cada nodo en el orden dado, se desplaza suavemente hacia él y lo hace pulsar.
     * @param nombre El nombre del recorrido (ej. "In-Orden").
     * @param orden  La lista de valores en el orden del recorrido.
     */
    private void playRecorrido(String nombre, List<Double> orden) {
        if (isArbolInvalido()) return;

        drawTree(false);
        setControlsDisabled(true);
        resultsArea.setText("Animando recorrido " + nombre + "…");

        SequentialTransition recorridoAnimation = new SequentialTransition();

        for (Double valor : orden) {
            StackPane pane = nodoUI.get(valor);
            if (pane == null) continue;

            // 1. Scroll suave hasta el nodo.
            Animation scroll = smoothScrollTo(pane, Duration.millis(350));

            // 2. Resaltado y pulso del nodo.
            PauseTransition highlightOn = new PauseTransition(Duration.millis(1));
            highlightOn.setOnFinished(e -> ((Circle) pane.getChildren().get(1)).getStyleClass().add("tree-node-traversed"));

            ScaleTransition pulse = new ScaleTransition(Duration.millis(400), pane);
            pulse.setByX(0.18);
            pulse.setByY(0.18);
            pulse.setCycleCount(2);
            pulse.setAutoReverse(true);

            PauseTransition highlightOff = new PauseTransition(Duration.millis(1));
            highlightOff.setOnFinished(e -> ((Circle) pane.getChildren().get(1)).getStyleClass().remove("tree-node-traversed"));

            recorridoAnimation.getChildren().addAll(scroll, highlightOn, pulse, highlightOff, new PauseTransition(Duration.millis(120)));
        }

        recorridoAnimation.setOnFinished(e -> {
            String resultadoTexto = orden.stream().map(x -> String.format("%.2f", x)).collect(Collectors.joining("  ->  "));
            resultsArea.setText("Recorrido " + nombre + ":\n" + resultadoTexto);
            smoothScrollToTop(Duration.millis(500)).play();
            setControlsDisabled(false);
        });
        recorridoAnimation.play();
    }

    /**
     * Resalta un nodo encontrado con una animación de scroll y pulso.
     */
    private void highlightFoundNode(StackPane pane) {
        if (pane == null) return;

        Circle c = (Circle) pane.getChildren().get(1);
        c.getStyleClass().add("tree-node-circle-found");

        Animation scroll = smoothScrollTo(pane, Duration.millis(400));
        ScaleTransition pulse = new ScaleTransition(Duration.millis(350), pane);
        pulse.setByX(0.18);
        pulse.setByY(0.18);
        pulse.setCycleCount(2);
        pulse.setAutoReverse(true);

        SequentialTransition seq = new SequentialTransition(scroll, pulse, new PauseTransition(Duration.millis(80)));
        seq.setOnFinished(e -> c.getStyleClass().remove("tree-node-circle-found"));
        seq.play();
    }

    /**
     * Crea una animación de desplazamiento suave del ScrollPane hacia un nodo específico.
     */
    private Animation smoothScrollTo(StackPane pane, Duration dur) {
        Bounds viewportBounds = scrollPane.getViewportBounds();
        double contentWidth = canvas.getWidth();
        double contentHeight = canvas.getHeight();
        double viewportWidth = viewportBounds.getWidth();
        double viewportHeight = viewportBounds.getHeight();

        double nodeCenterX = pane.getLayoutX() + NODE_RADIUS;
        double nodeCenterY = pane.getLayoutY() + NODE_RADIUS;

        double targetHValue = clamp((nodeCenterX - viewportWidth / 2) / (contentWidth - viewportWidth));
        double targetVValue = clamp((nodeCenterY - viewportHeight / 2) / (contentHeight - viewportHeight));

        return new Timeline(
                new KeyFrame(dur,
                        new KeyValue(scrollPane.hvalueProperty(), targetHValue, Interpolator.EASE_BOTH),
                        new KeyValue(scrollPane.vvalueProperty(), targetVValue, Interpolator.EASE_BOTH)
                )
        );
    }

    /**
     * Crea una animación de desplazamiento suave del ScrollPane a la esquina superior izquierda.
     */
    private Animation smoothScrollToTop(Duration dur) {
        return new Timeline(
                new KeyFrame(dur,
                        new KeyValue(scrollPane.hvalueProperty(), 0, Interpolator.EASE_BOTH),
                        new KeyValue(scrollPane.vvalueProperty(), 0, Interpolator.EASE_BOTH)
                )
        );
    }
    //endregion

    //region Métodos de Utilidad
    /**
     * Habilita o deshabilita todos los botones de control.
     * @param disabled true para deshabilitar, false para habilitar.
     */
    private void setControlsDisabled(boolean disabled) {
        controlButtons.forEach(b -> b.setDisable(disabled));
    }

    /**
     * Crea el círculo principal de un nodo con su estilo CSS.
     */
    private Circle createMainCircle() {
        Circle c = new Circle(NODE_RADIUS);
        c.getStyleClass().add("tree-node-circle");
        return c;
    }

    /**
     * Ajusta el tamaño de la fuente de una etiqueta para que quepa dentro del círculo del nodo.
     */
    private Label fitLabel(String text) {
        double maxWidth = NODE_RADIUS * 1.7;
        double fontSize = 16;
        Text tempText = new Text(text);
        while (true) {
            tempText.setFont(Font.font(fontSize));
            if (tempText.getLayoutBounds().getWidth() <= maxWidth || fontSize <= 8) break;
            fontSize -= 1.5;
        }
        Label label = new Label(text);
        label.setFont(Font.font(fontSize));
        label.getStyleClass().add("tree-node-label");
        return label;
    }

    /**
     * Genera un informe de texto detallado a partir de un resultado de búsqueda.
     */
    private String generarInformeBusqueda(ArbolBinarioBusqueda.ResultadoBusqueda res) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Resultado de la Búsqueda ---\n");
        sb.append("Estado: ENCONTRADO\n");
        sb.append("Valor Buscado: \t").append(String.format("%.2f", res.nodoEncontrado.getValor())).append("\n");
        sb.append("Frecuencia: \t\t").append(res.nodoEncontrado.getFrecuencia()).append("\n");
        sb.append("Nivel: \t\t\t").append(res.nivel).append(" (La raíz es Nivel 0)\n");
        sb.append("Posición: \t\t").append(res.posicion).append("\n");

        if (res.nodoPadre != null) {
            sb.append("Valor del Padre: \t").append(String.format("%.2f", res.nodoPadre.getValor())).append("\n");
        } else {
            sb.append("Valor del Padre: \tN/A (es el nodo raíz)\n");
        }

        NodoArbol izquierdo = res.nodoEncontrado.getIzquierdo();
        sb.append("Hijo Izquierdo: \t").append(izquierdo != null ? String.format("%.2f", izquierdo.getValor()) : "Ninguno (Hoja)").append("\n");

        NodoArbol derecho = res.nodoEncontrado.getDerecho();
        sb.append("Hijo Derecho: \t").append(derecho != null ? String.format("%.2f", derecho.getValor()) : "Ninguno (Hoja)").append("\n");

        return sb.toString();
    }

    /**
     * Convierte una lista de strings (formato "valor (frec: X)") a una lista de doubles.
     */
    private List<Double> toDouble(List<String> raw) {
        return raw.stream()
                .map(s -> Double.parseDouble(s.split(" ")[0].replace(",", ".")))
                .collect(Collectors.toList());
    }

    /**
     * Calcula la profundidad (altura) de un subárbol.
     */
    private int depth(NodoArbol nodo) {
        return nodo == null ? -1 : 1 + Math.max(depth(nodo.getIzquierdo()), depth(nodo.getDerecho()));
    }

    /**
     * Asegura que un valor esté entre 0.0 y 1.0.
     */
    private double clamp(double value) {
        if (value < 0) return 0;
        if (value > 1) return 1;
        return value;
    }

    /**
     * Verifica si el árbol es nulo o está vacío y muestra una notificación si lo es.
     */
    private boolean isArbolInvalido() {
        if (arbolDeVentas == null || arbolDeVentas.estaVacio()) {
            NotificationManager.showNotification(rootPane, "El árbol está vacío. Genere el árbol primero.", NotificationManager.NotificationType.ERROR);
            return true;
        }
        return false;
    }

    /**
     * Obtiene el ancho del viewport del ScrollPane de forma segura, con un fallback.
     */
    private double getViewportWidthSafe() {
        Bounds bounds = scrollPane.getViewportBounds();
        return (bounds == null || bounds.getWidth() == 0) ? scrollPane.getWidth() : bounds.getWidth();
    }
    //endregion
}
