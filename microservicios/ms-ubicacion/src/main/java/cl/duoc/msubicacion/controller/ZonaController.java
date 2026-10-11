package cl.duoc.msubicacion.controller;

import cl.duoc.msubicacion.dto.ZonaRequest;
import cl.duoc.msubicacion.model.Zona;
import cl.duoc.msubicacion.service.ZonaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zonas")
@Tag(
        name = "Zonas",
        description = "Operaciones para la gestión de zonas de SmartPark"
)
public class ZonaController {

    private final ZonaService zonaService;

    public ZonaController(ZonaService zonaService) {
        this.zonaService = zonaService;
    }

    @Operation(summary = "Listar zonas",
            description = "Obtiene todas las zonas registradas")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Lista de zonas obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<List<Zona>> listarZonas() {
        return ResponseEntity.ok(zonaService.listarZonas());
    }

    @Operation(summary = "Buscar zona por ID",
            description = "Obtiene una zona utilizando su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Zona encontrada"),
            @ApiResponse(responseCode = "404", description = "Zona no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Zona> buscarPorId(@PathVariable Long id) {
        return zonaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear zona",
            description = "Registra una nueva zona")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Zona creada correctamente"),
            @ApiResponse(responseCode = "400",
                    description = "Datos de la zona inválidos"),
            @ApiResponse(responseCode = "409",
                    description = "Ya existe una zona con ese nombre")
    })
    @PostMapping
    public ResponseEntity<Zona> crearZona(
            @Valid @RequestBody ZonaRequest request) {

        Zona zona = new Zona(null, request.getNombre(),
                request.getDescripcion());

        return ResponseEntity.ok(zonaService.guardarZona(zona));
    }

    @Operation(summary = "Actualizar zona",
            description = "Actualiza los datos de una zona existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Zona actualizada correctamente"),
            @ApiResponse(responseCode = "400",
                    description = "Datos de la zona inválidos"),
            @ApiResponse(responseCode = "404", description = "Zona no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Zona> actualizarZona(
            @PathVariable Long id,
            @Valid @RequestBody ZonaRequest request) {

        if (zonaService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Zona zona = new Zona(id, request.getNombre(),
                request.getDescripcion());

        return ResponseEntity.ok(zonaService.guardarZona(zona));
    }

    @Operation(summary = "Eliminar zona",
            description = "Elimina una zona si no tiene ubicaciones asociadas")
    @ApiResponses({
            @ApiResponse(responseCode = "204",
                    description = "Zona eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Zona no encontrada"),
            @ApiResponse(responseCode = "409",
                    description = "La zona tiene ubicaciones asociadas")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarZona(@PathVariable Long id) {

        if (zonaService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        zonaService.eliminarZona(id);

        return ResponseEntity.noContent().build();
    }
}
