package patrones.estructurales.decorator;

import modelo.habitacion.Habitacion;

public class EstadiaBase implements ComponenteEstadia {

    private Habitacion habitacion;
    private int noches;

    public EstadiaBase(Habitacion habitacion, int noches) {
        this.habitacion = habitacion;
        this.noches = noches;
    }

    @Override
    public double getCostoTotal() {
        return habitacion.getPrecioPorNoche() * noches;
    }

    @Override
    public String getDescripcion() {
        return noches + " noche(s) en " + habitacion.getDescripcion();
    }
}
