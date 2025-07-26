package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import service.CalculadoraService;
import util.InputValidador;
import util.ValidacionExcepcion;

/**
 * Controlador para la vista de la Calculadora Postfija (CalculadoraView.fxml).
 * Gestiona la entrada del usuario y la presentación de los resultados del cálculo.
 */
public class CalculadoraController {

    //region Componentes FXML
    @FXML private TextField txtExpresion;
    @FXML private TextArea resultsArea;
    //endregion

    /**
     * Instancia del servicio que contiene la lógica de conversión y evaluación
     * de expresiones matemáticas.
     */
    private final CalculadoraService calculadoraService;

    /**
     * Constructor que inicializa el servicio de la calculadora.
     */
    public CalculadoraController() {
        this.calculadoraService = new CalculadoraService();
    }

    /**
     * Se ejecuta al cargar la vista. Configura los listeners de los componentes.
     */
    @FXML
    public void initialize() {
        // Asigna la acción de evaluar al presionar la tecla "Enter" en el campo de texto.
        txtExpresion.setOnAction(event -> handleEvaluar());
    }

    /**
     * Maneja el evento para evaluar la expresión matemática introducida por el usuario.
     * Valida la entrada, la convierte a notación postfija, la evalúa y muestra
     * un informe completo o un mensaje de error en el área de resultados.
     */
    @FXML
    private void handleEvaluar() {
        try {
            // 1. Validar que la entrada no esté vacía.
            String expresion = InputValidador.validateNotEmpty(txtExpresion.getText(), "Expresión");

            // 2. Usar el servicio para procesar la expresión.
            String postfija = calculadoraService.convertirInfijaAPostfija(expresion);
            double resultado = calculadoraService.evaluarPostfija(postfija);

            // 3. Construir y mostrar el informe de resultados.
            String informe = "Expresión Infija: " + expresion + "\n" +
                    "Expresión Postfija: " + postfija + "\n" +
                    "-----------------------------\n" +
                    "Resultado: " + resultado;
            resultsArea.setText(informe);

        } catch (ValidacionExcepcion | IllegalArgumentException | ArithmeticException ex) {
            // Captura errores de validación, formato (ej. paréntesis) o matemáticos (ej. división por cero).
            resultsArea.setText("Error en la expresión:\n" + ex.getMessage());
        }
    }
}
