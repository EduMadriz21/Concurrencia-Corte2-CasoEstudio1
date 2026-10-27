package org.example.reservaconcurrenteentradas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ReservaApplication extends Application {

    private ReservaController controller;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ReservaApplication.class.getResource("reserva-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 420, 640);
        controller = fxmlLoader.getController();
        stage.setTitle("Reserva concurrente de entradas");
        stage.setScene(scene);
        stage.show();
    }

    // Si se cierra la ventana durante una simulación, se interrumpen los agentes para no dejar hilos vivos.
    @Override
    public void stop() {
        if (controller != null) {
            controller.detenerSimulacion();
        }
    }
}