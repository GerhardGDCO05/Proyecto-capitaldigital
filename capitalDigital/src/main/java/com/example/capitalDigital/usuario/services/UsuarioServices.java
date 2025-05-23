package com.example.capitalDigital.usuario.services;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.capitalDigital.usuario.models.UsuarioModel;
import com.example.capitalDigital.usuario.repositories.UsuarioRepository;

@Service
public class UsuarioServices {
    @Autowired
    UsuarioRepository usuarioRepository;

    public ArrayList<UsuarioModel> obtenerUsuarios() {
        return (ArrayList<UsuarioModel>) usuarioRepository.findAll();
    }

    public UsuarioModel guardarUsuario(UsuarioModel usuario) {
        try {
            System.out.println("Datos recibidos en el backend: " + usuario);

            // Validación: Verificar si el email ya está registrado
            Optional<UsuarioModel> usuarioExistente = usuarioRepository.findByEmail(usuario.getEmail());
            if (usuarioExistente.isPresent()) {
                throw new RuntimeException("El email ya está en uso.");
            }
            if (usuario.getFechaNacimiento() == null) {
                throw new RuntimeException("Ingrese Una Fecha Valida");
            }

            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Optional<UsuarioModel> obtenerPorEmail(String email) {
        System.out.println("Buscando usuario con email: " + email);
        Optional<UsuarioModel> usuario = usuarioRepository.findByEmail(email);
        System.out.println("Usuario encontrado: " + usuario.orElse(null));
        return usuario;
    }

    public boolean eliminarUsuarioPorEmail(String email) {
        try {
            Optional<UsuarioModel> usuario = usuarioRepository.findByEmail(email);
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
