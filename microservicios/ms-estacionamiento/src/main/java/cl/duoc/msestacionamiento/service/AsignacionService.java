package cl.duoc.msestacionamiento.service;

import cl.duoc.msestacionamiento.client.NotificacionClient;
import cl.duoc.msestacionamiento.client.VehiculoClient;
import cl.duoc.msestacionamiento.dto.NotificacionRequest;
import cl.duoc.msestacionamiento.dto.VehiculoResponse;
import cl.duoc.msestacionamiento.exception.ConflictoException;
import cl.duoc.msestacionamiento.exception.RecursoNoEncontradoException;
import cl.duoc.msestacionamiento.model.Asignacion;
import cl.duoc.msestacionamiento.model.Estacionamiento;
import cl.duoc.msestacionamiento.model.EstadoAsignacion;
import cl.duoc.msestacionamiento.model.EstadoEstacionamiento;
import cl.duoc.msestacionamiento.repository.AsignacionRepository;
import cl.duoc.msestacionamiento.repository.EstacionamientoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AsignacionService {

    private static final Logger logger =
            LoggerFactory.getLogger(AsignacionService.class);

    private final AsignacionRepository asignacionRepository;
    private final EstacionamientoRepository estacionamientoRepository;
    private final VehiculoClient vehiculoClient;
    private final NotificacionClient notificacionClient;

    public AsignacionService(AsignacionRepository asignacionRepository,
                             EstacionamientoRepository estacionamientoRepository,
                             VehiculoClient vehiculoClient,
                             NotificacionClient notificacionClient) {
        this.asignacionRepository = asignacionRepository;
        this.estacionamientoRepository = estacionamientoRepository;
        this.vehiculoClient = vehiculoClient;
        this.notificacionClient = notificacionClient;
    }

    public List<Asignacion> listar() {
        logger.info("Listando todas las asignaciones");
        return asignacionRepository.findAll();
    }

    public List<Asignacion> listarPorEstado(EstadoAsignacion estado) {
        logger.info("Listando asignaciones con estado: {}", estado);
        return asignacionRepository.findByEstado(estado);
    }

    public Optional<Asignacion> buscarPorId(Long id) {
        logger.info("Buscando asignación con ID: {}", id);
        return asignacionRepository.findById(id);
    }

    @Transactional
    public Asignacion asignar(Long estacionamientoId, Long vehiculoId) {
        Estacionamiento estacionamiento = obtenerEstacionamiento(estacionamientoId);

        if (estacionamiento.getEstado() != EstadoEstacionamiento.DISPONIBLE) {
            throw new ConflictoException("El estacionamiento "
                    + estacionamiento.getCodigo()
                    + " no está disponible (estado: "
                    + estacionamiento.getEstado() + ")");
        }

        if (asignacionRepository.existsByVehiculoIdAndEstado(
                vehiculoId, EstadoAsignacion.ACTIVA)) {
            throw new ConflictoException(
                    "El vehículo ya tiene un estacionamiento asignado");
        }

        VehiculoResponse vehiculo = vehiculoClient.buscarPorId(vehiculoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Vehículo no encontrado con ID: " + vehiculoId));

        estacionamiento.setEstado(EstadoEstacionamiento.OCUPADO);
        estacionamientoRepository.save(estacionamiento);

        Asignacion asignacion = new Asignacion(
                null, estacionamiento, vehiculoId,
                LocalDateTime.now(), null, EstadoAsignacion.ACTIVA);
        Asignacion guardada = asignacionRepository.save(asignacion);

        logger.info("Estacionamiento {} asignado al vehículo {}",
                estacionamiento.getCodigo(), vehiculo.getPatente());

        NotificacionRequest notificacion = new NotificacionRequest(
                vehiculo.getUsuarioId(),
                "Se asignó el estacionamiento " + estacionamiento.getCodigo()
                        + " al vehículo " + vehiculo.getPatente(),
                "ASIGNACION",
                false);

        // La notificación se envía solo si la transacción se confirmó
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            notificacionClient.enviar(notificacion);
                        }
                    });
        } else {
            notificacionClient.enviar(notificacion);
        }

        return guardada;
    }

    @Transactional
    public Asignacion liberar(Long estacionamientoId) {
        Estacionamiento estacionamiento = obtenerEstacionamiento(estacionamientoId);

        Asignacion asignacion = asignacionRepository
                .findByEstacionamientoIdAndEstado(
                        estacionamientoId, EstadoAsignacion.ACTIVA)
                .orElseThrow(() -> new ConflictoException(
                        "El estacionamiento no tiene una asignación activa"));

        asignacion.setEstado(EstadoAsignacion.FINALIZADA);
        asignacion.setFechaTermino(LocalDateTime.now());

        estacionamiento.setEstado(EstadoEstacionamiento.DISPONIBLE);
        estacionamientoRepository.save(estacionamiento);

        logger.info("Estacionamiento {} liberado", estacionamiento.getCodigo());
        return asignacionRepository.save(asignacion);
    }

    private Estacionamiento obtenerEstacionamiento(Long id) {
        return estacionamientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estacionamiento no encontrado con ID: " + id));
    }
}
