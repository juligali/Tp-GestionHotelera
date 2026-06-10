//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import enums.TipoHabitacion;
import modelo.habitacion.Habitacion;
import modelo.pago.Pago;
import modelo.promocion.Promocion;
import modelo.reserva.Reserva;
import modelo.estadia.Estadia;
import modelo.usuario.Administrador;
import modelo.usuario.Huesped;
import modelo.usuario.Recepcionista;
import patrones.comportamiento.observer.NotificacionEmailObserver;
import patrones.comportamiento.observer.NotificacionSMSObserver;
import patrones.comportamiento.strategy.DescuentoTemporada;
import patrones.creacionales.factory.HabitacionDobleFactory;
import patrones.creacionales.factory.HabitacionSimpleFactory;
import patrones.creacionales.factory.HabitacionSuiteFactory;
import patrones.estructurales.decorator.ComponenteEstadia;
import patrones.estructurales.decorator.DesayunoDecorator;
import patrones.estructurales.decorator.EstadiaBase;
import patrones.estructurales.decorator.SpaDecorator;
import servicios.EstadiaGestor;
import servicios.HabitacionGestor;
import servicios.ReservaGestor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Gestores
        HabitacionGestor habitacionGestor = new HabitacionGestor();
        ReservaGestor reservaGestor = new ReservaGestor();
        EstadiaGestor estadiaGestor = new EstadiaGestor();

        // Usuarios internos
        Administrador admin = new Administrador(1, "Carlos López", "carlos@hotel.com", "admin123");
        Recepcionista recepcionista = new Recepcionista(2, "María Pérez", "maria@hotel.com", "recep123");

        // Crear habitaciones con Factory Method — solo el Administrador puede
        habitacionGestor.crearHabitacion(admin, 101, new HabitacionSimpleFactory());
        habitacionGestor.crearHabitacion(admin, 102, new HabitacionDobleFactory());
        habitacionGestor.crearHabitacion(admin, 201, new HabitacionSuiteFactory());

        // Consultar disponibilidad
        List<Habitacion> disponibles = habitacionGestor.consultarDisponibilidad(
                LocalDate.of(2025, 7, 10),
                LocalDate.of(2025, 7, 15),
                TipoHabitacion.DOBLE
        );
        System.out.println("Habitaciones dobles disponibles: " + disponibles.size());

        // Crear huésped
        Huesped huesped = new Huesped(1, "Ana García", "ana@email.com", "1122334455", "1234");
        huesped.registrar();

        // Crear promoción
        Promocion promocion = new Promocion("Verano", 15.0,
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 8, 31)
        );

        // Crear reserva con Builder + Strategy — el Recepcionista crea la reserva
        Reserva reserva = reservaGestor.crearReserva(
                recepcionista,
                huesped,
                disponibles.get(0),
                LocalDate.of(2025, 7, 10),
                LocalDate.of(2025, 7, 15),
                new DescuentoTemporada(),
                promocion
        );

        // Registrar observadores
        reserva.agregarObservador(new NotificacionEmailObserver(huesped.getEmail()));
        reserva.agregarObservador(new NotificacionSMSObserver(huesped.getTelefono()));

        // Confirmar reserva — dispara Observer
        reservaGestor.confirmarReserva(recepcionista, reserva.getId());

        // Costo estimado
        System.out.println("Costo estimado: $" + reserva.calcularCostoTotal());

        // Check-in
        Estadia estadia = estadiaGestor.realizarCheckIn(recepcionista, reserva);

        // Agregar servicios con Decorator
        int noches = (int) ChronoUnit.DAYS.between(
                reserva.getFechaIngreso(), reserva.getFechaEgreso());
        ComponenteEstadia base = new EstadiaBase(reserva.getHabitacion(), noches);
        ComponenteEstadia conServicios = new DesayunoDecorator(new SpaDecorator(base));
        estadiaGestor.agregarServicio(recepcionista, estadia, conServicios);

        // Check-out — genera pago y comprobante
        Pago pago = estadiaGestor.realizarCheckOut(recepcionista, estadia, 1, "tarjeta");
        System.out.println("Costo final con servicios: $" + pago.getMonto());
    }
}
