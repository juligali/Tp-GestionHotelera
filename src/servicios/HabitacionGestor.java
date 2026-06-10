package servicios;

import enums.EstadoHabitacion;
import enums.TipoHabitacion;
import modelo.habitacion.Habitacion;
import patrones.creacionales.factory.HabitacionDobleFactory;
import patrones.creacionales.factory.HabitacionFactory;
import patrones.creacionales.factory.HabitacionSimpleFactory;
import patrones.creacionales.factory.HabitacionSuiteFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HabitacionGestor {

    private List<Habitacion> habitaciones;

    public HabitacionGestor() {
        this.habitaciones = new ArrayList<>();
    }

    public Habitacion crearHabitacion(int numero, TipoHabitacion tipo) {
        HabitacionFactory factory = obtenerFactory(tipo);
        Habitacion habitacion = factory.crearHabitacion(numero);
        habitaciones.add(habitacion);
        return habitacion;
    }

    private HabitacionFactory obtenerFactory(TipoHabitacion tipo) {
        switch (tipo) {
            case SIMPLE: return new HabitacionSimpleFactory();
            case DOBLE: return new HabitacionDobleFactory();
            case SUITE: return new HabitacionSuiteFactory();
            default: throw new IllegalArgumentException("Tipo de habitación no soportado: " + tipo);
        }
    }

    public List<Habitacion> consultarDisponibilidad(LocalDate fechaIngreso, LocalDate fechaEgreso, TipoHabitacion tipo) {
        List<Habitacion> disponibles = new ArrayList<>();
        for (Habitacion h : habitaciones) {
            if (h.getTipo() == tipo && h.estaDisponible()) {
                disponibles.add(h);
            }
        }
        return disponibles;
    }

    public void cambiarEstado(int numero, EstadoHabitacion nuevoEstado) {
        for (Habitacion h : habitaciones) {
            if (h.getNumero() == numero) {
                h.cambiarEstado(nuevoEstado);
                System.out.println("Habitación #" + numero + " → " + nuevoEstado);
                return;
            }
        }
        System.out.println("No se encontró la habitación #" + numero);
    }

    public List<Habitacion> getHabitaciones() {
        return new ArrayList<>(habitaciones);
    }
}
