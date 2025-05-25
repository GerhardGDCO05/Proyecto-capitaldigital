package com.example.capitalDigital.Validation_bank;

import java.util.regex.*;
import com.example.capitalDigital.Validation_bank.BancoService;

public class ValidateBeneficiaryInfo {

    public boolean validateInfo(String beneficiaryName, String ID, String bank, String accountNumber) {
         if (beneficiaryName == null || ID == null || bank == null || accountNumber == null) return false;
        BancoService bs = new BancoService();
        if (!bs.validarNumeroCuenta(bank, accountNumber)) return false;
        String IDNormalize = ID.replaceAll("[^0-9]", "");
        String IDRegex = "^\\d{7,8}$";
        boolean matches = Pattern.matches(IDRegex, IDNormalize);
        if(!matches) return false;
        String beneficiaryNameNormalize = beneficiaryName.toLowerCase();
        String beneficiaryNameRegex = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+(?:\\s[A-Za-zÁÉÍÓÚáéíóúÑñ]+)+$";
        matches = Pattern.matches(beneficiaryNameRegex, beneficiaryNameNormalize);
        if(!matches) return false;
        return true;

    }

}
