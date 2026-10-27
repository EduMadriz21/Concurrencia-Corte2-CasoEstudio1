package org.example.reservaconcurrenteentradas;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class AgenteReserva implements Runnable {

    public static final int INTENTOS = 10;

    private final int numero;
    private final SistemaReservas sistema;
    private final Consumer<String> registro;

    public AgenteReserva(int numero, SistemaReservas sistema, Consumer<String> registro) {
        this.numero = numero;
        this.sistema = sistema;
        this.registro = registro;
    }

    @Override
    public void run() {
        for (int intento = 1; intento <= INTENTOS; intento++) {
            if (sistema.reservar()) {
                registro.accept("Agente " + numero + " confirmó una reserva");
            } else {
                registro.accept("Agente " + numero + " encontró entradas agotadas");
            }

            try {
                // La pausa va fuera de la sección crítica para no bloquear a los otros agentes.
                Thread.sleep(ThreadLocalRandom.current().nextInt(100, 300));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}