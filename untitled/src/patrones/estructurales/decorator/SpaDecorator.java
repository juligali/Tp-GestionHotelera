package patrones.estructurales.decorator;

public class SpaDecorator extends ServicioDecorator {

    private static final double COSTO_SPA = 40.0;

    public SpaDecorator(ComponenteEstadia componente) {
        super(componente);
    }

    @Override
    public double getCostoTotal() {
        return componente.getCostoTotal() + COSTO_SPA;
    }

    @Override
    public String getDescripcion() {
        return componente.getDescripcion() + " + Spa";
    }
}
