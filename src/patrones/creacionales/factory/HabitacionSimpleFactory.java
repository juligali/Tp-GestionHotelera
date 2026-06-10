package patrones.creacionales.factory;

import modelo.habitacion.Habitacion;
import modelo.habitacion.HabitacionSimple;

public class HabitacionSimpleFactory extends HabitacionFactory {

    @Override
    public Habitacion crearHabitacion(int numero) {
        return new HabitacionSimple(numero);
    }
}

