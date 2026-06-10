package modelo.usuario;

import modelo.reserva.Reserva;
import java.util.ArrayList;
import java.util.List;

public class Huesped {

    private int id;
    private String nombre;
    private String email;
    private String telefono;
    private String contrasena;
    private List<Reserva> reservas;

    public Huesped(int id, String nombre, String email, String telefono, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.contrasena = contrasena;
        this.reservas = new ArrayList<>();
    }

    public void registrar() {
        // TODO: persistir en base de datos
        System.out.println("Huésped " + nombre + " registrado correctamente.");
    }

    public boolean login(String email, String contrasena) {
        return this.email.equals(email) && this.contrasena.equals(contrasena);
    }

    public List<Reserva> consultarReservas() {
        return new ArrayList<>(reservas);
    }

    public void agregarReserva(Reserva reserva) {
        this.reservas.add(reserva);
    }

    public void modificarPerfil(String nombre, String telefono) {
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
}
