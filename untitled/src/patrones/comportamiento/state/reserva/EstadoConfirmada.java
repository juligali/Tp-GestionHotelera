package patrones.comportamiento.state.reserva;
import modelo.reserva.Reserva;

public class EstadoConfirmada implements EstadoReserva {
    @Override
    public void confirmar(Reserva reserva) {
        System.out.println("La reserva ya está confirmada.");
    }

    @Override
    public void cancelar(Reserva reserva) {
        System.out.println("Reserva cancelada desde estado confirmada.");
        reserva.setEstado(new EstadoCancelada());
    }

    @Override
    public void finalizar(Reserva reserva) {
        System.out.println("Reserva finalizada.");
        reserva.setEstado(new EstadoFinalizada());
    }

    @Override
    public String getNombre() { return "CONFIRMADA"; }

}
