package patrones.comportamiento.strategy;

public class DescuentoClienteFrecuente implements EstrategiaDescuento {
    private static final double DESCUENTO_FIJO = 10.0;

    @Override
    public double calcularDescuento(double monto) {
        return monto - (monto * DESCUENTO_FIJO / 100);
    }


}


