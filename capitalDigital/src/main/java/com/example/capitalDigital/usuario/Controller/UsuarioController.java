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

    /*@GetMapping()
    public ResponseEntity<ArrayList<UsuarioModel>> obtenerUsuarios() {
        return ResponseEntity.ok(usuarioServices.obtenerUsuarios());
    } */

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
        return usuario.isPresent() ? ResponseEntity.ok(usuario.get()) : ResponseEntity.status(404).body("Usuario no encontrado.");
    }


    @PutMapping("/numeroDocumento/{numeroDocumento}")
    public ResponseEntity<?> modificarUsuarioPorNumeroDocumento(@PathVariable("numeroDocumento") String numeroDocumento, @Valid @RequestBody UsuarioModel usuarioActualizado, BindingResult result) {
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

        UsuarioModel usuarioGuardado = usuarioServices.guardarUsuario(usuario);
        return ResponseEntity.ok(usuarioGuardado);
    }

    @DeleteMapping("/numeroDocumento/{numeroDocumento}")
    public ResponseEntity<String> eliminarUsuarioPorNumeroDocumento(@PathVariable("numeroDocumento") String numeroDocumento) {
        boolean ok = usuarioServices.eliminarUsuarioPorNumeroDocumento(numeroDocumento);
        return ok ? ResponseEntity.ok("Se eliminó el usuario con email: " + numeroDocumento) : ResponseEntity.status(404).body("No se pudo eliminar el usuario con email: " + numeroDocumento);
    }
}
