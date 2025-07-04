package com.example.capitalDigital.usuario.Controller;

import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<?> guardarUsuario(@RequestBody Map<String, Object> datosUsuario) {
        try {
            UsuarioModel nuevoUsuario = usuarioServices.crearUsuario(datosUsuario);
            return ResponseEntity.ok(nuevoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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
                @RequestBody Map<String, Object> datosUsuario) {
            
            try {
                UsuarioModel usuarioGuardado = usuarioServices.actualizarUsuario(numeroDocumento, datosUsuario);
                return ResponseEntity.ok(usuarioGuardado);
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }

    @DeleteMapping("/numeroDocumento/{numeroDocumento}")
    public ResponseEntity<String> eliminarUsuarioPorNumeroDocumento(@PathVariable("numeroDocumento") String numeroDocumento) {
        boolean ok = usuarioServices.eliminarUsuarioPorNumeroDocumento(numeroDocumento);
        return ok ? ResponseEntity.ok("Se eliminó el usuario con numero de documento: " + numeroDocumento) : ResponseEntity.status(404).body("No se pudo eliminar el usuario con numero de documento: " + numeroDocumento);
    }
}
