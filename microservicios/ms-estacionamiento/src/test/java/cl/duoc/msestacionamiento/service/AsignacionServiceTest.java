package cl.duoc.msestacionamiento.service;

import cl.duoc.msestacionamiento.client.NotificacionClient;
import cl.duoc.msestacionamiento.client.VehiculoClient;
import cl.duoc.msestacionamiento.dto.NotificacionRequest;
import cl.duoc.msestacionamiento.dto.VehiculoResponse;
import cl.duoc.msestacionamiento.exception.ConflictoException;
import cl.duoc.msestacionamiento.exception.RecursoNoEncontradoException;
import cl.duoc.msestacionamiento.exception.ServicioNoDisponibleException;
import cl.duoc.msestacionamiento.model.Asignacion;
import cl.duoc.msestacionamiento.model.Estacionamiento;
import cl.duoc.msestacionamiento.model.EstadoAsignacion;
import cl.duoc.msestacionamiento.model.EstadoEstacionamiento;
import cl.duoc.msestacionamiento.repository.AsignacionRepository;
import cl.duoc.msestacionamiento.repository.EstacionamientoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignacionServiceTest {

    @Mock
    private AsignacionRepository asignacionRepository;

    @Mock
    private EstacionamientoRepository estacionamientoRepository;

    @Mock
    private VehiculoClient vehiculoClient;

    @Mock
    private NotificacionClient notificacionClient;

    @InjectMocks
    private AsignacionService asignacionService;

    private Estacionamiento crearEstacionamiento(EstadoEstacionamiento estado) {
        return new Estacionamiento(1L, "A-01", estado, 1L);
    }

    private VehiculoResponse crearVehiculo() {
        VehiculoResponse vehiculo = new VehiculoResponse();
        vehiculo.setId(5L);
        vehiculo.setPatente("ABCD12");
        vehiculo.setUsuarioId(3L);
        return vehiculo;
    }

    @Test
    void asignar_conDatosValidos_ocupaElEstacionamientoYNotifica() {
        Estacionamiento estacionamiento =
                crearEstacionamiento(EstadoEstacionamiento.DISPONIBLE);
        when(estacionamientoRepository.findById(1L))
                .thenReturn(Optional.of(estacionamiento));
        when(asignacionRepository.existsByVehiculoIdAndEstado(
                5L, EstadoAsignacion.ACTIVA)).thenReturn(false);
        when(vehiculoClient.buscarPorId(5L))
                .thenReturn(Optional.of(crearVehiculo()));
        when(asignacionRepository.save(any(Asignacion.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Asignacion resultado = asignacionService.asignar(1L, 5L);

        assertEquals(EstadoAsignacion.ACTIVA, resultado.getEstado());
        assertEquals(EstadoEstacionamiento.OCUPADO, estacionamiento.getEstado());
        assertEquals(5L, resultado.getVehiculoId());
        assertNotNull(resultado.getFechaInicio());
        assertNull(resultado.getFechaTermino());
        verify(estacionamientoRepository).save(estacionamiento);
        verify(notificacionClient).enviar(any(NotificacionRequest.class));
    }

    @Test
    void asignar_estacionamientoInexistente_lanzaNoEncontrado() {
        when(estacionamientoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> asignacionService.asignar(1L, 5L));

        verifyNoInteractions(vehiculoClient, notificacionClient);
    }

    @Test
    void asignar_estacionamientoOcupado_lanzaConflicto() {
        when(estacionamientoRepository.findById(1L)).thenReturn(
                Optional.of(crearEstacionamiento(EstadoEstacionamiento.OCUPADO)));

        assertThrows(ConflictoException.class,
                () -> asignacionService.asignar(1L, 5L));

        verifyNoInteractions(vehiculoClient, notificacionClient);
    }

    @Test
    void asignar_vehiculoConAsignacionActiva_lanzaConflicto() {
        when(estacionamientoRepository.findById(1L)).thenReturn(
                Optional.of(crearEstacionamiento(EstadoEstacionamiento.DISPONIBLE)));
        when(asignacionRepository.existsByVehiculoIdAndEstado(
                5L, EstadoAsignacion.ACTIVA)).thenReturn(true);

        assertThrows(ConflictoException.class,
                () -> asignacionService.asignar(1L, 5L));

        verifyNoInteractions(vehiculoClient, notificacionClient);
    }

    @Test
    void asignar_vehiculoInexistente_lanzaNoEncontradoYNoOcupa() {
        when(estacionamientoRepository.findById(1L)).thenReturn(
                Optional.of(crearEstacionamiento(EstadoEstacionamiento.DISPONIBLE)));
        when(asignacionRepository.existsByVehiculoIdAndEstado(
                5L, EstadoAsignacion.ACTIVA)).thenReturn(false);
        when(vehiculoClient.buscarPorId(5L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> asignacionService.asignar(1L, 5L));

        verify(estacionamientoRepository, never()).save(any(Estacionamiento.class));
        verifyNoInteractions(notificacionClient);
    }

    @Test
    void asignar_msVehiculoCaido_propagaServicioNoDisponible() {
        when(estacionamientoRepository.findById(1L)).thenReturn(
                Optional.of(crearEstacionamiento(EstadoEstacionamiento.DISPONIBLE)));
        when(asignacionRepository.existsByVehiculoIdAndEstado(
                5L, EstadoAsignacion.ACTIVA)).thenReturn(false);
        when(vehiculoClient.buscarPorId(5L)).thenThrow(
                new ServicioNoDisponibleException("ms-vehiculo no disponible"));

        assertThrows(ServicioNoDisponibleException.class,
                () -> asignacionService.asignar(1L, 5L));

        verify(estacionamientoRepository, never()).save(any(Estacionamiento.class));
        verifyNoInteractions(notificacionClient);
    }

    @Test
    void liberar_conAsignacionActiva_finalizaYDejaDisponible() {
        Estacionamiento estacionamiento =
                crearEstacionamiento(EstadoEstacionamiento.OCUPADO);
        Asignacion asignacion = new Asignacion(1L, estacionamiento, 5L,
                LocalDateTime.now(), null, EstadoAsignacion.ACTIVA);
        when(estacionamientoRepository.findById(1L))
                .thenReturn(Optional.of(estacionamiento));
        when(asignacionRepository.findByEstacionamientoIdAndEstado(
                1L, EstadoAsignacion.ACTIVA)).thenReturn(Optional.of(asignacion));
        when(asignacionRepository.save(any(Asignacion.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Asignacion resultado = asignacionService.liberar(1L);

        assertEquals(EstadoAsignacion.FINALIZADA, resultado.getEstado());
        assertNotNull(resultado.getFechaTermino());
        assertEquals(EstadoEstacionamiento.DISPONIBLE, estacionamiento.getEstado());
    }

    @Test
    void liberar_sinAsignacionActiva_lanzaConflicto() {
        when(estacionamientoRepository.findById(1L)).thenReturn(
                Optional.of(crearEstacionamiento(EstadoEstacionamiento.DISPONIBLE)));
        when(asignacionRepository.findByEstacionamientoIdAndEstado(
                1L, EstadoAsignacion.ACTIVA)).thenReturn(Optional.empty());

        assertThrows(ConflictoException.class,
                () -> asignacionService.liberar(1L));
    }
}
