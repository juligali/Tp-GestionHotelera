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
import modelo.habitacion.Habitacion;
import modelo.pago.Pago;
import modelo.promocion.Promocion;
import modelo.reserva.Reserva;
import modelo.usuario.Administrador;
import modelo.usuario.Huesped;
import modelo.usuario.Recepcionista;
import modelo.usuario.UsuarioInterno;
import patrones.comportamiento.observer.NotificacionEmailObserver;
import patrones.comportamiento.observer.NotificacionSMSObserver;
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

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HotelApp extends Application {

    private final HabitacionGestor habitacionGestor = new HabitacionGestor();
    private final ReservaGestor reservaGestor = new ReservaGestor();
    private final EstadiaGestor estadiaGestor = new EstadiaGestor();

    private final Administrador admin = new Administrador(1, "Administrador", "admin@hotel.com", "admin123");
    private final Recepcionista recepcionista = new Recepcionista(2, "Recepcionista", "recepcion@hotel.com", "recep123");
    private final PersonalAdministrativo administrativo = new PersonalAdministrativo(3, "Administrativo", "administrativo@hotel.com", "administ123");
    private UsuarioInterno usuarioInternoActual = admin;

    private final ObservableList<Habitacion> habitaciones = FXCollections.observableArrayList();
    private final ObservableList<Habitacion> habitacionesCliente = FXCollections.observableArrayList();
    private final ObservableList<Reserva> reservas = FXCollections.observableArrayList();
    private final ObservableList<Reserva> reservasCliente = FXCollections.observableArrayList();
    private final ObservableList<Estadia> estadias = FXCollections.observableArrayList();
    private final ObservableList<Huesped> huespedes = FXCollections.observableArrayList();
    private final ObservableList<Pago> pagos = FXCollections.observableArrayList();

    private BorderPane root;
    private Label modoActual;
    private TableView<Habitacion> tablaHabitacionesAdmin;
    private TableView<Habitacion> tablaHabitacionesCliente;
    private TableView<Reserva> tablaReservasAdmin;
    private TableView<Reserva> tablaReservasCliente;
    private TableView<Estadia> tablaEstadias;
    private TableView<Huesped> tablaHuespedes;
    private TableView<Pago> tablaPagos;
    private TextArea consola;
    private String emailClienteActual = "";
    private TextField emailClienteFiltro;
    private ComboBox<TipoHabitacion> tipoClienteFiltro;
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
        root.setCenter(crearShellCliente("Reservar estadia", crearVistaClienteReserva()));
        refrescarTablas();
        filtrarDisponibilidadCliente();
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
            sidebar.getChildren().add(crearNavButton("Habitaciones", () -> mostrarModuloInterno("Gestion de habitaciones", crearVistaHabitacionesAdmin())));
        }
        sidebar.getChildren().add(crearNavButton("Reservas", () -> mostrarModuloInterno("Reservas", crearVistaReservasAdmin())));
        if (usuarioInternoActual == admin || usuarioInternoActual == recepcionista) {
            sidebar.getChildren().add(crearNavButton("Check-in / out", () -> mostrarModuloInterno("Check-in / Check-out", crearVistaEstadiasAdmin())));
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
                crearNavButton("Reservar estadia", () -> root.setCenter(crearShellCliente("Reservar estadia", crearVistaClienteReserva()))),
                crearNavButton("Mis reservas", () -> root.setCenter(crearShellCliente("Mis reservas", crearVistaClienteReservas()))),
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
        acciones.getChildren().add(crearQuickAction("Nueva reserva", () -> mostrarModuloInterno("Reservas", crearVistaReservasAdmin())));
        if (usuarioInternoActual == admin || usuarioInternoActual == recepcionista) {
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
