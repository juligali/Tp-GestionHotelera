package modelo.usuario;

import enums.Rol;
import modelo.estadia.Estadia;
import modelo.habitacion.Habitacion;
import modelo.reserva.Reserva;
import modelo.usuario.Huesped;
import servicios.EstadiaGestor;
import servicios.HabitacionGestor;
import servicios.ReservaGestor;

public class Recepcionista extends UsuarioInterno {
    public Recepcionista(int id, String nombre, String email, String contrasena) {
        super(id, nombre, email, contrasena, Rol.RECEPCIONISTA);
    }

    public void gestionarReserva(ReservaGestor gestor, int idReserva) {
        gestor.confirmarReserva(idReserva);
    }

    public Estadia realizarCheckIn(ReservaGestor reservaGestor, EstadiaGestor estadiaGestor, int idReserva) {
        Reserva reserva = reservaGestor.getReservas().stream()
                .filter(r -> r.getId() == idReserva)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada: " + idReserva));
        return estadiaGestor.realizarCheckIn(reserva);
    }

    public void realizarCheckOut(EstadiaGestor estadiaGestor, Estadia estadia, int pagoId, String metodoPago) {
        estadiaGestor.realizarCheckOut(estadia, pagoId, metodoPago);
    }

    public void registrarHuesped(Huesped huesped) {
        huesped.registrar();
    }
}
