package com.example.capitalDigital.usuario.Controller;

import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.HashMap;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.example.capitalDigital.usuario.models.UsuarioModel;
import com.example.capitalDigital.usuario.services.UsuarioServices;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {

    @Autowired
    UsuarioServices usuarioServices;

    @PostMapping()
    public ResponseEntity<?> guardarUsuario(@Valid @RequestBody UsuarioModel usuario, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }
        try {
            UsuarioModel nuevoUsuario = usuarioServices.guardarUsuario(usuario);
            return ResponseEntity.ok(nuevoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); // Devuelve error si el email ya está en uso
        }
    }


    @GetMapping("/numeroDocumento/{numeroDocumento}")
    public ResponseEntity<?> obtenerUsuarioPorNumeroDocumento(@PathVariable String numeroDocumento) {
        System.out.println("Recibí en el controlador: " + numeroDocumento);
        Optional<UsuarioModel> usuario = usuarioServices.obtenerPorNumeroDocumento(numeroDocumento);
        UsuarioModel usuarioEncontrado = usuario.get();
        if (!usuarioEncontrado.getActivo() && usuarioEncontrado.getFechaBloqueo() != null) {
            LocalDateTime ahora = LocalDateTime.now();
            /* asignar 24 horas ---->>> ahora.isAfter(usuarioEncontrado.getFechaBloqueo().plusHours(24)*/
            if (ahora.isAfter(usuarioEncontrado.getFechaBloqueo().plusSeconds(60))) {
                usuarioEncontrado.setActivo(true);
                usuarioEncontrado.setFechaBloqueo(null);
                usuarioServices.guardarUsuario(usuarioEncontrado);
            }
        }
        return ResponseEntity.ok(usuarioEncontrado);

    }
    @PutMapping("/numeroDocumento/{numeroDocumento}")
    public ResponseEntity<?> modificarUsuarioPorNumeroDocumento(
            @PathVariable("numeroDocumento") String numeroDocumento,
            @Valid @RequestBody UsuarioModel usuarioActualizado,
            BindingResult result) {

        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }

        Optional<UsuarioModel> usuarioExistente = usuarioServices.obtenerPorNumeroDocumento(numeroDocumento);
        if (usuarioExistente.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado.");
        }

        UsuarioModel usuario = usuarioExistente.get();

        // 🛠️ Actualizar todos los campos excepto el estado de bloqueo aún
        usuario.setNombre(usuarioActualizado.getNombre());
        usuario.setApellido(usuarioActualizado.getApellido());
        usuario.setDocumento(usuarioActualizado.getDocumento());
        usuario.setNumeroDocumento(usuarioActualizado.getNumeroDocumento());
        usuario.setFechaNacimiento(usuarioActualizado.getFechaNacimiento());
        usuario.setDireccion(usuarioActualizado.getDireccion());
        usuario.setEmail(usuarioActualizado.getEmail());
        usuario.setCodigoPostal(usuarioActualizado.getCodigoPostal());
        usuario.setBanco(usuarioActualizado.getBanco());
        usuario.setNumeroCuenta(usuarioActualizado.getNumeroCuenta());
        usuario.setPassword(usuarioActualizado.getPassword());

        // 🔒 Si el usuario se bloquea ahora
        if (!usuarioActualizado.getActivo() && usuario.getActivo()) {
            usuario.setActivo(false);
            usuario.setFechaBloqueo(LocalDateTime.now());
        } else {
            usuario.setActivo(usuarioActualizado.getActivo());
        }

        UsuarioModel usuarioGuardado = usuarioServices.guardarUsuario(usuario);
        return ResponseEntity.ok(usuarioGuardado);
    }

    @DeleteMapping("/numeroDocumento/{numeroDocumento}")
    public ResponseEntity<String> eliminarUsuarioPorNumeroDocumento(@PathVariable("numeroDocumento") String numeroDocumento) {
        boolean ok = usuarioServices.eliminarUsuarioPorNumeroDocumento(numeroDocumento);
        return ok ? ResponseEntity.ok("Se eliminó el usuario con numero de documento: " + numeroDocumento) : ResponseEntity.status(404).body("No se pudo eliminar el usuario con numero de documento: " + numeroDocumento);
    }
}
