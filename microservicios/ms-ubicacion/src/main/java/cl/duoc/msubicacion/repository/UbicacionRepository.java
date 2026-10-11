package cl.duoc.msubicacion.repository;

import cl.duoc.msubicacion.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Long> {

    Optional<Ubicacion> findByEstacionamientoId(Long estacionamientoId);

    List<Ubicacion> findByZonaId(Long zonaId);
}
