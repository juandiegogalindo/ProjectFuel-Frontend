# PlataformaCombustible-Android

## 1. Nombre del Proyecto: Fuel Project

---

## 2. Descripción General
El sistema permite registrar y controlar las entradas y salidas de combustible, así como visualizar el estado actual del inventario mediante indicadores gráficos que facilitan el monitoreo de los niveles disponibles.

La aplicación implementa una estructura basada en historias de usuario y sigue principios de organización modular en el código, integrando una base de datos local para el almacenamiento y consulta de la información. Además, el sistema permite filtrar los datos por tipo de combustible, ciudad y zona, lo cual facilita la administración del inventario en diferentes ubicaciones.

Entre las funcionalidades principales del sistema se incluyen:

- Registro de entradas de combustible al inventario.
- Registro de salidas de combustible asociadas a solicitudes o despachos.
- Visualización del inventario general por ciudad.
- Visualización del inventario detallado por zonas dentro de cada ciudad.
- Representación gráfica de los niveles de combustible mediante barras de progreso, permitiendo identificar niveles altos, medios o bajos de disponibilidad.
- Consulta del historial de movimientos realizados en el sistema.

El desarrollo se realizó utilizando Android Studio, lenguaje Java, interfaz gráfica con ConstraintLayout y almacenamiento mediante SQLite para la gestión de datos locales.

Este proyecto forma parte del proceso académico de desarrollo de aplicaciones móviles, aplicando conceptos de diseño de interfaces, gestión de bases de datos, control de inventarios y organización de software en Android.
---

## 3. Objetivo
Desarrollar una aplicación Android que permita:
- Consultar precio de combustible según tipo de vehículo.
- Administrar inventario de combustible.
- Registrar salidas con cálculo automático y generación de historial.

---

## 4. Tecnologías Utilizadas
- **Lenguaje:** Java
- **IDE:** Android Studio
- **SDK mínimo:** API 21 (Android 5.0 Lollipop)
- **Arquitectura:** Activities + Intents
- **Componentes UI:**
  - LinearLayout
  - ScrollView
  - Button
  - EditText
  - Spinner
  - TextView
  - ListView
  - Toast
- **Estructuras de Datos:**
  - ArrayList
- **Control de versiones:** Git
- **Repositorio remoto:** GitHub

---

## 5. Historias de Usuario Implementadas
### 5.1 Consulta de Precio
Como usuario, deseo seleccionar el tipo de vehículo para consultar el precio del combustible correspondiente.
Funcionalidad:
- Spinner para selección.
- Cálculo simulado de precio.
- Visualización en pantalla.

---

### 5.2 Manejo de Inventario
Como estación de servicio, deseo registrar cantidades de combustible para llevar control del inventario disponible.
Funcionalidad:
- Registro de galones.
- Acumulación dinámica.
- Actualización en tiempo real.

---

### 5.3 Registro de Salidas
Como estación de servicio, deseo registrar salidas de combustible indicando tipo, cantidad y fecha.
Funcionalidad:
- Selector de tipo (Corriente, Extra, Diesel).
- Cálculo automático (galones × precio).
- Validación de inventario.
- Generación de historial dinámico.
- Registro automático de fecha.
- ListView con salidas recientes.

---

## 6. Alcance del Proyecto

Este proyecto representa una implementación básica de la capa de presentación.  

No incluye:

- Base de datos persistente.
- Backend.
- Autenticación.
- Microservicios.
- Integración normativa dinámica.

Su finalidad es académica y de aprendizaje del entorno Android.

---

