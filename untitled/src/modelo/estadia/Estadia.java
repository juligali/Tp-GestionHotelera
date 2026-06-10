package modelo.estadia;

import modelo.pago.Pago;
import modelo.reserva.Reserva;
import patrones.estructurales.decorator.ComponenteEstadia;
import patrones.estructurales.decorator.EstadiaBase;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Estadia {

    private Reserva reserva;
    private LocalDate fechaCheckIn;
    private LocalDate fechaCheckOut;
    private ComponenteEstadia componenteEstadia;

    public Estadia(Reserva reserva) {
        this.reserva = reserva;
        int noches = (int) ChronoUnit.DAYS.between(
                reserva.getFechaIngreso(),
                reserva.getFechaEgreso()
        );
        this.componenteEstadia = new EstadiaBase(reserva.getHabitacion(), noches);
    }

    public void realizarCheckIn() {
        this.fechaCheckIn = LocalDate.now();
        System.out.println("Check-in realizado para reserva #" + reserva.getId());
    }

    public Pago realizarCheckOut(int pagoId, String metodoPago) {
        this.fechaCheckOut = LocalDate.now();
        double costoFinal = getCostoTotal();
        Pago pago = new Pago(pagoId, costoFinal, metodoPago);
        pago.registrarPago();
        reserva.finalizar();
        System.out.println("Check-out realizado. " + pago.generarComprobante());
        return pago;
    }

    public void agregarServicio(ComponenteEstadia servicioDecorado) {
        this.componenteEstadia = servicioDecorado;
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