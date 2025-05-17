package com.example.capitalDigital.usuario.Controller;

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

    // Modificar cuenta existente por usuarioId y cuentaId
    @PutMapping("/{usuarioId}/modificar/{cuentaId}")
    public ResponseEntity<?> modificarCuenta(
            @PathVariable Long usuarioId,
            @PathVariable Long cuentaId,
            @Valid @RequestBody CuentaModel cuentaActualizada,
            BindingResult result) {

        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }

        Optional<UsuarioModel> usuarioOptional = usuarioRepository.findById(usuarioId);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado.");
        }

        Optional<CuentaModel> cuentaOptional = cuentaServices.obtenerCuentaPorId(cuentaId);
        if (cuentaOptional.isEmpty() || !cuentaOptional.get().getUsuario().getId().equals(usuarioId)) {
            return ResponseEntity.status(404).body("Cuenta no encontrada o no pertenece a este usuario.");
        }

        CuentaModel cuentaExistente = cuentaOptional.get();
        cuentaExistente.setBanco(cuentaActualizada.getBanco());
        cuentaExistente.setNumeroCuenta(cuentaActualizada.getNumeroCuenta());

        CuentaModel cuentaGuardada = cuentaServices.guardarCuenta(cuentaExistente);
        return ResponseEntity.ok(cuentaGuardada);
    }

    // Eliminar cuenta por usuarioId y cuentaId
    @DeleteMapping("/{usuarioId}/eliminar/{cuentaId}")
    public ResponseEntity<String> eliminarCuenta(
            @PathVariable Long usuarioId,
            @PathVariable Long cuentaId) {

        Optional<CuentaModel> cuentaOptional = cuentaServices.obtenerCuentaPorId(cuentaId);
        if (cuentaOptional.isEmpty() || !cuentaOptional.get().getUsuario().getId().equals(usuarioId)) {
            return ResponseEntity.status(404).body("Cuenta no encontrada o no pertenece a este usuario.");
        }

        boolean eliminado = cuentaServices.eliminarCuentaPorId(cuentaId);
        return eliminado ? ResponseEntity.ok("Cuenta eliminada correctamente.") : ResponseEntity.status(500).body("Error al eliminar la cuenta.");
    }

    // Obtener todas las cuentas de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerCuentasPorUsuario(@PathVariable Long usuarioId) {
        List<CuentaModel> cuentas = cuentaServices.obtenerCuentasPorUsuarioId(usuarioId);
        return ResponseEntity.ok(cuentas);
    }

    // Obtener cuenta por ID
    @GetMapping("/{cuentaId}")
    public ResponseEntity<?> obtenerCuentaPorId(@PathVariable Long cuentaId) {
        Optional<CuentaModel> cuenta = cuentaServices.obtenerCuentaPorId(cuentaId);
        return cuenta.isPresent() ? ResponseEntity.ok(cuenta.get()): ResponseEntity.status(404).body("Cuenta no encontrada.");
    }
}
