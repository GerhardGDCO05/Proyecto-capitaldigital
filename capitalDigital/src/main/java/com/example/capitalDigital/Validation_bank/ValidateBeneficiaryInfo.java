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

        if (beneficiaryName == null || ID == null || bank == null || accountNumber == null) {
            System.out.println("Datos nulos");
            return false;
        }

        if (bancoService == null) {
            System.out.println("BancoService no ha sido inyectado correctamente.");
            return false;
        }

        boolean cuentaValida = bancoService.validarNumeroCuenta(bank, accountNumber);
        System.out.println("Banco: " + bank + " | Cuenta: " + accountNumber + " | ¿Válida?: " + cuentaValida);

        if (!cuentaValida) {
            System.out.println("Número de cuenta inválido");
            return false;
        }

        String IDNormalize = ID.replaceAll("[^0-9]", "");
        String IDRegex = "^\\d{7,8}$";
        boolean matches = Pattern.matches(IDRegex, IDNormalize);
        if (!matches) {
            System.out.println("ID inválido");
            return false;
        }

        String beneficiaryNameNormalize = beneficiaryName.toLowerCase();
        String beneficiaryNameRegex = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+(?:\\s[A-Za-zÁÉÍÓÚáéíóúÑñ]+)+$";
        matches = Pattern.matches(beneficiaryNameRegex, beneficiaryNameNormalize);
        if (!matches) {
            System.out.println("Nombre del beneficiario inválido");
            return false;
        }

        return true;
    }