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

            System.out.println("Errores detectados: " + errores);  // Ver errores en la consola de Spring Boot
            return ResponseEntity.badRequest().body(errores);  // Enviar errores al frontend
        }

        UsuarioModel nuevoUsuario = usuarioServices.guardarUsuario(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable("id") Long id) {
        Optional<UsuarioModel> usuario = usuarioServices.obtenerPorId(id);
        return usuario.isPresent() ? ResponseEntity.ok(usuario.get()) : ResponseEntity.status(404).body("Usuario no encontrado.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPorId(@PathVariable("id") Long id) {
        boolean ok = usuarioServices.eliminarUsuario(id);
        return ok ? ResponseEntity.ok("Se eliminó el usuario con id: " + id) : ResponseEntity.status(404).body("No se pudo eliminar el usuario con id: " + id);
    }
}
