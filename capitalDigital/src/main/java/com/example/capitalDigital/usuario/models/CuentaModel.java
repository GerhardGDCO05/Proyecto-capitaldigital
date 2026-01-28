package com.example.capitalDigital.usuario.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CuentaModel {
    @NotBlank(message = "El banco no puede estar vacío")
    private String banco;

    @NotBlank(message = "El número de cuenta no puede estar vacío")
    @Size(min = 20, max = 20, message = "El número de cuenta debe tener 20 dígitos")
    private String numeroCuenta;

    @NotBlank(message = "El nombre de la cuenta no puede estar vacío")
    private String nombreCuenta;

    private double saldo;

    private String numeroTarjeta;
    private String validoHasta;
    private String nombreTarjeta;

    // Constructor vacío
    public CuentaModel() {}

    // Constructor completo
    public CuentaModel(String banco, String numeroCuenta, String nombreCuenta) {
        this.banco = banco;
        this.numeroCuenta = numeroCuenta;
        this.nombreCuenta = nombreCuenta;
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

    public String getNombreCuenta() {
        return nombreCuenta;
    }

    public void setNombreCuenta(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getValidoHasta() {
        return validoHasta;
    }

    public void setValidoHasta(String validoHasta) {
        this.validoHasta = validoHasta;
    }

    public String getNombreTarjeta() {
        return nombreTarjeta;
    }

    public void setNombreTarjeta(String nombreTarjeta) {
        this.nombreTarjeta = nombreTarjeta;
    }

    @Override
    public String toString() {
        return "CuentaModel{" +
               "banco='" + banco + '\'' +
               ", numeroCuenta='" + numeroCuenta + '\'' +
               ", nombreCuenta='" + nombreCuenta + '\'' +
               ", saldo=" + saldo +
               ", numeroTarjeta='" + numeroTarjeta + '\'' +
               ", validoHasta='" + validoHasta + '\'' +
               ", nombreTarjeta='" + nombreTarjeta + '\'' +
               '}';
    }
}
