package patrones.comportamiento.strategy;
import modelo.promocion.Promocion;

public interface EstrategiaDescuento {
    double calcularDescuento(double monto, Promocion promocion);

}
