# CorpVentas – Sistema de Gestión y Análisis de Ventas

**Versión:** 4.0.0  
**Fecha:** Julio 2025

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

> CorpVentas es una aplicación de escritorio desarrollada en Java y JavaFX, diseñada para la gestión y el análisis de datos de ventas corporativas. El proyecto se centra en la implementación y evaluación de estructuras de datos fundamentales desde cero, demostrando su aplicación en un entorno práctico para la manipulación y procesamiento eficiente de la información.

## Tabla de Contenidos
1. [Características Principales](#-características-principales)
2. [Estructuras de Datos y Algoritmos](#-estructuras-de-datos-y-algoritmos)
3. [Stack Tecnológico](#-stack-tecnológico)
4. [Requisitos](#-requisitos)
5. [Instalación](#-instalación)
6. [Uso Rápido](#-uso-rápido)
7. [Capturas de Pantalla](#-capturas-de-pantalla)
8. [Agradecimientos](#-agradecimientos)
9. [Licencia](#-licencia)

## ✨ Características Principales

* **Gestión de Pedidos:** Permite registrar, consultar, actualizar y eliminar pedidos de clientes, utilizando diferentes estructuras de datos para comparar su eficiencia.
* **Análisis de Datos:** Ofrece un dashboard para visualizar métricas clave y analizar el rendimiento de las ventas.
* **Gestión de Ventas Regionales:** Módulo para administrar y consultar información de ventas por región.
* **Operaciones Avanzadas:** Interfaz para realizar operaciones complejas sobre listas, como búsquedas específicas, fusiones o eliminaciones masivas.
* **Calculadora de Expresiones:** Una herramienta integrada que utiliza una pila para convertir expresiones infijas a postfijas y evaluarlas.
* **Test de Rendimiento:** Un panel de pruebas para medir y comparar el tiempo de ejecución de operaciones (inserción, búsqueda, eliminación) en las distintas estructuras de datos implementadas (Arrays, Listas, ABB).

## 🛠️ Estructuras de Datos y Algoritmos

El núcleo de este proyecto es la implementación manual de las siguientes estructuras de datos y algoritmos para demostrar un entendimiento profundo de sus operaciones y complejidad.

* **Array Dinámico (`ArrayRepositorio`):**
    * Implementación de un array que se redimensiona automáticamente.
    * Operaciones: Inserción, búsqueda lineal, eliminación y acceso por índice.

* **Lista Simplemente Enlazada (`ListaSimpleRepositorio`):**
    * Estructura de nodos enlazados en una sola dirección.
    * Operaciones: Inserción (al inicio, al final), búsqueda, eliminación y recorrido.

* **Lista Doblemente Enlazada (`ListaDobleRepositorio`):**
    * Nodos con punteros al elemento siguiente y anterior, permitiendo un recorrido bidireccional.
    * Operaciones: Inserción, búsqueda, eliminación más eficiente que en la lista simple.

* **Pila (`PilaManual`):**
    * Implementación LIFO (Last-In, First-Out) basada en una lista enlazada.
    * Usada principalmente en la calculadora para el algoritmo de conversión infijo-postfijo (Shunting-yard) y la evaluación de expresiones.
    * Operaciones: `push` (apilar), `pop` (desapilar), `peek` (ver cima).

* **Cola (`ColaManual`):**
    * Implementación FIFO (First-In, First-Out) basada en una lista enlazada.
    * Utilizada para la gestión secuencial de pedidos.
    * Operaciones: `enqueue` (encolar), `dequeue` (desencolar), `peek` (ver frente).

* **Árbol Binario de Búsqueda (`ArbolBinarioBusqueda`):**
    * Estructura de datos no lineal para búsquedas, inserciones y eliminaciones eficientes (promedio O(log n)).
    * Operaciones: Inserción, búsqueda, eliminación (con manejo de casos: sin hijos, un hijo, dos hijos) y recorridos (In-Order, Pre-Order, Post-Order).

## 🚀 Stack Tecnológico

* **Lenguaje:** Java 17
* **Framework de UI:** JavaFX
* **Gestor de Proyectos:** Apache Maven
* **Diseño de Vistas:** FXML y CSS

##📋 Requisitos

* JDK (Java Development Kit) 17 o superior.
* Apache Maven 3.8 o superior.

## ⚙️ Instalación

1.  **Clonar el repositorio:**
    ```
    git clone https://github.com/Ferinjoque/corpVentas.git
    cd corpVentas
    ```

2.  **Compilar el proyecto con Maven:**
    Este comando resolverá las dependencias y empaquetará la aplicación en un archivo `.jar`.
    ```
    mvn clean package
    ```

3.  **Ejecutar la aplicación:**
    Una vez compilado, puedes ejecutar el archivo JAR generado que se encuentra en el directorio `target/`.
    ```
    java -jar target/corpVentas.jar
    ```
    *(El nombre del archivo puede variar según la versión en tu `pom.xml`)*

## ⚡ Uso Rápido

1.  Al iniciar la aplicación, se mostrará el **Dashboard principal**.
2.  Usa el menú de la izquierda para navegar entre las diferentes secciones:
    * **Pedidos:** Gestiona los registros de ventas. Puedes elegir la estructura de datos a utilizar (Array, Lista, ABB) para observar cómo afecta al rendimiento.
    * **Performance Test:** Selecciona una estructura y una operación para ejecutar pruebas de rendimiento con datos generados aleatoriamente.
    * **Calculadora:** Ingresa una expresión matemática para evaluarla.
3.  Los datos iniciales se cargan desde archivos CSV ubicados en el proyecto para poblar las estructuras.

## 🖼️ Capturas de Pantalla

<table>
  <tr>
    <td align="center">
      <img src="src/main/resources/img/dashboard.png" alt="Dashboard Principal" width="100%">
      <br>
      <sub><b>Dashboard Principal</b></sub>
    </td>
    <td align="center">
      <img src="src/main/resources/img/operaciones-avanzadas.png" alt="Operaciones Avanzadas" width="100%">
      <br>
      <sub><b>Operaciones Avanzadas</b></sub>
    </td>
    <td align="center">
      <img src="src/main/resources/img/ventas-regionales.png" alt="Ventas Regionales" width="100%">
      <br>
      <sub><b>Ventas Regionales</b></sub>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="src/main/resources/img/cola-pedidos.png" alt="Cola de Pedidos" width="100%">
      <br>
      <sub><b>Cola de Pedidos</b></sub>
    </td>
    <td align="center">
      <img src="src/main/resources/img/analisis-datos.png" alt="Análisis de Datos" width="100%">
      <br>
      <sub><b>Análisis de Datos</b></sub>
    </td>
    <td align="center">
      <img src="src/main/resources/img/rendimiento.png" alt="Test de Rendimiento" width="100%">
      <br>
      <sub><b>Test de Rendimiento</b></sub>
    </td>
  </tr>
</table>

## 🙏 Agradecimientos

* **Equipo de desarrollo:**
    * Fabian Patiño
    * Axel Ramirez

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo `LICENSE` para más detalles.
