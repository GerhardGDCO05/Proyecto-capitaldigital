package com.example.capitalDigital.usuario.repositories;

import com.example.capitalDigital.usuario.models.CuentaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<CuentaModel, Long> {
    // Método para buscar cuentas por ID de usuario
    List<CuentaModel> findByUsuarioId(Long usuarioId);
}
