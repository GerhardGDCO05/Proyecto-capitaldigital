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
