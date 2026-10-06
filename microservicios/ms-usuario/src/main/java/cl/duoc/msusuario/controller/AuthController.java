package cl.duoc.msusuario.controller;

import cl.duoc.msusuario.dto.LoginRequest;
import cl.duoc.msusuario.security.JwtService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserDetailsService usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthController(
            UserDetailsService usuarios,
            PasswordEncoder encoder,
            JwtService jwtService) {

        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public Map<String, String> login(
            @Valid @RequestBody LoginRequest request) {

        try {
            var user = usuarios.loadUserByUsername(request.username());

            if (!encoder.matches(request.password(), user.getPassword())) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciales invalidas"
                );
            }

            return Map.of(
                    "token", jwtService.generarToken(user.getUsername()),
                    "tipo", "Bearer"
            );

        } catch (UsernameNotFoundException ex) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales invalidas"
            );
        }
    }
}