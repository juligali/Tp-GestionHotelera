package modelo.usuario;

import enums.Rol;
import servicios.HabitacionGestor;
import servicios.ReservaGestor;

import java.util.List;

public class Huesped extends UsuarioInterno {
    private List<UsuarioInterno> usuarios;

    public Administrador(int id, String nombre, String email, String contrasena) {
        super(id, nombre, email, contrasena, Rol.ADMINISTRADOR);
        this.usuarios = new java.util.ArrayList<>();
    }

    public void configurarSistema() {
        System.out.println("Sistema configurado por administrador: " + getNombre());
    }

    public void gestionarUsuarios(UsuarioInterno usuario) {
        usuarios.add(usuario);
        System.out.println("Usuario agregado: " + usuario.getNombre() + " — Rol: " + usuario.getRol());
    }

    public void consultarReportes(ReservaGestor reservaGestor, HabitacionGestor habitacionGestor) {
        System.out.println("=== REPORTE DE RESERVAS ===");
        reservaGestor.getReservas().forEach(r ->
                System.out.println("Reserva #" + r.getId() +
                        " — Huésped: " + r.getHuesped().getNombre() +
                        " — Estado: " + r.getEstadoNombre()));
        System.out.println("=== REPORTE DE HABITACIONES ===");
        habitacionGestor.getHabitaciones().forEach(h ->
                System.out.println("Habitación #" + h.getNumero() +
                        " — Tipo: " + h.getTipo() +
                        " — Estado: " + h.getEstado()));
    }

    public List<UsuarioInterno> getUsuarios() {
        return usuarios;
    }
}
