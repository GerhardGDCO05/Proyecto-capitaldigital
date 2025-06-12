package com.example.capitalDigital.usuario.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.capitalDigital.usuario.services.BeneficiaryService;

@RestController
@RequestMapping("/beneficiaries")
@CrossOrigin(origins = "http://localhost:5173")
public class BeneficiaryController {

    @Autowired
    private BeneficiaryService beneficiaryService;

    @PostMapping("/{holder}")

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
        if (beneficiaries.isEmpty()) {
        } else {
            return ResponseEntity.ok(beneficiaries);
        }
    }

    @GetMapping("/{holder}/{accountNumber}")
            @PathVariable String holder,
            @PathVariable String accountNumber) {


        if (beneficiary == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null);
        } else {
            return ResponseEntity.ok(beneficiary);
        }
    }
}