package patrones.comportamiento.observer;
import modelo.reserva.Reserva;

public interface ObservadorReserva {
    void actualizar(Reserva reserva);
}
