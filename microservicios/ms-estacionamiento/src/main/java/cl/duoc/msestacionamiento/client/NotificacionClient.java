package cl.duoc.msestacionamiento.client;

import cl.duoc.msestacionamiento.dto.NotificacionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.http.HttpClient;
import java.time.Duration;

@Component
public class NotificacionClient {

    private static final Logger logger =
            LoggerFactory.getLogger(NotificacionClient.class);

    private final RestClient restClient;

    public NotificacionClient(
            @Value("${notificacion.base-url}") String baseUrl) {

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(2))
                .build();

        JdkClientHttpRequestFactory factory =
                new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(3));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    /**
     * Envía la notificación. Si ms-notificacion no responde, solo se
     * registra el error: la asignación no debe fallar por una alerta.
     */
    public void enviar(NotificacionRequest notificacion) {
        try {
            restClient.post()
                    .uri("/api/notificaciones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(notificacion)
                    .retrieve()
                    .toBodilessEntity();
            logger.info("Notificación enviada al usuario {}",
                    notificacion.getUsuarioId());
        } catch (RestClientException e) {
            logger.warn("No se pudo enviar la notificación: {}", e.getMessage());
        }
    }
}
