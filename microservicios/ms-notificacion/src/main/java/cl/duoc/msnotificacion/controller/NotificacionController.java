package cl.duoc.msnotificacion.controller;

import cl.duoc.msnotificacion.dto.NotificacionRequest;
import cl.duoc.msnotificacion.model.Notificacion;
import cl.duoc.msnotificacion.service.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public ResponseEntity<List<Notificacion>> listarNotificaciones() {
        return ResponseEntity.ok(notificacionService.listarNotificaciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> buscarPorId(@PathVariable Long id) {
        return notificacionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNotificacion(@PathVariable Long id) {

        if (notificacionService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        notificacionService.eliminarNotificacion(id);

        return ResponseEntity.noContent().build();
    }
}