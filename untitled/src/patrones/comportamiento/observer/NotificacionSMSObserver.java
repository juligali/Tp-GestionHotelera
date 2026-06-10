package patrones.comportamiento.observer;
import modelo.reserva.Reserva;


public class NotificacionSMSObserver implements ObservadorReserva {
    private String telefono;

    public NotificacionSMSObserver(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public void actualizar(Reserva reserva) {
        System.out.println("SMS enviado a " + telefono +
                ": su reserva #" + reserva.getId() +
                " está en estado " + reserva.getEstadoNombre());
    }


}
