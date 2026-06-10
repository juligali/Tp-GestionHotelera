package modelo.pago;
import java.time.LocalDate;

public class Pago {
    private int id;
    private double monto;
    private LocalDate fecha;
    private String metodoPago;

    public Pago(int id, double monto, String metodoPago) {
        this.id = id;
        this.monto = monto;
        this.fecha = LocalDate.now();
        this.metodoPago = metodoPago;
    }

    public void registrarPago() {
        // TODO: persistir en base de datos
        System.out.println("Pago registrado: $" + monto + " via " + metodoPago);
    }

    public String generarComprobante() {
        return "=== COMPROBANTE DE PAGO ===" +
                "\nID: " + id +
                "\nMonto: $" + monto +
                "\nFecha: " + fecha +
                "\nMétodo: " + metodoPago +
                "\n==========================";
    }

    public int getId() { return id; }
    public double getMonto() { return monto; }
    public LocalDate getFecha() { return fecha; }
    public String getMetodoPago() { return metodoPago; }

}
