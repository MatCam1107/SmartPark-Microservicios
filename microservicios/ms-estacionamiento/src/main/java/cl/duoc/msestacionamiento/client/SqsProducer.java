package cl.duoc.msestacionamiento.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;

@Component
public class SqsProducer {

    private static final Logger logger =
            LoggerFactory.getLogger(SqsProducer.class);

    private final ObjectMapper json = new ObjectMapper();

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    @Value("${aws.region}")
    private String region;

    public void enviarAsignacion(Long asignacionId) {

        if (queueUrl == null || queueUrl.isBlank()) {
            logger.info("Amazon SQS no está configurado");
            return;
        }

        String pruebaId = UUID.randomUUID().toString();

        try (DefaultCredentialsProvider credenciales =
                     DefaultCredentialsProvider.builder().build();
             SqsClient sqs = SqsClient.builder()
                     .credentialsProvider(credenciales)
                     .region(Region.of(region))
                     .build()) {

            String cuerpo = json.writeValueAsString(
                    Map.of(
                            "evento", "ESTACIONAMIENTO_ASIGNADO",
                            "asignacionId", asignacionId,
                            "pruebaId", pruebaId
                    )
            );

            String messageId = sqs.sendMessage(request ->
                    request.queueUrl(queueUrl)
                            .messageBody(cuerpo)
            ).messageId();

            logger.info(
                    "ENVIADO pruebaId={} messageId={}",
                    pruebaId, messageId
            );

        } catch (Exception e) {
            logger.error(
                    "Error al enviar asignación a SQS", e
            );
        }
    }
}