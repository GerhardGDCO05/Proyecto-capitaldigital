package com.example.capitalDigital.usuario.Controller;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
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

    @GetMapping()
    public ResponseEntity<ArrayList<UsuarioModel>> obtenerUsuarios() {
        return ResponseEntity.ok(usuarioServices.obtenerUsuarios());
    }

    @PostMapping()
    public ResponseEntity<?> guardarUsuario(@Valid @RequestBody UsuarioModel usuario, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }

        UsuarioModel nuevoUsuario = usuarioServices.guardarUsuario(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> obtenerUsuarioPorEmail(@PathVariable String email) {
        System.out.println("Recibí en el controlador: " + email);
        Optional<UsuarioModel> usuario = usuarioServices.obtenerPorEmail(email);
        return usuario.isPresent() ? ResponseEntity.ok(usuario.get()) : ResponseEntity.status(404).body("Usuario no encontrado.");
    }

    @PutMapping("/email/{email}")
    public ResponseEntity<?> modificarUsuarioPorEmail(@PathVariable("email") String email, @Valid @RequestBody UsuarioModel usuarioActualizado, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }

        Optional<UsuarioModel> usuarioExistente = usuarioServices.obtenerPorEmail(email);
        if (usuarioExistente.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado.");
        }

        UsuarioModel usuario = usuarioExistente.get();
        usuario.setNombre(usuarioActualizado.getNombre());
        usuario.setApellido(usuarioActualizado.getApellido());
        usuario.setDocumento(usuarioActualizado.getDocumento());
        usuario.setNumeroDocumento(usuarioActualizado.getNumeroDocumento());
        usuario.setEstado(usuarioActualizado.getEstado());
        usuario.setDireccion(usuarioActualizado.getDireccion());
        usuario.setEmail(usuarioActualizado.getEmail());
        usuario.setCodigoPostal(usuarioActualizado.getCodigoPostal());
        usuario.setCuentas(usuarioActualizado.getCuentas());

        UsuarioModel usuarioGuardado = usuarioServices.guardarUsuario(usuario);
        return ResponseEntity.ok(usuarioGuardado);
    }

    @DeleteMapping("/email/{email}")
    public ResponseEntity<String> eliminarUsuarioPorEmail(@PathVariable("email") String email) {
        boolean ok = usuarioServices.eliminarUsuarioPorEmail(email);
        return ok ? ResponseEntity.ok("Se eliminó el usuario con email: " + email) : ResponseEntity.status(404).body("No se pudo eliminar el usuario con email: " + email);
    }
}
