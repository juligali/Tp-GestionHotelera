package modelo.promocion;
import java.time.LocalDate;

public class Promocion {
    private String nombre;
    private double porcentaje;
    private LocalDate vigenciaDesde;
    private LocalDate vigenciaHasta;

    public Promocion(String nombre, double porcentaje, LocalDate vigenciaDesde, LocalDate vigenciaHasta) {
        this.nombre = nombre;
        this.porcentaje = porcentaje;
        this.vigenciaDesde = vigenciaDesde;
        this.vigenciaHasta = vigenciaHasta;
    }

    public boolean estaVigente() {
        LocalDate hoy = LocalDate.now();
        return !hoy.isBefore(vigenciaDesde) && !hoy.isAfter(vigenciaHasta);
    }

    public String getNombre() { return nombre; }
    public double getPorcentaje() { return porcentaje; }
    public LocalDate getVigenciaDesde() { return vigenciaDesde; }
    public LocalDate getVigenciaHasta() { return vigenciaHasta; }
}
