package servicios;

import modelo.reserva.Reserva;
import modelo.usuario.Huesped;
import patrones.creacionales.builder.ReservaBuilder;

import java.util.ArrayList;
import java.util.List;

public class ReservaGestor {

    private List<Reserva> reservas;
    private int contadorId;

    public ReservaGestor() {
        this.reservas = new ArrayList<>();
        this.contadorId = 1;
    }

    public Reserva crearReserva(ReservaBuilder builder) {
        Reserva reserva = builder.build();
        reservas.add(reserva);
        reserva.getHuesped().agregarReserva(reserva);
        System.out.println("Reserva #" + reserva.getId() + " creada correctamente.");
        return reserva;
    }

    public void confirmarReserva(int id) {
        Reserva reserva = buscarPorId(id);
        if (reserva != null) {
            reserva.confirmar();
        } else {
            System.out.println("No se encontró la reserva #" + id);
        }
    }

    public void cancelarReserva(int id) {
        Reserva reserva = buscarPorId(id);
        if (reserva != null) {
            reserva.cancelar();
        } else {
            System.out.println("No se encontró la reserva #" + id);
        }
    }

    public List<Reserva> buscarReservas(String nombreHuesped) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getHuesped().getNombre().equalsIgnoreCase(nombreHuesped)) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    public int generarId() {
        return contadorId++;
    }

    private Reserva buscarPorId(int id) {
        for (Reserva r : reservas) {
            if (r.getId() == id) return r;
        }
        return null;
    }

    public List<Reserva> getReservas() {
        return new ArrayList<>(reservas);
    }
}
