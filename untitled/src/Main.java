import enums.EstadoHabitacion;
import enums.TipoHabitacion;
import modelo.estadia.Estadia;
import modelo.habitacion.Habitacion;
import modelo.pago.Pago;
import modelo.reserva.Reserva;
import modelo.usuario.Administrador;
import modelo.usuario.Huesped;
import modelo.usuario.PersonalAdministrativo;
import modelo.usuario.Recepcionista;
import modelo.usuario.UsuarioInterno;
import patrones.comportamiento.observer.NotificacionEmailObserver;
import patrones.comportamiento.observer.NotificacionSMSObserver;
import patrones.comportamiento.strategy.DescuentoClienteFrecuente;
import patrones.comportamiento.strategy.DescuentoTemporada;
import patrones.comportamiento.strategy.EstrategiaDescuento;
import patrones.comportamiento.strategy.SinDescuento;
import patrones.creacionales.factory.HabitacionDobleFactory;
import patrones.creacionales.factory.HabitacionFactory;
import patrones.creacionales.factory.HabitacionSimpleFactory;
import patrones.creacionales.factory.HabitacionSuiteFactory;
import patrones.estructurales.decorator.ComponenteEstadia;
import patrones.estructurales.decorator.DesayunoDecorator;
import patrones.estructurales.decorator.EstacionamientoDecorator;
import patrones.estructurales.decorator.EstadiaBase;
import patrones.estructurales.decorator.SpaDecorator;
import servicios.EstadiaGestor;
import servicios.HabitacionGestor;
import servicios.ReservaGestor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    private static final HabitacionGestor habitacionGestor = new HabitacionGestor();
    private static final ReservaGestor reservaGestor = new ReservaGestor();
    private static final EstadiaGestor estadiaGestor = new EstadiaGestor();

    private static final Administrador admin = new Administrador(1, "Administrador", "admin@hotel.com", "admin123");
    private static final Recepcionista recepcionista = new Recepcionista(2, "Recepcionista", "recepcion@hotel.com", "recep123");
    private static final PersonalAdministrativo administrativo = new PersonalAdministrativo(3, "Administrativo", "administrativo@hotel.com", "administ123");
    private static final List<Huesped> huespedes = new ArrayList<>();

    public static void main(String[] args) {
        cargarDatosIniciales();

        int opcion;
        do {
            mostrarMenuPrincipal();
            opcion = leerEntero("Opcion: ");
            try {
                switch (opcion) {
                    case 1 -> loginInterno();
                    case 2 -> portalCliente();
                    case 3 -> listarHabitaciones();
                    case 0 -> System.out.println("Saliendo...");
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        } while (opcion != 0);
    }

    private static void mostrarMenuPrincipal() {
        System.out.println();
        System.out.println("=== Bragado - Sistema de Gestion Hotelera ===");
        System.out.println("1. Acceso interno");
        System.out.println("2. Portal cliente");
        System.out.println("3. Ver habitaciones");
        System.out.println("0. Salir");
    }

    private static void loginInterno() {
        System.out.println();
        System.out.println("Usuarios: admin/admin123 - recepcion/recep123 - administrativo/administ123");
        String usuario = leerTexto("Usuario: ");
        String clave = leerTexto("Clave: ");
        UsuarioInterno interno = buscarUsuarioInterno(usuario, clave);
        if (interno == null) {
            System.out.println("Usuario o clave incorrectos.");
            return;
        }
        menuInterno(interno);
    }

    private static void menuInterno(UsuarioInterno usuario) {
        int opcion;
        do {
            System.out.println();
            System.out.println("=== Menu interno - " + usuario.getNombre() + " (" + usuario.getRol() + ") ===");
            if (usuario == admin) {
                System.out.println("1. Crear habitacion");
                System.out.println("2. Cambiar estado de habitacion");
                System.out.println("3. Listar huespedes e historial");
            }
            if (usuario == admin || usuario == recepcionista) {
                System.out.println("4. Crear reserva");
                System.out.println("5. Confirmar reserva y asignar descuento");
            }
            System.out.println("6. Cancelar reserva");
            if (usuario == admin || usuario == recepcionista) {
                System.out.println("7. Realizar check-in");
                System.out.println("8. Agregar servicios a estadia");
                System.out.println("9. Realizar check-out");
            }
            System.out.println("10. Listar habitaciones");
            System.out.println("11. Listar reservas");
            System.out.println("12. Listar estadias");
            System.out.println("13. Reportes");
            System.out.println("14. Listar pagos");
            System.out.println("0. Volver");

            opcion = leerEntero("Opcion: ");
            try {
                switch (opcion) {
                    case 1 -> ejecutarSiAdmin(usuario, Main::crearHabitacion);
                    case 2 -> ejecutarSiAdmin(usuario, () -> cambiarEstadoHabitacion(usuario));
                    case 3 -> ejecutarSiAdmin(usuario, Main::listarHuespedes);
                    case 4 -> ejecutarSiRecepcion(usuario, () -> crearReservaInterna(usuario));
                    case 5 -> ejecutarSiRecepcion(usuario, () -> confirmarReserva(usuario));
                    case 6 -> cancelarReserva(usuario);
                    case 7 -> ejecutarSiRecepcion(usuario, () -> realizarCheckIn(usuario));
                    case 8 -> ejecutarSiRecepcion(usuario, () -> agregarServicios(usuario));
                    case 9 -> ejecutarSiRecepcion(usuario, () -> realizarCheckOut(usuario));
                    case 10 -> listarHabitaciones();
                    case 11 -> listarReservas();
                    case 12 -> listarEstadias();
                    case 13 -> mostrarReportes();
                    case 14 -> listarPagos();
                    case 0 -> System.out.println("Volviendo...");
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        } while (opcion != 0);
    }

    private static void portalCliente() {
        String email = leerTexto("Email del cliente: ");
        if (email.isBlank() || !email.contains("@")) {
            System.out.println("Ingresa un email valido.");
            return;
        }

        int opcion;
        do {
            System.out.println();
            System.out.println("=== Portal cliente - " + email + " ===");
            System.out.println("1. Disponibilidad");
            System.out.println("2. Crear Reserva");
            System.out.println("3. Mis Reservas");
            System.out.println("4. Mi Perfil");
            System.out.println("0. Volver");
            opcion = leerEntero("Opcion: ");
            try {
                switch (opcion) {
                    case 1 -> mostrarDisponibilidadCliente();
                    case 2 -> crearReservaCliente(email);
                    case 3 -> listarReservasCliente(email);
                    case 4 -> mostrarPerfilCliente(email);
                    case 0 -> System.out.println("Volviendo...");
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        } while (opcion != 0);
    }

    private static void cargarDatosIniciales() {
        habitacionGestor.crearHabitacion(admin, 101, new HabitacionSimpleFactory());
        habitacionGestor.crearHabitacion(admin, 102, new HabitacionDobleFactory());
        habitacionGestor.crearHabitacion(admin, 201, new HabitacionSuiteFactory());
    }

    private static void crearHabitacion() {
        int numero = leerEntero("Numero de habitacion: ");
        Habitacion habitacion = habitacionGestor.crearHabitacion(admin, numero, elegirFactory());
        System.out.println("Habitacion creada: #" + habitacion.getNumero() + " - " + habitacion.getTipo());
    }

    private static void cambiarEstadoHabitacion(UsuarioInterno usuario) {
        listarHabitaciones();
        int numero = leerEntero("Numero de habitacion: ");
        EstadoHabitacion estado = elegirEstadoHabitacion();
        habitacionGestor.cambiarEstado(usuario, numero, estado);
    }

    private static void mostrarDisponibilidadCliente() {
        List<Habitacion> disponibles = new ArrayList<>();
        for (Habitacion habitacion : habitacionGestor.getHabitaciones()) {
            if (habitacion.estaDisponible()) {
                disponibles.add(habitacion);
            }
        }
        if (disponibles.isEmpty()) {
            System.out.println("No hay habitaciones disponibles.");
            return;
        }
        System.out.println("Habitaciones disponibles:");
        for (Habitacion habitacion : disponibles) {
            imprimirHabitacionDisponibleCliente(habitacion);
        }
    }

    private static void crearReservaInterna(UsuarioInterno usuario) {
        crearReserva(usuario, null);
    }

    private static void crearReservaCliente(String emailCliente) {
        LocalDate ingreso = leerFecha("Fecha de ingreso (AAAA-MM-DD): ");
        LocalDate egreso = leerFecha("Fecha de egreso (AAAA-MM-DD): ");
        validarFechas(ingreso, egreso);
        TipoHabitacion tipo = elegirTipoHabitacion();

        List<Habitacion> disponibles = habitacionGestor.consultarDisponibilidad(ingreso, egreso, tipo);
        if (disponibles.isEmpty()) {
            System.out.println("No hay habitaciones disponibles para esos filtros.");
            return;
        }

        System.out.println("Habitaciones disponibles para reservar:");
        for (Habitacion habitacion : disponibles) {
            imprimirHabitacionDisponibleCliente(habitacion);
        }

        int numeroHabitacion = leerEntero("Numero de habitacion a reservar: ");
        Habitacion seleccionada = disponibles.stream()
                .filter(h -> h.getNumero() == numeroHabitacion)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La habitacion elegida no esta en la lista filtrada."));

        Huesped huesped = buscarOCrearHuespedCliente(emailCliente);
        Reserva reserva = reservaGestor.crearReserva(
                recepcionista,
                huesped,
                seleccionada,
                ingreso,
                egreso
        );
        reserva.agregarObservador(new NotificacionEmailObserver(huesped.getEmail()));
        reserva.agregarObservador(new NotificacionSMSObserver(huesped.getTelefono()));
        System.out.println("Reserva creada con exito. Numero de reserva: " + reserva.getId());
    }

    private static void mostrarPerfilCliente(String email) {
        long reservasCliente = reservaGestor.getReservas().stream()
                .filter(r -> r.getHuesped().getEmail().equalsIgnoreCase(email))
                .count();
        System.out.println("Email: " + email);
        System.out.println("Reservas registradas: " + reservasCliente);
    }

    private static void crearReserva(UsuarioInterno creador, String emailForzado) {
        String nombre = leerTexto("Nombre del huesped: ");
        String email = emailForzado == null ? leerTexto("Email del huesped: ") : emailForzado;
        String telefono = leerTexto("Telefono del huesped: ");
        TipoHabitacion tipo = elegirTipoHabitacion();
        LocalDate ingreso = leerFecha("Fecha de ingreso (AAAA-MM-DD): ");
        LocalDate egreso = leerFecha("Fecha de egreso (AAAA-MM-DD): ");
        validarFechas(ingreso, egreso);

        Habitacion habitacion = habitacionGestor.consultarDisponibilidad(ingreso, egreso, tipo)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay habitaciones disponibles para ese tipo."));

        Huesped huesped = buscarOCrearHuesped(nombre, email, telefono);
        Reserva reserva = reservaGestor.crearReserva(creador, huesped, habitacion, ingreso, egreso);
        reserva.agregarObservador(new NotificacionEmailObserver(huesped.getEmail()));
        reserva.agregarObservador(new NotificacionSMSObserver(huesped.getTelefono()));
        System.out.println("Reserva creada: #" + reserva.getId() + " - Costo estimado: $" + reserva.calcularCostoTotal());
    }

    private static void confirmarReserva(UsuarioInterno usuario) {
        listarReservas();
        int id = leerEntero("ID de reserva a confirmar: ");
        EstrategiaDescuento estrategia = elegirEstrategiaDescuento();
        reservaGestor.confirmarReserva(usuario, id, estrategia);
    }

    private static void cancelarReserva(UsuarioInterno usuario) {
        listarReservas();
        int id = leerEntero("ID de reserva a cancelar: ");
        reservaGestor.cancelarReserva(usuario, id);
    }

    private static void modificarReservaCliente(String email) {
        listarReservasCliente(email);
        int id = leerEntero("ID de reserva a modificar: ");
        Reserva reserva = buscarReserva(id);
        if (reserva == null) {
            System.out.println("No existe la reserva.");
            return;
        }
        Huesped huesped = buscarOCrearHuesped(reserva.getHuesped().getNombre(), email, reserva.getHuesped().getTelefono());
        TipoHabitacion tipo = elegirTipoHabitacion();
        LocalDate ingreso = leerFecha("Nueva fecha de ingreso (AAAA-MM-DD): ");
        LocalDate egreso = leerFecha("Nueva fecha de egreso (AAAA-MM-DD): ");
        validarFechas(ingreso, egreso);
        Habitacion nuevaHabitacion = reserva.getHabitacion().getTipo() == tipo
                ? reserva.getHabitacion()
                : habitacionGestor.consultarDisponibilidad(ingreso, egreso, tipo)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay habitaciones disponibles para ese tipo."));
        reservaGestor.modificarReservaCliente(huesped, id, nuevaHabitacion, ingreso, egreso);
        System.out.println("Reserva modificada correctamente.");
    }

    private static void cancelarReservaCliente(String email) {
        listarReservasCliente(email);
        int id = leerEntero("ID de reserva a cancelar: ");
        Reserva reserva = buscarReserva(id);
        if (reserva == null) {
            System.out.println("No existe la reserva.");
            return;
        }
        Huesped huesped = buscarOCrearHuesped(reserva.getHuesped().getNombre(), email, reserva.getHuesped().getTelefono());
        reservaGestor.cancelarReservaCliente(huesped, id);
        System.out.println("Reserva cancelada correctamente.");
    }

    private static void realizarCheckIn(UsuarioInterno usuario) {
        listarReservas();
        int id = leerEntero("ID de reserva confirmada: ");
        Reserva reserva = buscarReserva(id);
        if (reserva == null) {
            System.out.println("No existe la reserva.");
            return;
        }
        Estadia estadia = estadiaGestor.realizarCheckIn(usuario, reserva);
        System.out.println("Estadia creada para reserva #" + estadia.getReserva().getId());
    }

    private static void agregarServicios(UsuarioInterno usuario) {
        listarEstadias();
        Estadia estadia = buscarEstadia(leerEntero("ID de reserva asociada a la estadia: "));
        if (estadia == null) {
            System.out.println("No existe la estadia.");
            return;
        }
        int noches = (int) ChronoUnit.DAYS.between(estadia.getReserva().getFechaIngreso(), estadia.getReserva().getFechaEgreso());
        ComponenteEstadia componente = new EstadiaBase(estadia.getReserva().getHabitacion(), noches);
        if (leerSiNo("Agregar desayuno? (s/n): ")) componente = new DesayunoDecorator(componente);
        if (leerSiNo("Agregar spa? (s/n): ")) componente = new SpaDecorator(componente);
        if (leerSiNo("Agregar estacionamiento? (s/n): ")) componente = new EstacionamientoDecorator(componente);
        estadiaGestor.agregarServicio(usuario, estadia, componente);
        System.out.println("Servicios actualizados. Total: $" + estadia.getCostoTotal());
    }

    private static void realizarCheckOut(UsuarioInterno usuario) {
        listarEstadias();
        Estadia estadia = buscarEstadia(leerEntero("ID de reserva asociada a la estadia: "));
        if (estadia == null) {
            System.out.println("No existe la estadia.");
            return;
        }
        String metodo = leerTexto("Metodo de pago: ");
        Pago pago = estadiaGestor.realizarCheckOut(usuario, estadia, estadiaGestor.getPagos().size() + 1, metodo);
        System.out.println(pago.generarComprobante());
    }

    private static void listarHabitaciones() {
        List<Habitacion> habitaciones = habitacionGestor.getHabitaciones();
        if (habitaciones.isEmpty()) {
            System.out.println("No hay habitaciones cargadas.");
            return;
        }
        for (Habitacion habitacion : habitaciones) {
            imprimirHabitacion(habitacion);
        }
    }

    private static void listarReservas() {
        List<Reserva> reservas = reservaGestor.getReservas();
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas cargadas.");
            return;
        }
        for (Reserva reserva : reservas) {
            imprimirReserva(reserva);
        }
    }

    private static void listarReservasCliente(String email) {
        boolean encontro = false;
        for (Reserva reserva : reservaGestor.getReservas()) {
            if (reserva.getHuesped().getEmail().equalsIgnoreCase(email)) {
                imprimirReserva(reserva);
                encontro = true;
            }
        }
        if (!encontro) {
            System.out.println("No hay reservas para " + email);
        }
    }

    private static void listarEstadias() {
        List<Estadia> estadias = estadiaGestor.getEstadias();
        if (estadias.isEmpty()) {
            System.out.println("No hay estadias cargadas.");
            return;
        }
        for (Estadia estadia : estadias) {
            System.out.println("Reserva #" + estadia.getReserva().getId()
                    + " - Huesped: " + estadia.getReserva().getHuesped().getNombre()
                    + " - Check-in: " + estadia.getFechaCheckIn()
                    + " - Check-out: " + estadia.getFechaCheckOut()
                    + " - Servicios: " + estadia.getDescripcionServicios()
                    + " - Total: $" + estadia.getCostoTotal());
        }
    }

    private static void listarHuespedes() {
        if (huespedes.isEmpty()) {
            System.out.println("No hay huespedes registrados.");
            return;
        }
        for (Huesped huesped : huespedes) {
            System.out.println(huesped.getNombre() + " - " + huesped.getEmail() + " - " + huesped.getTelefono());
            if (huesped.getReservas().isEmpty()) {
                System.out.println("  Sin reservas.");
            } else {
                for (Reserva reserva : huesped.getReservas()) {
                    System.out.println("  Reserva #" + reserva.getId() + " - " + reserva.getEstadoNombre());
                }
            }
        }
    }

    private static void mostrarReportes() {
        int total = habitacionGestor.getHabitaciones().size();
        long disponibles = contarHabitaciones(EstadoHabitacion.DISPONIBLE);
        long reservadas = contarHabitaciones(EstadoHabitacion.RESERVADA);
        long ocupadas = contarHabitaciones(EstadoHabitacion.OCUPADA);
        long activas = reservaGestor.getReservas().stream()
                .filter(r -> !"CANCELADA".equals(r.getEstadoNombre()) && !"FINALIZADA".equals(r.getEstadoNombre()))
                .count();
        double ocupacion = total == 0 ? 0 : (ocupadas * 100.0) / total;
        double ingresos = estadiaGestor.getPagos().stream().mapToDouble(Pago::getMonto).sum();

        System.out.println("Habitaciones totales: " + total);
        System.out.println("Disponibles: " + disponibles);
        System.out.println("Reservadas: " + reservadas);
        System.out.println("Ocupadas: " + ocupadas);
        System.out.println("Ocupacion actual: " + String.format(Locale.ROOT, "%.1f", ocupacion) + "%");
        System.out.println("Reservas activas: " + activas);
        System.out.println("Pagos registrados: " + estadiaGestor.getPagos().size());
        System.out.println("Ingresos simulados: $" + ingresos);
    }

    private static void listarPagos() {
        List<Pago> pagos = estadiaGestor.getPagos();
        if (pagos.isEmpty()) {
            System.out.println("No hay pagos registrados.");
            return;
        }
        for (Pago pago : pagos) {
            System.out.println(pago.generarComprobante());
        }
    }

    private static HabitacionFactory elegirFactory() {
        return switch (elegirTipoHabitacion()) {
            case DOBLE -> new HabitacionDobleFactory();
            case SUITE -> new HabitacionSuiteFactory();
            case SIMPLE -> new HabitacionSimpleFactory();
        };
    }

    private static TipoHabitacion elegirTipoHabitacion() {
        System.out.println("Tipo de habitacion:");
        System.out.println("1. Simple");
        System.out.println("2. Doble");
        System.out.println("3. Suite");
        return switch (leerEntero("Tipo: ")) {
            case 2 -> TipoHabitacion.DOBLE;
            case 3 -> TipoHabitacion.SUITE;
            default -> TipoHabitacion.SIMPLE;
        };
    }

    private static EstrategiaDescuento elegirEstrategiaDescuento() {
        System.out.println("Tipo de descuento:");
        System.out.println("1. Sin descuento");
        System.out.println("2. Descuento temporada (15%)");
        System.out.println("3. Cliente frecuente (10%)");
        return switch (leerEntero("Descuento: ")) {
            case 2 -> new DescuentoTemporada();
            case 3 -> new DescuentoClienteFrecuente();
            default -> new SinDescuento();
        };
    }

    private static EstadoHabitacion elegirEstadoHabitacion() {
        EstadoHabitacion[] estados = EstadoHabitacion.values();
        for (int i = 0; i < estados.length; i++) {
            System.out.println((i + 1) + ". " + estados[i]);
        }
        int opcion = leerEntero("Estado: ");
        if (opcion < 1 || opcion > estados.length) {
            return EstadoHabitacion.DISPONIBLE;
        }
        return estados[opcion - 1];
    }

    private static UsuarioInterno buscarUsuarioInterno(String usuario, String clave) {
        String normalizado = usuario.toLowerCase(Locale.ROOT);
        if ((normalizado.equals("admin") || normalizado.equals(admin.getEmail().toLowerCase(Locale.ROOT))) && admin.login(admin.getEmail(), clave)) {
            return admin;
        }
        if ((normalizado.equals("recepcion") || normalizado.equals(recepcionista.getEmail().toLowerCase(Locale.ROOT))) && recepcionista.login(recepcionista.getEmail(), clave)) {
            return recepcionista;
        }
        if ((normalizado.equals("administrativo") || normalizado.equals(administrativo.getEmail().toLowerCase(Locale.ROOT))) && administrativo.login(administrativo.getEmail(), clave)) {
            return administrativo;
        }
        return null;
    }

    private static Huesped buscarOCrearHuesped(String nombre, String email, String telefono) {
        for (Huesped huesped : huespedes) {
            if (huesped.getEmail().equalsIgnoreCase(email)) {
                return huesped;
            }
        }
        Huesped nuevo = new Huesped(huespedes.size() + 10, nombre, email, "1234", telefono);
        huespedes.add(nuevo);
        return nuevo;
    }

    private static Huesped buscarOCrearHuespedCliente(String email) {
        String nombre = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;
        return buscarOCrearHuesped(nombre, email, "Sin telefono");
    }

    private static Reserva buscarReserva(int id) {
        for (Reserva reserva : reservaGestor.getReservas()) {
            if (reserva.getId() == id) {
                return reserva;
            }
        }
        return null;
    }

    private static Estadia buscarEstadia(int reservaId) {
        for (Estadia estadia : estadiaGestor.getEstadias()) {
            if (estadia.getReserva().getId() == reservaId) {
                return estadia;
            }
        }
        return null;
    }

    private static long contarHabitaciones(EstadoHabitacion estado) {
        return habitacionGestor.getHabitaciones().stream()
                .filter(h -> h.getEstado() == estado)
                .count();
    }

    private static void imprimirHabitacion(Habitacion habitacion) {
        System.out.println("#" + habitacion.getNumero()
                + " - " + habitacion.getTipo()
                + " - Capacidad: " + habitacion.getCapacidad()
                + " - " + habitacion.getEstado()
                + " - $" + habitacion.getPrecioPorNoche()
                + " - " + habitacion.getDescripcion());
    }

    private static void imprimirHabitacionDisponibleCliente(Habitacion habitacion) {
        System.out.println("Numero: " + habitacion.getNumero()
                + " | Tipo: " + habitacion.getTipo()
                + " | Descripcion: " + habitacion.getDescripcion()
                + " | Precio por noche: $" + habitacion.getPrecioPorNoche()
                + " | Estado: Disponible");
    }

    private static void imprimirReserva(Reserva reserva) {
        System.out.println("#" + reserva.getId()
                + " - " + reserva.getHuesped().getNombre()
                + " - " + reserva.getHuesped().getEmail()
                + " - Habitacion " + reserva.getHabitacion().getNumero()
                + " - " + reserva.getHabitacion().getTipo()
                + " - " + reserva.getFechaIngreso() + " a " + reserva.getFechaEgreso()
                + " - " + reserva.getEstadoNombre()
                + " - $" + reserva.calcularCostoTotal());
    }

    private static void validarFechas(LocalDate ingreso, LocalDate egreso) {
        if (ingreso == null || egreso == null || !egreso.isAfter(ingreso)) {
            throw new IllegalArgumentException("La fecha de egreso debe ser posterior al ingreso.");
        }
    }

    private static void ejecutarSiAdmin(UsuarioInterno usuario, Runnable accion) {
        if (usuario != admin) {
            System.out.println("Esta opcion es solo para administrador.");
            return;
        }
        accion.run();
    }

    private static void ejecutarSiRecepcion(UsuarioInterno usuario, Runnable accion) {
        if (usuario != admin && usuario != recepcionista) {
            System.out.println("Esta opcion es solo para administrador o recepcionista.");
            return;
        }
        accion.run();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = scanner.nextLine();
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un numero valido.");
            }
        }
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private static LocalDate leerFecha(String mensaje) {
        while (true) {
            String valor = leerTexto(mensaje);
            try {
                return LocalDate.parse(valor);
            } catch (Exception e) {
                System.out.println("Formato invalido. Usa AAAA-MM-DD.");
            }
        }
    }

    private static boolean leerSiNo(String mensaje) {
        String respuesta = leerTexto(mensaje);
        return respuesta.equalsIgnoreCase("s") || respuesta.equalsIgnoreCase("si");
    }
}
