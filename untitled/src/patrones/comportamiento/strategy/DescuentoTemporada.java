package patrones.comportamiento.strategy;

public class DescuentoTemporada implements EstrategiaDescuento {
    private static final double PORCENTAJE_DESCUENTO = 15.0;

    @Override
    public double calcularDescuento(double monto) {
        return monto - (monto * PORCENTAJE_DESCUENTO / 100);
    }

}

