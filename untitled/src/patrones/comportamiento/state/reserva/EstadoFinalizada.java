package patrones.comportamiento.state.reserva;
import modelo.reserva.Reserva;

import java.time.LocalDate;

import modelo.habitacion.Habitacion;

public class EstadoFinalizada implements EstadoReserva{
    @Override
    public String confirmar(Reserva reserva) {
        throw new IllegalStateException("No se puede confirmar una reserva finalizada.");
    }

    @Override
    public String cancelar(Reserva reserva) {
        throw new IllegalStateException("No se puede cancelar una reserva finalizada.");
    }

    @Override
    public String finalizar(Reserva reserva) {
        return "La reserva ya está finalizada.";
    }

    @Override
    public void modificar(Reserva reserva, Habitacion nuevaHabitacion, LocalDate nuevaFechaIngreso, LocalDate nuevaFechaEgreso) {
        throw new IllegalStateException("No se puede modificar una reserva finalizada.");
    }

    @Override
    public String getNombre() { return "FINALIZADA"; }

}