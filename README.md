# 🌾 SaaS RRHH Agroindustrial

¡Bienvenido al sistema **SaaS RRHH Agroindustrial**! Esta plataforma es una solución integral monorepositorio diseñada para la automatización, control y optimización del talento humano en entornos agrícolas y agroindustriales. 

El proyecto reemplaza los registros tradicionales y manuales por procesos dinámicos y seguros, ofreciendo una API REST modular en el backend y una interfaz moderna y responsiva en el frontend.

---

## 🚀 Características Principales

*   **Control de Asistencia Antifraude:** Registro de entrada y salida validado mediante códigos QR dinámicos, códigos TOTP de seguridad y coordenadas de geolocalización para evitar suplantaciones en el campo.
*   **Gestión de Personal Integral:** Altas, bajas y modificaciones de empleados, registro de familiares y dependientes, administración de tipos de documentos e información laboral.
*   **Asignación de Áreas y Tareas de Trabajo:** Programación de labores agrícolas por parcelas o áreas de cultivo, reportes diarios de actividades y control del progreso de tareas asignadas.
*   **Nóminas y Boletas de Pago:** Cálculo y procesamiento de planillas, con generación automática de boletas de pago en formato PDF descargable mediante [OpenHTMLToPDF](https://github.com/danfickle/openhtmltopdf).
*   **Salud y Bienestar Laboral:** Encuestas de satisfacción y bienestar, medición activa de métricas de burnout (agotamiento laboral) y un buzón de feedback o sugerencias 100% anónimo.
*   **Dashboard y Analíticas:** Panel interactivo con gráficos visuales y dinámicos construidos con Chart.js para un control inmediato de la dotación, ausentismo e índices de bienestar.

---

## 🏗️ Arquitectura del Proyecto

Este proyecto se gestiona como un **monorepositorio** organizado de la siguiente manera:

*   **[frontend/](file:///c:/Users/alons/Documents/Ciclo%207/Desarrollo%20web%20Integrado/SaaSRRHH/SaaSRRHH/frontend):** Aplicación cliente SPA construida en **Angular 20** con Angular Material.
*   **[backend/](file:///c:/Users/alons/Documents/Ciclo%207/Desarrollo%20web%20Integrado/SaaSRRHH/SaaSRRHH/backend):** Servidor API REST desarrollado con **Spring Boot 3.2.1** y Java 17.

```
SaaSRRHH/
├── backend/           # Lógica del servidor (Spring Boot, Java 17)
├── frontend/          # Interfaz de usuario (Angular 20, Angular Material)
├── package.json       # Scripts de ejecución concurrente para desarrollo
└── README.md          # Documentación del proyecto
```

---

## ⚙️ Tecnologías Utilizadas

### Frontend
*   **Angular 20** (Framework principal de frontend)
*   **Angular Material** (Librería de componentes UI/UX adaptativa)
*   **Chart.js & ng2-charts** (Gráficos interactivos)
*   **html5-qrcode** & **qrcode** (Generación y escaneo de códigos QR para asistencia)
*   **@auth0/angular-jwt** (Manejo de tokens JWT de sesión)

### Backend
*   **Java 17 & Spring Boot 3.2.1** (Core del servidor)
*   **Spring Data JPA** (Persistencia y mapeo de datos)
*   **Spring Security** (Autenticación y autorización basada en tokens JWT)
*   **PostgreSQL** & **H2 Database** (Motores de bases de datos para producción y testing)
*   **Lombok** (Productividad en POJOs y DTOs)
*   **OpenHTMLToPDF** (Motor de renderizado HTML a PDF)
*   **Maven** (Gestor de dependencias)

---

## 🏁 Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:
*   [Node.js](https://nodejs.org/) (Versión 18 o superior)
*   [Java JDK 17](https://www.oracle.com/java/technologies/downloads/#java17)
*   [PostgreSQL](https://www.postgresql.org/) (Configurado con base de datos `SaaSRRHH` en puerto `5432`)

---

## 💻 Instrucciones de Instalación y Ejecución

### 1. Método Rápido (Ejecución Concurrente)
Puedes iniciar ambos servidores (frontend y backend) al mismo tiempo desde la carpeta raíz gracias a la configuración de `concurrently`:

```bash
# 1. Instalar dependencias de la raíz
npm install

# 2. Levantar frontend y backend simultáneamente
npm start
```
*   El **Backend** estará disponible en: `http://localhost:8080`
*   El **Frontend** estará disponible en: `http://localhost:4200`

---

### 2. Método Manual (Por separado)

#### Frontend Setup
```bash
# Navegar al directorio de frontend
cd frontend

# Instalar dependencias
npm install

# Iniciar servidor de desarrollo
ng serve
```
Abre tu navegador en `http://localhost:4200/`.

#### Backend Setup
```bash
# Navegar al directorio de backend
cd backend

# Ejecutar el proyecto con Maven Wrapper
./mvnw spring-boot:run
```
*Nota: Si estás en Windows, ejecuta `mvnw.cmd spring-boot:run` o abre el proyecto directamente en tu IDE preferido (IntelliJ IDEA, Eclipse, VS Code).*

---

## 🗄️ Configuración de Base de Datos

Por defecto, el backend está configurado en `backend/src/main/resources/application.properties` para conectarse a una base de datos PostgreSQL:

*   **URL:** `jdbc:postgresql://localhost:5432/SaaSRRHH`
*   **Username:** `postgres`
*   **Password:** `postgre`
*   **Esquema:** Las tablas se generan automáticamente mediante `spring.jpa.hibernate.ddl-auto=update`.

Si deseas utilizar una base de datos en memoria para pruebas rápidas, puedes modificar las propiedades de la base de datos o habilitar H2 Database en el archivo de propiedades.

---

## 🔗 Endpoints Clave del Backend

### Autenticación y Usuarios
*   `POST /api/auth/login` - Inicio de sesión (Retorna Token JWT)
*   `POST /api/auth/register` - Registro de nuevos usuarios
*   `GET /api/usuarios` - Listado de usuarios del sistema

### Empleados
*   `GET /api/empleados` - CRUD y búsqueda parametrizada
*   `GET /api/empleados/dni/{dni}` - Búsqueda de empleado por documento DNI

### Asistencia Antifraude
*   `POST /api/registros-asistencia/entrada` - Registro de entrada (QR + TOTP + Geocercas)
*   `POST /api/registros-asistencia/salida` - Registro de salida de jornada laboral

### Tareas y Áreas de Trabajo
*   `GET /api/tareas-asignadas` - Listado de tareas activas
*   `PATCH /api/tareas-asignadas/{id}/estado` - Cambiar estado de tareas en campo
*   `GET /api/areas-trabajo` - CRUD de áreas agrícolas

### Nómina, Boletas de Pago y Documentos
*   `GET /api/planillas` - Listado y gestión de planillas salariales
*   `GET /api/boletas-pago` - Consulta de boletas
*   `POST /api/documentos-privados` - Subida de documentos privados de empleados

### Bienestar y Feedback
*   `POST /api/encuestas-bienestar` - Respuestas a encuestas de bienestar laboral
*   `POST /api/feedback-anonimo` - Sugerencias y comentarios anónimos de los empleados
*   `GET /api/metricas-burnout` - Consulta de índices de burnout y fatiga

---

## 👥 Equipo de Desarrollo

*   David Garcia
*   Ricardo Hernandez
*   Miguel Paredes
*   Nancy Sandoval
*   Vahit Tineo

---

## 📄 Licencia

Este proyecto se distribuye bajo uso académico para la asignatura de Desarrollo Web Integrado.
