import apiClient from "./apiClient";

export default {
  obtenerUsuarios() {
    return apiClient.get("/usuario");
  },

  guardarUsuario(usuario) {
    console.log("usuario", usuario);
    return apiClient.post("/usuario", usuario);
  },

  obtenerUsuarioPorEmail(email) {
    return apiClient.get(`/usuario/${email}`);
  },

  modificarUsuarioPorEmail(){
    return apiClient.put(`/usuario/${email}`)
  },

  eliminarUsuario(email) {
    return apiClient.delete(`/usuario/${email}`);
  },

  /*agregarCuentaPorEmail(email, cuenta) {
    return apiClient.post(`/cuenta/usuario/${email}/agregar`, cuenta);
  },

  modificarCuentaPorEmail(email,cuenta){
    return apiClient.put(`/cuenta/usuario/${email}/agregar`, cuenta);
  },

  obtenerCuentaPorEmail(email,cuenta){
    return apiClient.get(`/cuenta/usuario/${email}/agregar`, cuenta);
  },

  eliminarCuentaPorEmail(email,cuenta){
    return apiClient.get(`/cuenta/usuario/${email}/agregar`, cuenta);
  }*/
};
