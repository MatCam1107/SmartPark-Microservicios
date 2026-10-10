package cl.duoc.msvehiculo.repository;

import cl.duoc.msvehiculo.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
}