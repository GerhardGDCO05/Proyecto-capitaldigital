/*package com.example.capitalDigital.usuario.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.capitalDigital.usuario.models.CuentaModel;
import com.example.capitalDigital.usuario.models.UsuarioModel;
import com.example.capitalDigital.usuario.services.CuentaService;
import com.example.capitalDigital.usuario.repositories.UsuarioRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cuenta")
@CrossOrigin(origins = "http://localhost:5173")
public class CuentaController {

    @Autowired
    private CuentaService cuentaServices;
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Agregar nueva cuenta por usuarioId
    @PostMapping("/{usuarioId}/agregar")
    public ResponseEntity<?> agregarCuenta(@PathVariable Long usuarioId, @Valid @RequestBody CuentaModel cuenta, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }

        Optional<UsuarioModel> usuarioOptional = usuarioRepository.findById(usuarioId);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado.");
        }

        UsuarioModel usuario = usuarioOptional.get();
        cuenta.setUsuario(usuario);
        CuentaModel nuevaCuenta = cuentaServices.guardarCuenta(cuenta);
        return ResponseEntity.ok(nuevaCuenta);
    }
    @PostMapping("/usuario/{email}/agregar")
    public ResponseEntity<?> agregarCuentaPorEmail(@PathVariable String email, @Valid @RequestBody CuentaModel cuenta) {
        Optional<UsuarioModel> usuarioOptional = usuarioRepository.findByEmail(email);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado.");
        }

        UsuarioModel usuario = usuarioOptional.get();
        cuenta.setUsuario(usuario);
        CuentaModel nuevaCuenta = cuentaServices.guardarCuenta(cuenta);
        return ResponseEntity.ok(nuevaCuenta);
    }

    //  Obtener cuentas por email del usuario
    @GetMapping("/usuario/{email}")
    public ResponseEntity<?> obtenerCuentasPorEmail(@PathVariable String email) {
        List<CuentaModel> cuentas = cuentaServices.obtenerCuentasPorEmail(email);
        return cuentas.isEmpty() ? ResponseEntity.status(404).body("No se encontraron cuentas.")
                : ResponseEntity.ok(cuentas);
    }

    //  Modificar cuenta por email
    @PutMapping("/usuario/{email}/modificar/{cuentaId}")
    public ResponseEntity<?> modificarCuentaPorEmail(
            @PathVariable String email,
            @PathVariable Long cuentaId,
            @Valid @RequestBody CuentaModel cuentaActualizada) {

        CuentaModel cuenta = cuentaServices.modificarCuentaPorEmail(email, cuentaId, cuentaActualizada);
        return cuenta != null ? ResponseEntity.ok(cuenta)
                : ResponseEntity.status(404).body("Cuenta no encontrada.");
    }

    //  Eliminar todas las cuentas de un usuario por email
    @DeleteMapping("/usuario/{email}/eliminar")
    public ResponseEntity<?> eliminarCuentasPorEmail(@PathVariable String email) {
        boolean eliminado = cuentaServices.eliminarCuentasPorEmail(email);
        return eliminado ? ResponseEntity.ok("Cuentas eliminadas correctamente.")
                : ResponseEntity.status(404).body("No se encontraron cuentas para eliminar.");
    }
}*/
