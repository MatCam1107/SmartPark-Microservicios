package cl.duoc.msnotificacion.controller;

import cl.duoc.msnotificacion.model.Notificacion;
import cl.duoc.msnotificacion.service.NotificacionService;
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
            @RequestBody Notificacion notificacion) {

        Notificacion nuevaNotificacion =
                notificacionService.guardarNotificacion(notificacion);

        return ResponseEntity.ok(nuevaNotificacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notificacion> actualizarNotificacion(
            @PathVariable Long id,
            @RequestBody Notificacion notificacion) {

        if (notificacionService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        notificacion.setId(id);

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