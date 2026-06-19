package patrones.comportamiento.state.reserva;
import modelo.reserva.Reserva;

import java.time.LocalDate;

import modelo.habitacion.Habitacion;


public class EstadoPendiente implements EstadoReserva {
    @Override
    public String confirmar(Reserva reserva) {
        reserva.setEstado(new EstadoConfirmada());
        return "Reserva confirmada.";
    }

    @Override
    public String cancelar(Reserva reserva) {
        reserva.setEstado(new EstadoCancelada());
        return "Reserva cancelada desde estado pendiente.";
    }

    @Override
    public String finalizar(Reserva reserva) {
        throw new IllegalStateException("No se puede finalizar una reserva pendiente.");
    }

    @Override
    public void modificar(Reserva reserva, Habitacion nuevaHabitacion, LocalDate nuevaFechaIngreso, LocalDate nuevaFechaEgreso) {
        reserva.aplicarModificacion(nuevaHabitacion, nuevaFechaIngreso, nuevaFechaEgreso);
    }

    @Override
    public String getNombre() { return "PENDIENTE"; }

}