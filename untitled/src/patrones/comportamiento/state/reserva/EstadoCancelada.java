package patrones.comportamiento.state.reserva;
import modelo.reserva.Reserva;

import java.time.LocalDate;

import modelo.habitacion.Habitacion;

public class EstadoCancelada implements EstadoReserva{
    @Override
    public String confirmar(Reserva reserva) {
        throw new IllegalStateException("No se puede confirmar una reserva cancelada.");
    }

    @Override
    public String cancelar(Reserva reserva) {
        return "La reserva ya está cancelada.";
    }

    @Override
    public String finalizar(Reserva reserva) {
        throw new IllegalStateException("No se puede finalizar una reserva cancelada.");
    }

    @Override
    public void modificar(Reserva reserva, Habitacion nuevaHabitacion, LocalDate nuevaFechaIngreso, LocalDate nuevaFechaEgreso) {
        throw new IllegalStateException("No se puede modificar una reserva cancelada.");
    }

    @Override
    public String getNombre() { return "CANCELADA"; }

}