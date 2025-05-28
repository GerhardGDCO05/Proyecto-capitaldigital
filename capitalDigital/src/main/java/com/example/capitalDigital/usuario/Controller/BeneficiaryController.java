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

import com.example.capitalDigital.usuario.models.BeneficiaryModel;
import com.example.capitalDigital.usuario.services.BeneficiaryService;

@RestController
@RequestMapping("/beneficiaries")
@CrossOrigin(origins = "http://localhost:5173")
public class BeneficiaryController {

    @Autowired
    private BeneficiaryService beneficiaryService;

    @PostMapping("/{holder}")
    public ResponseEntity<String> addBeneficiary(@PathVariable String holder,@RequestBody BeneficiaryModel beneficiary) {

        System.out.println("Holder: " + holder);
        System.out.println("Beneficiary Name: " + beneficiary.getBeneficiaryName());
        System.out.println("ID: " + beneficiary.getID());
        System.out.println("Account Number: " + beneficiary.getAccountNumber());
        System.out.println("Bank: " + beneficiary.getBank());

        boolean success = beneficiaryService.addBeneficiary(
            beneficiary.getBeneficiaryName(),
            beneficiary.getID(),
            beneficiary.getAccountNumber(),
            beneficiary.getBank(),
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
            @RequestBody BeneficiaryModel beneficiary) {

        boolean success = beneficiaryService.modifyBeneficiary(
            beneficiary.getBeneficiaryName(),
            beneficiary.getID(),
            beneficiary.getAccountNumber(),
            beneficiary.getBank(),
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
        if (beneficiaries.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            return ResponseEntity.ok(beneficiaries);
        }
    }

    @GetMapping("/{holder}/{accountNumber}")
    public ResponseEntity<BeneficiaryModel> getBeneficiary(
            @PathVariable String holder,
            @PathVariable String accountNumber) {

        BeneficiaryModel beneficiary = beneficiaryService.getBeneficiary(holder, accountNumber);

        if (beneficiary == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null);
        } else {
            return ResponseEntity.ok(beneficiary);
        }
    }
}