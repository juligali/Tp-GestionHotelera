package servicios;

import modelo.estadia.Estadia;
import modelo.pago.Pago;
import modelo.reserva.Reserva;
import patrones.estructurales.decorator.ComponenteEstadia;

import java.util.ArrayList;
import java.util.List;

public class EstadiaGestor {

    private List<Estadia> estadias;

    public EstadiaGestor() {
        this.estadias = new ArrayList<>();
    }

    public Estadia realizarCheckIn(Reserva reserva) {
        Estadia estadia = new Estadia(reserva);
        estadia.realizarCheckIn();
        estadias.add(estadia);
        return estadia;
    }

    public Pago realizarCheckOut(Estadia estadia, int pagoId, String metodoPago) {
        return estadia.realizarCheckOut(pagoId, metodoPago);
    }

    public void agregarServicio(Estadia estadia, ComponenteEstadia servicioDecorado) {
        estadia.agregarServicio(servicioDecorado);
        System.out.println("Servicio agregado. Descripción actual: " + estadia.getDescripcionServicios());
    }

    public List<Estadia> getEstadias() {
        return new ArrayList<>(estadias);
    }
}


