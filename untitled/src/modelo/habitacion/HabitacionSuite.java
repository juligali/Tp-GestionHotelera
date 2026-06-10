package modelo.habitacion;

import enums.TipoHabitacion;

public class HabitacionSuite extends Habitacion {
    public HabitacionSuite(int numero) {
        super(numero, 2, 150.0, TipoHabitacion.SUITE);
    }

    @Override
    public double getPrecioPorNoche() {
        return 150.0;
    }

    @Override
    public String getDescripcion() {
        return "Suite de lujo con servicios premium";
    }
}
