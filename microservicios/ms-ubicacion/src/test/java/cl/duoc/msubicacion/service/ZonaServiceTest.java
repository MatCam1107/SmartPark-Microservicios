package cl.duoc.msubicacion.service;

import cl.duoc.msubicacion.model.Zona;
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
class ZonaServiceTest {

    @Mock
    private ZonaRepository zonaRepository;

    @InjectMocks
    private ZonaService zonaService;

    @Test
    void guardarZona_devuelveLaZonaGuardada() {
        Zona nueva = new Zona(null, "Zona A", "Sector norte");
        when(zonaRepository.save(nueva))
                .thenReturn(new Zona(1L, "Zona A", "Sector norte"));

        Zona resultado = zonaService.guardarZona(nueva);

        assertEquals(1L, resultado.getId());
        assertEquals("Zona A", resultado.getNombre());
    }

    @Test
    void buscarPorId_inexistente_devuelveVacio() {
        when(zonaRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(zonaService.buscarPorId(99L).isEmpty());
    }
}
