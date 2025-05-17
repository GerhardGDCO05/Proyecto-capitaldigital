package com.example.capitalDigital.usuario.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.example.capitalDigital.usuario.models.CuentaModel;
import com.example.capitalDigital.usuario.repositories.CuentaRepository;

@Service
public class CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    // Guardar cuenta
    public CuentaModel guardarCuenta(CuentaModel cuenta) {
        return cuentaRepository.save(cuenta);
    }

    // Obtener cuenta por ID
    public Optional<CuentaModel> obtenerCuentaPorId(Long id) {
        return cuentaRepository.findById(id);
    }

    // Obtener todas las cuentas de un usuario
    public List<CuentaModel> obtenerCuentasPorUsuarioId(Long usuarioId) {
        return cuentaRepository.findByUsuarioId(usuarioId);
    }

    // Eliminar cuenta por ID
    public boolean eliminarCuentaPorId(Long id) {
        if (cuentaRepository.existsById(id)) {
            cuentaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
