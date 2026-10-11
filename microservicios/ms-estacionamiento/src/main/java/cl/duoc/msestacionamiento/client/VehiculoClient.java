package cl.duoc.msestacionamiento.client;

import cl.duoc.msestacionamiento.dto.VehiculoResponse;
import cl.duoc.msestacionamiento.exception.ServicioNoDisponibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;

@Component
public class VehiculoClient {

    private static final Logger logger =
            LoggerFactory.getLogger(VehiculoClient.class);

    private final RestClient restClient;

    public VehiculoClient(@Value("${vehiculo.base-url}") String baseUrl) {
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

    public Optional<VehiculoResponse> buscarPorId(Long id) {
        logger.info("Consultando vehículo con ID {} en ms-vehiculo", id);
        try {
            VehiculoResponse vehiculo = restClient.get()
                    .uri("/api/vehiculos/{id}", id)
                    .retrieve()
                    .body(VehiculoResponse.class);
            return Optional.ofNullable(vehiculo);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            logger.error("No se pudo consultar ms-vehiculo: {}", e.getMessage());
            throw new ServicioNoDisponibleException(
                    "El servicio de vehículos no está disponible");
        }
    }
}
