package com.example.capitalDigital.usuario.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "cuentas")
public class CuentaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del banco no puede estar vacío")
    private String banco;

    @NotBlank(message = "El número de cuenta no puede estar vacío")
    @Pattern(regexp = "^[0-9]+$", message = "El número de cuenta debe contener solo números")
    @Size(min = 18, message = "El número de cuenta debe tener al menos 18 digitos")
    @Size(max = 20, message = "El número de cuenta debe tener maximo 20 digitos")
    private String numeroCuenta;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonBackReference
    private UsuarioModel usuario;

    public CuentaModel() {}

    public CuentaModel(String banco, String numeroCuenta, UsuarioModel usuario) {
        this.banco = banco;
        this.numeroCuenta = numeroCuenta;
        this.usuario = usuario;
    }

    public Long getId() { return id; }
    public String getBanco() { return banco; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public UsuarioModel getUsuario() { return usuario; }

    public void setBanco(String banco) { this.banco = banco; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }
    public void setUsuario(UsuarioModel usuario) { this.usuario = usuario; }
}

