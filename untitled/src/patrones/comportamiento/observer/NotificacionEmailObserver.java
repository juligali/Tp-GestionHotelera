package patrones.comportamiento.observer;
import modelo.reserva.Reserva;

public class NotificacionEmailObserver implements ObservadorReserva {
    private String destinatario;

    public NotificacionEmailObserver(String destinatario) {
        this.destinatario = destinatario;
    }

    @Override
    public void actualizar(Reserva reserva) {
        System.out.println("Email enviado a " + destinatario +
                ": su reserva #" + reserva.getId() +
                " está en estado " + reserva.getEstadoNombre());
    }

}
