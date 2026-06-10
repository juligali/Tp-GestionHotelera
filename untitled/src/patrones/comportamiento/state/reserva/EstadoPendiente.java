package patrones.comportamiento.state.reserva;
import modelo.reserva.Reserva;


public class EstadoPendiente implements EstadoReserva {
    @Override
    public void confirmar(Reserva reserva) {
        System.out.println("Reserva confirmada.");
        reserva.setEstado(new EstadoConfirmada());
    }

    @Override
    public void cancelar(Reserva reserva) {
        System.out.println("Reserva cancelada desde estado pendiente.");
        reserva.setEstado(new EstadoCancelada());
    }

    @Override
    public void finalizar(Reserva reserva) {
        System.out.println("No se puede finalizar una reserva pendiente.");
    }

    @Override
    public String getNombre() { return "PENDIENTE"; }

}
