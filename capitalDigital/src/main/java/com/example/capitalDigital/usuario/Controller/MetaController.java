package com.example.capitalDigital.usuario.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
public class MetaController {

    @Autowired
    private MetaService metaService;

    @PostMapping("/numeroDocumento/{numeroDocumento}")
    public ResponseEntity<String> agregarMeta(@PathVariable String numeroDocumento, @RequestBody MetaFinanciera meta) {
        boolean guardado = metaService.guardarMetaEnXML(numeroDocumento, meta);
        if (guardado) {
            return ResponseEntity.ok("✅ Meta guardada correctamente");
        } else {
            return ResponseEntity.status(500).body("❌ Error al guardar la meta");
        }
    }

    @GetMapping("/numeroDocumento/{numeroDocumento}")
    public ResponseEntity<List<MetaFinanciera>> obtenerMetasPorNumeroDocumento(@PathVariable String numeroDocumento) {
        List<MetaFinanciera> metas = metaService.obtenerMetasPorUsuario(numeroDocumento);
        return ResponseEntity.ok(metas);
    }

    @PutMapping("/numeroDocumento/{numeroDocumento}/meta/{nombreMeta}")
    public ResponseEntity<String> modificarMeta(
            @PathVariable String numeroDocumento,
            @PathVariable String nombreMeta,
            @RequestBody MetaFinanciera nuevaMeta) {

        boolean modificada = metaService.modificarMetaEnXML(numeroDocumento, nombreMeta, nuevaMeta);
        if (modificada) {
            return ResponseEntity.ok("✅ Meta modificada correctamente");
        } else {
            return ResponseEntity.status(500).body("❌ Error al modificar la meta");
        }
    }
}