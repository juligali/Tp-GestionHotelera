package patrones.creacionales.builder;

import modelo.habitacion.Habitacion;
import modelo.promocion.Promocion;
import modelo.reserva.Reserva;
import modelo.usuario.Huesped;
import patrones.comportamiento.strategy.EstrategiaDescuento;
import patrones.comportamiento.strategy.SinDescuento;

import java.time.LocalDate;

public class ReservaBuilder {

    private int id;
    private Huesped huesped;
    private Habitacion habitacion;
    private LocalDate fechaIngreso;
    private LocalDate fechaEgreso;
    private EstrategiaDescuento estrategia;
    private Promocion promocion;

    public ReservaBuilder(int id) {
        this.id = id;
        this.estrategia = new SinDescuento();
        this.promocion = null;
    }

    public ReservaBuilder conHuesped(Huesped huesped) {
        this.huesped = huesped;
        return this;
    }

    public ReservaBuilder conHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
        return this;
    }

    public ReservaBuilder conFechas(LocalDate fechaIngreso, LocalDate fechaEgreso) {
        this.fechaIngreso = fechaIngreso;
        this.fechaEgreso = fechaEgreso;
        return this;
    }

    public ReservaBuilder conEstrategia(EstrategiaDescuento estrategia) {
        this.estrategia = estrategia;
        return this;
    }

    public ReservaBuilder conPromocion(Promocion promocion) {
        this.promocion = promocion;
        return this;
    }

    public Reserva build() {
        if (huesped == null) throw new IllegalStateException("La reserva debe tener un huésped.");
        if (habitacion == null) throw new IllegalStateException("La reserva debe tener una habitación.");
        if (fechaIngreso == null || fechaEgreso == null) throw new IllegalStateException("La reserva debe tener fechas.");
        if (!fechaEgreso.isAfter(fechaIngreso)) throw new IllegalStateException("La fecha de egreso debe ser posterior a la de ingreso.");

        return new Reserva(id, huesped, habitacion, fechaIngreso, fechaEgreso, estrategia, promocion);
    }
}
