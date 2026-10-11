package cl.duoc.msestacionamiento.service;

import cl.duoc.msestacionamiento.dto.EstacionamientoRequest;
import cl.duoc.msestacionamiento.exception.RecursoNoEncontradoException;
import cl.duoc.msestacionamiento.model.Estacionamiento;
import cl.duoc.msestacionamiento.model.EstadoEstacionamiento;
import cl.duoc.msestacionamiento.repository.EstacionamientoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstacionamientoService {

    private static final Logger logger =
            LoggerFactory.getLogger(EstacionamientoService.class);

    private final EstacionamientoRepository estacionamientoRepository;

    public EstacionamientoService(
            EstacionamientoRepository estacionamientoRepository) {
        this.estacionamientoRepository = estacionamientoRepository;
    }

    public List<Estacionamiento> listar() {
        logger.info("Listando todos los estacionamientos");
        return estacionamientoRepository.findAll();
    }

    public List<Estacionamiento> listarPorEstado(EstadoEstacionamiento estado) {
        logger.info("Listando estacionamientos con estado: {}", estado);
        return estacionamientoRepository.findByEstado(estado);
    }

    public Optional<Estacionamiento> buscarPorId(Long id) {
        logger.info("Buscando estacionamiento con ID: {}", id);
        return estacionamientoRepository.findById(id);
    }

    public Estacionamiento crear(EstacionamientoRequest request) {
        EstadoEstacionamiento estado = request.getEstado() != null
                ? request.getEstado()
                : EstadoEstacionamiento.DISPONIBLE;

        Estacionamiento estacionamiento = new Estacionamiento(
                null, request.getCodigo(), estado, request.getUbicacionId());

        logger.info("Creando estacionamiento con código: {}", request.getCodigo());
        return estacionamientoRepository.save(estacionamiento);
    }

    public Estacionamiento actualizar(Long id, EstacionamientoRequest request) {
        Estacionamiento actual = estacionamientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estacionamiento no encontrado con ID: " + id));

        actual.setCodigo(request.getCodigo());
        actual.setUbicacionId(request.getUbicacionId());
        if (request.getEstado() != null) {
            actual.setEstado(request.getEstado());
        }

        logger.info("Actualizando estacionamiento con ID: {}", id);
        return estacionamientoRepository.save(actual);
    }

    public void eliminar(Long id) {
        if (!estacionamientoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(
                    "Estacionamiento no encontrado con ID: " + id);
        }
        logger.info("Eliminando estacionamiento con ID: {}", id);
        estacionamientoRepository.deleteById(id);
    }
}
