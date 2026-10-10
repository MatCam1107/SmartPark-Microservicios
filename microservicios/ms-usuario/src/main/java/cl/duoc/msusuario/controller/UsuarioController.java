package cl.duoc.msusuario.controller;

import cl.duoc.msusuario.dto.UsuarioRequest;
import cl.duoc.msusuario.model.Usuario;
import cl.duoc.msusuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(
        name = "Usuarios",
        description = "Operaciones para la gestión de usuarios de SmartPark"
)
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(
            summary = "Listar usuarios",
            description = "Obtiene todos los usuarios registrados en SmartPark"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuarios obtenida correctamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Se requiere autenticación mediante JWT"
            )
    })
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @Operation(
            summary = "Buscar usuario por ID",
            description = "Obtiene un usuario utilizando su identificador"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Se requiere autenticación mediante JWT"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Crear usuario",
            description = "Registra un nuevo usuario en SmartPark"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario creado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos del usuario inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Se requiere autenticación mediante JWT"
            )
    })
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(
            @Valid @RequestBody UsuarioRequest request) {

        Usuario usuario = new Usuario(
                null,
                request.getNombre(),
                request.getApellido(),
                request.getEmail(),
                request.getPassword()
        );

        Usuario nuevoUsuario =
                usuarioService.guardarUsuario(usuario);

        return ResponseEntity.ok(nuevoUsuario);
    }

    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza los datos de un usuario existente"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario actualizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos del usuario inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Se requiere autenticación mediante JWT"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequest request) {

        if (usuarioService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = new Usuario(
                id,
                request.getNombre(),
                request.getApellido(),
                request.getEmail(),
                request.getPassword()
        );

        Usuario usuarioActualizado =
                usuarioService.guardarUsuario(usuario);

        return ResponseEntity.ok(usuarioActualizado);
    }

    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario utilizando su identificador"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuario eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Se requiere autenticación mediante JWT"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {

        if (usuarioService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        usuarioService.eliminarUsuario(id);

        return ResponseEntity.noContent().build();
    }
}