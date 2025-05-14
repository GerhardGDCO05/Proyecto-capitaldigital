package com.example.capitalDigital.usuario.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "usuarios")
public class UsuarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    private String apellido;

    @NotBlank(message = "El documento no puede estar vacío")
    private String documento;

    @NotBlank(message = "El número de documento no puede estar vacío")
    @Pattern(regexp = "^[0-9]+$", message = "El número de documento debe contener solo números")
    private String numeroDocumento;

    @NotBlank(message = "El estado no puede estar vacío")
    private String estado;

    @NotBlank(message = "La dirección no puede estar vacía")
    private String direccion;

    @NotBlank(message = "El código postal no puede estar vacío")
    @Pattern(regexp = "^[0-9]+$", message = "El código postal debe contener solo números")
    private String codigoPostal;

    @Column(name = "email", unique = true, nullable = false)
    @Email(message = "El email no es válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CuentaModel> cuentas = new ArrayList<>();

    public UsuarioModel() {}

    public UsuarioModel(String nombre, String apellido, String documento, String numeroDocumento, String estado,
                        String direccion, String codigoPostal, String email, String password, List<CuentaModel> cuentas) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.numeroDocumento = numeroDocumento;
        this.estado = estado;
        this.direccion = direccion;
        this.codigoPostal = codigoPostal;
        this.email = email;
        this.password = password;
        this.cuentas = cuentas;
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getDocumento() { return documento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public String getEstado() { return estado; }
    public String getDireccion() { return direccion; }
    public String getCodigoPostal() { return codigoPostal; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public List<CuentaModel> getCuentas() { return cuentas; }

    public void setId(long id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setDocumento(String documento) { this.documento = documento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setCuentas(List<CuentaModel> cuentas) { this.cuentas = cuentas; }
}
