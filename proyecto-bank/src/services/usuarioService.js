import apiClient from "./apiClient";

export default {
  obtenerUsuarios() {
    return apiClient.get("/usuario");
  },

  guardarUsuario(usuario) {
    console.log("usuario", usuario);
    return apiClient.post("/usuario", usuario);
  },

  obtenerUsuarioPorNumeroDocumento(numeroDocumento) {
    return apiClient.get(`/usuario/numeroDocumento/${numeroDocumento}`);
  },

  modificarUsuarioPorNumeroDocumento(numeroDocumento){
    return apiClient.put(`/usuario/numeroDocumento/${numeroDocumento}`)
  },

  eliminarUsuarioPorNumeroDocumento(numeroDocumento) {
    return apiClient.delete(`/usuario/numeroDocumento/${numeroDocumento}`);
  },
  agregarCuentaPorNumeroDocumento(numeroDocumento, cuenta) {
    return apiClient.post(`/cuenta/numeroDocumento/${numeroDocumento}`, cuenta);
  },

  modificarCuentaPorNumeroDocumento(numeroDocumento, nombreCuenta, cuenta) {
    return apiClient.put(`/cuenta/numeroDocumento/${numeroDocumento}/nombreCuenta/${nombreCuenta}`, cuenta);
  },

  obtenerCuentaPorNumeroDocumento(numeroDocumento) {
    return apiClient.get(`/cuenta/numeroDocumento/${numeroDocumento}`);
  },

  eliminarCuentaPorNumeroDocumento(numeroDocumento, numeroCuenta) {
    return apiClient.delete(`/cuenta/numeroDocumento/${numeroDocumento}/numeroCuenta/${numeroCuenta}`); 
  }

};
