# SmartPark - Microservicios

Sistema de gestión de estacionamientos construido con arquitectura de microservicios.
Proyecto de la **Evaluación Parcial N.º 2** de la asignatura JVY0101 - Java: Diseño y Construcción de Soluciones Nativas en Nube.

## Integrantes

| Integrante | GitHub | Microservicios a cargo |
|---|---|---|
| Elvira San Martín | [MatCam1107](https://github.com/MatCam1107) | ms-usuario, ms-vehiculo, ms-notificacion |
| Nicolás Rojas | [NicoRojasBarria](https://github.com/NicoRojasBarria) | ms-ubicacion, ms-estacionamiento |

## Tecnologías

- Java 21
- Spring Boot 4.1.1 (Spring Web MVC, Spring Data JPA, Spring Security, Validation)
- Hibernate / JPA
- MySQL 8.4
- Maven (con Maven Wrapper incluido en cada servicio)
- Docker y Docker Compose
- Swagger / OpenAPI (springdoc)
- JUnit 5 y Mockito
- Git y GitHub

## Arquitectura

Cada microservicio es un proyecto Spring Boot independiente, con su propia base de datos MySQL, organizado en capas:

```
controller  ->  service  ->  repository  ->  model
(REST)          (negocio)    (JPA)            (entidades)
         dto (validaciones)   exception (errores)   client (otros servicios)
```

| Microservicio | Responsabilidad | Puerto | Base de datos | Puerto MySQL |
|---|---|---|---|---|
| ms-usuario | Usuarios y autenticación (JWT) | 8083 | usuarios_db | 3311 |
| ms-vehiculo | Vehículos asociados a un usuario | 8084 | vehiculos_db | 3312 |
| ms-notificacion | Notificaciones a los usuarios | 8085 | notificaciones_db | 3313 |
| ms-ubicacion | Zonas y ubicaciones de los estacionamientos | 8086 | ubicaciones_db | 3314 |
| ms-estacionamiento | Estacionamientos, disponibilidad y asignaciones | 8087 | estacionamientos_db | 3315 |

Cada servicio mantiene su propia base de datos: no se comparten tablas entre servicios. Las referencias entre servicios se hacen por ID (por ejemplo, `vehiculoId` o `usuarioId`).

### Comunicación entre microservicios

`ms-estacionamiento` se comunica por REST con otros dos servicios al asignar un estacionamiento:

1. Consulta el vehículo en **ms-vehiculo** (`GET /api/vehiculos/{id}`). Si no existe responde 404; si ms-vehiculo no está disponible responde 503.
2. Envía una notificación a **ms-notificacion** (`POST /api/notificaciones`) una vez confirmada la asignación. Si ese servicio falla, la asignación se mantiene y solo se registra el error en el log.

Dentro de Docker los servicios se llaman por nombre (`http://ms-vehiculo:8080`). Al ejecutar con Maven local usan `localhost` con los puertos de la tabla anterior.

## Estructura del repositorio

```
SmartPark-Microservicios/
├── docker-compose.yml        # 5 microservicios + 5 bases de datos MySQL
├── README.md
├── postman/
│   └── SmartPark.postman_collection.json
└── microservicios/
    ├── ms-usuario/
    ├── ms-vehiculo/
    ├── ms-notificacion/
    ├── ms-ubicacion/
    └── ms-estacionamiento/
```

Cada carpeta de microservicio contiene su `pom.xml`, su `Dockerfile` y su código en `src/`.

## Requisitos

- Java 21 (JDK)
- Git
- Docker y Docker Compose
- Postman (para probar los endpoints)

Maven no necesita estar instalado: cada servicio incluye `mvnw`.

## Cómo clonar y ejecutar

### Opción A: todo con Docker (recomendada)

```bash
git clone https://github.com/MatCam1107/SmartPark-Microservicios.git
cd SmartPark-Microservicios
git switch develop
docker compose up -d --build
```

No es necesario compilar antes: cada `Dockerfile` compila el proyecto con Maven y luego ejecuta el JAR con Java 21. La primera vez tarda unos minutos.

Comprobar que todo está arriba (deben verse 10 contenedores `Up`):

```bash
docker compose ps
```

Detener y eliminar los contenedores:

```bash
docker compose down        # conserva los datos
docker compose down -v     # elimina también los datos de las bases
```

### Opción B: Maven local (bases de datos en Docker)

Levantar solo las bases de datos:

```bash
docker compose up -d mysql-usuarios mysql-vehiculos mysql-notificaciones mysql-ubicaciones mysql-estacionamientos
```

Luego, en una terminal por servicio:

```bash
cd microservicios/ms-ubicacion
./mvnw spring-boot:run
```

Los valores por defecto de cada `application.properties` ya apuntan a `localhost` y a los puertos de la tabla, por lo que no se necesita configuración adicional. Para `ms-estacionamiento`, `ms-vehiculo` y `ms-notificacion` deben estar en ejecución si se quiere probar la asignación completa.

## Maven: compilar, probar y empaquetar

Desde la carpeta de cualquier microservicio:

```bash
./mvnw clean            # limpia la carpeta target
./mvnw test             # ejecuta las pruebas
./mvnw clean package    # compila, prueba y genera el JAR
./mvnw clean install    # además lo instala en el repositorio local de Maven
```

El JAR generado queda en `target/`, por ejemplo `target/ms-estacionamiento-0.0.1-SNAPSHOT.jar`, y se ejecuta con:

```bash
java -jar target/ms-estacionamiento-0.0.1-SNAPSHOT.jar
```

Nota sobre las pruebas:

- `ms-ubicacion` (6 pruebas) y `ms-estacionamiento` (11 pruebas) usan pruebas unitarias con Mockito y **no requieren base de datos**.
- `ms-usuario`, `ms-vehiculo` y `ms-notificacion` incluyen una prueba de carga de contexto que **necesita su base de datos encendida**. Levantarla con `docker compose up -d mysql-usuarios mysql-vehiculos mysql-notificaciones` antes de empaquetar, o usar `./mvnw clean package -DskipTests`.

## Documentación de la API (Swagger)

Con los servicios en ejecución:

| Servicio | URL |
|---|---|
| ms-usuario | http://localhost:8083/swagger-ui.html |
| ms-vehiculo | http://localhost:8084/swagger-ui.html |
| ms-notificacion | http://localhost:8085/swagger-ui.html |
| ms-ubicacion | http://localhost:8086/swagger-ui.html |
| ms-estacionamiento | http://localhost:8087/swagger-ui.html |

## Endpoints principales

### ms-usuario (8083)

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/auth/login` | Autenticación, devuelve un token JWT |
| GET | `/api/usuarios` | Listar usuarios (requiere token) |
| GET | `/api/usuarios/{id}` | Consultar un usuario |
| POST | `/api/usuarios` | Crear usuario |
| PUT | `/api/usuarios/{id}` | Actualizar usuario |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario |

### ms-vehiculo (8084)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/vehiculos` | Listar vehículos |
| GET | `/api/vehiculos/{id}` | Consultar un vehículo |
| POST | `/api/vehiculos` | Registrar un vehículo (patente de 6 caracteres) |
| PUT | `/api/vehiculos/{id}` | Actualizar vehículo |
| DELETE | `/api/vehiculos/{id}` | Eliminar vehículo |

### ms-notificacion (8085)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/notificaciones` | Listar notificaciones |
| GET | `/api/notificaciones/{id}` | Consultar una notificación |
| POST | `/api/notificaciones` | Crear notificación |
| PUT | `/api/notificaciones/{id}` | Actualizar notificación |
| DELETE | `/api/notificaciones/{id}` | Eliminar notificación |

### ms-ubicacion (8086)

Modelo: una **Zona** agrupa varias **Ubicaciones**; cada ubicación se asocia a un estacionamiento por su ID.

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/zonas` | Listar zonas |
| GET | `/api/zonas/{id}` | Consultar una zona |
| POST | `/api/zonas` | Crear zona |
| PUT | `/api/zonas/{id}` | Actualizar zona |
| DELETE | `/api/zonas/{id}` | Eliminar zona (409 si tiene ubicaciones) |
| GET | `/api/ubicaciones` | Listar ubicaciones (filtro opcional `?zonaId=`) |
| GET | `/api/ubicaciones/{id}` | Consultar una ubicación |
| GET | `/api/ubicaciones/estacionamiento/{estacionamientoId}` | Ubicación de un estacionamiento |
| POST | `/api/ubicaciones` | Crear ubicación (404 si la zona no existe) |
| PUT | `/api/ubicaciones/{id}` | Actualizar ubicación |
| DELETE | `/api/ubicaciones/{id}` | Eliminar ubicación |

### ms-estacionamiento (8087)

Estados de un estacionamiento: `DISPONIBLE`, `OCUPADO`, `MANTENCION`.

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/estacionamientos` | Listar (filtro opcional `?estado=`) |
| GET | `/api/estacionamientos/disponibles` | Listar los disponibles |
| GET | `/api/estacionamientos/{id}` | Consultar un estacionamiento y su estado |
| POST | `/api/estacionamientos` | Crear estacionamiento (queda DISPONIBLE) |
| PUT | `/api/estacionamientos/{id}` | Actualizar estacionamiento |
| DELETE | `/api/estacionamientos/{id}` | Eliminar estacionamiento |
| POST | `/api/estacionamientos/{id}/asignar` | Asignar a un vehículo (`{"vehiculoId": 1}`) |
| POST | `/api/estacionamientos/{id}/liberar` | Liberar el estacionamiento |
| GET | `/api/asignaciones` | Historial de asignaciones (filtro opcional `?estado=`) |
| GET | `/api/asignaciones/{id}` | Consultar una asignación |

Reglas de la asignación:

- El estacionamiento debe estar `DISPONIBLE`, si no responde **409**.
- Un vehículo no puede tener dos asignaciones activas, si no responde **409**.
- El vehículo debe existir en ms-vehiculo, si no responde **404**.
- Si ms-vehiculo no está disponible responde **503**.
- Al asignar, el estacionamiento pasa a `OCUPADO`; al liberar vuelve a `DISPONIBLE` y la asignación queda `FINALIZADA`.

## Pruebas con Postman

1. Abrir Postman e importar `postman/SmartPark.postman_collection.json` (botón **Import**).
2. Levantar el sistema con `docker compose up -d --build`.
3. Ejecutar la colección con **Run**. Las carpetas deben ejecutarse en orden, porque la primera crea los datos que usan las demás.

| Carpeta | Contenido |
|---|---|
| 1. Flujo completo | Login, vehículo, zona, estacionamiento, ubicación, asignar, notificación, liberar |
| 2. Validaciones y errores | Respuestas 400, 404 y 409 |
| 3. CRUD ms-ubicacion | Operaciones sobre zonas y ubicaciones |
| 4. CRUD ms-estacionamiento | Operaciones sobre estacionamientos |

Credenciales de laboratorio para el login: usuario `admin`, contraseña `1234`.

## Seguridad

- `ms-usuario` autentica con **JWT**: `POST /auth/login` entrega el token y el resto de sus endpoints lo exigen en el encabezado `Authorization: Bearer <token>`.
- Los demás microservicios tienen sus endpoints abiertos en esta evaluación. La protección con API Gateway queda para una etapa posterior.
- Las contraseñas incluidas en el `docker-compose.yml` (usuario de base de datos `demo` / `Demo123!`) son **solo para laboratorio**.

## Control de versiones

- `main`: rama principal.
- `develop`: rama de integración.
- `avances-elvira` y `avances-nicolas`: ramas de trabajo de cada integrante, integradas a `develop` mediante Pull Request.

## Próximas etapas

API Gateway, Service Registry, Circuit Breaker, Event Bus y despliegue en la nube, según el diagrama de arquitectura de la Evaluación Parcial N.º 1.
