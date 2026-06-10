package patrones.comportamiento.strategy;
import modelo.promocion.Promocion;

public class SinDescuento implements EstrategiaDescuento {
    @Override
    public double calcularDescuento(double monto, Promocion promocion) {
        return monto;
    }
}
