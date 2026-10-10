package cl.duoc.msvehiculo.controller;

import cl.duoc.msvehiculo.dto.VehiculoRequest;
import cl.duoc.msvehiculo.model.Vehiculo;
import cl.duoc.msvehiculo.service.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public ResponseEntity<List<Vehiculo>> listarVehiculos() {
        return ResponseEntity.ok(vehiculoService.listarVehiculos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> buscarPorId(@PathVariable Long id) {
        return vehiculoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Vehiculo> crearVehiculo(
            @Valid @RequestBody VehiculoRequest request) {

        Vehiculo vehiculo = new Vehiculo(
                null,
                request.getPatente(),
                request.getMarca(),
                request.getModelo(),
                request.getColor(),
                request.getUsuarioId()
        );

        Vehiculo nuevoVehiculo =
                vehiculoService.guardarVehiculo(vehiculo);

        return ResponseEntity.ok(nuevoVehiculo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehiculo> actualizarVehiculo(
            @PathVariable Long id,
            @Valid @RequestBody VehiculoRequest request) {

        if (vehiculoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Vehiculo vehiculo = new Vehiculo(
                id,
                request.getPatente(),
                request.getMarca(),
                request.getModelo(),
                request.getColor(),
                request.getUsuarioId()
        );

        Vehiculo vehiculoActualizado =
                vehiculoService.guardarVehiculo(vehiculo);

        return ResponseEntity.ok(vehiculoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable Long id) {

        if (vehiculoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        vehiculoService.eliminarVehiculo(id);

        return ResponseEntity.noContent().build();
    }
}