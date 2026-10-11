# SmartPark - Microservicios

Sistema de gestión de estacionamientos construido con arquitectura de microservicios.  
Proyecto de la **Evaluación Parcial N.º 2** de la asignatura **JVY0101 - Java: Diseño y Construcción de Soluciones Nativas en Nube**.

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

```text
controller  ->  service  ->  repository  ->  model
(REST)          (negocio)    (JPA)            (entidades)

dto (validaciones)
exception (manejo de errores)
client (comunicación con otros servicios, cuando corresponde)
```

| Microservicio | Responsabilidad | Puerto | Base de datos | Puerto MySQL |
|---|---|---:|---|---:|
| ms-usuario | Usuarios y autenticación JWT | 8083 | usuarios_db | 3311 |
| ms-vehiculo | Vehículos asociados a un usuario | 8084 | vehiculos_db | 3312 |
| ms-notificacion | Notificaciones a los usuarios | 8085 | notificaciones_db | 3313 |
| ms-ubicacion | Zonas y ubicaciones de estacionamientos | 8086 | ubicaciones_db | 3314 |
| ms-estacionamiento | Estacionamientos, disponibilidad y asignaciones | 8087 | estacionamientos_db | 3315 |

Cada servicio mantiene su propia base de datos y no comparte tablas con otros microservicios.

Las referencias hacia entidades pertenecientes a otros servicios se mantienen mediante sus identificadores, por ejemplo `usuarioId`, `vehiculoId` o `ubicacionId`.

Dentro de un mismo microservicio se utilizan relaciones JPA cuando corresponde. Por ejemplo, una `Ubicacion` pertenece a una `Zona`, y una `Asignacion` pertenece a un `Estacionamiento`.

### Comunicación entre microservicios

`ms-estacionamiento` se comunica mediante REST con otros servicios durante la asignación de un estacionamiento:

1. Consulta el vehículo en **ms-vehiculo** mediante `GET /api/vehiculos/{id}`.
2. Si el vehículo existe y el estacionamiento está disponible, registra la asignación y cambia el estacionamiento a `OCUPADO`.
3. Envía una notificación a **ms-notificacion** mediante `POST /api/notificaciones`.
4. Al liberar el estacionamiento, este vuelve a `DISPONIBLE` y la asignación queda `FINALIZADA`.

Si el vehículo no existe se responde `404`. Si `ms-vehiculo` no está disponible, se responde `503`.

Si `ms-notificacion` falla, la asignación se mantiene y el error se registra en el log.

Dentro de Docker los servicios se comunican mediante sus nombres, por ejemplo:

```text
http://ms-vehiculo:8080
```

Al ejecutarlos localmente con Maven se utilizan `localhost` y los puertos externos correspondientes.

## Estructura del repositorio

```text
SmartPark-Microservicios/
├── docker-compose.yml
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

Cada carpeta de microservicio contiene su propio `pom.xml`, `Dockerfile`, Maven Wrapper y código fuente en `src/`.

## Requisitos

- Java 21 (JDK)
- Git
- Docker y Docker Compose
- Postman para las pruebas de endpoints

Maven no necesita estar instalado globalmente, ya que cada servicio incluye Maven Wrapper (`mvnw` / `mvnw.cmd`).

## Cómo clonar y ejecutar

### Opción A: ejecutar todo con Docker

Clonar el repositorio:

```bash
git clone https://github.com/MatCam1107/SmartPark-Microservicios.git
cd SmartPark-Microservicios
git switch develop
```

Construir y levantar el sistema:

```bash
docker compose up -d --build
```

No es necesario compilar previamente. Cada `Dockerfile` construye su proyecto con Maven y posteriormente ejecuta el JAR con Java 21.

Comprobar los contenedores:

```bash
docker compose ps
```

La solución completa utiliza **10 contenedores**:

- 5 microservicios.
- 5 bases de datos MySQL.

Para detenerlos:

```bash
docker compose down
```

Para detenerlos y eliminar también los volúmenes de las bases de datos:

```bash
docker compose down -v
```

### Opción B: Maven local y bases de datos en Docker

Levantar solamente las bases de datos:

```bash
docker compose up -d mysql-usuarios mysql-vehiculos mysql-notificaciones mysql-ubicaciones mysql-estacionamientos
```

Luego ejecutar cada microservicio desde su propia carpeta. Ejemplo:

```bash
cd microservicios/ms-ubicacion
./mvnw spring-boot:run
```

En Windows también puede utilizarse:

```powershell
.\mvnw.cmd spring-boot:run
```

Los valores predeterminados de cada `application.properties` utilizan `localhost` y los puertos indicados anteriormente.

Para probar el flujo completo de asignación deben estar disponibles `ms-estacionamiento`, `ms-vehiculo` y `ms-notificacion`.

## Maven: compilar, probar y empaquetar

Desde la carpeta de cada microservicio:

```bash
./mvnw clean
./mvnw test
./mvnw clean package
./mvnw clean install
```

En Windows:

```powershell
.\mvnw.cmd clean package
```

`clean package` compila el proyecto, ejecuta las pruebas y genera el archivo JAR dentro de `target/`.

Ejemplo:

```text
target/ms-estacionamiento-0.0.1-SNAPSHOT.jar
```

Puede ejecutarse mediante:

```bash
java -jar target/ms-estacionamiento-0.0.1-SNAPSHOT.jar
```

### Pruebas Maven verificadas

Los cinco microservicios fueron compilados y empaquetados correctamente mediante Maven.

- `ms-usuario`: prueba de carga de contexto ejecutada correctamente.
- `ms-vehiculo`: prueba de carga de contexto ejecutada correctamente.
- `ms-notificacion`: prueba de carga de contexto ejecutada correctamente.
- `ms-ubicacion`: **6 pruebas**, sin fallos ni errores.
- `ms-estacionamiento`: **11 pruebas**, sin fallos ni errores.

En los cinco proyectos se obtuvo `BUILD SUCCESS` y se generó correctamente el JAR correspondiente.

Las pruebas de carga de contexto de `ms-usuario`, `ms-vehiculo` y `ms-notificacion` requieren que sus bases de datos estén disponibles.

## Documentación de API - Swagger

Con los servicios en ejecución:

| Servicio | Swagger |
|---|---|
| ms-usuario | `http://localhost:8083/swagger-ui/index.html` |
| ms-vehiculo | `http://localhost:8084/swagger-ui/index.html` |
| ms-notificacion | `http://localhost:8085/swagger-ui/index.html` |
| ms-ubicacion | `http://localhost:8086/swagger-ui/index.html` |
| ms-estacionamiento | `http://localhost:8087/swagger-ui/index.html` |

## Endpoints principales

### ms-usuario - Puerto 8083

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/auth/login` | Autenticación y obtención de token JWT |
| GET | `/api/usuarios` | Listar usuarios |
| GET | `/api/usuarios/{id}` | Consultar usuario |
| POST | `/api/usuarios` | Crear usuario |
| PUT | `/api/usuarios/{id}` | Actualizar usuario |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario |

Los endpoints de usuarios requieren autenticación mediante JWT.

### ms-vehiculo - Puerto 8084

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/vehiculos` | Listar vehículos |
| GET | `/api/vehiculos/{id}` | Consultar vehículo |
| POST | `/api/vehiculos` | Registrar vehículo |
| PUT | `/api/vehiculos/{id}` | Actualizar vehículo |
| DELETE | `/api/vehiculos/{id}` | Eliminar vehículo |

### ms-notificacion - Puerto 8085

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/notificaciones` | Listar notificaciones |
| GET | `/api/notificaciones/{id}` | Consultar notificación |
| POST | `/api/notificaciones` | Crear notificación |
| PUT | `/api/notificaciones/{id}` | Actualizar notificación |
| DELETE | `/api/notificaciones/{id}` | Eliminar notificación |

### ms-ubicacion - Puerto 8086

Una **Zona** puede agrupar múltiples **Ubicaciones**. Cada ubicación se asocia a un estacionamiento mediante `estacionamientoId`.

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/zonas` | Listar zonas |
| GET | `/api/zonas/{id}` | Consultar zona |
| POST | `/api/zonas` | Crear zona |
| PUT | `/api/zonas/{id}` | Actualizar zona |
| DELETE | `/api/zonas/{id}` | Eliminar zona |
| GET | `/api/ubicaciones` | Listar ubicaciones; permite filtro `?zonaId=` |
| GET | `/api/ubicaciones/{id}` | Consultar ubicación |
| GET | `/api/ubicaciones/estacionamiento/{estacionamientoId}` | Consultar ubicación de un estacionamiento |
| POST | `/api/ubicaciones` | Crear ubicación |
| PUT | `/api/ubicaciones/{id}` | Actualizar ubicación |
| DELETE | `/api/ubicaciones/{id}` | Eliminar ubicación |

Al intentar eliminar una zona que tiene ubicaciones asociadas se obtiene respuesta `409 Conflict`.

### ms-estacionamiento - Puerto 8087

Estados utilizados por un estacionamiento:

- `DISPONIBLE`
- `OCUPADO`
- `MANTENCION`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/estacionamientos` | Listar estacionamientos; permite filtro `?estado=` |
| GET | `/api/estacionamientos/disponibles` | Listar estacionamientos disponibles |
| GET | `/api/estacionamientos/{id}` | Consultar estacionamiento |
| POST | `/api/estacionamientos` | Crear estacionamiento |
| PUT | `/api/estacionamientos/{id}` | Actualizar estacionamiento |
| DELETE | `/api/estacionamientos/{id}` | Eliminar estacionamiento |
| POST | `/api/estacionamientos/{id}/asignar` | Asignar estacionamiento a vehículo |
| POST | `/api/estacionamientos/{id}/liberar` | Liberar estacionamiento |
| GET | `/api/asignaciones` | Listar historial de asignaciones; permite filtro `?estado=` |
| GET | `/api/asignaciones/{id}` | Consultar asignación |

Ejemplo para asignar un vehículo:

```json
{
  "vehiculoId": 2
}
```

### Reglas de asignación

- El estacionamiento debe estar `DISPONIBLE`.
- Al asignarlo cambia a `OCUPADO`.
- Un vehículo no puede tener dos asignaciones activas.
- El vehículo debe existir en `ms-vehiculo`.
- La asignación genera una notificación en `ms-notificacion`.
- Al liberar el estacionamiento vuelve a `DISPONIBLE`.
- La asignación pasa de `ACTIVA` a `FINALIZADA`.
- Intentar liberar nuevamente un estacionamiento sin asignación activa devuelve `409 Conflict`.
- No se puede eliminar un estacionamiento que tenga asignaciones asociadas; se devuelve `409 Conflict`.

## Pruebas funcionales con Postman

El repositorio incluye:

```text
postman/SmartPark.postman_collection.json
```

Para utilizarla:

1. Levantar el sistema mediante `docker compose up -d --build`.
2. Abrir Postman.
3. Importar `SmartPark.postman_collection.json`.
4. Ejecutar las solicitudes correspondientes.

Durante las pruebas funcionales se verificaron operaciones CRUD, validaciones y respuestas HTTP de los cinco microservicios.

Se comprobaron, entre otras, respuestas:

- `200 OK`: consultas, creación y actualización.
- `204 No Content`: eliminación correcta.
- `400 Bad Request`: validaciones de datos.
- `401 Unauthorized`: acceso a `ms-usuario` sin token válido.
- `404 Not Found`: consulta de recursos inexistentes.
- `409 Conflict`: operaciones que violan reglas o restricciones.

También se verificó el flujo integrado:

```text
ms-estacionamiento
        |
        +----> ms-vehiculo
        |
        +----> ms-notificacion
```

Al asignar el estacionamiento se comprobó que:

1. El vehículo fue consultado correctamente.
2. El estacionamiento cambió de `DISPONIBLE` a `OCUPADO`.
3. Se creó una asignación `ACTIVA`.
4. `ms-notificacion` recibió la notificación de asignación.
5. Al liberar el estacionamiento volvió a `DISPONIBLE`.
6. La asignación pasó a `FINALIZADA`.

### Credenciales de laboratorio

Para obtener el JWT de `ms-usuario`:

```json
{
  "username": "admin",
  "password": "1234"
}
```

## Seguridad

`ms-usuario` utiliza autenticación mediante **JWT**.

El endpoint:

```text
POST /auth/login
```

genera un token que debe enviarse en las solicitudes protegidas:

```text
Authorization: Bearer <token>
```

Las contraseñas y credenciales configuradas en `docker-compose.yml` corresponden únicamente al entorno académico/de laboratorio.

Los demás microservicios mantienen sus endpoints disponibles para las pruebas de esta evaluación.

## Control de versiones

El proyecto utiliza Git y GitHub mediante una estrategia de ramas:

- `main`: rama principal.
- `develop`: rama de integración.
- `avances-elvira`: rama de trabajo de Elvira.
- `avances-nicolas`: rama de trabajo de Nicolás.

Los avances de ambos integrantes fueron integrados a `develop` mediante **Pull Requests**, manteniendo el historial individual de commits.

## Estado de la entrega

La solución implementa los **5 de 5 microservicios definidos para SmartPark**, equivalente al **100 % de los microservicios definidos para esta entrega**:

- ✅ ms-usuario
- ✅ ms-vehiculo
- ✅ ms-notificacion
- ✅ ms-ubicacion
- ✅ ms-estacionamiento

Los cinco servicios cuentan con persistencia MySQL y arquitectura organizada por responsabilidades.

Además, se verificaron:

- CRUD y endpoints REST.
- Validaciones y manejo de errores.
- Persistencia mediante JPA / Hibernate.
- Relaciones JPA donde corresponde.
- Compilación, pruebas y empaquetado mediante Maven.
- Generación de archivos JAR.
- Ejecución mediante Docker Compose.
- 5 microservicios y 5 bases MySQL ejecutándose en 10 contenedores.
- Documentación Swagger / OpenAPI.
- Pruebas funcionales mediante Postman.
- Comunicación entre microservicios en el flujo de asignación.
- Trabajo colaborativo mediante ramas y Pull Requests.

## Próximas etapas

Como evolución de la arquitectura se pueden incorporar API Gateway, Service Registry, Circuit Breaker, Event Bus y despliegue en la nube, de acuerdo con la arquitectura planteada para etapas posteriores del proyecto.