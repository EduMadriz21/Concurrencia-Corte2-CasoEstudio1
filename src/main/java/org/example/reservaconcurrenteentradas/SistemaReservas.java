package org.example.reservaconcurrenteentradas;

public class SistemaReservas {

    public static final int ENTRADAS_INICIALES = 20;

    private int entradasDisponibles = ENTRADAS_INICIALES;
    private int reservasConfirmadas;
    private int solicitudesRechazadas;

    // La comprobación y el descuento están en el mismo método synchronized:
    // ningún otro agente puede entrar entre "¿quedan entradas?" y "descontar".
    public synchronized boolean reservar() {
        if (entradasDisponibles > 0) {
            entradasDisponibles--;
            reservasConfirmadas++;
            return true;
        }
        solicitudesRechazadas++;
        return false;
    }

    public synchronized void reiniciar() {
        entradasDisponibles = ENTRADAS_INICIALES;
        reservasConfirmadas = 0;
        solicitudesRechazadas = 0;
    }

    // Lee los tres contadores con el mismo candado para obtener un estado coherente.
    public synchronized Estado obtenerEstado() {
        return new Estado(entradasDisponibles, reservasConfirmadas, solicitudesRechazadas);
    }

    public record Estado(int disponibles, int confirmadas, int rechazadas) {
    }
}