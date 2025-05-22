package com.example.capitalDigital.usuario.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.capitalDigital.usuario.models.UsuarioModel;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {
    Optional<UsuarioModel> findByEmail(String email);
    Optional<UsuarioModel> findByNumeroDocumento(String numeroDocumento);  // ✅ Corrección
}
