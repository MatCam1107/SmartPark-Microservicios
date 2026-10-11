package cl.duoc.msubicacion.controller;

import cl.duoc.msubicacion.dto.UbicacionRequest;
import cl.duoc.msubicacion.model.Ubicacion;
import cl.duoc.msubicacion.service.UbicacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ubicaciones")
@Tag(
        name = "Ubicaciones",
        description = "Operaciones para la gestión de ubicaciones de estacionamientos"
)
public class UbicacionController {

    private final UbicacionService ubicacionService;

    public UbicacionController(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    @Operation(summary = "Listar ubicaciones",
            description = "Obtiene todas las ubicaciones. Se puede filtrar por zona con el parámetro zonaId")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Lista de ubicaciones obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<List<Ubicacion>> listarUbicaciones(
            @RequestParam(required = false) Long zonaId) {

        if (zonaId != null) {
            return ResponseEntity.ok(ubicacionService.listarPorZona(zonaId));
        }
        return ResponseEntity.ok(ubicacionService.listarUbicaciones());
    }

    @Operation(summary = "Buscar ubicación por ID",
            description = "Obtiene una ubicación utilizando su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ubicación encontrada"),
            @ApiResponse(responseCode = "404", description = "Ubicación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Ubicacion> buscarPorId(@PathVariable Long id) {
        return ubicacionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Consultar ubicación de un estacionamiento",
            description = "Obtiene la ubicación asociada al ID de un estacionamiento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ubicación encontrada"),
            @ApiResponse(responseCode = "404",
                    description = "El estacionamiento no tiene ubicación registrada")
    })
    @GetMapping("/estacionamiento/{estacionamientoId}")
    public ResponseEntity<Ubicacion> buscarPorEstacionamiento(
            @PathVariable Long estacionamientoId) {

        return ubicacionService.buscarPorEstacionamiento(estacionamientoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear ubicación",
            description = "Registra la ubicación de un estacionamiento dentro de una zona")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Ubicación creada correctamente"),
            @ApiResponse(responseCode = "400",
                    description = "Datos de la ubicación inválidos"),
            @ApiResponse(responseCode = "404", description = "Zona no encontrada"),
            @ApiResponse(responseCode = "409",
                    description = "El estacionamiento ya tiene una ubicación")
    })
    @PostMapping
    public ResponseEntity<Ubicacion> crearUbicacion(
            @Valid @RequestBody UbicacionRequest request) {

        return ResponseEntity.ok(ubicacionService.crearUbicacion(request));
    }

    @Operation(summary = "Actualizar ubicación",
            description = "Actualiza los datos de una ubicación existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Ubicación actualizada correctamente"),
            @ApiResponse(responseCode = "400",
                    description = "Datos de la ubicación inválidos"),
            @ApiResponse(responseCode = "404",
                    description = "Ubicación o zona no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Ubicacion> actualizarUbicacion(
            @PathVariable Long id,
            @Valid @RequestBody UbicacionRequest request) {

        return ResponseEntity.ok(
                ubicacionService.actualizarUbicacion(id, request));
    }

    @Operation(summary = "Eliminar ubicación",
            description = "Elimina una ubicación utilizando su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "204",
                    description = "Ubicación eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Ubicación no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUbicacion(@PathVariable Long id) {

        if (ubicacionService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ubicacionService.eliminarUbicacion(id);

        return ResponseEntity.noContent().build();
    }
}
