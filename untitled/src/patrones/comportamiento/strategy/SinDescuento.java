package patrones.comportamiento.strategy;

public class SinDescuento implements EstrategiaDescuento {
    @Override
    public double calcularDescuento(double monto) {
        return monto;
    }
}
