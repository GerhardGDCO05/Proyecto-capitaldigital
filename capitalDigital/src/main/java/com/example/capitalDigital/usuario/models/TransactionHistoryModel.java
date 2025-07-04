package com.example.capitalDigital.usuario.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class TransactionHistoryModel {
    private String beneficiario;
    private LocalDateTime fechaHora; 
    private String monto;
    private String bancoOrigen;
    private String numCuentaOrigen;
    private String bancoDestino;
    private String numCuentaDestino;

    public TransactionHistoryModel() {
        this.fechaHora = LocalDateTime.now(); // Inicializar con fecha actual
    }

    public TransactionHistoryModel(String beneficiario, String monto, String bancoOrigen, String numCuentaOrigen, String bancoDestino, String numCuentaDestino) {
        this.beneficiario = beneficiario;
        this.monto = monto;
        this.bancoOrigen = bancoOrigen;
        this.numCuentaOrigen = numCuentaOrigen;
        this.bancoDestino = bancoDestino;
        this.numCuentaDestino = numCuentaDestino;
        this.fechaHora = LocalDateTime.now(); // Solo se establece cuando se crea una nueva transacción
    }

    // Getters y Setters
    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getBancoOrigen() {
        return bancoOrigen;
    }

    public void setBancoOrigen(String bancoOrigen) {
        this.bancoOrigen = bancoOrigen;
    }

    public String getNumCuentaOrigen() {
        return numCuentaOrigen;
    }

    public void setNumCuentaOrigen(String numCuentaOrigen) {
        this.numCuentaOrigen = numCuentaOrigen;
    }

    public String getBancoDestino() {
        return bancoDestino;
    }

    public void setBancoDestino(String bancoDestino) {
        this.bancoDestino = bancoDestino;
    }

    public String getNumCuentaDestino() {
        return numCuentaDestino;
    }

    public void setNumCuentaDestino(String numCuentaDestino) {
        this.numCuentaDestino = numCuentaDestino;
    }

    // Getter y Setter para fechaHora (oculto del JSON)
    @JsonIgnore
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    @JsonIgnore
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    // Método para obtener la fecha y hora formateada como String
    public String getFecha() {
        if (this.fechaHora != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return this.fechaHora.format(formatter);
        }
        return "";
    }
} 