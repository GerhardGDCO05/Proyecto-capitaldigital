package com.example.capitalDigital.usuario.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.capitalDigital.usuario.models.TransactionHistoryModel;
import com.example.capitalDigital.usuario.services.TransactionHistoryService; 

@RestController
@RequestMapping("/transactionhistory")
@CrossOrigin(origins = "http://localhost:5173")
public class TransactionHistoryController {

    @Autowired
    private TransactionHistoryService transactionHistoryService; 

    @PostMapping("/numeroDocumento/{numeroDocumento}")
    public ResponseEntity<String> agregarMeta(@PathVariable String numeroDocumento, @RequestBody  Map<String, Object>historyData) {
        System.out.println("=== RECIBIENDO PETICIÓN POST TRANSACCION ===");
        System.out.println("Número de documento: " + numeroDocumento);
        System.out.println("Datos recibidos: "+historyData);

        try {
            boolean guardado = transactionHistoryService.guardarTransactionEnXML(numeroDocumento, historyData);
            if (guardado) {
                return ResponseEntity.ok("Transacción guardada correctamente");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al guardar la Transacción");
            }
        } catch (Exception e) {
            System.err.println("Error en controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }
    @GetMapping("/numeroDocumento/{numeroDocumento}")
    public ResponseEntity<?> consultarTransacciones(@PathVariable String numeroDocumento) {
        try {
            List<TransactionHistoryModel> transacciones = transactionHistoryService.consultarTransacciones(numeroDocumento);
            if (transacciones.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron transacciones para el usuario");
            }
            return ResponseEntity.ok(transacciones);
        } catch (Exception e) {
            System.err.println("Error en controller al consultar transacciones: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }
}

