package com.example.capitalDigital.usuario.models;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "usuarios")
public class UsuarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(max = 50, message = "El apellido no puede tener más de 50 caracteres")
    private String apellido;

    @NotBlank(message = "El documento no puede estar vacío")
    private String documento;

    @NotBlank(message = "El número de documento no puede estar vacío")
    @Pattern(regexp = "^[0-9]+$", message = "El número de documento debe contener solo números")
    @Size(min = 5, max = 20, message = "El número de documento debe tener entre 5 y 20 caracteres")
    private String numeroDocumento;

    @NotNull(message = "La Fecha de Nacimiento no puede estar vacía")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "La dirección no puede estar vacía")
    @Size(max = 100, message = "La dirección no puede tener más de 100 caracteres")
    private String direccion;

    @NotBlank(message = "El código postal no puede estar vacío")
    @Pattern(regexp = "^[0-9]+$", message = "El código postal debe contener solo números")
    @Size(min = 3, max = 10, message = "El código postal debe tener entre 3 y 10 caracteres")
    private String codigoPostal;

    @Column(name = "email", unique = true, nullable = false)
    @Email(message = "El email no es válido")
    @NotBlank(message = "El email es obligatorio")
    @Size(max = 100, message = "El email no puede tener más de 100 caracteres")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, max = 20, message = "La contraseña debe tener entre 6 y 20 caracteres")
    private String password;

    @NotBlank(message = "El nombre del banco no puede estar vacío")
    private String banco;

    @NotBlank(message = "El número de cuenta no puede estar vacío")
    @Pattern(regexp = "^[0-9]+$", message = "El número de cuenta debe contener solo números")
    @Size(min = 18, message = "El número de cuenta debe tener al menos 18 dígitos")
    @Size(max = 20, message = "El número de cuenta debe tener máximo 20 dígitos")
    private String numeroCuenta;

    // Constructor vacío
    public UsuarioModel() {
    }

    public UsuarioModel(Long id, String nombre,
                        String apellido, String documento,
                        String numeroDocumento,
                        LocalDate fechaNacimiento,
                        String direccion, String codigoPostal,
                        String email, String password,
                        String banco, String numeroCuenta) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.numeroDocumento = numeroDocumento;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.codigoPostal = codigoPostal;
        this.email = email;
        this.password = password;
        this.banco = banco;
        this.numeroCuenta = numeroCuenta;
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getNombre() {return nombre;}

    public void setNombre(String nombre) {this.nombre = nombre;}

    public String getApellido() {return apellido;}

    public void setApellido(String apellido) {this.apellido = apellido;}

    public String getDocumento() {return documento;}

    public void setDocumento(String documento) {this.documento = documento;}

    public String getNumeroDocumento() {return numeroDocumento;}

    public void setNumeroDocumento(String numeroDocumento) {this.numeroDocumento = numeroDocumento;}

    public LocalDate getFechaNacimiento() {return fechaNacimiento;}

    public void setFechaNacimiento(LocalDate fechaNacimiento) {this.fechaNacimiento = fechaNacimiento;}

    public String getDireccion() {return direccion;}

    public void setDireccion(String direccion) {this.direccion = direccion;}

    public String getCodigoPostal() {return codigoPostal;}

    public void setCodigoPostal(String codigoPostal) {this.codigoPostal = codigoPostal;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public String getBanco() {return banco;}

    public void setBanco(String banco) {this.banco = banco;}

    public String getNumeroCuenta() {return numeroCuenta;}

    public void setNumeroCuenta(String numeroCuenta) {this.numeroCuenta = numeroCuenta;}

    //Método toString
    @Override
    public String toString() {
        return "UsuarioModel{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", documento='" + documento + '\'' +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", fechaNacimiento='" + fechaNacimiento + '\'' +
                ", direccion='" + direccion + '\'' +
                ", codigoPostal='" + codigoPostal + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", banco=" + banco +'\'' +
                ",numeroCuenta="+numeroCuenta+'\''+
                '}';
    }
}
