package patrones.estructurales.decorator;

public abstract class ServicioDecorator implements ComponenteEstadia {

    protected ComponenteEstadia componente;

    public ServicioDecorator(ComponenteEstadia componente) {
        this.componente = componente;
    }

    @Override
    public double getCostoTotal() {
        return componente.getCostoTotal();
    }

    @Override
    public String getDescripcion() {
        return componente.getDescripcion();
    }
}

