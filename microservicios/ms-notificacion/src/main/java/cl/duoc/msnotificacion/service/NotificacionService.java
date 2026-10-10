package cl.duoc.msnotificacion.service;

import cl.duoc.msnotificacion.model.Notificacion;
import cl.duoc.msnotificacion.repository.NotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificacionService {

    private static final Logger logger =
            LoggerFactory.getLogger(NotificacionService.class);

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public List<Notificacion> listarNotificaciones() {
        logger.info("Listando todas las notificaciones");
        return notificacionRepository.findAll();
    }

    public Optional<Notificacion> buscarPorId(Long id) {
        logger.info("Buscando notificación con ID: {}", id);
        return notificacionRepository.findById(id);
    }

    public Notificacion guardarNotificacion(Notificacion notificacion) {
        logger.info(
                "Guardando notificación para usuario ID: {}",
                notificacion.getUsuarioId()
        );

        return notificacionRepository.save(notificacion);
    }

    public void eliminarNotificacion(Long id) {
        logger.info("Eliminando notificación con ID: {}", id);
        notificacionRepository.deleteById(id);
    }
}