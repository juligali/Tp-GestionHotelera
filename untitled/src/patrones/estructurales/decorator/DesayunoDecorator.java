package patrones.estructurales.decorator;

public class DesayunoDecorator extends ServicioDecorator {

    private static final double COSTO_DESAYUNO = 15.0;

    public DesayunoDecorator(ComponenteEstadia componente) {
        super(componente);
    }

    @Override
    public double getCostoTotal() {
        return componente.getCostoTotal() + COSTO_DESAYUNO;
    }

    @Override
    public String getDescripcion() {
        return componente.getDescripcion() + " + Desayuno";
    }
}


