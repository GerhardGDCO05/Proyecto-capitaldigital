package com.example.capitalDigital.Validation_bank;

import org.springframework.stereotype.Service;

@Service
public class BancoService {

    /**
     * Verifica si un número de cuenta sigue el patrón de alguno de los bancos soportados
     * @param banco Nombre del banco
     * @param numeroCuenta Número de cuenta a validar
     * @return true si el número de cuenta es válido para el banco especificado, false en caso contrario
     */
    public boolean validarNumeroCuenta(String banco, String numeroCuenta) {
        if (banco == null || numeroCuenta == null) {
            return false;
        }

        // Normalizar el número de cuenta (eliminar guiones y espacios)
        String numeroNormalizado = numeroCuenta.replaceAll("[^0-9]", "");

        // Validar según el banco
        switch (banco.toUpperCase()) {
            case "MERCANTIL":
            case "MERCANTIL BANCO UNIVERSAL":
                return validarMercantil(numeroNormalizado);

            case "BBVA":
            case "BBVA VENEZUELA":
                return validarBBVA(numeroNormalizado);

            case "BDV":
            case "BANCO DE VENEZUELA":
                return validarBDV(numeroNormalizado);

            default:
                System.out.println("Banco no soportado: " + banco);
                return false;
        }
    }

    /**
    Valida el formato de número de cuenta de Mercantil Banco Universal
    @param numeroCuenta Número de cuenta sin guiones
    @return true si es válido, false en caso contrario
     */
    private boolean validarMercantil(String numeroCuenta) {

        // Los primeros 4 dígitos deben ser 0123
        if (!numeroCuenta.startsWith("0123")) {
            return false;
        }

        // El resto deben ser dígitos
        return numeroCuenta.matches("^0123[0-9]{16}$");
    }

    /**
    Valida el formato de número de cuenta de BBVA Venezuela
    @param numeroCuenta Número de cuenta sin guiones
    @return true si es válido, false en caso contrario
     */
    private boolean validarBBVA(String numeroCuenta) {

        // Los primeros 4 dígitos deben ser 0111
        if (!numeroCuenta.startsWith("0111")) {
            return false;
        }

        // El resto deben ser dígitos
        return numeroCuenta.matches("^0111[0-9]{16}$");
    }

    /**
    Valida el formato de número de cuenta de BDV
    @param numeroCuenta Número de cuenta sin guiones
    @return true si es válido, false en caso contrario
    */
    private boolean validarBDV(String numeroCuenta) {

        // Los primeros 4 dígitos deben ser 0102
        if (!numeroCuenta.startsWith("0102")) {
            return false;
        }

        // El resto deben ser dígitos
        return numeroCuenta.matches("^0102[0-9]{16}$");
    }

    /**
     * Valida el formato de número de cuenta con guiones para cualquier banco
     * @param banco Nombre del banco
     * @param numeroCuenta Número de cuenta con guiones
     * @return true si es válido, false en caso contrario
     */
    public boolean validarNumeroCuentaConGuiones(String banco, String numeroCuenta) {
        if (banco == null || numeroCuenta == null) {
            return false;
        }

        switch (banco.toUpperCase()) {
            case "MERCANTIL":
            case "MERCANTIL BANCO UNIVERSAL":
                return numeroCuenta.matches("^0123-[0-9]{2}-[0-9]{14}$");

            case "BBVA":
            case "BBVA VENEZUELA":
                return numeroCuenta.matches("^0111-[0-9]{3}-[0-9]{13}$");

            case "BDV":
            case "BANCO DE VENEZUELA":
                return numeroCuenta.matches("^0102-[0-9]{4}-[0-9]{2}-[0-9]{10}$");

            default:
                System.out.println("Banco no soportado: " + banco);
                return false;
        }
    }

    /**
     * Normaliza un número de cuenta (elimina guiones y espacios)
     * @param numeroCuenta Número de cuenta a normalizar
     * @return Número de cuenta sin guiones ni espacios
     */
    public String normalizarNumeroCuenta(String numeroCuenta) {
        if (numeroCuenta == null) {
            return "";
        }
        return numeroCuenta.replaceAll("[^0-9]", "");
    }
}
