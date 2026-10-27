# Reserva concurrente de entradas

Caso de Estudio #1 de Programación de Aplicaciones de Escritorio.

Aplicación JavaFX que simula 3 agentes atendiendo al mismo tiempo la reserva de 20 entradas.
Cada agente hace 10 intentos de 1 entrada (30 solicitudes en total), por lo que toda simulación
termina en **0 disponibles, 20 confirmadas y 10 rechazadas**.

## Requisitos

- JDK 21
- Maven 3.9+ (o el wrapper `mvnw` incluido)

## Cómo ejecutar

- **IntelliJ IDEA:** ejecutar la clase `Launcher`.
- **Terminal:** `mvn clean javafx:run`
- **Pruebas:** `mvn clean test`

## Estructura

| Clase | Responsabilidad |
|---|---|
| `ReservaApplication` | Inicia JavaFX, carga `reserva-view.fxml` y crea la ventana. |
| `ReservaController` | Controla la interfaz, crea los 3 agentes en un `ExecutorService`, impide el doble inicio y detecta el final. |
| `SistemaReservas` | Recurso compartido: entradas disponibles, reservas confirmadas y solicitudes rechazadas. |
| `AgenteReserva` | `Runnable` que hace 10 intentos de reserva sobre el mismo `SistemaReservas`. |
| `Launcher` | Punto de entrada para ejecutar desde IntelliJ. |

## Concurrencia y sincronización

- Los 3 agentes corren en `Executors.newFixedThreadPool(3)`, nunca en el hilo de JavaFX.
- `SistemaReservas.reservar()` es `synchronized`: la comprobación "¿quedan entradas?" y el
  descuento ocurren en la misma sección crítica, así que no puede haber sobreventa ni inventario negativo.
- Los agentes no tocan controles JavaFX; la interfaz se actualiza con `Platform.runLater`.
- `simulacionActiva` (además del botón deshabilitado) impide iniciar otra simulación mientras hay una en curso.
- Cuando terminan los 3 agentes se muestra "Simulación finalizada" y se habilita de nuevo el botón.

## Registro de simulaciones

| Simulación | Disponibles | Confirmadas | Rechazadas | Observaciones |
|---|---|---|---|---|
| 1 | | | | |
| 2 | | | | |
| 3 | | | | |
| 4 | | | | |
| 5 | | | | |
