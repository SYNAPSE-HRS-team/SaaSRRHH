# SaaS RRHH Agroindustrial - Backend

Sistema backend desarrollado con **Spring Boot** para la gestión inteligente de recursos humanos en el sector agroindustrial. La solución permite automatizar procesos críticos como asistencia, gestión de personal, tareas en campo y nómina.

---

## Descripción del Proyecto

Este backend forma parte de una plataforma SaaS orientada a optimizar la gestión del talento humano en entornos agrícolas, reemplazando procesos manuales por servicios REST seguros, escalables y modulares.

El sistema está diseñado bajo arquitectura por capas y expone una API REST que permite:

- Gestión de usuarios y roles
- Administración de empleados
- Control de asistencia antifraude (QR + TOTP)
- Asignación de tareas en campo
- Gestión de áreas de trabajo
- Procesamiento de información para nómina y analítica

---

## 🏗️ Arquitectura

El proyecto sigue una arquitectura en capas:

controller  → Manejo de endpoints REST  
service     → Lógica de negocio  
repository  → Acceso a datos (JPA)  
model       → Entidades de base de datos  
DTO         → Transferencia de datos  

---

## ⚙️ Tecnologías Utilizadas

- Java 25
- Spring Boot
- Spring Data JPA
- Spring Web (REST)
- H2 Database
- Lombok
- Maven
- JUnit + Mockito

---

## 📂 Estructura del Proyecto


```
src/
├── main/
│   ├── java/com/SaasRRHH/main/
│   │   ├── controller
│   │   ├── services
│   │   ├── repository
│   │   ├── model
│   │   └── DTO
│   └── resources/
└── test/
    └── java/com/SaasRRHH/main/
        ├── controller
        └── services
```

## 🔗 Endpoints Principales

### Usuarios
- GET /api/usuarios
- POST /api/usuarios
- GET /api/usuarios/{id}

### Empleados
- GET /api/empleados
- POST /api/empleados
- GET /api/empleados/dni/{dni}

### Asistencia
- POST /api/registros-asistencia/entrada
- POST /api/registros-asistencia/salida

### Tareas
- GET /api/tareas-asignadas
- PATCH /api/tareas-asignadas/{id}/estado

### Áreas de Trabajo
- GET /api/areas-trabajo
- POST /api/areas-trabajo

---

## 📡 Códigos HTTP

200 OK  
201 Created  
204 No Content  
400 Bad Request  
404 Not Found  

---

## Testing

Se implementa TDD:

- Pruebas unitarias (Service)
- Pruebas de integración (Controller)

Patrón:
Arrange → Act → Assert

---

## Ejecución

git clone <URL_DEL_REPO>  
mvn spring-boot:run  

API:
http://localhost:8080/api  

H2 Console:
http://localhost:8080/h2-console  

---

## Estado

✔ Backend funcional  
✔ CRUD completo  
✔ Testing implementado  
🚧 Seguridad (JWT) en proceso  

---

## Equipo

- David Garcia  
- Ricardo Hernandez  
- Miguel Paredes  
- Nancy Sandoval  
- Vahit Tineo  

---

## Licencia

Uso académico.
