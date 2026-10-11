package cl.duoc.msubicacion.service;

import cl.duoc.msubicacion.model.Zona;
import cl.duoc.msubicacion.repository.ZonaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ZonaService {

    private static final Logger logger =
            LoggerFactory.getLogger(ZonaService.class);

    private final ZonaRepository zonaRepository;

    public ZonaService(ZonaRepository zonaRepository) {
        this.zonaRepository = zonaRepository;
    }

    public List<Zona> listarZonas() {
        logger.info("Listando todas las zonas");
        return zonaRepository.findAll();
    }

    public Optional<Zona> buscarPorId(Long id) {
        logger.info("Buscando zona con ID: {}", id);
        return zonaRepository.findById(id);
    }

    public Zona guardarZona(Zona zona) {
        logger.info("Guardando zona: {}", zona.getNombre());
        return zonaRepository.save(zona);
    }

    public void eliminarZona(Long id) {
        logger.info("Eliminando zona con ID: {}", id);
        zonaRepository.deleteById(id);
    }
}
