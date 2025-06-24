package com.example.capitalDigital.usuario.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.capitalDigital.usuario.models.MetaFinanciera;
import com.example.capitalDigital.usuario.services.MetaService;

@RestController
@RequestMapping("/meta")
@CrossOrigin(origins = "http://localhost:5173")
public class MetaController {

    @Autowired
    private MetaService metaService;

    @PostMapping("/numeroDocumento/{numeroDocumento}")
    public ResponseEntity<String> agregarMeta(@PathVariable String numeroDocumento, @RequestBody MetaFinanciera meta) {
        System.out.println("=== RECIBIENDO PETICIÓN POST ===");
        System.out.println("Número de documento: " + numeroDocumento);
        System.out.println("Meta recibida: " + meta.getNombre());
        System.out.println("Fecha inicio: " + meta.getFechaInicio());
        System.out.println("Fecha fin: " + meta.getFechaFin());
        System.out.println("Monto requerido: " + meta.getMontoRequerido());
        
        try {
            boolean guardado = metaService.guardarMetaEnXML(numeroDocumento, meta);
            if (guardado) {
                return ResponseEntity.ok("Meta guardada correctamente");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al guardar la meta: Meta duplicada o datos inválidos");
            }
        } catch (Exception e) {
            System.err.println("Error en controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor");
        }
    }

    @GetMapping("/numeroDocumento/{numeroDocumento}")
    public ResponseEntity<List<MetaFinanciera>> obtenerMetasPorNumeroDocumento(@PathVariable String numeroDocumento) {
        System.out.println("=== OBTENIENDO METAS ===");
        System.out.println("Número de documento: " + numeroDocumento);
        
        List<MetaFinanciera> metas = metaService.obtenerMetasPorUsuario(numeroDocumento);
        if (metas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(metas);
    }

    @PutMapping("/numeroDocumento/{numeroDocumento}/{nombreMeta}")
    public ResponseEntity<String> modificarMeta(
            @PathVariable String numeroDocumento,
            @PathVariable String nombreMeta,
            @RequestBody MetaFinanciera nuevaMeta) {

        System.out.println("=== MODIFICANDO META ===");
        System.out.println("Número de documento: " + numeroDocumento);
        System.out.println("Nombre meta a modificar: " + nombreMeta);

        boolean modificada = metaService.modificarMetaEnXML(numeroDocumento, nombreMeta, nuevaMeta);
        if (modificada) {
            return ResponseEntity.ok("Meta modificada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Meta no encontrada o error al modificar");
        }
    }
    
    @DeleteMapping("/{numeroDocumento}/{nombreMeta}")
    public ResponseEntity<String> eliminarMeta(
            @PathVariable String numeroDocumento,
            @PathVariable String nombreMeta) {

        try {
            boolean success = metaService.eliminarMeta(numeroDocumento, nombreMeta);

            if (success) {
                return ResponseEntity.ok("Meta eliminada exitosamente");
            } else {
                return ResponseEntity.status(404).body("Meta no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar la meta: " + e.getMessage());
        }
    }

    
}