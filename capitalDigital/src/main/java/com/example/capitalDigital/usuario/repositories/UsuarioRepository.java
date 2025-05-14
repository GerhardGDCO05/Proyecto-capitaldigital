package com.example.capitalDigital.usuario.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.example.capitalDigital.usuario.models.UsuarioModel;

@Repository
public interface UsuarioRepository extends CrudRepository<UsuarioModel, Long> {
    @Query("SELECT COALESCE(MIN(u.id) + 1, 1) FROM UsuarioModel u WHERE u.id NOT IN (SELECT id FROM UsuarioModel)")
    Long obtenerIdDisponible();
    

}
