package org.example.reservaconcurrenteentradas;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SistemaReservasTest {

    @RepeatedTest(5)
    void tresAgentesTerminanConCeroDisponiblesVeinteConfirmadasYDiezRechazadas() throws InterruptedException {
        SistemaReservas sistema = new SistemaReservas();
        simular(sistema, mensaje -> { });

        SistemaReservas.Estado estado = sistema.obtenerEstado();
        assertEquals(0, estado.disponibles());
        assertEquals(20, estado.confirmadas());
        assertEquals(10, estado.rechazadas());
    }

    @Test
    void cadaAgenteHaceDiezIntentosYNuncaHayInventarioNegativo() throws InterruptedException {
        SistemaReservas sistema = new SistemaReservas();
        AtomicInteger intentos = new AtomicInteger();
        AtomicBoolean estadoInvalido = new AtomicBoolean(false);

        simular(sistema, mensaje -> {
            intentos.incrementAndGet();
            SistemaReservas.Estado estado = sistema.obtenerEstado();
            if (estado.disponibles() < 0
                    || estado.disponibles() + estado.confirmadas() != SistemaReservas.ENTRADAS_INICIALES) {
                estadoInvalido.set(true);
            }
        });

        assertEquals(30, intentos.get());
        assertFalse(estadoInvalido.get());
    }

    @Test
    void reiniciarVuelveAlEstadoInicial() throws InterruptedException {
        SistemaReservas sistema = new SistemaReservas();
        simular(sistema, mensaje -> { });

        sistema.reiniciar();

        assertEquals(new SistemaReservas.Estado(20, 0, 0), sistema.obtenerEstado());
    }

    // Aquí sí se puede esperar con awaitTermination porque es el hilo del test, no el de JavaFX.
    private void simular(SistemaReservas sistema, Consumer<String> registro) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (int i = 1; i <= 3; i++) {
            executor.execute(new AgenteReserva(i, sistema, registro));
        }
        executor.shutdown();
        assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS));
    }
}