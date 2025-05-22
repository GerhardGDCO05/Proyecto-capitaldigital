package com.example.capitalDigital.usuario.models;

import jakarta.validation.constraints.*;


public class CuentaModel {
    @NotBlank(message = "El nombre del banco no puede estar vacío")
    private String banco;

    @NotBlank(message = "El número de cuenta no puede estar vacío")
    @Pattern(regexp = "^[0-9]+$", message = "El número de cuenta debe contener solo números")
    @Size(min = 20, max = 20, message = "El número de cuenta debe tener 20 dígitos")
    private String numeroCuenta;

    // Constructores
    public CuentaModel() {}

    public CuentaModel(String banco, String numeroCuenta) {
        this.banco = banco;
        this.numeroCuenta = numeroCuenta;
    }

    // Getters y Setters
    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    @Override
    public String toString() {
        return "CuentaModel{" +
                "banco='" + banco + '\'' +
                ", numeroCuenta='" + numeroCuenta + '\'' +
                '}';
    }
}
