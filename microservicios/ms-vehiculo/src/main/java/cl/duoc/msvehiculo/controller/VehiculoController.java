package cl.duoc.msvehiculo.controller;

import cl.duoc.msvehiculo.dto.VehiculoRequest;
import cl.duoc.msvehiculo.model.Vehiculo;
import cl.duoc.msvehiculo.service.VehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@Tag(
        name = "Vehículos",
        description = "Operaciones para la gestión de vehículos de SmartPark"
)
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @Operation(
            summary = "Listar vehículos",
            description = "Obtiene todos los vehículos registrados en SmartPark"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Lista de vehículos obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<List<Vehiculo>> listarVehiculos() {
        return ResponseEntity.ok(vehiculoService.listarVehiculos());
    }

    @Operation(
            summary = "Buscar vehículo por ID",
            description = "Obtiene un vehículo utilizando su identificador"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Vehículo encontrado"),
            @ApiResponse(responseCode = "404",
                    description = "Vehículo no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> buscarPorId(@PathVariable Long id) {
        return vehiculoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Crear vehículo",
            description = "Registra un nuevo vehículo asociado a un usuario"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Vehículo creado correctamente"),
            @ApiResponse(responseCode = "400",
                    description = "Datos del vehículo inválidos")
    })
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

    @Operation(
            summary = "Actualizar vehículo",
            description = "Actualiza los datos de un vehículo existente"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Vehículo actualizado correctamente"),
            @ApiResponse(responseCode = "400",
                    description = "Datos del vehículo inválidos"),
            @ApiResponse(responseCode = "404",
                    description = "Vehículo no encontrado")
    })
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

    @Operation(
            summary = "Eliminar vehículo",
            description = "Elimina un vehículo utilizando su identificador"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204",
                    description = "Vehículo eliminado correctamente"),
            @ApiResponse(responseCode = "404",
                    description = "Vehículo no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable Long id) {

        if (vehiculoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        vehiculoService.eliminarVehiculo(id);

        return ResponseEntity.noContent().build();
    }
}