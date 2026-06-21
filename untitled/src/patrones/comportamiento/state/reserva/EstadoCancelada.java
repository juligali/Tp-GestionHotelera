package patrones.comportamiento.state.reserva;
import modelo.reserva.Reserva;

public class EstadoCancelada implements EstadoReserva{
    @Override
    public void confirmar(Reserva reserva) {
        System.out.println("No se puede confirmar una reserva cancelada.");
    }

    @Override
    public void cancelar(Reserva reserva) {
        System.out.println("La reserva ya está cancelada.");
    }

    @Override
    public void finalizar(Reserva reserva) {
        System.out.println("No se puede finalizar una reserva cancelada.");
    }

    @Override
    public String getNombre() { return "CANCELADA"; }

}
