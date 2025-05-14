package com.example.capitalDigital.usuario.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.capitalDigital.usuario.models.CuentaModel;
import com.example.capitalDigital.usuario.models.UsuarioModel;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.capitalDigital.usuario.services.CuentaService;

import jakarta.validation.Valid;

import com.example.capitalDigital.usuario.repositories.UsuarioRepository;

@RestController
@RequestMapping("/cuenta")
@CrossOrigin(origins = "http://localhost:5173")
public class CuentaController {

    @Autowired
    private CuentaService cuentaServices;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/{usuarioId}/agregar")
    public ResponseEntity<?> agregarCuenta(@PathVariable Long usuarioId, @Valid @RequestBody CuentaModel cuenta, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));

            System.out.println("Errores detectados: " + errores);  // Revisa si los errores aparecen en la consola
            return ResponseEntity.badRequest().body(errores);  // Envía los errores al frontend
        }

        UsuarioModel usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(404).body("Usuario no encontrado.");
        }

        cuenta.setUsuario(usuario);
        CuentaModel nuevaCuenta = cuentaServices.guardarCuenta(cuenta);
        return ResponseEntity.ok(nuevaCuenta);
    }

}

