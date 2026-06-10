package modelo.reserva;


import enums.EstadoHabitacion;
import modelo.habitacion.Habitacion;
import modelo.promocion.Promocion;
import modelo.usuario.Huesped;
import patrones.comportamiento.observer.ObservadorReserva;
import patrones.comportamiento.state.reserva.EstadoReserva;
import patrones.comportamiento.state.reserva.EstadoPendiente;
import patrones.comportamiento.strategy.EstrategiaDescuento;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Reserva {

    private int id;
    private Huesped huesped;
    private Habitacion habitacion;
    private LocalDate fechaIngreso;
    private LocalDate fechaEgreso;
    private EstadoReserva estado;
    private EstrategiaDescuento estrategia;
    private Promocion promocion;
    private List<ObservadorReserva> observadores;

    public Reserva(int id, Huesped huesped, Habitacion habitacion,
                   LocalDate fechaIngreso, LocalDate fechaEgreso,
                   EstrategiaDescuento estrategia, Promocion promocion) {
        this.id = id;
        this.huesped = huesped;
        this.habitacion = habitacion;
        this.fechaIngreso = fechaIngreso;
        this.fechaEgreso = fechaEgreso;
        this.estrategia = estrategia;
        this.promocion = promocion;
        this.estado = new EstadoPendiente();
        this.observadores = new ArrayList<>();
        habitacion.cambiarEstado(EstadoHabitacion.RESERVADA);
    }

    public void confirmar() {
        estado.confirmar(this);
        notificarObservadores();
    }

    public void cancelar() {
        estado.cancelar(this);
        habitacion.cambiarEstado(EstadoHabitacion.DISPONIBLE);
        notificarObservadores();
    }

    public void finalizar() {
        estado.finalizar(this);
        notificarObservadores();
    }

    public double calcularCostoTotal() {
        long noches = ChronoUnit.DAYS.between(fechaIngreso, fechaEgreso);
        double costoBase = noches * habitacion.getPrecioPorNoche();
        return estrategia.calcularDescuento(costoBase, promocion);
    }

    public void agregarObservador(ObservadorReserva observador) {
        observadores.add(observador);
    }

    public void eliminarObservador(ObservadorReserva observador) {
        observadores.remove(observador);
    }

    public void notificarObservadores() {
        for (ObservadorReserva observador : observadores) {
            observador.actualizar(this);
        }
    }

    public void setEstado(EstadoReserva nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public String getEstadoNombre() {
        return estado.getNombre();
    }

    public int getId() { return id; }
    public Huesped getHuesped() { return huesped; }
    public Habitacion getHabitacion() { return habitacion; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public LocalDate getFechaEgreso() { return fechaEgreso; }
    public EstadoReserva getEstado() { return estado; }
    public Promocion getPromocion() { return promocion; }
}
