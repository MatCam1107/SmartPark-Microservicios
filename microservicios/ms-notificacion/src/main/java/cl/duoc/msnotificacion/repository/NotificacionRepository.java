package cl.duoc.msnotificacion.repository;

import cl.duoc.msnotificacion.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
}