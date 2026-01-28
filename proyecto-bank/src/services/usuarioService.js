import ModificarBeneficiario from "@/components/bankcomponents/transacciones/crudbeneficiarios/ModificarBeneficiario.vue";
import apiClient from "./apiClient";

export default {
  //usuario
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

  modificarUsuarioPorNumeroDocumento(numeroDocumento, usuario) {
    return apiClient.put(`/usuario/numeroDocumento/${numeroDocumento}`, usuario);
  },

  eliminarUsuarioPorNumeroDocumento(numeroDocumento) {
    return apiClient.delete(`/usuario/numeroDocumento/${numeroDocumento}`);
  },

  //cuentas
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
  },
  
  //beneficiario
  agregarBeneficiario(holder){
    return apiClient.post(`/beneficiaries/${holder}`); 
  },

  ModificarBeneficiario(holder, numeroCuentaBeneficiario) {
    return apiClient.put(`/beneficiaries/${holder}/${numeroCuentaBeneficiario}`);
  },

  obtenerTodosLosBeneficiarios(holder) {
    return apiClient.get(`/beneficiaries/${holder}`);
  },

  obtenerBeneficiarioEspecifico(holder,numeroCuenta) {
    return apiClient.get(`/beneficiaries/${holder}/${numeroCuenta}`);
  },

  eliminarBeneficiario(holder, numeroCuenta) {
    return apiClient.delete(`/beneficiaries/${holder}/${numeroCuenta}`);
  },

  //Metas
  agregarMeta(numeroDocumento){
    return apiClient.post(`/meta/numeroDocumento/${numeroDocumento}`,nuevameta)
  },
  obtenerMeta(numeroDocumento){
    return apiClient.get(`/meta/numeroDocumento/${numeroDocumento}`)
  },
  modificarMeta(numeroDocumento, nombreMeta){
    return apiClient.put(`/meta/numeroDocumento/${numeroDocumento}/${nombreMeta}`,nuevameta)
  },
  eliminarMeta(numeroDocumento,nombreMeta){
    return apiClient.delete(`/meta/${numeroDocumento}/${nombreMeta}`)
  }
};
