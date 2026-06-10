package modelo.habitacion;

import enums.TipoHabitacion;


public class HabitacionDoble extends Habitacion {
    public HabitacionDoble(int numero) {
        super(numero, 2, 80.0, TipoHabitacion.DOBLE);
    }

    @Override
    public double getPrecioPorNoche() {
        return 80.0;
    }

    @Override
    public String getDescripcion() {
        return "Habitación doble para dos personas";
    }
}
