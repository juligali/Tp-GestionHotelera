package patrones.creacionales.factory;

import modelo.habitacion.Habitacion;

public abstract class HabitacionFactory {

    public abstract Habitacion crearHabitacion(int numero);
}
