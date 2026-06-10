package patrones.comportamiento.state.reserva;
import modelo.reserva.Reserva;

public class EstadoFinalizada implements EstadoReserva{
    @Override
    public void confirmar(Reserva reserva) {
        System.out.println("No se puede confirmar una reserva finalizada.");
    }

    @Override
    public void cancelar(Reserva reserva) {
        System.out.println("No se puede cancelar una reserva finalizada.");
    }

    @Override
    public void finalizar(Reserva reserva) {
        System.out.println("La reserva ya está finalizada.");
    }

    @Override
    public String getNombre() { return "FINALIZADA"; }

}
