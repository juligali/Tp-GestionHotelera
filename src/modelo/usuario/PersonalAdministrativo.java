package modelo.usuario;

import enums.Rol;
import modelo.reserva.Reserva;
import servicios.ReservaGestor;

import java.util.List;

public class PersonalAdministrativo extends UsuarioInterno {

    public PersonalAdministrativo(int id, String nombre, String email, String contrasena) {
        super(id, nombre, email, contrasena, Rol.PERSONAL_ADMINISTRATIVO);
    }

    public List<Reserva> consultarReservas(ReservaGestor gestor) {
        return gestor.getReservas();
    }

    public void verificarPagos(ReservaGestor gestor) {
        gestor.getReservas().forEach(r ->
                System.out.println("Reserva #" + r.getId() +
                        " — Estado: " + r.getEstadoNombre()));
    }

    public void emitirComprobante(ReservaGestor gestor, int idReserva) {
        gestor.getReservas().stream()
                .filter(r -> r.getId() == idReserva)
                .findFirst()
                .ifPresent(r -> System.out.println("Comprobante emitido para reserva #" + r.getId()));
    }
}
