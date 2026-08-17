# ProjectFuel Frontend

Cliente Android nativo de **ProjectFuel**, una plataforma de gestión de una red de distribución de combustible desarrollada como proyecto de software para la Universidad Piloto de Colombia. La aplicación ofrece tres experiencias basadas en roles —cliente, operador de estación y distribuidor— sobre una única base de código, consumiendo la API REST de [ProjectFuel Backend](https://github.com/juandiegogalindo/ProjectFuel-Backend).

## Tabla de Contenidos

- [Descripción General](#descripción-general)
- [Roles y Funcionalidades](#roles-y-funcionalidades)
- [Arquitectura](#arquitectura)
- [Stack Tecnológico](#stack-tecnológico)
- [Inventario de Pantallas](#inventario-de-pantallas)
- [Prerrequisitos](#prerrequisitos)
- [Instalación y Puesta en Marcha](#instalación-y-puesta-en-marcha)
- [Configuración](#configuración)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Contribuciones](#contribuciones)
- [Autor](#autor)

## Descripción General

ProjectFuel Frontend es la cara móvil de un sistema de cadena de suministro de combustible: permite a los clientes buscar y consultar precios de combustible, a los operadores de estación gestionar el día a día de inventario y precios, y a los distribuidores administrar pedidos de entrega y rutas. El paquete y el nombre internos de la app (`co.edu.unipiloto.scrumbacklog`, `ScrumBacklog`) reflejan su origen como proyecto académico basado en Scrum; la lógica de dominio, en cambio, está completamente orientada a la distribución de combustible.

La aplicación no se distribuye en builds separados por rol. En su lugar, `MainActivity` lee el rol devuelto por el backend al iniciar sesión y muestra u oculta cada funcionalidad según corresponda, de modo que un mismo APK sirve tanto a clientes como a operadores y distribuidores.

## Roles y Funcionalidades

| Rol | Capacidades |
|---|---|
| **Cliente** | Consultar precios de combustible por ciudad/zona (`ConsultaActivity`), revisar horarios de atención de estaciones (`HorariosActivity`), ubicar estaciones en un mapa con trazado de ruta en tiempo real (`MapaEstacionesActivity`), validar códigos de subsidio (`SubsidioActivity`) |
| **Operador** (estación de servicio) | Registrar entradas y salidas de inventario (`InventarioActivity`, `SalidasActivity`), recibir despachos de combustible entrantes (`RecepcionCombustibleActivity`), ajustar precios por ubicación o zona (`ReguladorPreciosActivity`), programar nuevos pedidos (`ProgramarPedidoActivity`), revisar pedidos cancelados (`PedidosCanceladosActivity`), consultar el historial operativo (`HistorialOperadorActivity`), recibir alertas de inventario bajo (`NotificadorActivity`) |
| **Distribuidor** | Gestionar el inventario de distribución (`ControlInventarioActivity`), revisar pedidos pendientes y por entregar (`PedidosPendientesActivity`, `PedidosAEntregarActivity`), rastrear una ruta de entrega en vivo sobre el mapa (`RutaDistribucionActivity`), consultar el historial de entregas (`HistoricoDistribuidorActivity`), visualizar métricas agregadas de pedidos —pendientes, aceptados, entregados, cancelados, galones totales— en un dashboard (`DashboardDistribuidorActivity`) |
| **Admin** | Subconjunto de pantallas de operador (consulta de estaciones, inventario, alertas y control de inventario) |

Todas las pantallas (excepto login/registro) comparten un `Toolbar` común con una acción de información y una acción de cierre de sesión que limpia la sesión local y regresa a la pantalla de login.

## Arquitectura

La aplicación sigue una estructura simple de **una Activity por pantalla**, sin capa de ViewModel/Repository: cada `Activity` gestiona directamente sus propias vistas, invoca `ApiService` a través de Retrofit y actualiza la interfaz desde el callback de la respuesta. Las pantallas con listas usan implementaciones de `BaseAdapter` (por ejemplo `HistoricoAdapter`, `PedidoAdapter`, `MovimientoAdapter`) enlazadas a `ListView`, no a `RecyclerView`.

```mermaid
flowchart TD
    A[LoginScreenActivity] -->|Iniciar sesión| B[LoginActivity]
    A -->|Registrarse| C[RegisterActivity]
    B -->|POST usuarios/login| D[(ProjectFuel Backend)]
    D -->|rol, id_ubicacion| E[SharedPreferences: sesion]
    E --> F[MainActivity]
    F -->|rol = cliente| G[Pantallas Cliente]
    F -->|rol = operador / admin| H[Pantallas Operador]
    F -->|rol = distribuidor| I[Pantallas Distribuidor]
    G & H & I -->|Llamadas Retrofit| D
```

Dos pantallas (`MapaEstacionesActivity`, `RutaDistribucionActivity`) combinan además `FusedLocationProviderClient` para el rastreo GPS en vivo con la API de Directions de Google Maps, decodificando la polyline recibida con Android Maps Utils para dibujar rutas sobre el mapa.

## Stack Tecnológico

| Categoría | Tecnología |
|---|---|
| Lenguaje | Java 11 |
| Plataforma | Android, `minSdk` 23, `targetSdk`/`compileSdk` 36 |
| Build | Gradle 9.2.1 (Groovy DSL) con catálogo de versiones (`libs.versions.toml`); toolchain del daemon de Gradle fijado a JDK 21 |
| Networking | Retrofit 2.11.0 + convertidor Gson, interceptor de logging de OkHttp |
| Mapas y ubicación | Google Maps SDK for Android, Google Play Services Location, API de Directions de Google Maps (vía Volley), Android Maps Utils |
| UI | AndroidX AppCompat, Material Components, ConstraintLayout, View Binding |
| Persistencia local | `SharedPreferences` (sesión: id de usuario, rol, id de estación) |
| Pruebas | JUnit 4, AndroidX Test, Espresso (solo scaffolding por defecto) |

## Inventario de Pantallas

<details>
<summary>Listado completo de las 21 Activities</summary>

| Paquete | Activity | Propósito |
|---|---|---|
| `logIn` | `LoginScreenActivity` | Punto de entrada de la app / launcher activity |
| `logIn` | `LoginActivity` | Autentica y almacena los datos de sesión |
| `logIn` | `RegisterActivity` | Auto-registro de clientes |
| — | `MainActivity` | Centro de navegación basado en rol |
| `cliente` | `ConsultaActivity` | Consulta de precios de combustible por ubicación/zona |
| `cliente` | `HorariosActivity` | Horarios de atención de estaciones |
| `cliente` | `MapaEstacionesActivity` | Mapa de estaciones + ruta hacia la estación seleccionada |
| `cliente` | `SubsidioActivity` | Validación de códigos de subsidio |
| `operador` | `InventarioActivity` | Registro de entradas de inventario |
| `operador` | `SalidasActivity` | Registro de salidas de inventario |
| `operador` | `RecepcionCombustibleActivity` | Recepción de despachos de combustible programados |
| `operador` | `ReguladorPreciosActivity` | Ajuste de precios por ubicación/zona |
| `operador` | `ProgramarPedidoActivity` | Programación de un nuevo pedido |
| `operador` | `PedidosCanceladosActivity` | Revisión de pedidos cancelados |
| `operador` | `HistorialOperadorActivity` | Historial de movimientos operativos |
| `operador` | `NotificadorActivity` | Verificación de alertas de inventario bajo |
| `distribuidor` | `ControlInventarioActivity` | Control de inventario de distribución |
| `distribuidor` | `PedidosPendientesActivity` | Cola de pedidos pendientes |
| `distribuidor` | `PedidosAEntregarActivity` | Pedidos listos para entrega |
| `distribuidor` | `RutaDistribucionActivity` | Rastreo de ruta de entrega en vivo |
| `distribuidor` | `HistoricoDistribuidorActivity` | Historial de entregas |
| `distribuidor` | `DashboardDistribuidorActivity` | Dashboard de métricas de pedidos |

</details>

## Prerrequisitos

- Android Studio (Ladybug o superior, compatible con AGP 9.0.1 / compileSdk 36)
- JDK 21 (toolchain del daemon de Gradle) — JDK 11 se usa únicamente como compatibilidad de código fuente/target del módulo de la app
- Un emulador de Android o dispositivo físico, API nivel 23 o superior
- Una instancia en ejecución de [ProjectFuel Backend](https://github.com/juandiegogalindo/ProjectFuel-Backend) accesible desde la red del dispositivo/emulador
- Una clave de API de Google Maps con **Maps SDK for Android** y **Directions API** habilitadas

## Instalación y Puesta en Marcha

```bash
git clone https://github.com/juandiegogalindo/ProjectFuel-Frontend.git
cd ProjectFuel-Frontend
```

Abrir el proyecto en Android Studio y dejar que Gradle sincronice, o compilar desde la línea de comandos:

```bash
./gradlew assembleDebug
```

Instalar en un dispositivo/emulador conectado:

```bash
./gradlew installDebug
```

## Configuración

Antes de usar la app contra tu propio entorno, hay que ajustar dos valores:

**1. URL base del backend**, definida de forma fija en `ApiClient.java`:

```java
// app/src/main/java/co/edu/unipiloto/scrumbacklog/api/apiconfiguracion/ApiClient.java
private static final String BASE_URL = "http://192.168.2.10:8080/";
```

Debe apuntarse a donde esté corriendo tu instancia del backend. Para un emulador que se conecta a un backend en la misma máquina anfitriona, `10.0.2.2` es la dirección de loopback convencional en lugar de `localhost`.

**2. Clave de API de Google Maps**, actualmente duplicada en tres lugares: `res/values/strings.xml`, `AndroidManifest.xml`, y un string literal dentro de la URL de la petición Directions en `MapaEstacionesActivity.java`. Se debe reemplazar en los tres sitios por una clave propia.

## Estructura del Proyecto

```
ProjectFuel-Frontend/
├── app/
│   ├── src/main/java/co/edu/unipiloto/scrumbacklog/
│   │   ├── activity/
│   │   │   ├── logIn/          # LoginScreenActivity, LoginActivity, RegisterActivity
│   │   │   ├── cliente/        # Consulta, Horarios, MapaEstaciones, Subsidio
│   │   │   ├── operador/       # Inventario, Salidas, Notificador, ReguladorPrecios,
│   │   │   │                   # ProgramarPedido, PedidosCancelados,
│   │   │   │                   # RecepcionCombustible, HistorialOperador (+ adapters)
│   │   │   ├── distribuidor/   # ControlInventario, DashboardDistribuidor,
│   │   │   │                   # PedidosPendientes/AEntregar, RutaDistribucion,
│   │   │   │                   # Historico (+ adapters)
│   │   │   └── MainActivity.java   # Centro de navegación basado en rol
│   │   ├── api/
│   │   │   ├── apiconfiguracion/   # ApiClient (configuración Retrofit), ApiService (endpoints)
│   │   │   ├── login/               # LoginRequest/Response, RegisterRequest
│   │   │   └── *.java               # DTOs de Request/Response (Pedido, Movimiento, Subsidio, ...)
│   │   ├── model/               # Modelos de dominio: Usuario, Ubicacion, Pedido, Inventario, ...
│   │   └── Spinner/              # Listener auxiliar SimpleItemSelected
│   ├── src/test/                # Scaffolding de pruebas unitarias (JUnit)
│   ├── src/androidTest/         # Scaffolding de pruebas instrumentadas (Espresso)
│   └── src/main/res/
│       ├── layout/               # 21 layouts de Activity + 8 layouts de ítem de lista
│       ├── navigation/           # nav_graph residual sin uso (ver Limitaciones Conocidas)
│       ├── values/               # strings, colors, themes
│       └── xml/                  # network_security_config, reglas de backup/data extraction
├── gradle/                      # Catálogo de versiones y wrapper (Gradle 9.2.1)
└── build.gradle / settings.gradle
```

## Contribuciones

Este es un proyecto académico desarrollado para la Universidad Piloto de Colombia. Sugerencias y comentarios son bienvenidos a través de issues o pull requests.

![Java](https://img.shields.io/badge/Java-11-orange)
![Android](https://img.shields.io/badge/Android-API%2023--36-3DDC84)
![Gradle](https://img.shields.io/badge/Gradle-9.2.1-02303A)
![Retrofit](https://img.shields.io/badge/Retrofit-2.11.0-informational)
![Google Maps](https://img.shields.io/badge/Google%20Maps-SDK-4285F4)
![License](https://img.shields.io/badge/licencia-MIT-green)

## Autor

**Juan Diego Galindo**
Estudiante de Ingeniería de Sistemas
 
- GitHub: [@juandiegogalindo](https://github.com/juandiegogalindo)
- LinkedIn: [Juan Diego Galindo - Full Stack](https://linkedin.com/in/jdgalindo6)
