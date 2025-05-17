package com.example.capitalDigital.usuario.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "cuentas")
public class CuentaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Cambiado de long a Long

    @NotBlank(message = "El nombre del banco no puede estar vacío")
    private String banco;

    @NotBlank(message = "El número de cuenta no puede estar vacío")
    @Pattern(regexp = "^[0-9]+$", message = "El número de cuenta debe contener solo números")
    @Size(min = 18, message = "El número de cuenta debe tener al menos 18 dígitos")
    @Size(max = 20, message = "El número de cuenta debe tener máximo 20 dígitos")
    private String numeroCuenta;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonBackReference
    private UsuarioModel usuario;

    // Constructores
    public CuentaModel() {}

    public CuentaModel(String banco, String numeroCuenta, UsuarioModel usuario) {
        this.banco = banco;
        this.numeroCuenta = numeroCuenta;
        this.usuario = usuario;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public UsuarioModel getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioModel usuario) {
        this.usuario = usuario;
    }
}
