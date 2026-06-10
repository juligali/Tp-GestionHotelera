package modelo.usuario;

import enums.Rol;
import modelo.reserva.Reserva;
import java.util.ArrayList;
import java.util.List;

public class Huesped extends UsuarioInterno {
    private List<Reserva> reservas;
    private String telefono;

    public Huesped(int id, String nombre, String email, String contrasena, String telefono) {
        super(id, nombre, email, contrasena, Rol.HUESPED);
        this.telefono = telefono;
        this.reservas = new ArrayList<>();
    }

    public void registrar() {
        System.out.println("Huésped registrado: " + getNombre());
    }

    public void agregarReserva(Reserva reserva) {
        reservas.add(reserva);
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public String getTelefono() {
        return telefono;
    }

    public void consultarHistorial() {
        System.out.println("=== HISTORIAL DE RESERVAS DE " + getNombre() + " ===");
        reservas.forEach(r ->
                System.out.println("Reserva #" + r.getId() +
                        " - Estado: " + r.getEstadoNombre()));
    }
}