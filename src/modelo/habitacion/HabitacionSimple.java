package modelo.habitacion;

import enums.TipoHabitacion;

public class HabitacionSimple extends Habitacion {

    public HabitacionSimple(int numero) {
        super(numero, 1, 50.0, TipoHabitacion.SIMPLE);
    }

    @Override
    public double getPrecioPorNoche() {
        return 50.0;
    }

    @Override
    public String getDescripcion() {
        return "Habitación simple para una persona";
    }
}

