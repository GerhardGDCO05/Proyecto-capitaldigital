import axios from "axios";

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"
});


// Interceptores para manejar errores globalmente (opcional)
apiClient.interceptors.response.use(
  response => response,
  error => {
    console.error("Error en la API:", error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export default apiClient;
