# Integración Serverless - SmartPark

## 1. Objetivo

Implementar una comunicación mediante mensajería asíncrona
entre el microservicio de estacionamientos y AWS Lambda,
utilizando Amazon SQS y Amazon CloudWatch.

## 2. Tecnologías utilizadas

- Java 21.
- Spring Boot.
- AWS SDK para Java.
- Amazon EC2.
- Docker Swarm.
- Nginx como API Gateway.
- Amazon SQS.
- AWS Lambda con Python 3.13.
- Amazon CloudWatch.

## 3. Arquitectura de la integración

Cliente HTTP
    |
    v
API Gateway Nginx
    |
    v
Microservicio de estacionamientos
    |
    +-- MySQL
    |
    +-- Microservicio de vehículos (REST)
    |
    +-- Microservicio de notificaciones (REST)
    |
    v
Amazon SQS
    |
    v
AWS Lambda
    |
    v
Amazon CloudWatch

## 4. Productor de mensajes

El productor se encuentra en la clase SqsProducer.java
del microservicio ms-estacionamiento.

Cuando se realiza una asignación correctamente, después
de confirmar la transacción, se publica un mensaje en
Amazon SQS.

El mensaje contiene:

- evento: tipo de evento generado.
- asignacionId: identificador de la asignación.
- pruebaId: identificador único de seguimiento.

Se utiliza la cadena de credenciales del AWS SDK y el
rol IAM asociado a la instancia EC2, sin almacenar
credenciales de AWS en el código fuente.

## 5. Configuración de Amazon SQS

- Nombre: smartpark-asignaciones.
- Tipo: Standard.
- Región: us-east-1.
- Tiempo de visibilidad: 60 segundos.
- Retención de mensajes: 4 días.
- Retraso de entrega: 0 segundos.
- Cifrado: SSE-SQS.

## 6. Configuración de AWS Lambda

- Nombre: smartpark-receptor-asignaciones.
- Runtime: Python 3.13.
- Arquitectura: x86_64.
- Rol de ejecución: LabRole (AWS Academy).
- Handler: lambda_function.lambda_handler.
- Desencadenador: cola smartpark-asignaciones.
- Tamaño del lote: 1.
- Ventana de procesamiento por lotes: 0 segundos.

El código de la función se encuentra en:

serverless/lambda_function.py

La función recibe los mensajes de SQS, interpreta
su contenido JSON y registra los datos en CloudWatch.

## 7. Configuración del despliegue

El archivo docker-compose.swarm.yml permite configurar
Amazon SQS mediante las variables de entorno:

- SQS_QUEUE_URL
- AWS_REGION

Desde la terminal de EC2:

```bash
export AWS_REGION=us-east-1

export SQS_QUEUE_URL="$(aws sqs get-queue-url \
  --queue-name smartpark-asignaciones \
  --region us-east-1 \
  --query QueueUrl \
  --output text)"
```

Para desplegar utilizando las imágenes locales:

```bash
sudo --preserve-env=SQS_QUEUE_URL,AWS_REGION docker stack deploy \
  --resolve-image never \
  -c docker-compose.swarm.yml smartpark
```

Para iniciar el API Gateway:

```bash
sudo docker compose \
  -f api-gateway/docker-compose.gateway.yml up -d
```

La instancia EC2 debe contar con un rol IAM autorizado
para enviar mensajes a la cola SQS.

Si SQS_QUEUE_URL no está configurada, el microservicio
omite el envío a SQS y registra esta condición.

## 8. Prueba de integración

### Crear vehículo

```bash
curl -i -X POST http://localhost:8080/api/vehiculos \
-H "Content-Type: application/json" \
-d '{"patente":"ABCD12","marca":"Toyota","modelo":"Yaris","color":"Rojo","usuarioId":1}'
```

### Crear estacionamiento

```bash
curl -i -X POST http://localhost:8080/api/estacionamientos \
-H "Content-Type: application/json" \
-d '{"codigo":"AWS001"}'
```

### Realizar asignación

```bash
curl -i -X POST \
http://localhost:8080/api/estacionamientos/1/asignar \
-H "Content-Type: application/json" \
-d '{"vehiculoId":1}'
```

Los identificadores deben ajustarse según los registros
creados en cada ejecución.

### Consultar asignaciones

```bash
curl -i http://localhost:8080/api/asignaciones
```

### Consultar registros del productor

```bash
sudo docker service logs --since 10m \
smartpark_ms-estacionamiento 2>&1 | \
grep -E 'ENVIADO|Error al enviar|SQS'
```

## 9. Verificación en CloudWatch

Ingresar a Amazon CloudWatch y seleccionar el grupo:

/aws/lambda/smartpark-receptor-asignaciones

Buscar el registro MENSAJE_RECIBIDO y comparar los
identificadores messageId y pruebaId con los registrados
por el productor Java.

## 10. Resultados obtenidos

Prueba realizada el 24 de noviembre de 2026,
a las 23:59 horas de Chile.

- Solicitud HTTP: 200.
- Identificador de asignación: 1.
- Estacionamiento: AWS001.
- Estado del estacionamiento: OCUPADO.
- Estado de la asignación: ACTIVA.
- Envío desde Java a Amazon SQS: exitoso.
- Recepción y procesamiento por Lambda: exitoso.
- Registro en CloudWatch: exitoso.

Identificadores de seguimiento:

- pruebaId: 2434a235-982f-4360-8574-165a8b9c024a
- messageId: 49abaa28-7b81-431e-85f1-e19ff24c1a57

Ambos identificadores coinciden entre los registros
del productor Java y los registros de AWS Lambda.

## 11. Pruebas unitarias

Se ejecutaron las pruebas del microservicio de
estacionamientos mediante Maven.

- Pruebas ejecutadas: 11.
- Fallos: 0.
- Errores: 0.
- Pruebas omitidas: 0.
- Resultado: BUILD SUCCESS.

## 12. Alcance y consideraciones

La integración de extremo a extremo fue verificada
en una instancia EC2 con Docker Swarm.

Durante la prueba se ejecutaron cinco microservicios
y cinco bases de datos MySQL, con un total de
13 réplicas activas.

El despliegue probado utilizó un nodo manager.

La configuración mediante variables de entorno fue
validada localmente con docker stack config.
La prueba original en EC2 utilizó los valores de
configuración directamente en el archivo de despliegue.

La cola Standard ofrece entrega al menos una vez,
por lo que pueden existir mensajes duplicados.

El productor registra los errores de envío, pero
no implementa una recuperación persistente de mensajes
fallidos.

## 13. Conclusión

La integración demuestra el funcionamiento de una
arquitectura basada en microservicios, contenedores,
mensajería asíncrona y procesamiento serverless
en AWS.

Se verificó el flujo completo desde la solicitud
HTTP hasta el procesamiento del mensaje por AWS Lambda,
utilizando CloudWatch como evidencia de trazabilidad.