package org.example.reservaconcurrenteentradas;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReservaController {

    private static final int NUMERO_AGENTES = 3;

    @FXML
    private Label lblDisponibles;
    @FXML
    private Label lblConfirmadas;
    @FXML
    private Label lblRechazadas;
    @FXML
    private Label lblEstado;
    @FXML
    private Button btnIniciar;
    @FXML
    private TextArea txtRegistro;

    // Única instancia compartida por los tres agentes.
    private final SistemaReservas sistema = new SistemaReservas();
    private ExecutorService executor;

    // Estas variables solo se usan en el hilo de JavaFX, por eso no necesitan sincronización.
    private boolean simulacionActiva = false;
    private int agentesPendientes;

    @FXML
    private void initialize() {
        mostrarEstado();
    }

    @FXML
    private void iniciarSimulacion() {
        if (simulacionActiva) {
            txtRegistro.appendText("Ya hay una simulación en curso; se ignoró el nuevo inicio\n");
            return;
        }
        simulacionActiva = true;
        btnIniciar.setDisable(true);

        sistema.reiniciar();
        txtRegistro.clear();
        mostrarEstado();
        lblEstado.setText("Estado: Simulación en ejecución...");

        agentesPendientes = NUMERO_AGENTES;
        executor = Executors.newFixedThreadPool(NUMERO_AGENTES);
        for (int i = 1; i <= NUMERO_AGENTES; i++) {
            AgenteReserva agente = new AgenteReserva(i, sistema, this::registrarEvento);
            executor.execute(() -> {
                try {
                    agente.run();
                } finally {
                    Platform.runLater(this::agenteTerminado);
                }
            });
        }
        // No acepta más tareas; los hilos terminan solos cuando acaban los agentes.
        executor.shutdown();
    }

    public void detenerSimulacion() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    // Se llama desde los hilos de los agentes: los controles solo se tocan dentro de Platform.runLater.
    private void registrarEvento(String mensaje) {
        Platform.runLater(() -> {
            txtRegistro.appendText(mensaje + "\n");
            mostrarEstado();
        });
    }

    private void agenteTerminado() {
        agentesPendientes--;
        if (agentesPendientes == 0) {
            finalizarSimulacion();
        }
    }

    private void finalizarSimulacion() {
        mostrarEstado();
        lblEstado.setText("Estado: Simulación finalizada");
        simulacionActiva = false;
        btnIniciar.setDisable(false);
    }

    private void mostrarEstado() {
        SistemaReservas.Estado estado = sistema.obtenerEstado();
        lblDisponibles.setText(String.valueOf(estado.disponibles()));
        lblConfirmadas.setText(String.valueOf(estado.confirmadas()));
        lblRechazadas.setText(String.valueOf(estado.rechazadas()));
    }
}