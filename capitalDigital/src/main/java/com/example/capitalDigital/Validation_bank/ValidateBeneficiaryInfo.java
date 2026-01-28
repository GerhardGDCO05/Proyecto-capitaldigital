package com.example.capitalDigital.Validation_bank;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidateBeneficiaryInfo {

    @Autowired
    private BancoService bancoService; 

    public boolean validateInfo(String beneficiaryName, String ID, String bank, String accountNumber) {
        System.out.println("Ejecutando validateInfo()...");

        if (beneficiaryName == null || bank == null || accountNumber == null) {
            System.out.println("Datos nulos");
            return false;
        }

        // Si ID es null o vacío, asignamos uno por defecto
        if (ID == null || ID.trim().isEmpty()) {
            System.out.println("ID inválido o vacío. Asignando valor por defecto.");
            ID = "12345678"; // 👈 Valor por defecto
        }

        if (bancoService == null) {
            System.out.println("BancoService no ha sido inyectado correctamente.");
            return false;
        }

        // Validar cuenta bancaria
        boolean cuentaValida = bancoService.validarNumeroCuenta(bank, accountNumber);
        System.out.println("Banco: " + bank + " | Cuenta: " + accountNumber + " | ¿Válida?: " + cuentaValida);

        if (!cuentaValida) {
            System.out.println("Número de cuenta inválido");
            return false;
        }

        // Validar formato del ID (solo números y 7-8 dígitos)
        String IDNormalize = ID.replaceAll("[^0-9]", "");
        String IDRegex = "^\\d{7,8}$";
        boolean matches = Pattern.matches(IDRegex, IDNormalize);
        if (!matches) {
            System.out.println("ID inválido (debe tener entre 7 y 8 dígitos)");
            return false;
        }

        // Validar nombre del beneficiario
        String beneficiaryNameNormalize = beneficiaryName.toLowerCase();
        String beneficiaryNameRegex = "^[a-záéíóúüñ\\s]+$"; // ✔️ Permite cualquier combinación de letras y espacios
        matches = Pattern.matches(beneficiaryNameRegex, beneficiaryNameNormalize);
        if (!matches) {
            System.out.println("Nombre del beneficiario inválido");
            return false;
        }

        return true;
    }
}

