package patrones.comportamiento.state.reserva;
import modelo.reserva.Reserva;

public interface EstadoReserva {
    void confirmar(Reserva reserva);
    void cancelar(Reserva reserva);
    void finalizar(Reserva reserva);
    String getNombre();

}
