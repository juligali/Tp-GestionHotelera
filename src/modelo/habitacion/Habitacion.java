package modelo.habitacion;

import enums.EstadoHabitacion;
import enums.TipoHabitacion;

public abstract class Habitacion {

    private int numero;
    private int capacidad;
    private double precioPorNoche;
    private TipoHabitacion tipo;
    private EstadoHabitacion estado;

    public Habitacion(int numero, int capacidad, double precioPorNoche, TipoHabitacion tipo) {
        this.numero = numero;
        this.capacidad = capacidad;
        this.precioPorNoche = precioPorNoche;
        this.tipo = tipo;
        this.estado = EstadoHabitacion.DISPONIBLE;
    }

    public abstract double getPrecioPorNoche();
    public abstract String getDescripcion();

    public boolean estaDisponible() {
        return this.estado == EstadoHabitacion.DISPONIBLE;
    }

    public void cambiarEstado(EstadoHabitacion nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public int getNumero() { return numero; }
    public int getCapacidad() { return capacidad; }
    public TipoHabitacion getTipo() { return tipo; }
    public EstadoHabitacion getEstado() { return estado; }
    public void setPrecioPorNoche(double precioPorNoche) { this.precioPorNoche = precioPorNoche; }
}
