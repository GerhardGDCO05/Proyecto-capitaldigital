package com.example.capitalDigital.usuario.repositories;
import com.example.capitalDigital.usuario.models.CuentaModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaRepository extends JpaRepository<CuentaModel, Long> {

}

