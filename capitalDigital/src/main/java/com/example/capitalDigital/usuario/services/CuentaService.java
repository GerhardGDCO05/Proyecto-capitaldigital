package com.example.capitalDigital.usuario.services;

import com.example.capitalDigital.usuario.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
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

    public List<CuentaModel> obtenerCuentasPorEmail(String email) {
        return cuentaRepository.findByUsuarioEmail(email);
    }

    @Transactional
    public boolean eliminarCuentasPorEmail(String email) {
        List<CuentaModel> cuentas = cuentaRepository.findByUsuarioEmail(email);
        if (!cuentas.isEmpty()) {
            cuentaRepository.deleteAll(cuentas);
            return true;
        }
        return false;
    }

    @Transactional
    public CuentaModel modificarCuentaPorEmail(String email, Long cuentaId, CuentaModel cuentaActualizada) {
        List<CuentaModel> cuentas = cuentaRepository.findByUsuarioEmail(email);
        Optional<CuentaModel> cuentaOptional = cuentas.stream()
                .filter(cuenta -> cuenta.getId().equals(cuentaId))
                .findFirst();

        if (cuentaOptional.isPresent()) {
            CuentaModel cuentaExistente = cuentaOptional.get();
            cuentaExistente.setBanco(cuentaActualizada.getBanco());
            cuentaExistente.setNumeroCuenta(cuentaActualizada.getNumeroCuenta());
            return cuentaRepository.save(cuentaExistente);
        }
        return null;
    }
}

