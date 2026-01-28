package com.example.capitalDigital.usuario.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import com.example.capitalDigital.usuario.models.UsuarioModel;
import com.example.capitalDigital.usuario.repositories.UsuarioRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class UsuarioServices {
    @Autowired
    UsuarioRepository usuarioRepository;
    
    @Autowired
    private Validator validator;

    public ArrayList<UsuarioModel> obtenerUsuarios() {
        return (ArrayList<UsuarioModel>) usuarioRepository.findAll();
    }

    public UsuarioModel crearUsuario(Map<String, Object> datosUsuario) {
        try {
            // Crear nueva instancia de UsuarioModel
            UsuarioModel usuario = new UsuarioModel();
            
            // Mapear los datos recibidos al objeto UsuarioModel
            if (datosUsuario.get("nombre") != null) {
                usuario.setNombre((String) datosUsuario.get("nombre"));
            }
            if (datosUsuario.get("apellido") != null) {
                usuario.setApellido((String) datosUsuario.get("apellido"));
            }
            if (datosUsuario.get("documento") != null) {
                usuario.setDocumento((String) datosUsuario.get("documento"));
            }
            if (datosUsuario.get("numeroDocumento") != null) {
                usuario.setNumeroDocumento((String) datosUsuario.get("numeroDocumento"));
            }
            if (datosUsuario.get("fechaNacimiento") != null) {
                // Convertir String a LocalDate
                String fechaStr = (String) datosUsuario.get("fechaNacimiento");
                LocalDate fecha = LocalDate.parse(fechaStr);
                usuario.setFechaNacimiento(fecha);
            }
            if (datosUsuario.get("direccion") != null) {
                usuario.setDireccion((String) datosUsuario.get("direccion"));
            }
            if (datosUsuario.get("email") != null) {
                usuario.setEmail((String) datosUsuario.get("email"));
            }
            if (datosUsuario.get("codigoPostal") != null) {
                usuario.setCodigoPostal((String) datosUsuario.get("codigoPostal"));
            }
            if (datosUsuario.get("banco") != null) {
                usuario.setBanco((String) datosUsuario.get("banco"));
            }
            if (datosUsuario.get("numeroCuenta") != null) {
                usuario.setNumeroCuenta((String) datosUsuario.get("numeroCuenta"));
            }
            if (datosUsuario.get("password") != null) {
                usuario.setPassword((String) datosUsuario.get("password"));
            }
            if (datosUsuario.get("activo") != null) {
                usuario.setActivo((Boolean) datosUsuario.get("activo"));
            } else {
                usuario.setActivo(true); // Valor por defecto
            }

            // Validar usando las anotaciones del modelo
            validarUsuario(usuario);

            System.out.println("Usuario creado en el servicio: " + usuario);

            // Validar si el usuario ya existe
            Optional<UsuarioModel> usuarioExistente = usuarioRepository.findByNumeroDocumento(usuario.getNumeroDocumento());
            if (usuarioExistente.isPresent()) {
                throw new RuntimeException("El número de documento ya está en uso.");
            }

            // Validar si el email ya existe
            Optional<UsuarioModel> emailExistente = usuarioRepository.findByEmail(usuario.getEmail());
            if (emailExistente.isPresent()) {
                throw new RuntimeException("El email ya está en uso.");
            }
            return guardarUsuario(usuario);
        } catch (Exception e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al crear usuario: " + e.getMessage());
        }
    }

    private void validarUsuario(UsuarioModel usuario) {
        Set<ConstraintViolation<UsuarioModel>> violations = validator.validate(usuario);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<UsuarioModel> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            throw new RuntimeException("Errores de validación: " + sb.toString());
        }
    }

    public UsuarioModel guardarUsuario(UsuarioModel usuario) {
        try {
            System.out.println("Datos recibidos en el backend: " + usuario);

            Optional<UsuarioModel> usuarioExistente = usuarioRepository.findByNumeroDocumento(usuario.getNumeroDocumento());

            if (usuarioExistente.isEmpty()) { // Solo validar email si el usuario no existe aún
                Optional<UsuarioModel> emailExistente = usuarioRepository.findByEmail(usuario.getEmail());
                if (emailExistente.isPresent()) {
                    throw new RuntimeException("El email ya está en uso.");
                }
            }

            if (usuario.getFechaNacimiento() == null) {
                throw new RuntimeException("Ingrese una fecha válida.");
            }

            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    } 
    public UsuarioModel actualizarUsuario(String numeroDocumento, Map<String, Object> datosUsuario) {
        Optional<UsuarioModel> usuarioExistente = usuarioRepository.findByNumeroDocumento(numeroDocumento);
        
        if (usuarioExistente.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado.");
        }

        UsuarioModel usuario = usuarioExistente.get();
        
        try {
            // Mapear los datos recibidos al objeto UsuarioModel existente
            if (datosUsuario.get("nombre") != null) {
                usuario.setNombre((String) datosUsuario.get("nombre"));
            }
            if (datosUsuario.get("apellido") != null) {
                usuario.setApellido((String) datosUsuario.get("apellido"));
            }
            if (datosUsuario.get("documento") != null) {
                usuario.setDocumento((String) datosUsuario.get("documento"));
            }
            if (datosUsuario.get("numeroDocumento") != null) {
                String nuevoNumeroDocumento = (String) datosUsuario.get("numeroDocumento");
                // Validar que el nuevo número de documento no esté en uso por otro usuario
                if (!nuevoNumeroDocumento.equals(usuario.getNumeroDocumento())) {
                    Optional<UsuarioModel> usuarioConMismoDocumento = usuarioRepository.findByNumeroDocumento(nuevoNumeroDocumento);
                    if (usuarioConMismoDocumento.isPresent()) {
                        throw new RuntimeException("El número de documento ya está en uso por otro usuario.");
                    }
                }
                usuario.setNumeroDocumento(nuevoNumeroDocumento);
            }
            if (datosUsuario.get("fechaNacimiento") != null) {
                String fechaStr = (String) datosUsuario.get("fechaNacimiento");
                LocalDate fecha = LocalDate.parse(fechaStr);
                usuario.setFechaNacimiento(fecha);
            }
            if (datosUsuario.get("direccion") != null) {
                usuario.setDireccion((String) datosUsuario.get("direccion"));
            }
            if (datosUsuario.get("email") != null) {
                String nuevoEmail = (String) datosUsuario.get("email");
                usuario.setEmail(nuevoEmail);
            }
            if (datosUsuario.get("codigoPostal") != null) {
                usuario.setCodigoPostal((String) datosUsuario.get("codigoPostal"));
            }
            if (datosUsuario.get("banco") != null) {
                usuario.setBanco((String) datosUsuario.get("banco"));
            }
            if (datosUsuario.get("numeroCuenta") != null) {
                usuario.setNumeroCuenta((String) datosUsuario.get("numeroCuenta"));
            }
            if (datosUsuario.get("password") != null) {
                usuario.setPassword((String) datosUsuario.get("password"));
            }

            // Manejar el estado activo y la fecha de bloqueo
            if (datosUsuario.get("activo") != null) {
                boolean nuevoEstadoActivo = (Boolean) datosUsuario.get("activo");
                
                // Si el usuario se bloquea ahora (cambia de activo a inactivo)
                if (!nuevoEstadoActivo && usuario.getActivo()) {
                    usuario.setActivo(false);
                    usuario.setFechaBloqueo(LocalDateTime.now());
                } 
                // Si el usuario se desbloquea (cambia de inactivo a activo)
                else if (nuevoEstadoActivo && !usuario.getActivo()) {
                    usuario.setActivo(true);
                    usuario.setFechaBloqueo(null);
                }
                // Si no hay cambio en el estado, mantener el estado actual
                else {
                    usuario.setActivo(nuevoEstadoActivo);
                }
            }

            // Validar el usuario usando las anotaciones del modelo
            validarUsuario(usuario);

            // Guardar el usuario actualizado
            return guardarUsuario(usuario);
            
        } catch (Exception e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar usuario: " + e.getMessage());
        }
    }

    public Optional<UsuarioModel> obtenerPorNumeroDocumento(String numeroDocumento) {
        System.out.println("Buscando usuario con numero de documento: " + numeroDocumento);
        Optional<UsuarioModel> usuario = usuarioRepository.findByNumeroDocumento(numeroDocumento);
        System.out.println("Usuario encontrado: " + usuario.orElse(null));
        return usuario;
    }

    public boolean eliminarUsuarioPorNumeroDocumento(String numeroDocumento) {
        try {
            Optional<UsuarioModel> usuario = usuarioRepository.findByNumeroDocumento(numeroDocumento);
            if (usuario.isPresent()) {
                usuarioRepository.delete(usuario.get());
                return true;
            }
            return false;
        } catch (Exception err) {
            System.err.println("Error al eliminar usuario: " + err.getMessage());
            return false;
        }
    }
}