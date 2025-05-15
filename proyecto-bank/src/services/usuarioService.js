import apiClient from "./apiClient";

export default {
  obtenerUsuarios() {
    return apiClient.get("/usuario");
  },

  guardarUsuario(usuario) {
    console.log("usuario", usuario);
    return apiClient.post("/usuario", usuario);
  },

  obtenerUsuarioPorId(id) {
    return apiClient.get(`/usuario/${id}`);
  },

  eliminarUsuario(id) {
    return apiClient.delete(`/usuario/${id}`);
  }
};
