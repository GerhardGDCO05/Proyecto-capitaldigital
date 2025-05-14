package com.example.capitalDigital.usuario.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.capitalDigital.usuario.models.CuentaModel;
import com.example.capitalDigital.usuario.repositories.CuentaRepository;

@Service
public class CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    public CuentaModel guardarCuenta(CuentaModel cuenta) {
        return cuentaRepository.save(cuenta);
    }
}

