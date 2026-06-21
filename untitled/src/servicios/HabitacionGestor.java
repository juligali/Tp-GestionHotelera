package servicios;

import enums.EstadoHabitacion;
import enums.Rol;
import enums.TipoHabitacion;
import modelo.habitacion.Habitacion;
import modelo.reserva.Reserva;
import modelo.usuario.UsuarioInterno;
import patrones.creacionales.factory.HabitacionFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HabitacionGestor {
    private List<Habitacion> habitaciones;


    public HabitacionGestor() {
        this.habitaciones = new ArrayList<>();
    }


    public Habitacion crearHabitacion(UsuarioInterno usuario, int numero, HabitacionFactory factory) {
        validarPermiso(usuario, Rol.ADMINISTRADOR);
        boolean numeroRepetido = habitaciones.stream()
                .anyMatch(h -> h.getNumero() == numero);
        if (numeroRepetido) {
            throw new IllegalArgumentException("Ya existe la habitacion #" + numero + ".");
        }
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

    public List<Habitacion> consultarDisponibilidad(LocalDate fechaIngreso, LocalDate fechaEgreso,
                                                    TipoHabitacion tipo, int capacidadMinima,
                                                    List<Reserva> reservas) {
        List<Habitacion> disponiblesPorFecha = consultarDisponibilidad(fechaIngreso, fechaEgreso, reservas);
        if (capacidadMinima <= 0) {
            throw new IllegalArgumentException("La cantidad de huespedes debe ser mayor a cero.");
        }

        List<Habitacion> disponibles = new ArrayList<>();
        for (Habitacion habitacion : disponiblesPorFecha) {
            if (habitacion.getTipo() == tipo && habitacion.getCapacidad() >= capacidadMinima) {
                disponibles.add(habitacion);
            }
        }
        return disponibles;
    }

    public List<Habitacion> consultarDisponibilidad(LocalDate fechaIngreso, LocalDate fechaEgreso,
                                                    List<Reserva> reservas) {
        if (fechaIngreso == null || fechaEgreso == null || !fechaEgreso.isAfter(fechaIngreso)) {
            throw new IllegalArgumentException("La fecha de egreso debe ser posterior al ingreso.");
        }

        List<Habitacion> disponibles = new ArrayList<>();
        for (Habitacion habitacion : habitaciones) {
            if (habitacion.getEstado() == EstadoHabitacion.OCUPADA
                    || habitacion.getEstado() == EstadoHabitacion.LIMPIEZA
                    || habitacion.getEstado() == EstadoHabitacion.FUERA_DE_SERVICIO) continue;

            boolean tieneCruce = reservas.stream()
                    .filter(r -> r.getHabitacion().getNumero() == habitacion.getNumero())
                    .filter(r -> !"CANCELADA".equals(r.getEstadoNombre())
                            && !"FINALIZADA".equals(r.getEstadoNombre()))
                    .anyMatch(r -> fechaIngreso.isBefore(r.getFechaEgreso())
                            && fechaEgreso.isAfter(r.getFechaIngreso()));

            if (!tieneCruce) disponibles.add(habitacion);
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
