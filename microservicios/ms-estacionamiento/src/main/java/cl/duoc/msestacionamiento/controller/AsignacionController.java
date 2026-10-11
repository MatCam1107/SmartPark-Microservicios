package cl.duoc.msestacionamiento.controller;

import cl.duoc.msestacionamiento.model.Asignacion;
import cl.duoc.msestacionamiento.model.EstadoAsignacion;
import cl.duoc.msestacionamiento.service.AsignacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
@Tag(
        name = "Asignaciones",
        description = "Consulta del historial de asignaciones de estacionamientos"
)
public class AsignacionController {

    private final AsignacionService asignacionService;

    public AsignacionController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    @Operation(summary = "Listar asignaciones",
            description = "Obtiene todas las asignaciones. Se puede filtrar por estado")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Lista obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<List<Asignacion>> listar(
            @RequestParam(required = false) EstadoAsignacion estado) {

        if (estado != null) {
            return ResponseEntity.ok(asignacionService.listarPorEstado(estado));
        }
        return ResponseEntity.ok(asignacionService.listar());
    }

    @Operation(summary = "Buscar asignación por ID",
            description = "Obtiene una asignación utilizando su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asignación encontrada"),
            @ApiResponse(responseCode = "404", description = "Asignación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Asignacion> buscarPorId(@PathVariable Long id) {
        return asignacionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
