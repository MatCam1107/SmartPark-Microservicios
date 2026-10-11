package cl.duoc.msestacionamiento.controller;

import cl.duoc.msestacionamiento.dto.AsignarRequest;
import cl.duoc.msestacionamiento.dto.EstacionamientoRequest;
import cl.duoc.msestacionamiento.model.Asignacion;
import cl.duoc.msestacionamiento.model.Estacionamiento;
import cl.duoc.msestacionamiento.model.EstadoEstacionamiento;
import cl.duoc.msestacionamiento.service.AsignacionService;
import cl.duoc.msestacionamiento.service.EstacionamientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estacionamientos")
@Tag(
        name = "Estacionamientos",
        description = "Gestión de estacionamientos, disponibilidad y asignaciones"
)
public class EstacionamientoController {

    private final EstacionamientoService estacionamientoService;
    private final AsignacionService asignacionService;

    public EstacionamientoController(
            EstacionamientoService estacionamientoService,
            AsignacionService asignacionService) {
        this.estacionamientoService = estacionamientoService;
        this.asignacionService = asignacionService;
    }

    @Operation(summary = "Listar estacionamientos",
            description = "Obtiene todos los estacionamientos. Se puede filtrar por estado")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Lista obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<List<Estacionamiento>> listar(
            @RequestParam(required = false) EstadoEstacionamiento estado) {

        if (estado != null) {
            return ResponseEntity.ok(estacionamientoService.listarPorEstado(estado));
        }
        return ResponseEntity.ok(estacionamientoService.listar());
    }

    @Operation(summary = "Listar estacionamientos disponibles",
            description = "Obtiene los estacionamientos en estado DISPONIBLE")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Lista obtenida correctamente")
    })
    @GetMapping("/disponibles")
    public ResponseEntity<List<Estacionamiento>> listarDisponibles() {
        return ResponseEntity.ok(estacionamientoService
                .listarPorEstado(EstadoEstacionamiento.DISPONIBLE));
    }

    @Operation(summary = "Consultar estacionamiento por ID",
            description = "Obtiene un estacionamiento y su estado actual")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Estacionamiento encontrado"),
            @ApiResponse(responseCode = "404",
                    description = "Estacionamiento no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Estacionamiento> buscarPorId(@PathVariable Long id) {
        return estacionamientoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear estacionamiento",
            description = "Registra un estacionamiento. Si no se indica estado queda DISPONIBLE")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Estacionamiento creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409",
                    description = "Ya existe un estacionamiento con ese código")
    })
    @PostMapping
    public ResponseEntity<Estacionamiento> crear(
            @Valid @RequestBody EstacionamientoRequest request) {
        return ResponseEntity.ok(estacionamientoService.crear(request));
    }

    @Operation(summary = "Actualizar estacionamiento",
            description = "Actualiza código, estado o ubicación de un estacionamiento")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Estacionamiento actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404",
                    description = "Estacionamiento no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Estacionamiento> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EstacionamientoRequest request) {
        return ResponseEntity.ok(estacionamientoService.actualizar(id, request));
    }

    @Operation(summary = "Eliminar estacionamiento",
            description = "Elimina un estacionamiento que no tenga asignaciones registradas")
    @ApiResponses({
            @ApiResponse(responseCode = "204",
                    description = "Estacionamiento eliminado correctamente"),
            @ApiResponse(responseCode = "404",
                    description = "Estacionamiento no encontrado"),
            @ApiResponse(responseCode = "409",
                    description = "El estacionamiento tiene asignaciones asociadas")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        estacionamientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Asignar estacionamiento a un vehículo",
            description = "Valida el vehículo en ms-vehiculo, marca el estacionamiento como OCUPADO y notifica al usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Estacionamiento asignado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404",
                    description = "Estacionamiento o vehículo no encontrado"),
            @ApiResponse(responseCode = "409",
                    description = "Estacionamiento no disponible o vehículo ya asignado"),
            @ApiResponse(responseCode = "503",
                    description = "ms-vehiculo no está disponible")
    })
    @PostMapping("/{id}/asignar")
    public ResponseEntity<Asignacion> asignar(
            @PathVariable Long id,
            @Valid @RequestBody AsignarRequest request) {
        return ResponseEntity.ok(
                asignacionService.asignar(id, request.getVehiculoId()));
    }

    @Operation(summary = "Liberar estacionamiento",
            description = "Finaliza la asignación activa y deja el estacionamiento DISPONIBLE")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Estacionamiento liberado correctamente"),
            @ApiResponse(responseCode = "404",
                    description = "Estacionamiento no encontrado"),
            @ApiResponse(responseCode = "409",
                    description = "El estacionamiento no tiene una asignación activa")
    })
    @PostMapping("/{id}/liberar")
    public ResponseEntity<Asignacion> liberar(@PathVariable Long id) {
        return ResponseEntity.ok(asignacionService.liberar(id));
    }
}
