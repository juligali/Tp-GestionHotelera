package patrones.creacionales.factory;

import modelo.habitacion.Habitacion;
import modelo.habitacion.HabitacionSuite;

public class HabitacionSuiteFactory extends HabitacionFactory {
    @Override
    public Habitacion crearHabitacion(int numero) {
        return new HabitacionSuite(numero);
    }
}
