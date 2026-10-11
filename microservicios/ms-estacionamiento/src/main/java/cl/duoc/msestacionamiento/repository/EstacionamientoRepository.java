package cl.duoc.msestacionamiento.repository;

import cl.duoc.msestacionamiento.model.Estacionamiento;
import cl.duoc.msestacionamiento.model.EstadoEstacionamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstacionamientoRepository
        extends JpaRepository<Estacionamiento, Long> {

    List<Estacionamiento> findByEstado(EstadoEstacionamiento estado);
}
