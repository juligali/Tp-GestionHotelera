package patrones.comportamiento.strategy;
import modelo.promocion.Promocion;

public class DescuentoClienteFrecuente implements EstrategiaDescuento {
    private static final double DESCUENTO_FIJO = 10.0;

    @Override
    public double calcularDescuento(double monto, Promocion promocion) {
        double montoConDescuento = monto - (monto * DESCUENTO_FIJO / 100);
        if (promocion != null && promocion.estaVigente()) {
            montoConDescuento -= (montoConDescuento * promocion.getPorcentaje() / 100);
        }
        return montoConDescuento;
    }


}

