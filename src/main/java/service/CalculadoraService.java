package service;

import repository.PilaManual;
import java.util.EmptyStackException;

/**
 * Servicio que encapsula la lógica para la conversión y evaluación de expresiones
 * aritméticas. Utiliza una implementación manual de Pila para gestionar
 * los operadores y operandos.
 */
public class CalculadoraService {

    /**
     * Asigna un valor de precedencia a los operadores aritméticos.
     * Un valor mayor indica una mayor precedencia, lo que determina el orden
     * de las operaciones en el algoritmo de conversión.
     *
     * @param operador El operador a evaluar ('+', '-', '*', '/', '^').
     * @return Un entero representando el nivel de precedencia (1, 2 o 3),
     * o -1 si el carácter no es un operador estándar.
     */
    private int precedencia(char operador) {
        switch (operador) {
            case '+': case '-': return 1; // Menor precedencia
            case '*': case '/': return 2;
            case '^': return 3; // Mayor precedencia
            default: return -1; // Carácter no es un operador (ej. paréntesis)
        }
    }

    /**
     * Convierte una expresión matemática de notación infija a postfija
     * (Notación Polaca Inversa) utilizando el algoritmo POLACA.
     *
     * @param expresionInfija La expresión en notación infija (ej: "5 * (4 + 3)").
     * @return La expresión equivalente en notación postfija (ej: "5 4 3 + *").
     * @throws IllegalArgumentException si la expresión tiene paréntesis desbalanceados.
     */
    public String convertirInfijaAPostfija(String expresionInfija) {
        // Pre-procesa la cadena para asegurar que cada token (número, operador, paréntesis)
        // esté separado por espacios, facilitando su procesamiento.
        String expresionProcesada = expresionInfija.replaceAll("([*+()\\-/^])", " $1 ");
        String[] tokens = expresionProcesada.trim().split("\\s+");

        StringBuilder resultado = new StringBuilder();
        PilaManual<Character> pila = new PilaManual<>();

        for (String token : tokens) {
            if (token.isEmpty()) continue;

            if (token.matches("-?\\d+(\\.\\d+)?")) {
                // Si el token es un número (operando), se añade directamente al resultado.
                resultado.append(token).append(" ");
            } else if (token.equals("(")) {
                // Si es un paréntesis de apertura, se apila.
                pila.push('(');
            } else if (token.equals(")")) {
                // Si es un paréntesis de cierre, se desapilan operadores hasta encontrar el '('.
                while (!pila.isEmpty() && pila.peek() != '(') {
                    resultado.append(pila.pop()).append(" ");
                }
                if (pila.isEmpty()) {
                    throw new IllegalArgumentException("Expresión con paréntesis desbalanceados (falta '(').");
                }
                pila.pop(); // Descarta el '(' de la pila.
            } else {
                // Si es un operador, se desapilan los operadores de mayor o igual precedencia.
                char operadorActual = token.charAt(0);
                while (!pila.isEmpty() && precedencia(operadorActual) <= precedencia(pila.peek())) {
                    resultado.append(pila.pop()).append(" ");
                }
                // Finalmente, se apila el operador actual.
                pila.push(operadorActual);
            }
        }

        // Al final del recorrido, se desapilan todos los operadores restantes.
        while (!pila.isEmpty()) {
            if (pila.peek() == '(') {
                throw new IllegalArgumentException("Expresión con paréntesis desbalanceados (falta ')').");
            }
            resultado.append(pila.pop()).append(" ");
        }

        return resultado.toString().trim();
    }

    /**
     * Evalúa una expresión en notación postfija.
     *
     * @param expresionPostfija La expresión a evaluar, con tokens separados por espacios.
     * @return El resultado numérico del cálculo.
     * @throws IllegalArgumentException si la expresión está malformada (faltan o sobran operandos).
     * @throws ArithmeticException si ocurre una división por cero.
     */
    public double evaluarPostfija(String expresionPostfija) {
        PilaManual<Double> pila = new PilaManual<>();
        String[] tokens = expresionPostfija.split("\\s+");

        for (String token : tokens) {
            if (token.isEmpty()) continue;

            if (token.matches("-?\\d+(\\.\\d+)?")) {
                // Si el token es un número, se apila.
                pila.push(Double.parseDouble(token));
            } else {
                // Si es un operador, se desapilan los dos últimos operandos para operar.
                try {
                    double val2 = pila.pop();
                    double val1 = pila.pop();

                    switch (token.charAt(0)) {
                        case '+' -> pila.push(val1 + val2);
                        case '-' -> pila.push(val1 - val2);
                        case '*' -> pila.push(val1 * val2);
                        case '/' -> {
                            if (val2 == 0) throw new ArithmeticException("División por cero.");
                            pila.push(val1 / val2);
                        }
                        case '^' -> pila.push(Math.pow(val1, val2));
                        default -> throw new IllegalArgumentException("Operador desconocido: " + token);
                    }
                } catch (EmptyStackException e) {
                    throw new IllegalArgumentException("Expresión postfija malformada (faltan operandos para el operador '" + token + "').");
                }
            }
        }

        // Al final, la pila debe contener exactamente un elemento: el resultado final.
        if (pila.size() != 1) {
            throw new IllegalArgumentException("Expresión postfija malformada (sobran operandos).");
        }
        return pila.pop();
    }
}
