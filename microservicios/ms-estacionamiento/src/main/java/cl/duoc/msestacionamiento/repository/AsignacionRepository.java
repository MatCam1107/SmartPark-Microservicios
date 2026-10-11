package cl.duoc.msestacionamiento.repository;

import cl.duoc.msestacionamiento.model.Asignacion;
import cl.duoc.msestacionamiento.model.EstadoAsignacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    List<Asignacion> findByEstado(EstadoAsignacion estado);

    boolean existsByVehiculoIdAndEstado(Long vehiculoId, EstadoAsignacion estado);

    Optional<Asignacion> findByEstacionamientoIdAndEstado(
            Long estacionamientoId, EstadoAsignacion estado);
}
