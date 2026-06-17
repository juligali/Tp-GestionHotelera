package servicios;


import enums.Rol;
import modelo.habitacion.Habitacion;
import modelo.promocion.Promocion;
import modelo.reserva.Reserva;
import modelo.usuario.Huesped;
import modelo.usuario.UsuarioInterno;
import patrones.comportamiento.strategy.EstrategiaDescuento;
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
                                LocalDate fechaIngreso, LocalDate fechaEgreso,
                                EstrategiaDescuento estrategia, Promocion promocion) {
        validarPermiso(usuario, Rol.RECEPCIONISTA, Rol.ADMINISTRADOR);
        builder = new ReservaBuilder(generarId())
                .conHuesped(huesped)
                .conHabitacion(habitacion)
                .conFechas(fechaIngreso, fechaEgreso)
                .conEstrategia(estrategia)
                .conPromocion(promocion);
        Reserva reserva = builder.build();
        reservas.add(reserva);
        reserva.getHuesped().agregarReserva(reserva);
        System.out.println("Reserva #" + reserva.getId() + " creada por " + usuario.getNombre());
        return reserva;
    }


    public void confirmarReserva(UsuarioInterno usuario, int id) {
        validarPermiso(usuario, Rol.RECEPCIONISTA, Rol.ADMINISTRADOR);
        Reserva reserva = buscarPorId(id);
        if (reserva != null) {
            reserva.confirmar();
        } else {
            System.out.println("No se encontró la reserva #" + id);
        }
    }


    public void cancelarReservaCliente(Huesped huesped, int id) {
        Reserva reserva = buscarPorId(id);
        if (reserva == null) {
            throw new IllegalArgumentException("No se encontro la reserva #" + id);
        }
        validarReservaDelHuesped(huesped, reserva);
        reserva.cancelar();
    }
    public void modificarReservaCliente(Huesped huesped, int id, Habitacion nuevaHabitacion,
                                        LocalDate fechaIngreso, LocalDate fechaEgreso,
                                        EstrategiaDescuento estrategia, Promocion promocion) {
        Reserva reserva = buscarPorId(id);
        if (reserva == null) {
            throw new IllegalArgumentException("No se encontro la reserva #" + id);
        }
        validarReservaDelHuesped(huesped, reserva);
        reserva.modificar(nuevaHabitacion, fechaIngreso, fechaEgreso, estrategia, promocion);
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