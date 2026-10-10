package cl.duoc.msvehiculo.service;

import cl.duoc.msvehiculo.model.Vehiculo;
import cl.duoc.msvehiculo.repository.VehiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehiculoService {

    private static final Logger logger =
            LoggerFactory.getLogger(VehiculoService.class);

    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<Vehiculo> listarVehiculos() {
        logger.info("Listando todos los vehículos");
        return vehiculoRepository.findAll();
    }

    public Optional<Vehiculo> buscarPorId(Long id) {
        logger.info("Buscando vehículo con ID: {}", id);
        return vehiculoRepository.findById(id);
    }

    public Vehiculo guardarVehiculo(Vehiculo vehiculo) {
        logger.info("Guardando vehículo con patente: {}", vehiculo.getPatente());
        return vehiculoRepository.save(vehiculo);
    }

    public void eliminarVehiculo(Long id) {
        logger.info("Eliminando vehículo con ID: {}", id);
        vehiculoRepository.deleteById(id);
    }
}
