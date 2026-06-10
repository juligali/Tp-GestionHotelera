package patrones.comportamiento.strategy;
import modelo.promocion.Promocion;

public class DescuentoTemporada implements EstrategiaDescuento {
    @Override
    public double calcularDescuento(double monto, Promocion promocion) {
        if (promocion != null && promocion.estaVigente()) {
            return monto - (monto * promocion.getPorcentaje() / 100);
        }
        return monto;
    }

}
