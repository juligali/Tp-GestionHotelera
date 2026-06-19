package patrones.comportamiento.state.reserva;
import modelo.reserva.Reserva;

import java.time.LocalDate;

import modelo.habitacion.Habitacion;

public class EstadoConfirmada implements EstadoReserva {
    @Override
    public String confirmar(Reserva reserva) {
        return "La reserva ya está confirmada.";
    }

    @Override
    public String cancelar(Reserva reserva) {
        reserva.setEstado(new EstadoCancelada());
        return "Reserva cancelada desde estado confirmada.";
    }

    @Override
    public String finalizar(Reserva reserva) {
        reserva.setEstado(new EstadoFinalizada());
        return "Reserva finalizada.";
    }

    @Override
    public void modificar(Reserva reserva, Habitacion nuevaHabitacion, LocalDate nuevaFechaIngreso, LocalDate nuevaFechaEgreso) {
        reserva.aplicarModificacion(nuevaHabitacion, nuevaFechaIngreso, nuevaFechaEgreso);
    }

    @Override
    public String getNombre() { return "CONFIRMADA"; }

}