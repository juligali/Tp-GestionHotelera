package modelo.estadia;

public enum TipoAmenity {
    DESAYUNO("Desayuno", 15.0),
    SPA("Spa", 40.0),
    ESTACIONAMIENTO("Estacionamiento", 10.0);

    private final String nombre;
    private final double precio;

    TipoAmenity(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }
}
