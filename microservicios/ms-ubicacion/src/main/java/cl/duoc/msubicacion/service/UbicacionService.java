package cl.duoc.msubicacion.service;

import cl.duoc.msubicacion.dto.UbicacionRequest;
import cl.duoc.msubicacion.exception.RecursoNoEncontradoException;
import cl.duoc.msubicacion.model.Ubicacion;
import cl.duoc.msubicacion.model.Zona;
import cl.duoc.msubicacion.repository.UbicacionRepository;
import cl.duoc.msubicacion.repository.ZonaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UbicacionService {

    private static final Logger logger =
            LoggerFactory.getLogger(UbicacionService.class);

    private final UbicacionRepository ubicacionRepository;
    private final ZonaRepository zonaRepository;

    public UbicacionService(UbicacionRepository ubicacionRepository,
                            ZonaRepository zonaRepository) {
        this.ubicacionRepository = ubicacionRepository;
        this.zonaRepository = zonaRepository;
    }

    public List<Ubicacion> listarUbicaciones() {
        logger.info("Listando todas las ubicaciones");
        return ubicacionRepository.findAll();
    }

    public List<Ubicacion> listarPorZona(Long zonaId) {
        logger.info("Listando ubicaciones de la zona con ID: {}", zonaId);
        return ubicacionRepository.findByZonaId(zonaId);
    }

    public Optional<Ubicacion> buscarPorId(Long id) {
        logger.info("Buscando ubicación con ID: {}", id);
        return ubicacionRepository.findById(id);
    }

    public Optional<Ubicacion> buscarPorEstacionamiento(Long estacionamientoId) {
        logger.info("Buscando ubicación del estacionamiento con ID: {}",
                estacionamientoId);
        return ubicacionRepository.findByEstacionamientoId(estacionamientoId);
    }

    public Ubicacion crearUbicacion(UbicacionRequest request) {
        return guardar(null, request);
    }

    public Ubicacion actualizarUbicacion(Long id, UbicacionRequest request) {
        if (!ubicacionRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(
                    "Ubicación no encontrada con ID: " + id);
        }
        return guardar(id, request);
    }

    public void eliminarUbicacion(Long id) {
        logger.info("Eliminando ubicación con ID: {}", id);
        ubicacionRepository.deleteById(id);
    }

    private Ubicacion guardar(Long id, UbicacionRequest request) {
        Zona zona = zonaRepository.findById(request.getZonaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Zona no encontrada con ID: " + request.getZonaId()));

        Ubicacion ubicacion = new Ubicacion(
                id,
                zona,
                request.getEstacionamientoId(),
                request.getPiso(),
                request.getSector(),
                request.getLatitud(),
                request.getLongitud()
        );

        logger.info("Guardando ubicación del estacionamiento: {}",
                request.getEstacionamientoId());
        return ubicacionRepository.save(ubicacion);
    }
}
