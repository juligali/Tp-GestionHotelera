# Bragado - Sistema de Gestion Hotelera y servicios para huespedes

## Integrantes del grupo

- Luz Barrientos - 1193268
- Juliana Galiano - 1193349
- Paloma Macchión - 1197513

Grupo 16 - Miércoles turno mañana.

## Descripción breve del sistema

BRAGADO es un sistema de gestión hotelera orientado a objetos, desarrollado con el objetivo de centralizar y organizar las principales operaciones de una cadena hotelera.

El sistema permite administrar huéspedes, habitaciones, reservas, estadías, servicios adicionales, pagos y comprobantes. Además, busca automatizar procesos que normalmente podrían realizarse de forma manual, como la consulta de disponibilidad, la creación y cancelación de reservas, el check-in, el check-out, el cálculo de costos y la aplicación de descuentos.

La propuesta está pensada para mejorar la organización interna del hotel, reducir errores en la asignación de habitaciones, evitar reservas superpuestas y facilitar la incorporación de nuevas funcionalidades en el futuro, como nuevos tipos de habitaciones, servicios adicionales o políticas comerciales.

La aplicación cuenta con una interfaz gráfica desarrollada en JavaFX y también puede ejecutarse por consola desde la clase `Main`.

## Roles del sistema

La aplicacion contempla distintos tipos de usuarios:

- Administrador: consulta habitaciones disponibles y gestiona huespedes, reservas, estadias, reportes y pagos.
- Recepcionista: gestiona reservas, check-in, check-out, servicios y pagos.
- Personal administrativo: consulta reservas, reportes y pagos.
- Huesped/cliente: consulta disponibilidad, crea reservas y administra sus propias reservas.

## Funcionalidades principales

- Gestión de habitaciones simples, dobles y suites.
- Control de estados de habitaciones: disponible, reservada, ocupada, limpieza y fuera de servicio. El administrador y el recepcionista pueden asignar manualmente los estados de limpieza y fuera de servicio.
- Registro y consulta de huéspedes (listado disponible para administrador y personal administrativo).
- Creación, confirmación, modificación y cancelación de reservas.
- Portal de cliente para consultar disponibilidad, reservar, ver, modificar y cancelar sus reservas propias.
- Gestión de estadías mediante check-in y check-out.
- Incorporación dinámica de servicios adicionales: desayuno, spa y estacionamiento.
- Cálculo automático del costo total de una estadía.
- Aplicación de descuentos elegidos por el administrador al momento de confirmar la reserva.
- Registro de pagos simulados y generación de comprobantes.
- Notificaciones simuladas por email y SMS.
- Reportes básicos de ocupación.
- Control de acceso por roles internos.

## Instrucciones para ejecutar el proyecto

1. Clonar o descargar el repositorio del proyecto:

   ```bash
   git clone https://github.com/juligali/Tp-GestionHotelera
   ```

2. Abrir el proyecto en un entorno de desarrollo compatible con Java (IntelliJ IDEA, Eclipse o Visual Studio Code).
3. Verificar que el proyecto tenga configurado el JDK correspondiente y la librería de JavaFX para la interfaz gráfica.
4. Ejecutar la clase principal según la forma de uso deseada:
    - **Interfaz gráfica (JavaFX)**: ejecutar `gui/GuiLauncher.java`.
    - **Consola**: ejecutar `Main.java`. Al iniciar, la clase `Main` ejecuta `cargarDatosIniciales()`, que precarga tres habitaciones de ejemplo (simple, doble y suite), de modo que el sistema queda listo para probar los flujos sin necesidad de cargar datos manualmente.

Desde la ejecución del programa se podrán probar las funcionalidades principales del sistema, como la creación de reservas, la gestión de habitaciones, la aplicación de descuentos, la incorporación de servicios adicionales y el cálculo de costos.

## Casos de Prueba

El funcionamiento del sistema se demuestra de forma guiada a través de la clase `Main`, que se ejecuta por consola con **datos precargados**. Al iniciar, el método `cargarDatosIniciales()` da de alta tres habitaciones de ejemplo:

- Habitación **101** – Simple
- Habitación **102** – Doble
- Habitación **201** – Suite

Esto permite recorrer los flujos principales y alternativos del sistema sin tener que cargar datos a mano. El menú está organizado en dos accesos: **acceso interno** (con login para administrador, recepcionista y personal administrativo) y **portal cliente** (identificado por email).

Credenciales internas precargadas:

| Rol | Usuario | Clave |
|---|---|---|
| Administrador | `admin` | `admin123` |
| Recepcionista | `recepcion` | `recep123` |
| Personal administrativo | `administrativo` | `administ123` |

### Flujos principales (camino feliz)

1. **Ciclo completo de una reserva (interno):** iniciar sesión como recepcionista o administrador → *Crear reserva* (elige tipo de habitación, fechas y huésped) → *Reservas* para confirmarla y asignar el descuento → *Check-in* (crea la estadía y permite cargar amenities) → *Check-out* (genera el pago, el comprobante y finaliza la reserva). Este recorrido ejercita los patrones Builder, State, Strategy, Decorator y Observer en conjunto.
2. **Reserva desde el portal del cliente:** ingresar por *Portal cliente* con un email → *Disponibilidad* → *Crear reserva* (el huésped solo puede reservar para su propia cuenta) → *Mis reservas* → *Cancelar reserva propia*.
3. **Gestión de habitaciones:** como administrador, *Crear habitación* (Factory Method) y cambiar el estado de una habitación; consultar disponibilidad y reportes de ocupación.
4. **Servicios adicionales (amenities):** durante el check-in o desde *Agregar/modificar amenities*, sumar o quitar desayuno, spa y estacionamiento, viendo cómo se recalcula el costo total (Decorator).
5. **Descuentos:** al confirmar una reserva, elegir entre *Sin descuento*, *Descuento temporada (15%)* o *Cliente frecuente (10%)* y verificar el total resultante (Strategy).

### Flujos alternativos y validaciones

El sistema también contempla los casos de error, que se pueden reproducir desde los menús:

- **Permisos por rol:** intentar una operación no autorizada (por ejemplo, que el personal administrativo cree una habitación, o que un huésped opere sobre una reserva ajena) muestra un mensaje de acceso denegado.
- **Estados inválidos de la reserva:** confirmar una reserva ya confirmada, cancelar una finalizada o hacer check-in de una reserva no confirmada se rechazan con el mensaje correspondiente (patrón State).
- **Fechas inválidas:** una fecha de egreso anterior o igual a la de ingreso es rechazada al crear la reserva.
- **Disponibilidad:** el sistema evita reservas superpuestas detectando cruces de fechas sobre la misma habitación, y no permite reservar habitaciones ocupadas, en limpieza o fuera de servicio.
- **Check-in/check-out duplicados:** no se puede registrar dos veces el check-in o el check-out de la misma estadía, ni hacer check-out sin un check-in previo.

> **Nota:** En esta etapa los casos de prueba se ejecutan de forma interactiva mediante la clase `Main` con datos precargados. La incorporación de pruebas automatizadas con un framework de testing (por ejemplo, JUnit) figura entre las mejoras a futuro.

## Patrones aplicados

En el proyecto se aplicaron patrones de diseño creacionales, estructurales y de comportamiento para lograr un sistema más organizado, flexible y fácil de mantener.

### Builder

El patrón Builder se aplicó en la creación de reservas mediante la clase `ReservaBuilder`.

Una reserva necesita varios datos para poder construirse correctamente, como huésped, habitación, fechas de ingreso y egreso, y estrategia de descuento. Utilizar un constructor con muchos parámetros podría generar errores o hacer que el objeto quede incompleto.

Por eso se utiliza `ReservaBuilder`, que permite construir una reserva paso a paso de forma controlada.

### Factory Method

El patrón Factory Method se aplicó para la creación de habitaciones.

El sistema trabaja con distintos tipos de habitaciones, como simple, doble y suite. En lugar de crear estos objetos directamente con `new`, se delega esa responsabilidad a fábricas concretas:

- `HabitacionSimpleFactory`
- `HabitacionDobleFactory`
- `HabitacionSuiteFactory`

Todas implementan la interfaz `HabitacionFactory`, lo que permite agregar nuevos tipos de habitaciones en el futuro sin modificar el código existente.

### Decorator

El patrón Decorator se aplicó para agregar servicios adicionales a una estadía.

Una estadía puede incluir desayuno, spa, estacionamiento u otros servicios. Como estos servicios pueden combinarse de distintas formas, no conviene crear una clase diferente para cada combinación posible.

Por eso se utiliza una estructura basada en decoradores, donde cada servicio adicional envuelve a la estadía base (`EstadiaBase`) a través del componente `ComponenteEstadia` y suma su propio costo y descripción:

- `DesayunoDecorator`
- `SpaDecorator`
- `EstacionamientoDecorator`

Todos heredan del decorador abstracto `ServicioDecorator`.

### State

El patrón State se aplicó para representar el ciclo de vida de una reserva.

Una reserva puede estar en distintos estados:

- Pendiente (`EstadoPendiente`)
- Confirmada (`EstadoConfirmada`)
- Cancelada (`EstadoCancelada`)
- Finalizada (`EstadoFinalizada`)

Cada estado tiene un comportamiento distinto frente a operaciones como confirmar, cancelar o finalizar. En lugar de llenar la clase `Reserva` con muchos condicionales, se creó una interfaz `EstadoReserva` y clases concretas para cada estado.

### Strategy

El patrón Strategy se aplicó para el cálculo de descuentos.

El sistema puede aplicar distintas políticas de descuento:

- `SinDescuento`
- `DescuentoTemporada`
- `DescuentoClienteFrecuente`

Cada política se encapsula en una clase distinta que implementa la interfaz `EstrategiaDescuento`. Esto permite cambiar o agregar nuevas formas de calcular descuentos sin modificar la clase `Reserva`.

### Observer

El patrón Observer se aplicó para el sistema de notificaciones.

Cuando ocurre un evento importante en una reserva, como una confirmación, cancelación o check-out, el sistema notifica automáticamente al huésped.

La clase `Reserva` no se acopla directamente a un canal específico de comunicación, sino que notifica a observadores registrados que implementan la interfaz `ObservadorReserva`:

- `NotificacionEmailObserver`
- `NotificacionSMSObserver`

Esto permite agregar nuevos canales de notificación sin modificar la lógica principal de la reserva.

## Principios SOLID aplicados

### SRP - Single Responsibility Principle

Cada clase del sistema tiene una responsabilidad específica. Por ejemplo:

- `Reserva` gestiona los datos y el ciclo de vida de una reserva.
- `Pago` registra la información relacionada con una transacción.
- `Habitacion` administra sus características y estado.
- `EstadiaGestor` coordina operaciones relacionadas con estadías.

De esta manera, se evita que una misma clase concentre demasiadas responsabilidades.

### OCP - Open/Closed Principle

El sistema está abierto a la extensión, pero cerrado a la modificación. Esto se cumple porque se pueden agregar nuevas funcionalidades sin modificar clases ya existentes:

- Nuevos tipos de habitaciones mediante Factory Method.
- Nuevos servicios adicionales mediante Decorator.
- Nuevas políticas de descuento mediante Strategy.
- Nuevos canales de notificación mediante Observer.

### DIP - Dependency Inversion Principle

Las clases principales dependen de abstracciones y no de implementaciones concretas:

- `Reserva` depende de la interfaz `EstadoReserva`, no de un estado concreto.
- `Reserva` depende de `EstrategiaDescuento`, no de una clase específica de descuento.
- `EstadiaGestor` trabaja con `ComponenteEstadia`, sin depender directamente de los decoradores concretos.

Esto permite que el sistema sea más flexible y fácil de modificar.

## Patrones GRASP aplicados

### Controller

Los gestores actúan como controladores del sistema, ya que reciben las solicitudes principales y coordinan las operaciones:

- `ReservaGestor`
- `HabitacionGestor`
- `EstadiaGestor`

Estos gestores no concentran toda la lógica de negocio, sino que delegan las responsabilidades en las clases correspondientes.

### Creator

El patrón Creator se aplica porque algunas clases son responsables de crear objetos relacionados con la información que manejan. Por ejemplo, `ReservaGestor` puede crear una reserva porque posee o coordina los datos necesarios: huésped, habitación, fechas y estrategia de descuento. También `EstadiaGestor` puede crear un pago al finalizar una estadía, ya que conoce el costo total que debe abonarse.

### Low Coupling

El sistema busca mantener bajo acoplamiento entre sus clases. Esto significa que las clases no dependen fuertemente unas de otras y en muchos casos dependen de interfaces, lo que permite modificar o reemplazar implementaciones sin afectar todo el sistema. Por ejemplo, `Reserva` no necesita conocer todas las clases concretas de descuento o notificación, sino que trabaja con interfaces.

### High Cohesion

Cada clase mantiene una alta cohesión, es decir, sus responsabilidades están relacionadas entre sí. Por ejemplo:

- `Habitacion` se encarga de los datos y estado de una habitación.
- `Huesped` se encarga de la información del huésped.
- `Estadia` administra la duración de la estadía y el cálculo de costos.
- Los gestores agrupan operaciones relacionadas con reservas, habitaciones o estadías.

Esto hace que el código sea más claro y fácil de mantener.

### Polymorphism

El polimorfismo se aplica en varios patrones del proyecto:

- En **Strategy**, cada estrategia calcula descuentos de forma distinta.
- En **Decorator**, cada servicio adicional suma su propio costo.
- En **State**, cada estado de reserva responde de manera diferente a las operaciones confirmar, cancelar o finalizar.

Gracias al polimorfismo, se evita el uso excesivo de condicionales y se mejora la extensibilidad del sistema.

## Diagrama de casos de uso
https://tinyurl.com/msuaur3d

## Diagrama de clases
https://tinyurl.com/ytm4j34d

## Distribución de tareas

El trabajo fue desarrollado de manera colaborativa, pero cada integrante se enfocó en distintas partes del proyecto:

### Luz Barrientos

- Definición de la estructura inicial de paquetes del modelo.
- Implementación de las clases del modelo de habitaciones (`Habitacion`, `HabitacionSimple`, `HabitacionDoble`, `HabitacionSuite`).
- Implementación de las clases de usuarios (`UsuarioInterno`, `Administrador`, `Recepcionista`, `PersonalAdministrativo`, `Huesped`).
- Implementación de los enums (`EstadoHabitacion`, `TipoHabitacion`, `Rol`).
- Implementación de las factories de habitaciones (`HabitacionFactory`, `HabitacionSimpleFactory`, `HabitacionDobleFactory`, `HabitacionSuiteFactory`).
- Implementación inicial de los gestores (`HabitacionGestor`, `EstadiaGestor`, `ReservaGestor`).
- Actualización de la interfaz gráfica (GUI) y finalización del estilo CSS.

### Juliana Galiano

- Configuración inicial del proyecto (`.gitignore`, archivos de IntelliJ).
- Implementación completa del patrón **State** para reserva (`EstadoReserva`, `EstadoPendiente`, `EstadoConfirmada`, `EstadoCancelada`, `EstadoFinalizada`).
- Implementación completa del patrón **Observer** (`ObservadorReserva`, `NotificacionEmailObserver`, `NotificacionSMSObserver`).
- Implementación completa del patrón **Strategy** (`EstrategiaDescuento`, `SinDescuento`, `DescuentoTemporada`, `DescuentoClienteFrecuente`).
- Implementación de la clase `Pago`.
- Desarrollo inicial de la interfaz gráfica (`HotelApp`).
- Seguimiento y ajustes del estilo CSS.
- Redacción inicial del README.

### Paloma Macchión

- Implementación de la clase `Reserva` y del builder `ReservaBuilder`.
- Implementación de la clase `Estadia`.
- Implementación completa del patrón **Decorator** (`ComponenteEstadia`, `EstadiaBase`, `ServicioDecorator`, `DesayunoDecorator`, `SpaDecorator`, `EstacionamientoDecorator`).
- Implementación de la clase `Main` y la integración de los patrones desde consola.
- Actualización de la interfaz gráfica y comienzo del estilo CSS (`GuiLauncher`, `HotelApp`, `hotel-theme.css`).
- Ajustes estéticos finales de la interfaz gráfica.

## Estructura general del proyecto

El proyecto se organiza en paquetes para separar responsabilidades y facilitar el mantenimiento del código.

```
src/
├── Main.java
│
├── gui/
│   ├── GuiLauncher.java
│   ├── HotelApp.java
│   └── hotel-theme.css
│
├── modelo/
│   ├── usuario/
│   │   ├── UsuarioInterno.java
│   │   ├── Administrador.java
│   │   ├── Recepcionista.java
│   │   ├── PersonalAdministrativo.java
│   │   └── Huesped.java
│   ├── habitacion/
│   │   ├── Habitacion.java
│   │   ├── HabitacionSimple.java
│   │   ├── HabitacionDoble.java
│   │   └── HabitacionSuite.java
│   ├── reserva/
│   │   └── Reserva.java
│   ├── estadia/
│   │   └── Estadia.java
│   │   └── TipoAmenity.java
│   └── pago/
│       └── Pago.java
│
├── patrones/
│   ├── creacionales/
│   │   ├── factory/
│   │   │   ├── HabitacionFactory.java
│   │   │   ├── HabitacionSimpleFactory.java
│   │   │   ├── HabitacionDobleFactory.java
│   │   │   └── HabitacionSuiteFactory.java
│   │   └── builder/
│   │       └── ReservaBuilder.java
│   │
│   ├── estructurales/
│   │   └── decorator/
│   │       ├── ComponenteEstadia.java
│   │       ├── EstadiaBase.java
│   │       ├── ServicioDecorator.java
│   │       ├── DesayunoDecorator.java
│   │       ├── SpaDecorator.java
│   │       └── EstacionamientoDecorator.java
│   │
│   └── comportamiento/
│       ├── state/
│       │   └── reserva/
│       │       ├── EstadoReserva.java
│       │       ├── EstadoPendiente.java
│       │       ├── EstadoConfirmada.java
│       │       ├── EstadoCancelada.java
│       │       └── EstadoFinalizada.java
│       ├── strategy/
│       │   ├── EstrategiaDescuento.java
│       │   ├── SinDescuento.java
│       │   ├── DescuentoTemporada.java
│       │   └── DescuentoClienteFrecuente.java
│       └── observer/
│           ├── ObservadorReserva.java
│           ├── NotificacionEmailObserver.java
│           └── NotificacionSMSObserver.java
│
├── servicios/
│   ├── ReservaGestor.java
│   ├── HabitacionGestor.java
│   └── EstadiaGestor.java
│
└── enums/
    ├── TipoHabitacion.java
    ├── EstadoHabitacion.java
    └── Rol.java
```

Esta estructura permite separar las entidades del dominio, los patrones de diseño, los servicios o gestores, la interfaz gráfica y las enumeraciones utilizadas por el sistema.

## Repositorio

El código del proyecto se encuentra disponible en el siguiente repositorio:

<https://github.com/juligali/Tp-GestionHotelera>