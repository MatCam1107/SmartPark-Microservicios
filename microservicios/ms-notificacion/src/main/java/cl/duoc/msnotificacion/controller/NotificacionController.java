package cl.duoc.msnotificacion.controller;

import cl.duoc.msnotificacion.dto.NotificacionRequest;
import cl.duoc.msnotificacion.model.Notificacion;
import cl.duoc.msnotificacion.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@Tag(
        name = "Notificaciones",
        description = "Operaciones para la gestión de notificaciones de SmartPark"
)
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @Operation(
            summary = "Listar notificaciones",
            description = "Obtiene todas las notificaciones registradas en SmartPark"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de notificaciones obtenida correctamente"
            )
    })
    @GetMapping
    public ResponseEntity<List<Notificacion>> listarNotificaciones() {
        return ResponseEntity.ok(
                notificacionService.listarNotificaciones()
        );
    }

    @Operation(
            summary = "Buscar notificación por ID",
            description = "Obtiene una notificación utilizando su identificador"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notificación encontrada"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notificación no encontrada"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> buscarPorId(
            @PathVariable Long id) {

        return notificacionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Crear notificación",
            description = "Registra una nueva notificación para un usuario"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notificación creada correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de la notificación inválidos"
            )
    })
    @PostMapping
    public ResponseEntity<Notificacion> crearNotificacion(
            @Valid @RequestBody NotificacionRequest request) {

        Notificacion notificacion = new Notificacion(
                null,
                request.getUsuarioId(),
                request.getMensaje(),
                request.getTipo(),
                request.getLeida()
        );

        Notificacion nuevaNotificacion =
                notificacionService.guardarNotificacion(notificacion);

        return ResponseEntity.ok(nuevaNotificacion);
    }

    @Operation(
            summary = "Actualizar notificación",
            description = "Actualiza los datos de una notificación existente"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notificación actualizada correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de la notificación inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notificación no encontrada"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Notificacion> actualizarNotificacion(
            @PathVariable Long id,
            @Valid @RequestBody NotificacionRequest request) {

        if (notificacionService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Notificacion notificacion = new Notificacion(
                id,
                request.getUsuarioId(),
                request.getMensaje(),
                request.getTipo(),
                request.getLeida()
        );

        Notificacion notificacionActualizada =
                notificacionService.guardarNotificacion(notificacion);

        return ResponseEntity.ok(notificacionActualizada);
    }

    @Operation(
            summary = "Eliminar notificación",
            description = "Elimina una notificación utilizando su identificador"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Notificación eliminada correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notificación no encontrada"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNotificacion(
            @PathVariable Long id) {

        if (notificacionService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        notificacionService.eliminarNotificacion(id);

        return ResponseEntity.noContent().build();
    }
}