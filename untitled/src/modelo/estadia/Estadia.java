package modelo.estadia;

import enums.EstadoHabitacion;
import modelo.pago.Pago;
import modelo.reserva.Reserva;
import patrones.estructurales.decorator.ComponenteEstadia;
import patrones.estructurales.decorator.DesayunoDecorator;
import patrones.estructurales.decorator.EstacionamientoDecorator;
import patrones.estructurales.decorator.EstadiaBase;
import patrones.estructurales.decorator.SpaDecorator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class Estadia {

    private Reserva reserva;
    private LocalDate fechaCheckIn;
    private LocalDate fechaCheckOut;
    private ComponenteEstadia componenteEstadia;
    private final Set<TipoAmenity> amenities = EnumSet.noneOf(TipoAmenity.class);

    public Estadia(Reserva reserva) {
        this.reserva = reserva;
        int noches = (int) ChronoUnit.DAYS.between(
                reserva.getFechaIngreso(),
                reserva.getFechaEgreso()
        );
        this.componenteEstadia = new EstadiaBase(reserva.getHabitacion(), noches);
    }

    public void realizarCheckIn() {
        if (fechaCheckIn != null) {
            throw new IllegalStateException("La estadia ya tiene un check-in registrado.");
        }
        this.fechaCheckIn = LocalDate.now();
        reserva.getHabitacion().cambiarEstado(EstadoHabitacion.OCUPADA);
        System.out.println("Check-in realizado para reserva #" + reserva.getId());
    }

    public Pago realizarCheckOut(int pagoId, String metodoPago) {
        if (fechaCheckIn == null) {
            throw new IllegalStateException("No se puede hacer check-out sin check-in.");
        }
        if (fechaCheckOut != null) {
            throw new IllegalStateException("La estadia ya tiene un check-out registrado.");
        }
        this.fechaCheckOut = LocalDate.now();
        double costoBaseSinDescuento = reserva.getHabitacion().getPrecioPorNoche()
                * ChronoUnit.DAYS.between(reserva.getFechaIngreso(), reserva.getFechaEgreso());
        double costoServicios = Math.max(0, getCostoTotal() - costoBaseSinDescuento);
        double costoFinal = reserva.calcularCostoTotal() + costoServicios;
        Pago pago = new Pago(pagoId, costoFinal, metodoPago);
        pago.registrarPago();
        reserva.finalizar();
        reserva.getHabitacion().cambiarEstado(EstadoHabitacion.DISPONIBLE);
        System.out.println("Check-out realizado. " + pago.generarComprobante());
        return pago;
    }

    public void agregarServicio(ComponenteEstadia servicioDecorado) {
        this.componenteEstadia = servicioDecorado;
    }

    public boolean agregarAmenity(TipoAmenity amenity) {
        if (!amenities.add(amenity)) {
            return false;
        }
        reconstruirComponenteEstadia();
        return true;
    }

    public boolean quitarAmenity(TipoAmenity amenity) {
        if (!amenities.remove(amenity)) {
            return false;
        }
        reconstruirComponenteEstadia();
        return true;
    }

    public Set<TipoAmenity> getAmenities() {
        return Collections.unmodifiableSet(amenities);
    }

    private void reconstruirComponenteEstadia() {
        int noches = (int) ChronoUnit.DAYS.between(
                reserva.getFechaIngreso(),
                reserva.getFechaEgreso()
        );
        ComponenteEstadia componente = new EstadiaBase(reserva.getHabitacion(), noches);
        if (amenities.contains(TipoAmenity.DESAYUNO)) componente = new DesayunoDecorator(componente);
        if (amenities.contains(TipoAmenity.SPA)) componente = new SpaDecorator(componente);
        if (amenities.contains(TipoAmenity.ESTACIONAMIENTO)) componente = new EstacionamientoDecorator(componente);
        this.componenteEstadia = componente;
    }

    public double getCostoTotal() {
        return componenteEstadia.getCostoTotal();
    }

    public String getDescripcionServicios() {
        return componenteEstadia.getDescripcion();
    }

    public LocalDate getFechaCheckIn() { return fechaCheckIn; }
    public LocalDate getFechaCheckOut() { return fechaCheckOut; }
    public Reserva getReserva() { return reserva; }
}
