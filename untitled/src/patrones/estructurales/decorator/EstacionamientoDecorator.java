package patrones.estructurales.decorator;

public class EstacionamientoDecorator extends ServicioDecorator {

    private static final double COSTO_ESTACIONAMIENTO = 10.0;

    public EstacionamientoDecorator(ComponenteEstadia componente) {
        super(componente);
    }

    @Override
    public double getCostoTotal() {
        return componente.getCostoTotal() + COSTO_ESTACIONAMIENTO;
    }

    @Override
    public String getDescripcion() {
        return componente.getDescripcion() + " + Estacionamiento";
    }
}
