package servicios;

import enums.EstadoHabitacion;
import enums.Rol;
import enums.TipoHabitacion;
import modelo.habitacion.Habitacion;
import modelo.usuario.UsuarioInterno;
import patrones.creacionales.factory.HabitacionFactory;

public class HabitacionGestor {
    private List<Habitacion> habitaciones;


    public HabitacionGestor() {
        this.habitaciones = new ArrayList<>();
    }


    public Habitacion crearHabitacion(UsuarioInterno usuario, int numero, HabitacionFactory factory) {
        validarPermiso(usuario, Rol.ADMINISTRADOR);
        Habitacion habitacion = factory.crearHabitacion(numero);
        habitaciones.add(habitacion);
        return habitacion;
    }


    public List<Habitacion> consultarDisponibilidad(LocalDate fechaIngreso, LocalDate fechaEgreso, TipoHabitacion tipo) {
        List<Habitacion> disponibles = new ArrayList<>();
        for (Habitacion h : habitaciones) {
            if (h.getTipo() == tipo && h.estaDisponible()) {
                disponibles.add(h);
            }
        }
        return disponibles;
    }


    public void cambiarEstado(UsuarioInterno usuario, int numero, EstadoHabitacion nuevoEstado) {
        validarPermiso(usuario, Rol.RECEPCIONISTA, Rol.ADMINISTRADOR);
        for (Habitacion h : habitaciones) {
            if (h.getNumero() == numero) {
                h.cambiarEstado(nuevoEstado);
                System.out.println("Habitación #" + numero + " → " + nuevoEstado);
                return;
            }
        }
        System.out.println("No se encontró la habitación #" + numero);
    }


    private void validarPermiso(UsuarioInterno usuario, Rol... rolesPermitidos) {
        for (Rol rol : rolesPermitidos) {
            if (usuario.getRol() == rol) return;
        }
        throw new IllegalStateException("El usuario " + usuario.getNombre() +
                " no tiene permisos para realizar esta operación.");
    }


    public List<Habitacion> getHabitaciones() {
        return new ArrayList<>(habitaciones);
    }
}
