\# SmartPark - Microservicios



\## Descripción del proyecto



SmartPark es un sistema desarrollado con una arquitectura basada en microservicios para la gestión de estacionamientos.



El proyecto permite administrar usuarios, vehículos, estacionamientos, ubicaciones y notificaciones mediante servicios independientes, cada uno con responsabilidades específicas y persistencia de datos.



Este proyecto corresponde a la Evaluación Parcial N.º 2 de la asignatura Desarrollo Fullstack.



\---



\## Integrantes



\- Elvira San Martín

\- Nicolás Rojas



\---



\## Tecnologías utilizadas



\- Java 21

\- Spring Boot

\- Spring Web MVC

\- Spring Data JPA

\- Hibernate

\- Maven

\- MySQL

\- Docker

\- Docker Compose

\- Git

\- GitHub

\- Swagger / OpenAPI



\---



\## Arquitectura



El proyecto está organizado en microservicios independientes.



Cada microservicio utiliza una arquitectura por capas:



Controller → Service → Repository → Model



\### Microservicios



| Microservicio | Responsabilidad |

|---|---|

| ms-usuario | Gestión de usuarios |

| ms-vehiculo | Gestión de vehículos asociados a usuarios |

| ms-estacionamiento | Gestión de estacionamientos |

| ms-ubicacion | Gestión de ubicaciones |

| ms-notificacion | Gestión de notificaciones |



Cada microservicio mantiene su propia responsabilidad y persistencia de datos.



\---



\## Estructura del repositorio



```text

SmartPark-Microservicios

│

├── microservicios

│   ├── ms-usuario

│   ├── ms-vehiculo

│   ├── ms-estacionamiento

│   ├── ms-ubicacion

│   └── ms-notificacion

│

├── docker-compose.yml

├── .env

└── README.md

```



\---



\## Requisitos



Para ejecutar el proyecto se recomienda tener instalado:



\- Java JDK 21

\- Maven

\- Docker Desktop

\- Git



Para comprobar las instalaciones se pueden utilizar los siguientes comandos:



```bash

java -version

mvn -version

docker --version

git --version

```



\---



\## Clonar el repositorio



```bash

git clone https://github.com/MatCam1107/SmartPark-Microservicios.git

```



Ingresar a la carpeta:



```bash

cd SmartPark-Microservicios

```



\---



\## Ejecución con Docker



Desde la carpeta raíz del proyecto ejecutar:



```bash

docker compose up --build

```



Este comando construye las imágenes necesarias e inicia los microservicios y sus bases de datos configuradas en Docker Compose.



Para detener los contenedores:



```bash

docker compose down

```



\---



\## Microservicio de Usuarios



Puerto local:



```text

8083

```



Endpoint principal:



```text

http://localhost:8083/api/usuarios

```



Swagger:



```text

http://localhost:8083/swagger-ui/index.html

```



Este microservicio implementa operaciones CRUD para usuarios y utiliza MySQL para la persistencia de los datos.



\---



\## Microservicio de Vehículos



Puerto local:



```text

8084

```



Endpoint principal:



```text

http://localhost:8084/api/vehiculos

```



Swagger:



```text

http://localhost:8084/swagger-ui/index.html

```



Este microservicio implementa operaciones CRUD para vehículos y permite asociar un vehículo a un usuario mediante su identificador.



\---



\## Microservicio de Notificaciones



Puerto local:



```text

8085

```



Endpoint principal:



```text

http://localhost:8085/api/notificaciones

```



Swagger:



```text

http://localhost:8085/swagger-ui/index.html

```



Este microservicio implementa operaciones CRUD para las notificaciones generadas para los usuarios.



\---



\## Microservicio de Ubicaciones



La configuración definitiva de este microservicio se incorporará durante la integración final del proyecto.



\---



\## Microservicio de Estacionamientos



La configuración definitiva de este microservicio se incorporará durante la integración final del proyecto.



\---



\## Compilación con Maven



Cada microservicio es un proyecto Maven independiente.



Para compilar un microservicio se debe ingresar a su carpeta y ejecutar:



```bash

mvn clean package

```



Maven compila el proyecto, ejecuta las pruebas configuradas y genera el archivo ejecutable `.jar` dentro de la carpeta:



```text

target/

```



\---



\## Base de datos



Los microservicios utilizan MySQL para la persistencia de los datos.



Actualmente se encuentran configuradas bases de datos independientes para:



```text

usuarios\_db

vehiculos\_db

notificaciones\_db

```



Las bases de datos restantes se incorporarán durante la integración de los microservicios correspondientes.



\---



\## Pruebas de endpoints



Los endpoints REST permiten comprobar las operaciones principales:



```text

GET     → Listar o consultar registros

POST    → Crear registros

PUT     → Actualizar registros

DELETE  → Eliminar registros

```



También se validan respuestas HTTP correspondientes a operaciones exitosas y situaciones de error, como:



```text

200 OK

204 No Content

400 Bad Request

401 Unauthorized

404 Not Found

```



\---



\## Documentación de API



Los microservicios implementados incluyen documentación mediante Swagger / OpenAPI.



Swagger permite visualizar y probar los endpoints REST disponibles desde el navegador.



\---



\## Control de versiones



El proyecto utiliza Git y GitHub para el control de versiones.



La estrategia de ramas utilizada considera:



```text

main

develop

avances-elvira

avances-nicolas

```



`main` corresponde a la versión estable del proyecto.



`develop` se utiliza para integrar los avances desarrollados por el equipo antes de incorporarlos a la versión final.



Las ramas de avances permiten que cada integrante desarrolle sus microservicios de manera independiente.



\---



\## Estado del proyecto



Actualmente se encuentran implementados y probados:



\- ms-usuario

\- ms-vehiculo

\- ms-notificacion



Pendientes de integración:



\- ms-ubicacion

\- ms-estacionamiento



Una vez integrados todos los microservicios se realizará una prueba completa del proyecto y se actualizará este documento con la configuración definitiva.

