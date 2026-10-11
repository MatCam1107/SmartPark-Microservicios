package cl.duoc.msestacionamiento.service;

import cl.duoc.msestacionamiento.dto.EstacionamientoRequest;
import cl.duoc.msestacionamiento.exception.RecursoNoEncontradoException;
import cl.duoc.msestacionamiento.model.Estacionamiento;
import cl.duoc.msestacionamiento.model.EstadoEstacionamiento;
import cl.duoc.msestacionamiento.repository.EstacionamientoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstacionamientoServiceTest {

    @Mock
    private EstacionamientoRepository estacionamientoRepository;

    @InjectMocks
    private EstacionamientoService estacionamientoService;

    @Test
    void crear_sinEstado_quedaDisponible() {
        EstacionamientoRequest request = new EstacionamientoRequest();
        request.setCodigo("A-01");
        when(estacionamientoRepository.save(any(Estacionamiento.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Estacionamiento resultado = estacionamientoService.crear(request);

        assertEquals("A-01", resultado.getCodigo());
        assertEquals(EstadoEstacionamiento.DISPONIBLE, resultado.getEstado());
    }

    @Test
    void actualizar_inexistente_lanzaNoEncontrado() {
        EstacionamientoRequest request = new EstacionamientoRequest();
        request.setCodigo("A-01");
        when(estacionamientoRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> estacionamientoService.actualizar(9L, request));

        verify(estacionamientoRepository, never()).save(any(Estacionamiento.class));
    }

    @Test
    void eliminar_inexistente_lanzaNoEncontrado() {
        when(estacionamientoRepository.existsById(9L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class,
                () -> estacionamientoService.eliminar(9L));

        verify(estacionamientoRepository, never()).deleteById(any(Long.class));
    }
}
