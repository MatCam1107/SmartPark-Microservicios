package cl.duoc.msubicacion.service;

import cl.duoc.msubicacion.dto.UbicacionRequest;
import cl.duoc.msubicacion.exception.RecursoNoEncontradoException;
import cl.duoc.msubicacion.model.Ubicacion;
import cl.duoc.msubicacion.model.Zona;
import cl.duoc.msubicacion.repository.UbicacionRepository;
import cl.duoc.msubicacion.repository.ZonaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UbicacionServiceTest {

    @Mock
    private UbicacionRepository ubicacionRepository;

    @Mock
    private ZonaRepository zonaRepository;

    @InjectMocks
    private UbicacionService ubicacionService;

    private UbicacionRequest crearRequest() {
        UbicacionRequest request = new UbicacionRequest();
        request.setZonaId(1L);
        request.setEstacionamientoId(10L);
        request.setPiso(1);
        request.setSector("A-01");
        request.setLatitud(-33.45);
        request.setLongitud(-70.66);
        return request;
    }

    @Test
    void crearUbicacion_conZonaExistente_guardaLaUbicacion() {
        Zona zona = new Zona(1L, "Zona A", "Sector norte");
        when(zonaRepository.findById(1L)).thenReturn(Optional.of(zona));
        when(ubicacionRepository.save(any(Ubicacion.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Ubicacion resultado = ubicacionService.crearUbicacion(crearRequest());

        assertEquals(zona, resultado.getZona());
        assertEquals(10L, resultado.getEstacionamientoId());
        assertEquals("A-01", resultado.getSector());
        verify(ubicacionRepository).save(any(Ubicacion.class));
    }

    @Test
    void crearUbicacion_conZonaInexistente_lanzaNoEncontrado() {
        when(zonaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> ubicacionService.crearUbicacion(crearRequest()));

        verify(ubicacionRepository, never()).save(any(Ubicacion.class));
    }

    @Test
    void actualizarUbicacion_inexistente_lanzaNoEncontrado() {
        when(ubicacionRepository.existsById(9L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class,
                () -> ubicacionService.actualizarUbicacion(9L, crearRequest()));

        verify(ubicacionRepository, never()).save(any(Ubicacion.class));
    }

    @Test
    void buscarPorEstacionamiento_existente_devuelveLaUbicacion() {
        Ubicacion ubicacion = new Ubicacion(1L,
                new Zona(1L, "Zona A", "Sector norte"),
                10L, 1, "A-01", null, null);
        when(ubicacionRepository.findByEstacionamientoId(10L))
                .thenReturn(Optional.of(ubicacion));

        Optional<Ubicacion> resultado =
                ubicacionService.buscarPorEstacionamiento(10L);

        assertTrue(resultado.isPresent());
        assertEquals("A-01", resultado.get().getSector());
    }
}
