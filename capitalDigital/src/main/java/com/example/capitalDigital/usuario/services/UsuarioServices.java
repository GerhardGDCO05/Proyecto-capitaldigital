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
            System.out.println("Datos recibidos en el backend: " + usuario);  // ✅ Ver qué llega realmente
            if (usuario.getCuentas() != null && !usuario.getCuentas().isEmpty()) {
                usuario.getCuentas().forEach(cuenta -> cuenta.setUsuario(usuario));
            }
            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    

    public Optional<UsuarioModel> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public boolean eliminarUsuario(Long id) {
        try {
            usuarioRepository.deleteById(id);
            return true;
        } catch (Exception err) {
            return false;
        }
    }
}
