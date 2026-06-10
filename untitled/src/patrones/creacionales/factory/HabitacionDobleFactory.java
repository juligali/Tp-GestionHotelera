package patrones.creacionales.factory;
import modelo.habitacion.Habitacion;
import modelo.habitacion.HabitacionDoble;

public class HabitacionDobleFactory extends HabitacionFactory {
    @Override
    public Habitacion crearHabitacion(int numero) {
        return new HabitacionDoble(numero);
    }
}
