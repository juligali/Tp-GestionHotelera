package servicios;


import enums.Rol;
import modelo.habitacion.Habitacion;
import modelo.reserva.Reserva;
import modelo.usuario.Huesped;
import modelo.usuario.UsuarioInterno;
import patrones.comportamiento.strategy.EstrategiaDescuento;
import patrones.comportamiento.strategy.SinDescuento;
import patrones.creacionales.builder.ReservaBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ReservaGestor {
    private List<Reserva> reservas;
    private int contadorId;
    private ReservaBuilder builder;


    public ReservaGestor() {
        this.reservas = new ArrayList<>();
        this.contadorId = 1;
    }


    public Reserva crearReserva(UsuarioInterno usuario, Huesped huesped, Habitacion habitacion,
                                LocalDate fechaIngreso, LocalDate fechaEgreso) {
        validarPermiso(usuario, Rol.RECEPCIONISTA, Rol.ADMINISTRADOR);
        builder = new ReservaBuilder(generarId())
                .conHuesped(huesped)
                .conHabitacion(habitacion)
                .conFechas(fechaIngreso, fechaEgreso)
                .conEstrategia(new SinDescuento());
        Reserva reserva = builder.build();
        reservas.add(reserva);
        reserva.getHuesped().agregarReserva(reserva);
        System.out.println("Reserva #" + reserva.getId() + " creada por " + usuario.getNombre());
        return reserva;
    }


    public String confirmarReserva(UsuarioInterno usuario, int id, EstrategiaDescuento estrategia) {
        validarPermiso(usuario, Rol.RECEPCIONISTA, Rol.ADMINISTRADOR);
        Reserva reserva = buscarPorId(id);
        if (reserva == null) {
            throw new IllegalArgumentException("No se encontró la reserva #" + id);
        }
        reserva.cambiarEstrategia(estrategia);
        return reserva.confirmar();
    }


    public String cancelarReserva(UsuarioInterno usuario, int id) {
        validarPermiso(usuario, Rol.RECEPCIONISTA, Rol.ADMINISTRADOR, Rol.PERSONAL_ADMINISTRATIVO);
        Reserva reserva = buscarPorId(id);
        if (reserva == null) {
            throw new IllegalArgumentException("No se encontró la reserva #" + id);
        }
        return reserva.cancelar();
    }


    public String cancelarReservaCliente(Huesped huesped, int id) {
        Reserva reserva = buscarPorId(id);
        if (reserva == null) {
            throw new IllegalArgumentException("No se encontro la reserva #" + id);
        }
        validarReservaDelHuesped(huesped, reserva);
        return reserva.cancelar();
    }


    public void modificarReservaCliente(Huesped huesped, int id, Habitacion nuevaHabitacion,
                                        LocalDate fechaIngreso, LocalDate fechaEgreso) {
        Reserva reserva = buscarPorId(id);
        if (reserva == null) {
            throw new IllegalArgumentException("No se encontro la reserva #" + id);
        }
        validarReservaDelHuesped(huesped, reserva);
        reserva.modificar(nuevaHabitacion, fechaIngreso, fechaEgreso);
    }


    public List<Reserva> buscarReservas(UsuarioInterno usuario, String nombreHuesped) {
        validarPermiso(usuario, Rol.RECEPCIONISTA, Rol.ADMINISTRADOR, Rol.PERSONAL_ADMINISTRATIVO);
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getHuesped().getNombre().equalsIgnoreCase(nombreHuesped)) {
                resultado.add(r);
            }
        }
        return resultado;
    }


    private void validarPermiso(UsuarioInterno usuario, Rol... rolesPermitidos) {
        for (Rol rol : rolesPermitidos) {
            if (usuario.getRol() == rol) return;
        }
        throw new IllegalStateException("El usuario " + usuario.getNombre() +
                " no tiene permisos para realizar esta operación.");
    }


    private void validarReservaDelHuesped(Huesped huesped, Reserva reserva) {
        if (!reserva.getHuesped().getEmail().equalsIgnoreCase(huesped.getEmail())) {
            throw new IllegalStateException("El huesped solo puede operar sobre sus propias reservas.");
        }
    }


    public int generarId() {
        return contadorId++;
    }


    private Reserva buscarPorId(int id) {
        for (Reserva r : reservas) {
            if (r.getId() == id) return r;
        }
        return null;
    }


    public List<Reserva> getReservas() {
        return new ArrayList<>(reservas);
    }
}
