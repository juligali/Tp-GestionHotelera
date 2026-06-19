package patrones.comportamiento.state.reserva;
import modelo.reserva.Reserva;

import java.time.LocalDate;

import modelo.habitacion.Habitacion;

public interface EstadoReserva {
    String confirmar(Reserva reserva);
    String cancelar(Reserva reserva);
    String finalizar(Reserva reserva);
    void modificar(Reserva reserva, Habitacion nuevaHabitacion, LocalDate nuevaFechaIngreso, LocalDate nuevaFechaEgreso);
    String getNombre();

}