package com.example.capitalDigital.usuario.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.capitalDigital.usuario.models.CuentaModel;
import com.example.capitalDigital.usuario.services.CuentaService;

@RestController
@RequestMapping("/cuenta")
@Validated 
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @PostMapping("/numeroDocumento/{numeroDocumento}")
    public ResponseEntity<?> agregarCuentaEnXML(@PathVariable String numeroDocumento, @RequestBody Map<String, Object> datosCuenta) {
        try {
            System.out.println("Recibiendo petición POST para documento: " + numeroDocumento);
            System.out.println("Datos de cuenta recibidos: " + datosCuenta);

            boolean guardado = cuentaService.guardarCuentaEnXML(numeroDocumento, datosCuenta);

            if (guardado) {
                return ResponseEntity.ok("Cuenta guardada correctamente en XML");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("La cuenta podría ya existir o el número de cuenta no es válido para el banco especificado");
            }
        } catch (RuntimeException e) {
            System.err.println("Error de validación en el controlador POST: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error en el controlador POST: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor: " + e.getMessage());
        }
    }

    @GetMapping("/numeroDocumento/{numeroDocumento}")
    public ResponseEntity<?> obtenerCuentasPorNumeroDocumento(@PathVariable String numeroDocumento) {
        try {
            System.out.println("Recibiendo petición GET para documento: " + numeroDocumento);

            List<CuentaModel> cuentas = cuentaService.obtenerCuentasPorNumeroDocumento(numeroDocumento);

            if (cuentas.isEmpty()) {
                System.out.println("El usuario no posee cuentas extras registradas.");
                return ResponseEntity.ok("No tiene cuentas secundarias registradas.");
            } else {
                return ResponseEntity.ok(cuentas);
            }

        } catch (Exception e) {
            System.err.println("Error en el controlador GET: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor: " + e.getMessage());
        }
    }
    @GetMapping("/todas")
    public ResponseEntity<?> obtenerTodasLasCuentas() {
        try {
            System.out.println("Recibiendo petición GET para todas las cuentas");

            List<CuentaModel> cuentas = cuentaService.obtenerTodasLasCuentas();

            if (cuentas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontraron cuentas registradas");
            } else {
                return ResponseEntity.ok(cuentas);
            }
        } catch (Exception e) {
            System.err.println("Error en el controlador GET todas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor: " + e.getMessage());
        }
    }



    @DeleteMapping("/numeroDocumento/{numeroDocumento}/numeroCuenta/{numeroCuenta}")
    public ResponseEntity<?> eliminarCuentaEnXML(@PathVariable String numeroDocumento, @PathVariable String numeroCuenta) {
        try {
            System.out.println("Recibiendo petición DELETE para documento: " + numeroDocumento + ", cuenta: " + numeroCuenta);

            boolean eliminado = cuentaService.eliminarCuentaEnXML(numeroDocumento, numeroCuenta);

            if (eliminado) {
                return ResponseEntity.ok("Cuenta eliminada correctamente.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cuenta no encontrada para el documento: " + numeroDocumento);
            }
        } catch (Exception e) {
            System.err.println("Error en el controlador DELETE: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor: " + e.getMessage());
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage()); // ✅ Captura mensajes personalizados
        }
        return ResponseEntity.badRequest().body(errores);
    }


    @PutMapping("/numeroDocumento/{documento}/nombreCuenta/{oldNombre}")
    public ResponseEntity<?> modificarNombreCuenta(@PathVariable String documento,
                                                    @PathVariable String oldNombre,
                                                    @RequestBody Map<String, Object> datosCuenta) {
        try {
            System.out.println("Recibiendo petición PUT para documento: " + documento);
            System.out.println("Nombre actual: " + oldNombre);
            System.out.println("Datos de cuenta recibidos: " + datosCuenta);

            boolean modificado = cuentaService.modificarCuentaEnXML(documento, oldNombre, datosCuenta);
                                                
            if (modificado) {
                return ResponseEntity.ok("✅ Nombre de cuenta actualizado exitosamente");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Cuenta no encontrada o nombre inválido");
            }
        } catch (RuntimeException e) {
            System.err.println("Error de validación en el controlador PUT: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error en el controlador PUT: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor: " + e.getMessage());
        }
    }

}