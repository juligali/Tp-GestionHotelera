package gui;

import enums.EstadoHabitacion;
import enums.TipoHabitacion;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modelo.usuario.PersonalAdministrativo;
import modelo.estadia.Estadia;
import modelo.estadia.TipoAmenity;
import modelo.habitacion.Habitacion;
import modelo.pago.Pago;
import modelo.reserva.Reserva;
import modelo.usuario.Administrador;
import modelo.usuario.Huesped;
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
import servicios.EstadiaGestor;
import servicios.HabitacionGestor;
import servicios.ReservaGestor;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HotelApp extends Application {

    private final HabitacionGestor habitacionGestor = new HabitacionGestor();
    private final ReservaGestor reservaGestor = new ReservaGestor();
    private final EstadiaGestor estadiaGestor = new EstadiaGestor();

    private final Administrador admin = new Administrador(1, "Administrador", "admin@hotel.com", "admin123");
    private final Recepcionista recepcionista = new Recepcionista(2, "Recepcionista", "recepcion@hotel.com", "recep123");
    private final PersonalAdministrativo administrativo = new PersonalAdministrativo(3, "Administrativo", "administrativo@hotel.com", "administ123");
    private UsuarioInterno usuarioInternoActual = admin;

    private final ObservableList<Habitacion> habitaciones = FXCollections.observableArrayList();
    private final ObservableList<Habitacion> habitacionesDisponiblesInterno = FXCollections.observableArrayList();
    private final ObservableList<Habitacion> habitacionesCliente = FXCollections.observableArrayList();
    private final ObservableList<Reserva> reservas = FXCollections.observableArrayList();
    private final ObservableList<Reserva> reservasCliente = FXCollections.observableArrayList();
    private final ObservableList<Estadia> estadias = FXCollections.observableArrayList();
    private final ObservableList<Huesped> huespedes = FXCollections.observableArrayList();
    private final ObservableList<Pago> pagos = FXCollections.observableArrayList();
    private final Map<Integer, Pago> pagosPorReserva = new HashMap<>();
    private final Map<Integer, Double> totalesFinalesPorReserva = new HashMap<>();

    private BorderPane root;
    private Label modoActual;
    private TableView<Habitacion> tablaHabitacionesAdmin;
    private TableView<Habitacion> tablaHabitacionesCliente;
    private TableView<Reserva> tablaReservasAdmin;
    private TableView<Reserva> tablaReservasCliente;
    private TableView<Estadia> tablaEstadias;
    private TableView<Huesped> tablaHuespedes;
    private TableView<Pago> tablaPagos;
    private ComboBox<Integer> reservaCheckInCombo;
    private TextArea consola;
    private String emailClienteActual = "";
    private TextField emailClienteFiltro;
    private ComboBox<TipoHabitacion> tipoClienteFiltro;
    private TextField capacidadClienteFiltro;
    private DatePicker ingresoClienteFiltro;
    private DatePicker egresoClienteFiltro;

    @Override
    public void start(Stage stage) {
        cargarDatosIniciales();

        root = new BorderPane();
        root.getStyleClass().add("app-root");
        modoActual = new Label("Elegir modo de uso");
        modoActual.getStyleClass().add("mode-label");
        root.setCenter(crearLogin());

        Scene scene = new Scene(root, 1240, 780);
        aplicarTema(scene);
        stage.setTitle("Bragado Hotel Management");
        stage.setScene(scene);
        stage.setMinWidth(1080);
        stage.setMinHeight(680);
        stage.show();
        log("Sistema Bragado iniciado.");
    }

    private HBox crearEncabezado() {
        Label titulo = new Label("Bragado");
        titulo.getStyleClass().add("app-title");
        modoActual = new Label("Elegir modo de uso");
        modoActual.getStyleClass().add("mode-label");

        Button adminButton = new Button("Acceso interno");
        adminButton.getStyleClass().addAll("button-primary", "nav-button");
        adminButton.setOnAction(e -> root.setCenter(crearLogin()));
        Button clienteButton = new Button("Cliente");
        clienteButton.getStyleClass().addAll("button-secondary", "nav-button");
        clienteButton.setOnAction(e -> mostrarModoCliente());

        HBox acciones = new HBox(10, adminButton, clienteButton);
        acciones.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(acciones, Priority.ALWAYS);

        VBox textos = new VBox(4, titulo, modoActual);
        HBox header = new HBox(18, textos, acciones);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("app-header");
        return header;
    }

    private VBox crearLogin() {
        Label titulo = new Label("Acceso al sistema");
        titulo.getStyleClass().add("hero-title");
        Label ayuda = new Label("Bragado centraliza reservas, huespedes, habitaciones y estadias en una experiencia simple para equipos de hoteleria.");
        ayuda.getStyleClass().add("hero-subtitle");
        ayuda.setWrapText(true);

        Label credenciales = new Label("Accesos internos de prueba: admin/admin123 - recepcion/recep123 - administrativo/administ123");
        credenciales.getStyleClass().add("credentials-hint");
        credenciales.setWrapText(true);

        TextField usuarioAdmin = new TextField();
        usuarioAdmin.setPromptText("admin, recepcion o administrativo");
        PasswordField claveAdmin = new PasswordField();
        claveAdmin.setPromptText("Clave");
        Button adminButton = new Button("Entrar como usuario interno");
        adminButton.setMaxWidth(Double.MAX_VALUE);
        adminButton.getStyleClass().add("button-primary");
        adminButton.setOnAction(e -> validarAdministrador(usuarioAdmin, claveAdmin));

        TextField emailCliente = new TextField();
        emailCliente.setPromptText("Email del cliente");
        Button clienteButton = new Button("Entrar como cliente");
        clienteButton.setMaxWidth(Double.MAX_VALUE);
        clienteButton.getStyleClass().add("button-secondary");
        clienteButton.setOnAction(e -> validarCliente(emailCliente));

        GridPane adminForm = crearGrid();
        adminForm.add(new Label("Usuario interno"), 0, 0, 2, 1);
        adminForm.add(usuarioAdmin, 0, 1);
        adminForm.add(claveAdmin, 1, 1);
        adminForm.add(adminButton, 0, 2, 2, 1);
        adminForm.getStyleClass().add("login-card");

        GridPane clienteForm = crearGrid();
        clienteForm.add(new Label("Cliente"), 0, 0);
        clienteForm.add(emailCliente, 0, 1);
        clienteForm.add(clienteButton, 0, 2);
        clienteForm.getStyleClass().add("login-card");

        HBox botones = new HBox(14, adminForm, clienteForm);
        botones.setAlignment(Pos.CENTER);
        HBox.setHgrow(adminForm, Priority.ALWAYS);
        HBox.setHgrow(clienteForm, Priority.ALWAYS);

        VBox selector = new VBox(18, titulo, ayuda, credenciales, botones);
        selector.setAlignment(Pos.CENTER);
        selector.getStyleClass().add("login-view");
        return selector;
    }

    private void validarAdministrador(TextField usuario, PasswordField clave) {
        UsuarioInterno usuarioValidado = buscarUsuarioInterno(valor(usuario, ""), valor(clave, ""));
        if (usuarioValidado == null) {
            mostrarAlerta("Usuario o clave incorrectos. Usa admin/admin123, recepcion/recep123 o administrativo/administ123.");
            return;
        }
        usuarioInternoActual = usuarioValidado;
        mostrarModoAdministrador();
    }

    private void validarCliente(TextField email) {
        String valorEmail = valor(email, "");
        if (valorEmail.isEmpty() || !valorEmail.contains("@")) {
            mostrarAlerta("Ingresa un email valido para entrar como cliente.");
            return;
        }
        emailClienteActual = valorEmail;
        mostrarModoCliente();
    }

    private void mostrarModoAdministrador() {
        modoActual.setText("Modo interno - " + usuarioInternoActual.getRol());
        refrescarTablas();
        root.setTop(null);
        mostrarModuloInterno("Dashboard", crearDashboardInterno());
        log("Vista interna activa para " + usuarioInternoActual.getNombre() + ".");
    }

    private void mostrarModoCliente() {
        modoActual.setText("Modo cliente");
        root.setTop(null);
        root.setCenter(crearShellCliente("Disponibilidad", crearVistaDisponibilidadCliente()));
        refrescarTablas();
        filtrarReservasCliente();
        log("Vista de cliente activa.");
    }

    private void mostrarModuloInterno(String titulo, Region contenido) {
        refrescarTablas();
        root.setCenter(crearShellInterno(titulo, contenido));
    }

    private BorderPane crearShellInterno(String titulo, Region contenido) {
        BorderPane shell = new BorderPane();
        shell.getStyleClass().add("app-shell");
        shell.setLeft(crearSidebarInterno());
        shell.setCenter(crearAreaTrabajo(titulo, contenido));
        return shell;
    }

    private BorderPane crearShellCliente(String titulo, Region contenido) {
        BorderPane shell = new BorderPane();
        shell.getStyleClass().add("app-shell");
        shell.setLeft(crearSidebarCliente());
        shell.setCenter(crearAreaTrabajo(titulo, contenido));
        return shell;
    }

    private VBox crearSidebarInterno() {
        VBox sidebar = crearSidebarBase();
        sidebar.getChildren().addAll(
                crearMarca(),
                crearNavButton("Dashboard", () -> mostrarModuloInterno("Dashboard", crearDashboardInterno()))
        );
        if (usuarioInternoActual == admin) {
            sidebar.getChildren().add(crearNavButton("Huespedes", () -> mostrarModuloInterno("Gestion de huespedes", crearVistaHuespedes())));
            sidebar.getChildren().add(crearNavButton("Habitaciones", () -> mostrarModuloInterno("Habitaciones disponibles", crearVistaHabitacionesAdmin())));
        }
        if (usuarioInternoActual == admin || usuarioInternoActual == recepcionista) {
            sidebar.getChildren().add(crearNavButton("Crear reserva", () -> mostrarModuloInterno("Crear reserva", crearVistaCrearReservaInterna())));
            sidebar.getChildren().add(crearNavButton("Confirmar / cancelar", () -> mostrarModuloInterno("Confirmar / cancelar reservas", crearVistaGestionReservas())));
            sidebar.getChildren().add(crearNavButton("Check-in / out", () -> mostrarModuloInterno("Check-in / Check-out", crearVistaEstadiasAdmin())));
        } else {
            sidebar.getChildren().add(crearNavButton("Reservas", () -> mostrarModuloInterno("Reservas", crearVistaGestionReservas())));
        }
        sidebar.getChildren().add(crearNavButton("Reportes", () -> mostrarModuloInterno("Reportes", crearVistaReportes())));
        sidebar.getChildren().add(crearNavButton("Pagos", () -> mostrarModuloInterno("Pagos", crearVistaPagos())));
        sidebar.getChildren().add(crearSidebarSpacer());
        sidebar.getChildren().add(crearNavButton("Salir", () -> root.setCenter(crearLogin())));
        return sidebar;
    }

    private VBox crearSidebarCliente() {
        VBox sidebar = crearSidebarBase();
        sidebar.getChildren().addAll(
                crearMarca(),
                crearNavButton("Disponibilidad", () -> root.setCenter(crearShellCliente("Disponibilidad", crearVistaDisponibilidadCliente()))),
                crearNavButton("Crear reserva", () -> root.setCenter(crearShellCliente("Crear reserva", crearVistaClienteReserva()))),
                crearNavButton("Mis reservas", () -> root.setCenter(crearShellCliente("Mis reservas", crearVistaClienteReservas()))),
                crearNavButton("Mi perfil", () -> root.setCenter(crearShellCliente("Mi perfil", crearVistaPerfilCliente()))),
                crearSidebarSpacer(),
                crearNavButton("Salir", () -> root.setCenter(crearLogin()))
        );
        return sidebar;
    }

    private VBox crearSidebarBase() {
        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setFillWidth(true);
        return sidebar;
    }

    private VBox crearMarca() {
        Label marca = new Label("Bragado");
        marca.getStyleClass().add("brand-title");
        Label subtitulo = new Label("Hotel Management");
        subtitulo.getStyleClass().add("brand-subtitle");
        VBox box = new VBox(2, marca, subtitulo);
        box.getStyleClass().add("brand-block");
        return box;
    }

    private Region crearSidebarSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private Button crearNavButton(String texto, Runnable accion) {
        Button button = new Button(texto);
        button.getStyleClass().add("nav-item");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setOnAction(e -> accion.run());
        return button;
    }

    private VBox crearAreaTrabajo(String titulo, Region contenido) {
        Label seccion = new Label(titulo);
        seccion.getStyleClass().add("page-title");
        Label contexto = new Label("Modo cliente".equals(modoActual.getText()) ? emailClienteActual : usuarioInternoActual.getNombre() + " - " + usuarioInternoActual.getRol());
        contexto.getStyleClass().add("page-subtitle");
        VBox header = new VBox(4, seccion, contexto);
        header.getStyleClass().add("page-header");

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("workspace-scroll");

        VBox area = new VBox(18, header, scroll);
        area.getStyleClass().add("workspace");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return area;
    }

    private VBox crearDashboardInterno() {
        TilePane metricas = new TilePane();
        metricas.getStyleClass().add("stats-grid");
        metricas.setHgap(14);
        metricas.setVgap(14);
        metricas.setPrefColumns(4);
        metricas.getChildren().addAll(
                crearStatCard("Ocupacion", calcularOcupacion() + "%", "habitaciones ocupadas"),
                crearStatCard("Reservas activas", String.valueOf(contarReservasActivas()), "pendientes o confirmadas"),
                crearStatCard("Huespedes", String.valueOf(huespedes.size()), "registrados"),
                crearStatCard("Ingresos", "$" + totalPagos(), "pagos simulados")
        );

        HBox acciones = new HBox(12);
        if (usuarioInternoActual == admin || usuarioInternoActual == recepcionista) {
            acciones.getChildren().add(crearQuickAction("Nueva reserva", () -> mostrarModuloInterno("Crear reserva", crearVistaCrearReservaInterna())));
            acciones.getChildren().add(crearQuickAction("Check-in", () -> mostrarModuloInterno("Check-in / Check-out", crearVistaEstadiasAdmin())));
        }
        acciones.getChildren().add(crearQuickAction("Reportes", () -> mostrarModuloInterno("Reportes", crearVistaReportes())));
        acciones.getStyleClass().add("quick-actions");

        BorderPane resumen = new BorderPane();
        resumen.getStyleClass().add("feature-panel");
        VBox textos = new VBox(8,
                crearLabelConClase("Operaciones del dia", "panel-title"),
                crearLabelConClase("Reservas, habitaciones y estadias organizadas en una sola vista para equipos de recepcion y administracion.", "panel-copy")
        );
        resumen.setLeft(textos);
        resumen.setRight(acciones);

        VBox dashboard = new VBox(18, metricas, resumen, crearVistaReportes());
        dashboard.getStyleClass().add("dashboard");
        VBox.setVgrow(dashboard, Priority.ALWAYS);
        return dashboard;
    }

    private VBox crearStatCard(String titulo, String valor, String detalle) {
        Label tituloLabel = new Label(titulo);
        tituloLabel.getStyleClass().add("stat-title");
        Label valorLabel = new Label(valor);
        valorLabel.getStyleClass().add("stat-value");
        Label detalleLabel = new Label(detalle);
        detalleLabel.getStyleClass().add("stat-detail");
        VBox card = new VBox(8, tituloLabel, valorLabel, detalleLabel);
        card.getStyleClass().add("stat-card");
        return card;
    }

    private Button crearQuickAction(String texto, Runnable accion) {
        Button button = new Button(texto);
        button.getStyleClass().add("button-secondary");
        button.setOnAction(e -> accion.run());
        return button;
    }

    private Label crearLabelConClase(String texto, String clase) {
        Label label = new Label(texto);
        label.getStyleClass().add(clase);
        label.setWrapText(true);
        return label;
    }

    private BorderPane crearVistaHabitacionesAdmin() {
        DatePicker ingreso = new DatePicker(LocalDate.now().plusDays(1));
        DatePicker egreso = new DatePicker(LocalDate.now().plusDays(2));
        cargarDisponibilidadPorFecha(ingreso, egreso, habitacionesDisponiblesInterno, null);
        tablaHabitacionesAdmin = crearTablaHabitaciones(habitacionesDisponiblesInterno);

        Button buscar = new Button("Buscar disponibilidad");
        buscar.getStyleClass().add("button-secondary");
        buscar.setOnAction(e -> cargarDisponibilidadPorFecha(
                ingreso, egreso, habitacionesDisponiblesInterno, tablaHabitacionesAdmin));

        Label ayuda = new Label("Selecciona un rango para ver las habitaciones sin reservas cruzadas en esas fechas.");
        ayuda.getStyleClass().add("section-help");
        HBox filtros = new HBox(10,
                new Label("Ingreso:"), ingreso,
                new Label("Egreso:"), egreso,
                buscar);
        filtros.setAlignment(Pos.CENTER_LEFT);
        VBox top = new VBox(10, ayuda, filtros);
        top.getStyleClass().add("toolbar-card");
        BorderPane pane = new BorderPane(tablaHabitacionesAdmin);
        pane.setTop(top);
        pane.getStyleClass().add("content-pane");
        return pane;
    }

    private BorderPane crearVistaCrearReservaInterna() {
        tablaReservasAdmin = crearTablaReservas(reservas);

        TextField nombre = new TextField();
        nombre.setPromptText("Nombre huesped");
        TextField email = new TextField();
        email.setPromptText("Email");
        TextField telefono = new TextField();
        telefono.setPromptText("Telefono");
        ComboBox<TipoHabitacion> tipo = new ComboBox<>(FXCollections.observableArrayList(TipoHabitacion.values()));
        tipo.setValue(TipoHabitacion.SIMPLE);
        TextField capacidad = new TextField("1");
        capacidad.setPromptText("Cantidad");
        DatePicker ingreso = new DatePicker(LocalDate.now().plusDays(1));
        DatePicker egreso = new DatePicker(LocalDate.now().plusDays(2));
        Button crear = new Button("Crear reserva");
        crear.getStyleClass().add("button-primary");
        crear.setOnAction(e -> crearReserva(nombre, email, telefono, tipo, capacidad, ingreso, egreso, false));

        GridPane form = crearGrid();
        form.add(new Label("Huesped:"), 0, 0);
        form.add(nombre, 1, 0);
        form.add(new Label("Email:"), 2, 0);
        form.add(email, 3, 0);
        form.add(new Label("Telefono:"), 4, 0);
        form.add(telefono, 5, 0);
        form.add(new Label("Tipo:"), 0, 1);
        form.add(tipo, 1, 1);
        form.add(new Label("Ingreso:"), 2, 1);
        form.add(ingreso, 3, 1);
        form.add(new Label("Egreso:"), 4, 1);
        form.add(egreso, 5, 1);
        form.add(new Label("Huespedes:"), 0, 2);
        form.add(capacidad, 1, 2);
        form.add(crear, 3, 3);
        form.getStyleClass().add("toolbar-card");

        BorderPane pane = new BorderPane(tablaReservasAdmin);
        pane.setTop(form);
        pane.getStyleClass().add("content-pane");
        return pane;
    }

    private BorderPane crearVistaGestionReservas() {
        tablaReservasAdmin = crearTablaReservas(reservas);

        ComboBox<String> descuento = new ComboBox<>(FXCollections.observableArrayList(
                "Sin descuento", "Temporada (15%)", "Cliente frecuente (10%)"));
        descuento.setValue("Sin descuento");

        Button confirmar = new Button("Confirmar seleccionada");
        confirmar.getStyleClass().add("button-secondary");
        confirmar.setOnAction(e -> cambiarEstadoReserva(true, descuento.getValue()));
        Button cancelar = new Button("Cancelar seleccionada");
        cancelar.getStyleClass().add("button-danger");
        cancelar.setOnAction(e -> cambiarEstadoReserva(false, null));

        boolean puedeGestionar = usuarioInternoActual == admin || usuarioInternoActual == recepcionista;
        HBox acciones = new HBox(10);
        acciones.setAlignment(Pos.CENTER_LEFT);
        if (puedeGestionar) {
            acciones.getChildren().addAll(
                    new Label("Descuento al confirmar:"), descuento, confirmar, cancelar);
        } else {
            acciones.getChildren().add(new Label("Vista de consulta. Tu rol no puede confirmar ni cancelar reservas."));
        }
        acciones.getStyleClass().add("toolbar-card");

        BorderPane pane = new BorderPane(tablaReservasAdmin);
        pane.setTop(acciones);
        pane.getStyleClass().add("content-pane");
        return pane;
    }

    private BorderPane crearVistaEstadiasAdmin() {
        tablaEstadias = new TableView<>(estadias);
        tablaEstadias.getStyleClass().add("data-table");
        tablaEstadias.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaEstadias.getColumns().add(columnaTexto("Reserva", e -> String.valueOf(e.getReserva().getId()), 80));
        tablaEstadias.getColumns().add(columnaTexto("Huesped", e -> e.getReserva().getHuesped().getNombre(), 160));
        tablaEstadias.getColumns().add(columnaTexto("Check-in", e -> String.valueOf(e.getFechaCheckIn()), 120));
        tablaEstadias.getColumns().add(columnaTexto("Check-out", e -> String.valueOf(e.getFechaCheckOut()), 120));
        tablaEstadias.getColumns().add(columnaTexto("Servicios", Estadia::getDescripcionServicios, 420));
        tablaEstadias.getColumns().add(columnaTexto("Total", e -> "$" + e.getCostoTotal(), 100));

        reservaCheckInCombo = new ComboBox<>();
        reservaCheckInCombo.setPromptText("Reserva confirmada");
        reservaCheckInCombo.setItems(FXCollections.observableArrayList(
                reservas.stream()
                        .filter(r -> "CONFIRMADA".equals(r.getEstadoNombre()))
                        .map(Reserva::getId)
                        .toList()));

        ComboBox<String> metodoPago = new ComboBox<>(FXCollections.observableArrayList(
                "Efectivo", "Tarjeta", "Transferencia"));
        metodoPago.setValue("Efectivo");

        ComboBox<TipoAmenity> amenity = new ComboBox<>(FXCollections.observableArrayList(TipoAmenity.values()));
        amenity.setValue(TipoAmenity.DESAYUNO);

        Button checkIn = new Button("Check-in reserva seleccionada");
        checkIn.getStyleClass().add("button-primary");
        checkIn.setOnAction(e -> realizarCheckIn());
        Button agregarServicio = new Button("Agregar amenity");
        agregarServicio.getStyleClass().add("button-secondary");
        agregarServicio.setOnAction(e -> modificarAmenity(amenity.getValue(), true));
        Button quitarServicio = new Button("Quitar amenity");
        quitarServicio.getStyleClass().add("button-danger");
        quitarServicio.setOnAction(e -> modificarAmenity(amenity.getValue(), false));
        Button checkOut = new Button("Check-out");
        checkOut.getStyleClass().add("button-danger");
        checkOut.setOnAction(e -> realizarCheckOut(metodoPago.getValue()));

        FlowPane acciones = new FlowPane(10, 10,
                new Label("Reserva:"), reservaCheckInCombo, checkIn,
                new Label("Amenity:"), amenity, agregarServicio, quitarServicio,
                new Label("Metodo de pago:"), metodoPago, checkOut);
        acciones.setAlignment(Pos.CENTER_LEFT);
        acciones.getStyleClass().add("action-row");

        Label ayuda = new Label("Selecciona una reserva confirmada para check-in. Para agregar/quitar amenities o hacer check-out, selecciona una estadia de la tabla.");
        ayuda.getStyleClass().add("section-help");
        ayuda.setWrapText(true);

        VBox top = new VBox(acciones, ayuda);
        top.getStyleClass().add("toolbar-card");
        BorderPane pane = new BorderPane(tablaEstadias);
        pane.setTop(top);
        pane.getStyleClass().add("content-pane");
        return pane;
    }

    private BorderPane crearVistaHuespedes() {
        tablaHuespedes = new TableView<>(huespedes);
        tablaHuespedes.getStyleClass().add("data-table");
        tablaHuespedes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaHuespedes.getColumns().add(columnaTexto("Nombre", Huesped::getNombre, 180));
        tablaHuespedes.getColumns().add(columnaTexto("Email", Huesped::getEmail, 220));
        tablaHuespedes.getColumns().add(columnaTexto("Telefono", Huesped::getTelefono, 140));
        tablaHuespedes.getColumns().add(columnaTexto("Reservas", h -> String.valueOf(h.getReservas().size()), 100));
        tablaHuespedes.getColumns().add(columnaTexto("Historial", h -> historialHuesped(h), 420));

        Label ayuda = new Label("Los huespedes se registran automaticamente cuando se crea una reserva desde cliente o usuario interno.");
        ayuda.getStyleClass().add("section-help");
        ayuda.setWrapText(true);

        VBox top = new VBox(ayuda);
        top.getStyleClass().add("toolbar-card");

        BorderPane pane = new BorderPane(tablaHuespedes);
        pane.setTop(top);
        pane.getStyleClass().add("content-pane");
        return pane;
    }

    private BorderPane crearVistaReportes() {
        refrescarTablas();
        GridPane metricas = crearGrid();
        metricas.getStyleClass().add("report-grid");
        agregarMetricaReporte(metricas, 0, "Habitaciones totales", String.valueOf(habitaciones.size()));
        agregarMetricaReporte(metricas, 1, "Disponibles", String.valueOf(contarHabitaciones(EstadoHabitacion.DISPONIBLE)));
        agregarMetricaReporte(metricas, 2, "Reservadas", String.valueOf(contarHabitaciones(EstadoHabitacion.RESERVADA)));
        agregarMetricaReporte(metricas, 3, "Ocupadas", String.valueOf(contarHabitaciones(EstadoHabitacion.OCUPADA)));
        agregarMetricaReporte(metricas, 4, "Ocupacion actual", calcularOcupacion() + "%");
        agregarMetricaReporte(metricas, 5, "Reservas activas", String.valueOf(contarReservasActivas()));
        agregarMetricaReporte(metricas, 6, "Estadias registradas", String.valueOf(estadias.size()));
        agregarMetricaReporte(metricas, 7, "Amenity mas utilizado", amenityMasUtilizado());
        agregarMetricaReporte(metricas, 8, "Pagos registrados", String.valueOf(pagosPorReserva.size()));
        agregarMetricaReporte(metricas, 9, "Ingresos simulados", "$" + totalPagos());

        Button actualizar = new Button("Actualizar reporte");
        actualizar.getStyleClass().add("button-secondary");
        actualizar.setOnAction(e -> mostrarModuloInterno("Reportes", crearVistaReportes()));

        VBox contenido = new VBox(18, metricas, actualizar);
        contenido.getStyleClass().add("toolbar-card");
        BorderPane pane = new BorderPane(contenido);
        pane.getStyleClass().add("content-pane");
        return pane;
    }

    private void agregarMetricaReporte(GridPane grid, int fila, String nombre, String valor) {
        Label nombreLabel = new Label(nombre + ":");
        nombreLabel.getStyleClass().add("report-label");
        Label valorLabel = new Label(valor);
        valorLabel.getStyleClass().add("report-value");
        grid.add(nombreLabel, 0, fila);
        grid.add(valorLabel, 1, fila);
    }

    private BorderPane crearVistaPagos() {
        tablaPagos = new TableView<>(pagos);
        tablaPagos.getStyleClass().add("data-table");
        tablaPagos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaPagos.getColumns().add(columna("ID", "id", 70));
        tablaPagos.getColumns().add(columnaTexto("Reserva", p -> idReservaPorPago(p), 90));
        tablaPagos.getColumns().add(columnaTexto("Cliente", p -> clientePorPago(p), 180));
        tablaPagos.getColumns().add(columnaTexto("Total final", p -> "$" + totalFinalPorPago(p), 120));
        tablaPagos.getColumns().add(columnaTexto("Fecha", p -> p.getFecha().toString(), 140));
        tablaPagos.getColumns().add(columna("Metodo", "metodoPago", 160));
        tablaPagos.getColumns().add(columnaTexto("Comprobante", Pago::generarComprobante, 500));

        Label ayuda = new Label("Los pagos se registran al realizar check-out y quedan disponibles como transacciones internas simuladas.");
        ayuda.getStyleClass().add("section-help");
        ayuda.setWrapText(true);

        VBox top = new VBox(ayuda);
        top.getStyleClass().add("toolbar-card");

        BorderPane pane = new BorderPane(tablaPagos);
        pane.setTop(top);
        pane.getStyleClass().add("content-pane");
        return pane;
    }

    private BorderPane crearVistaClienteReserva() {
        tablaHabitacionesCliente = crearTablaHabitaciones(habitacionesCliente);
        tablaReservasCliente = crearTablaReservas(reservasCliente);

        TextField nombre = new TextField();
        nombre.setPromptText("Nombre completo");
        TextField email = new TextField();
        email.setPromptText("Email para ver tus reservas");
        email.setText(emailClienteActual);
        TextField telefono = new TextField();
        telefono.setPromptText("Telefono");
        ComboBox<TipoHabitacion> tipo = new ComboBox<>(FXCollections.observableArrayList(TipoHabitacion.values()));
        tipo.setValue(TipoHabitacion.SIMPLE);
        TextField capacidad = new TextField("1");
        capacidad.setPromptText("Cantidad de huespedes");
        DatePicker ingreso = new DatePicker(LocalDate.now().plusDays(1));
        DatePicker egreso = new DatePicker(LocalDate.now().plusDays(2));
        emailClienteFiltro = email;
        tipoClienteFiltro = tipo;
        capacidadClienteFiltro = capacidad;
        ingresoClienteFiltro = ingreso;
        egresoClienteFiltro = egreso;

        Button buscar = new Button("Buscar disponibilidad");
        buscar.getStyleClass().add("button-secondary");
        buscar.setOnAction(e -> filtrarDisponibilidadCliente());
        Button reservar = new Button("Reservar");
        reservar.getStyleClass().add("button-primary");
        reservar.setOnAction(e -> crearReservaCliente(nombre, email, telefono, tipo, capacidad, ingreso, egreso));
        GridPane form = crearGrid();
        form.add(new Label("Nombre:"), 0, 0);
        form.add(nombre, 1, 0);
        form.add(new Label("Email:"), 2, 0);
        form.add(email, 3, 0);
        form.add(new Label("Telefono:"), 4, 0);
        form.add(telefono, 5, 0);
        form.add(new Label("Tipo:"), 0, 1);
        form.add(tipo, 1, 1);
        form.add(new Label("Ingreso:"), 2, 1);
        form.add(ingreso, 3, 1);
        form.add(new Label("Egreso:"), 4, 1);
        form.add(egreso, 5, 1);
        form.add(new Label("Huespedes:"), 0, 2);
        form.add(capacidad, 1, 2);
        form.add(buscar, 3, 2);
        form.add(reservar, 4, 2);
        form.getStyleClass().add("toolbar-card");

        Label disponibles = new Label("Habitaciones disponibles - selecciona una para reservar");
        disponibles.getStyleClass().add("section-title");

        VBox centro = new VBox(10, disponibles, tablaHabitacionesCliente);
        centro.getStyleClass().add("split-content");
        VBox.setVgrow(tablaHabitacionesCliente, Priority.ALWAYS);

        BorderPane pane = new BorderPane(centro);
        pane.setTop(form);
        pane.getStyleClass().add("content-pane");
        return pane;
    }

    private BorderPane crearVistaDisponibilidadCliente() {
        DatePicker ingreso = new DatePicker(LocalDate.now().plusDays(1));
        DatePicker egreso = new DatePicker(LocalDate.now().plusDays(2));
        cargarDisponibilidadPorFecha(ingreso, egreso, habitacionesCliente, null);
        tablaHabitacionesCliente = crearTablaHabitaciones(habitacionesCliente);

        Button buscar = new Button("Buscar disponibilidad");
        buscar.getStyleClass().add("button-secondary");
        buscar.setOnAction(e -> cargarDisponibilidadPorFecha(
                ingreso, egreso, habitacionesCliente, tablaHabitacionesCliente));

        Label ayuda = new Label("Selecciona las fechas para consultar todas las habitaciones libres durante ese periodo.");
        ayuda.getStyleClass().add("section-help");
        ayuda.setWrapText(true);

        BorderPane pane = new BorderPane(tablaHabitacionesCliente);
        HBox filtros = new HBox(10,
                new Label("Ingreso:"), ingreso,
                new Label("Egreso:"), egreso,
                buscar);
        filtros.setAlignment(Pos.CENTER_LEFT);
        VBox top = new VBox(10, ayuda, filtros);
        top.getStyleClass().add("toolbar-card");
        pane.setTop(top);
        pane.getStyleClass().add("content-pane");
        return pane;
    }

    private BorderPane crearVistaClienteReservas() {
        tablaReservasCliente = crearTablaReservas(reservasCliente);

        TextField email = new TextField();
        email.setPromptText("Email para ver tus reservas");
        email.setText(emailClienteActual);
        emailClienteFiltro = email;

        Button ver = new Button("Actualizar mis reservas");
        ver.getStyleClass().add("button-secondary");
        ver.setOnAction(e -> filtrarReservasCliente());
        Button cancelar = new Button("Cancelar seleccionada");
        cancelar.getStyleClass().add("button-danger");
        cancelar.setOnAction(e -> cancelarReservaCliente(email));

        GridPane form = crearGrid();
        form.add(new Label("Email:"), 0, 0);
        form.add(email, 1, 0);
        form.add(ver, 2, 0);
        form.add(cancelar, 3, 0);
        form.getStyleClass().add("toolbar-card");

        Label propias = new Label("Mis reservas");
        propias.getStyleClass().add("section-title");

        VBox centro = new VBox(10, propias, tablaReservasCliente);
        centro.getStyleClass().add("split-content");
        VBox.setVgrow(tablaReservasCliente, Priority.ALWAYS);

        BorderPane pane = new BorderPane(centro);
        pane.setTop(form);
        pane.getStyleClass().add("content-pane");
        filtrarReservasCliente();
        return pane;
    }

    private BorderPane crearVistaPerfilCliente() {
        Huesped huesped = buscarHuespedPorEmail(emailClienteActual);
        long cantidadReservas = reservas.stream()
                .filter(r -> r.getHuesped().getEmail().equalsIgnoreCase(emailClienteActual))
                .count();

        VBox perfil = new VBox(12);
        perfil.getStyleClass().add("toolbar-card");
        perfil.getChildren().addAll(
                crearLabelConClase(huesped == null ? nombreDesdeEmail(emailClienteActual) : huesped.getNombre(), "panel-title"),
                new Label("Email: " + emailClienteActual),
                new Label("Telefono: " + (huesped == null ? "Sin telefono" : huesped.getTelefono())),
                new Label("Reservas registradas: " + cantidadReservas)
        );

        BorderPane pane = new BorderPane(perfil);
        pane.getStyleClass().add("content-pane");
        return pane;
    }

    private TableView<Habitacion> crearTablaHabitaciones(ObservableList<Habitacion> items) {
        TableView<Habitacion> tabla = new TableView<>(items);
        tabla.getStyleClass().add("data-table");
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.getColumns().add(columna("Numero", "numero", 90));
        tabla.getColumns().add(columna("Tipo", "tipo", 120));
        tabla.getColumns().add(columna("Capacidad", "capacidad", 100));
        tabla.getColumns().add(columna("Estado", "estado", 150));
        tabla.getColumns().add(columnaTexto("Precio", h -> "$" + h.getPrecioPorNoche(), 100));
        tabla.getColumns().add(columnaTexto("Descripcion", Habitacion::getDescripcion, 360));
        return tabla;
    }

    private TableView<Reserva> crearTablaReservas(ObservableList<Reserva> items) {
        TableView<Reserva> tabla = new TableView<>(items);
        tabla.getStyleClass().add("data-table");
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.getColumns().add(columna("ID", "id", 70));
        tabla.getColumns().add(columnaTexto("Huesped", r -> r.getHuesped().getNombre(), 160));
        tabla.getColumns().add(columnaTexto("Habitacion", r -> String.valueOf(r.getHabitacion().getNumero()), 100));
        tabla.getColumns().add(columnaTexto("Tipo", r -> r.getHabitacion().getTipo().toString(), 100));
        tabla.getColumns().add(columnaTexto("Ingreso", r -> r.getFechaIngreso().toString(), 110));
        tabla.getColumns().add(columnaTexto("Egreso", r -> r.getFechaEgreso().toString(), 110));
        tabla.getColumns().add(columnaTexto("Estado", Reserva::getEstadoNombre, 120));
        tabla.getColumns().add(columnaTexto("Habitacion c/desc.", r -> "$" + r.calcularCostoTotal(), 125));
        tabla.getColumns().add(columnaTexto("Amenities", this::obtenerDescripcionAmenities, 190));
        tabla.getColumns().add(columnaTexto("Extra", r -> "$" + calcularCostoAmenities(r), 90));
        tabla.getColumns().add(columnaTexto("Total actualizado", r -> "$" + calcularTotalActualReserva(r), 125));
        tabla.getColumns().add(columnaTexto("Pago", r -> pagosPorReserva.containsKey(r.getId()) ? "REGISTRADO" : "PENDIENTE", 105));
        return tabla;
    }

    private GridPane crearGrid() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(14));
        return form;
    }

    private void aplicarTema(Scene scene) {
        URL tema = getClass().getResource("hotel-theme.css");
        if (tema != null) {
            scene.getStylesheets().add(tema.toExternalForm());
            return;
        }

        File temaEnSrc = new File("src/gui/hotel-theme.css");
        if (!temaEnSrc.exists()) {
            temaEnSrc = new File("untitled/src/gui/hotel-theme.css");
        }
        if (temaEnSrc.exists()) {
            scene.getStylesheets().add(temaEnSrc.toURI().toString());
        }
    }

    private void cargarDatosIniciales() {
        habitacionGestor.crearHabitacion(admin, 101, new HabitacionSimpleFactory());
        habitacionGestor.crearHabitacion(admin, 102, new HabitacionDobleFactory());
        habitacionGestor.crearHabitacion(admin, 201, new HabitacionSuiteFactory());
        refrescarTablas();
    }

    private void crearHabitacion(TextField numeroField, ComboBox<TipoHabitacion> tipoCombo) {
        try {
            int numero = Integer.parseInt(numeroField.getText().trim());
            HabitacionFactory factory = factoryPorTipo(tipoCombo.getValue());
            habitacionGestor.crearHabitacion(admin, numero, factory);
            numeroField.clear();
            refrescarTablas();
            log("Habitacion creada correctamente.");
        } catch (Exception ex) {
            mostrarError("No se pudo crear la habitacion", ex);
        }
    }

    private void cambiarEstadoHabitacion(ComboBox<EstadoHabitacion> estadoCombo) {
        Habitacion seleccionada = tablaHabitacionesAdmin.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Selecciona una habitacion de la tabla.");
            return;
        }
        habitacionGestor.cambiarEstado(usuarioInternoActual, seleccionada.getNumero(), estadoCombo.getValue());
        refrescarTablas();
        log("Estado de habitacion actualizado.");
    }

    private void crearReservaCliente(TextField nombre, TextField email, TextField telefono,
                                     ComboBox<TipoHabitacion> tipo, TextField capacidad, DatePicker ingreso,
                                     DatePicker egreso) {
        crearReserva(nombre, email, telefono, tipo, capacidad, ingreso, egreso, true);
    }

    private void crearReserva(TextField nombre, TextField email, TextField telefono,
                              ComboBox<TipoHabitacion> tipo, TextField capacidad, DatePicker ingreso,
                              DatePicker egreso, boolean desdeCliente) {
        try {
            validarFechas(ingreso.getValue(), egreso.getValue());
            int cantidadHuespedes = Integer.parseInt(valor(capacidad, "0"));
            List<Habitacion> disponibles = habitacionGestor.consultarDisponibilidad(
                    ingreso.getValue(), egreso.getValue(), tipo.getValue(), cantidadHuespedes,
                    reservaGestor.getReservas());
            Habitacion disponible;
            if (desdeCliente) {
                disponible = tablaHabitacionesCliente == null
                        ? null
                        : tablaHabitacionesCliente.getSelectionModel().getSelectedItem();
                if (disponible == null || !disponibles.contains(disponible)) {
                    throw new IllegalStateException("Busca y selecciona una habitacion disponible de la tabla.");
                }
            } else {
                disponible = disponibles.stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No hay habitaciones disponibles para esos filtros."));
            }

            Huesped huesped = buscarOCrearHuesped(valor(nombre, "Sin nombre"), valor(email, "sin@email.com"), valor(telefono, "Sin telefono"));
            UsuarioInterno creador = desdeCliente ? huesped : usuarioInternoActual;
            Reserva reserva = reservaGestor.crearReserva(creador, huesped, disponible, ingreso.getValue(), egreso.getValue());
            reserva.agregarObservador(new NotificacionEmailObserver(huesped.getEmail()));
            reserva.agregarObservador(new NotificacionSMSObserver(huesped.getTelefono()));

            nombre.clear();
            telefono.clear();
            if (desdeCliente) {
                emailClienteActual = valor(email, emailClienteActual);
            } else {
                email.clear();
            }
            refrescarTablas();
            filtrarDisponibilidadCliente();
            filtrarReservasCliente();
            String origen = desdeCliente ? "Solicitud de cliente" : "Reserva";
            log(origen + " #" + reserva.getId() + " creada. Costo estimado: $" + reserva.calcularCostoTotal());
        } catch (Exception ex) {
            mostrarError("No se pudo crear la reserva", ex);
        }
    }

    private void modificarReservaCliente(TextField nombre, TextField email, TextField telefono,
                                         ComboBox<TipoHabitacion> tipo, DatePicker ingreso,
                                         DatePicker egreso) {
        Reserva seleccionada = tablaReservasCliente.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Selecciona una reserva propia de la tabla.");
            return;
        }
        try {
            validarFechas(ingreso.getValue(), egreso.getValue());
            Huesped huesped = buscarOCrearHuesped(
                    valor(nombre, seleccionada.getHuesped().getNombre()),
                    valor(email, seleccionada.getHuesped().getEmail()),
                    valor(telefono, seleccionada.getHuesped().getTelefono())
            );
            Habitacion habitacionNueva = seleccionada.getHabitacion().getTipo() == tipo.getValue()
                    ? seleccionada.getHabitacion()
                    : habitacionGestor.consultarDisponibilidad(ingreso.getValue(), egreso.getValue(), tipo.getValue())
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No hay habitaciones disponibles para ese tipo."));
            reservaGestor.modificarReservaCliente(huesped, seleccionada.getId(), habitacionNueva, ingreso.getValue(), egreso.getValue());
            emailClienteActual = huesped.getEmail();
            refrescarTablas();
            filtrarDisponibilidadCliente();
            filtrarReservasCliente();
            log("Reserva #" + seleccionada.getId() + " modificada por el cliente.");
        } catch (Exception ex) {
            mostrarError("No se pudo modificar la reserva", ex);
        }
    }

    private void cancelarReservaCliente(TextField email) {
        Reserva seleccionada = tablaReservasCliente.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Selecciona una reserva propia de la tabla.");
            return;
        }
        try {
            Huesped huesped = buscarOCrearHuesped(seleccionada.getHuesped().getNombre(), valor(email, seleccionada.getHuesped().getEmail()), seleccionada.getHuesped().getTelefono());
            reservaGestor.cancelarReservaCliente(huesped, seleccionada.getId());
            refrescarTablas();
            filtrarDisponibilidadCliente();
            filtrarReservasCliente();
            log("Reserva #" + seleccionada.getId() + " cancelada por el cliente.");
        } catch (Exception ex) {
            mostrarError("No se pudo cancelar la reserva", ex);
        }
    }

    private void cambiarEstadoReserva(boolean confirmar, String descuentoSeleccionado) {
        Reserva seleccionada = tablaReservasAdmin.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Selecciona una reserva de la tabla.");
            return;
        }
        try {
            if (confirmar) {
                EstrategiaDescuento estrategia = estrategiaPorNombre(descuentoSeleccionado);
                reservaGestor.confirmarReserva(usuarioInternoActual, seleccionada.getId(), estrategia);
                log("Reserva #" + seleccionada.getId() + " confirmada.");
            } else {
                reservaGestor.cancelarReserva(usuarioInternoActual, seleccionada.getId());
                log("Reserva #" + seleccionada.getId() + " cancelada.");
            }
            refrescarTablas();
            filtrarDisponibilidadCliente();
            filtrarReservasCliente();
        } catch (Exception ex) {
            mostrarError("No se pudo cambiar el estado de la reserva", ex);
        }
    }

    private EstrategiaDescuento estrategiaPorNombre(String nombre) {
        if ("Temporada (15%)".equals(nombre)) return new DescuentoTemporada();
        if ("Cliente frecuente (10%)".equals(nombre)) return new DescuentoClienteFrecuente();
        return new SinDescuento();
    }

    private void realizarCheckIn() {
        Integer reservaId = reservaCheckInCombo == null ? null : reservaCheckInCombo.getValue();
        Reserva reserva = reservaId == null ? null : buscarReserva(reservaId);
        if (reserva == null) {
            mostrarAlerta("Selecciona una reserva confirmada para realizar el check-in.");
            return;
        }
        try {
            Estadia estadia = estadiaGestor.realizarCheckIn(usuarioInternoActual, reserva);
            refrescarTablas();
            mostrarAlerta("Check-in realizado para la reserva #" + reserva.getId()
                    + "\nAhora puedes agregar o quitar amenities desde esta pantalla."
                    + "\nTotal actualizado: $" + calcularTotalActualReserva(reserva));
        } catch (Exception ex) {
            mostrarError("No se pudo realizar el check-in", ex);
        }
    }

    private void modificarAmenity(TipoAmenity amenity, boolean agregar) {
        Estadia estadia = tablaEstadias.getSelectionModel().getSelectedItem();
        if (estadia == null) {
            mostrarAlerta("Selecciona una estadia de la tabla.");
            return;
        }
        try {
            boolean cambio = agregar
                    ? estadiaGestor.agregarAmenity(usuarioInternoActual, estadia, amenity)
                    : estadiaGestor.quitarAmenity(usuarioInternoActual, estadia, amenity);
            if (!cambio) {
                mostrarAlerta(agregar
                        ? amenity.getNombre() + " ya esta agregado."
                        : amenity.getNombre() + " no esta agregado.");
                return;
            }
            refrescarTablas();
            mostrarAlerta(amenity.getNombre() + (agregar ? " agregado." : " quitado.")
                    + "\n" + obtenerDescripcionAmenities(estadia.getReserva())
                    + "\nExtra amenities: $" + calcularCostoAmenities(estadia.getReserva())
                    + "\nTotal actualizado: $" + calcularTotalActualReserva(estadia.getReserva()));
        } catch (Exception ex) {
            mostrarError("No se pudo modificar el amenity", ex);
        }
    }

    private void realizarCheckOut(String metodoPago) {
        Estadia estadia = tablaEstadias.getSelectionModel().getSelectedItem();
        if (estadia == null) {
            mostrarAlerta("Selecciona una estadia de la tabla.");
            return;
        }
        try {
            Reserva reserva = estadia.getReserva();
            double totalFinal = calcularTotalActualReserva(reserva);
            Pago pago = estadiaGestor.realizarCheckOut(usuarioInternoActual, estadia, pagos.size() + 1, metodoPago.toLowerCase(Locale.ROOT));
            pagosPorReserva.put(reserva.getId(), pago);
            totalesFinalesPorReserva.put(reserva.getId(), totalFinal);
            refrescarTablas();
            filtrarReservasCliente();
            mostrarComprobanteCheckOut(reserva, pago, totalFinal);
        } catch (Exception ex) {
            mostrarError("No se pudo realizar el check-out", ex);
        }
    }

    private void filtrarDisponibilidadCliente() {
        if (tipoClienteFiltro == null || capacidadClienteFiltro == null
                || ingresoClienteFiltro == null || egresoClienteFiltro == null) return;
        try {
            validarFechas(ingresoClienteFiltro.getValue(), egresoClienteFiltro.getValue());
            int capacidad = Integer.parseInt(valor(capacidadClienteFiltro, "0"));
            habitacionesCliente.setAll(habitacionGestor.consultarDisponibilidad(
                    ingresoClienteFiltro.getValue(),
                    egresoClienteFiltro.getValue(),
                    tipoClienteFiltro.getValue(),
                    capacidad,
                    reservaGestor.getReservas()
            ));
            if (tablaHabitacionesCliente != null) tablaHabitacionesCliente.refresh();
        } catch (Exception ex) {
            mostrarError("No se pudo consultar disponibilidad", ex);
        }
    }

    private void filtrarReservasCliente() {
        if (emailClienteFiltro == null) return;
        String email = valor(emailClienteFiltro, "").toLowerCase(Locale.ROOT);
        if (email.isEmpty()) {
            reservasCliente.clear();
        } else {
            reservasCliente.setAll(reservas.stream()
                    .filter(r -> r.getHuesped().getEmail().toLowerCase(Locale.ROOT).equals(email))
                    .collect(java.util.stream.Collectors.toList()));
        }
        if (tablaReservasCliente != null) tablaReservasCliente.refresh();
    }

    private double calcularCostoBaseSinDescuento(Reserva reserva) {
        long noches = ChronoUnit.DAYS.between(reserva.getFechaIngreso(), reserva.getFechaEgreso());
        return noches * reserva.getHabitacion().getPrecioPorNoche();
    }

    private double calcularCostoAmenities(Reserva reserva) {
        Estadia estadia = buscarEstadia(reserva.getId());
        if (estadia == null) return 0;
        return Math.max(0, estadia.getCostoTotal() - calcularCostoBaseSinDescuento(reserva));
    }

    private double calcularTotalActualReserva(Reserva reserva) {
        if (totalesFinalesPorReserva.containsKey(reserva.getId())) {
            return totalesFinalesPorReserva.get(reserva.getId());
        }
        return reserva.calcularCostoTotal() + calcularCostoAmenities(reserva);
    }

    private String obtenerDescripcionAmenities(Reserva reserva) {
        Estadia estadia = buscarEstadia(reserva.getId());
        if (estadia == null || estadia.getDescripcionServicios() == null || estadia.getDescripcionServicios().isBlank()) {
            return "Sin amenities cargados";
        }
        return estadia.getDescripcionServicios();
    }

    private Estadia buscarEstadia(int reservaId) {
        for (Estadia estadia : estadiaGestor.getEstadias()) {
            if (estadia.getReserva().getId() == reservaId) return estadia;
        }
        return null;
    }

    private Reserva buscarReserva(int reservaId) {
        for (Reserva reserva : reservaGestor.getReservas()) {
            if (reserva.getId() == reservaId) return reserva;
        }
        return null;
    }

    private Huesped buscarHuespedPorEmail(String email) {
        for (Huesped huesped : huespedes) {
            if (huesped.getEmail().equalsIgnoreCase(email)) return huesped;
        }
        return null;
    }

    private String nombreDesdeEmail(String email) {
        int arroba = email.indexOf('@');
        return arroba > 0 ? email.substring(0, arroba) : email;
    }

    private String idReservaPorPago(Pago pago) {
        for (Map.Entry<Integer, Pago> entry : pagosPorReserva.entrySet()) {
            if (entry.getValue() == pago) return String.valueOf(entry.getKey());
        }
        return "-";
    }

    private String clientePorPago(Pago pago) {
        String id = idReservaPorPago(pago);
        if ("-".equals(id)) return "Sin datos";
        Reserva reserva = buscarReserva(Integer.parseInt(id));
        return reserva == null ? "Sin datos" : reserva.getHuesped().getNombre();
    }

    private double totalFinalPorPago(Pago pago) {
        String id = idReservaPorPago(pago);
        if ("-".equals(id)) return pago.getMonto();
        return totalesFinalesPorReserva.getOrDefault(Integer.parseInt(id), pago.getMonto());
    }

    private void mostrarComprobanteCheckOut(Reserva reserva, Pago pago, double totalFinal) {
        String mensaje = "Reserva #" + reserva.getId() + " finalizada."
                + "\nTotal habitacion con descuento: $" + reserva.calcularCostoTotal()
                + "\nCargo extra por amenities: $" + calcularCostoAmenities(reserva)
                + "\nTotal final: $" + totalFinal
                + "\n\n" + pago.generarComprobante();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        alert.setHeaderText("Check-out realizado");
        alert.showAndWait();
    }

    private void refrescarTablas() {
        habitaciones.setAll(habitacionGestor.getHabitaciones());
        reservas.setAll(reservaGestor.getReservas());
        estadias.setAll(estadiaGestor.getEstadias());
        pagos.setAll(estadiaGestor.getPagos());
        if (tablaHabitacionesAdmin != null) tablaHabitacionesAdmin.refresh();
        if (tablaReservasAdmin != null) tablaReservasAdmin.refresh();
        if (tablaEstadias != null) tablaEstadias.refresh();
        if (tablaHuespedes != null) tablaHuespedes.refresh();
        if (tablaPagos != null) tablaPagos.refresh();
        if (reservaCheckInCombo != null) {
            reservaCheckInCombo.setItems(FXCollections.observableArrayList(
                    reservas.stream()
                            .filter(r -> "CONFIRMADA".equals(r.getEstadoNombre()))
                            .map(Reserva::getId)
                            .toList()));
        }
    }

    private void cargarDisponibilidadPorFecha(DatePicker ingreso, DatePicker egreso,
                                              ObservableList<Habitacion> destino,
                                              TableView<Habitacion> tabla) {
        try {
            validarFechas(ingreso.getValue(), egreso.getValue());
            destino.setAll(habitacionGestor.consultarDisponibilidad(
                    ingreso.getValue(), egreso.getValue(), reservaGestor.getReservas()));
            if (tabla != null) tabla.refresh();
        } catch (Exception ex) {
            mostrarError("No se pudo consultar disponibilidad", ex);
        }
    }

    private UsuarioInterno buscarUsuarioInterno(String usuario, String clave) {
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

    private Huesped buscarOCrearHuesped(String nombre, String email, String telefono) {
        for (Huesped huesped : huespedes) {
            if (huesped.getEmail().equalsIgnoreCase(email)) {
                return huesped;
            }
        }
        Huesped nuevo = new Huesped(huespedes.size() + 10, nombre, email, "1234", telefono);
        huespedes.add(nuevo);
        return nuevo;
    }

    private String historialHuesped(Huesped huesped) {
        if (huesped.getReservas().isEmpty()) {
            return "Sin reservas";
        }
        List<String> historial = new ArrayList<>();
        for (Reserva reserva : huesped.getReservas()) {
            historial.add("#" + reserva.getId() + " " + reserva.getEstadoNombre());
        }
        return String.join(", ", historial);
    }

    private long contarHabitaciones(EstadoHabitacion estado) {
        return habitaciones.stream().filter(h -> h.getEstado() == estado).count();
    }

    private long contarReservasActivas() {
        return reservas.stream()
                .filter(r -> !"CANCELADA".equals(r.getEstadoNombre()) && !"FINALIZADA".equals(r.getEstadoNombre()))
                .count();
    }

    private String calcularOcupacion() {
        if (habitaciones.isEmpty()) {
            return "0";
        }
        double porcentaje = (contarHabitaciones(EstadoHabitacion.OCUPADA) * 100.0) / habitaciones.size();
        return String.format(Locale.ROOT, "%.1f", porcentaje);
    }

    private double totalPagos() {
        return totalesFinalesPorReserva.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    private String amenityMasUtilizado() {
        Map<TipoAmenity, Integer> usos = new HashMap<>();
        for (Estadia estadia : estadias) {
            for (TipoAmenity amenity : estadia.getAmenities()) {
                usos.merge(amenity, 1, Integer::sum);
            }
        }
        return usos.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey().getNombre() + " (" + e.getValue() + ")")
                .orElse("Sin amenities registrados");
    }

    private HabitacionFactory factoryPorTipo(TipoHabitacion tipo) {
        return switch (tipo) {
            case SIMPLE -> new HabitacionSimpleFactory();
            case DOBLE -> new HabitacionDobleFactory();
            case SUITE -> new HabitacionSuiteFactory();
        };
    }

    private void validarFechas(LocalDate ingreso, LocalDate egreso) {
        if (ingreso == null || egreso == null) {
            throw new IllegalArgumentException("Completa fecha de ingreso y egreso.");
        }
        if (!egreso.isAfter(ingreso)) {
            throw new IllegalArgumentException("La fecha de egreso debe ser posterior al ingreso.");
        }
    }

    private <T, V> TableColumn<T, V> columna(String titulo, String propiedad, int ancho) {
        TableColumn<T, V> col = new TableColumn<>(titulo);
        col.setCellValueFactory(new PropertyValueFactory<>(propiedad));
        col.setPrefWidth(ancho);
        return col;
    }

    private <T> TableColumn<T, String> columnaTexto(String titulo, TextoCelda<T> extractor, int ancho) {
        TableColumn<T, String> col = new TableColumn<>(titulo);
        col.setCellValueFactory(data -> new SimpleStringProperty(extractor.obtener(data.getValue())));
        col.setPrefWidth(ancho);
        return col;
    }

    private String valor(TextInputControl field, String defecto) {
        String texto = field.getText() == null ? "" : field.getText().trim();
        return texto.isEmpty() ? defecto : texto;
    }

    private void log(String mensaje) {
        if (consola != null) consola.appendText(mensaje + "\n");
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void mostrarError(String titulo, Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
        alert.setHeaderText(titulo);
        alert.showAndWait();
        log(titulo + ": " + ex.getMessage());
    }

    @FunctionalInterface
    private interface TextoCelda<T> {
        String obtener(T item);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
