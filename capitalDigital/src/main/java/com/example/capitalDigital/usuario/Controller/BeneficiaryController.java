package com.example.capitalDigital.usuario.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.capitalDigital.usuario.models.BeneficiaryModel;
import com.example.capitalDigital.usuario.services.BeneficiaryService;

@RestController
@RequestMapping("/beneficiaries")
@CrossOrigin(origins = "http://localhost:5173")
public class BeneficiaryController {

    @Autowired
    private BeneficiaryService beneficiaryService;

    @PostMapping("/{holder}")
    public ResponseEntity<String> addBeneficiary(
            @PathVariable String holder,
            @RequestBody Map<String, String> requestData) {

        boolean success = beneficiaryService.addBeneficiary(
            requestData.get("beneficiaryName"),
            requestData.get("id"),
            requestData.get("accountNumber"),
            requestData.get("bank"),
            holder
            
        );

        if (success) {
            return ResponseEntity.ok("Beneficiario agregado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error al agregar beneficiario: Datos inválidos o número de cuenta duplicado");
        }
    }

    @PutMapping("/{holder}/{oldAccountNumber}")
    public ResponseEntity<String> modifyBeneficiary(
            @PathVariable String holder,
            @PathVariable String oldAccountNumber,
            @RequestBody Map<String, String> requestData) {

        boolean success = beneficiaryService.modifyBeneficiary(
            requestData.get("beneficiaryName"),
            requestData.get("id"),
            requestData.get("accountNumber"),
            requestData.get("bank"),
            holder,
            oldAccountNumber
        );

        if (success) {
            return ResponseEntity.ok("Beneficiario modificado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error al modificar beneficiario: Beneficiario no encontrado o datos inválidos");
        }
    }

    // Los demás métodos (delete, get) permanecen exactamente iguales
    @DeleteMapping("/{holder}/{accountNumber}")
    public ResponseEntity<String> deleteBeneficiary(
            @PathVariable String holder,
            @PathVariable String accountNumber) {
        boolean success = beneficiaryService.deleteBeneficiary(holder, accountNumber);
        if (success) {
            return ResponseEntity.ok("Beneficiario eliminado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Beneficiario no encontrado");
        }
    }

    @GetMapping("/{holder}")
    public ResponseEntity<List<BeneficiaryModel>> getBeneficiariesByHolder(@PathVariable String holder) {
        List<BeneficiaryModel> beneficiaries = beneficiaryService.getBeneficiariesByDocumento(holder);
        return ResponseEntity.ok(beneficiaries);
    }

    @GetMapping("/{holder}/{accountNumber}")
    public ResponseEntity<BeneficiaryModel> getBeneficiary(
            @PathVariable String holder,
            @PathVariable String accountNumber) {
        BeneficiaryModel beneficiary = beneficiaryService.getBeneficiary(holder, accountNumber);
        return ResponseEntity.ok(beneficiary);
    }
}