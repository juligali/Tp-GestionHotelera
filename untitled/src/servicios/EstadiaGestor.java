package servicios;

import enums.Rol;
import modelo.estadia.Estadia;
import modelo.pago.Pago;
import modelo.reserva.Reserva;
import modelo.usuario.UsuarioInterno;
import patrones.estructurales.decorator.ComponenteEstadia;


import java.util.ArrayList;
import java.util.List;

public class EstadiaGestor {
    private List<Estadia> estadias;
    private List<Pago> pagos;


    public EstadiaGestor() {
        this.estadias = new ArrayList<>();
        this.pagos = new ArrayList<>();
    }


    public Estadia realizarCheckIn(UsuarioInterno usuario, Reserva reserva) {
        validarPermiso(usuario, Rol.RECEPCIONISTA, Rol.ADMINISTRADOR);
        if (!"CONFIRMADA".equals(reserva.getEstadoNombre())) {
            throw new IllegalStateException("Solo se puede hacer check-in de reservas confirmadas.");
        }
        Estadia estadia = new Estadia(reserva);
        estadia.realizarCheckIn();
        estadias.add(estadia);
        return estadia;
    }


    public Pago realizarCheckOut(UsuarioInterno usuario, Estadia estadia, int pagoId, String metodoPago) {
        validarPermiso(usuario, Rol.RECEPCIONISTA, Rol.ADMINISTRADOR);
        Pago pago = estadia.realizarCheckOut(pagoId, metodoPago);
        pagos.add(pago);
        return pago;
    }


    public void agregarServicio(UsuarioInterno usuario, Estadia estadia, ComponenteEstadia servicioDecorado) {
        validarPermiso(usuario, Rol.RECEPCIONISTA, Rol.ADMINISTRADOR);
        estadia.agregarServicio(servicioDecorado);
        System.out.println("Servicio agregado. Descripción actual: " + estadia.getDescripcionServicios());
    }


    private void validarPermiso(UsuarioInterno usuario, Rol... rolesPermitidos) {
        for (Rol rol : rolesPermitidos) {
            if (usuario.getRol() == rol) return;
        }
        throw new IllegalStateException("El usuario " + usuario.getNombre() +
                " no tiene permisos para realizar esta operación.");
    }


    public List<Estadia> getEstadias() {
        return new ArrayList<>(estadias);
    }


    public List<Pago> getPagos() {
        return new ArrayList<>(pagos);
    }
}
