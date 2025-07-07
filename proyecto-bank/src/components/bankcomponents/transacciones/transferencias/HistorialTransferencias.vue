<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUsuarioStore } from 'C:/Users/simon/OneDrive/Escritorio/CapitalDigitalISProyect/Proyecto-capitaldigital/proyecto-bank/src/stores/useUsuarioStore.js';
import axios from 'axios';

const router = useRouter();
const usuarioStore = useUsuarioStore();
const transacciones = ref([]);
const mensaje = ref('');

// Función para formatear el monto como "1.234,56 Bs"
const formatMonto = (monto: number): string => {
  if (!monto && monto !== 0) return '0,00 Bs';
  const value = Number(monto).toFixed(2).replace('.', ',');
  const [integerPart, decimalPart] = value.split(',');
  const formattedInteger = parseInt(integerPart, 10).toLocaleString('es-VE');
  return `${formattedInteger},${decimalPart} Bs`;
};

const cargarTransacciones = async () => {
  const numeroDocumento = usuarioStore.usuario?.numeroDocumento;

  if (!numeroDocumento) {
    mensaje.value = 'Por favor, inicia sesión nuevamente.';
    console.error('No se encontró numeroDocumento en el store de usuario');
    router.push('/login');
    return;
  }

  try {
    console.log('Haciendo petición a: http://localhost:8080/transactionhistory/numeroDocumento/' + numeroDocumento);
    const response = await axios.get(`http://localhost:8080/transactionhistory/numeroDocumento/${numeroDocumento}`);
    console.log('Respuesta de transacciones:', response.data);
    transacciones.value = response.data;
    if (transacciones.value.length === 0) {
      mensaje.value = 'No tienes transacciones registradas.';
    } else {
      mensaje.value = '';
    }
  } catch (error) {
    console.error('Error al cargar transacciones:', error);
    transacciones.value = [];
    mensaje.value = 'Error al cargar el historial de transacciones: ' + (error.response?.data || error.message);
  }
};

onMounted(() => {
  cargarTransacciones();
});
</script>

<template>
  <header class="header">
    <h1>Historial de Transacciones</h1>
  </header>
  <main>
    <div v-if="mensaje" class="mensaje">{{ mensaje }}</div>
    <div v-else class="table-container">
      <table>
        <thead>
        <tr>
          <th>Banco de Origen</th>
          <th>Número de Cuenta de Origen</th>
          <th>Destinatario</th>
          <th>Número de Cuenta de Destino</th>
          <th>Banco de Destino</th>
          <th>Concepto</th>
          <th>Monto de la Transacción</th>
          <th>Fecha</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="transaccion in transacciones" :key="transaccion.numCuentaOrigen + transaccion.fecha">
          <td>{{ transaccion.bancoOrigen }}</td>
          <td>{{ transaccion.numCuentaOrigen }}</td>
          <td>{{ transaccion.beneficiario }}</td>
          <td>{{ transaccion.numCuentaDestino }}</td>
          <td>{{ transaccion.bancoDestino }}</td>
          <td>{{ transaccion.concepto }}</td>
          <td>{{ formatMonto(transaccion.monto) }}</td>
          <td>{{ transaccion.fecha }}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </main>
</template>

<style scoped>
.header {
  position: absolute;
  top: 0%;
  left: 15%;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  font-size: 16px;
  width: 80%;
  height: 60px;
  background-color: rgb(255, 255, 255);
  border-bottom: 2px solid black;
  display: flex;
  align-items: center;
  padding-left: 20px;
  margin: 20px auto 0; /* Centrado horizontal y margen superior */
}

main {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  padding: 20px;
  min-height: calc(100vh - 80px); /* Ajusta la altura para evitar que el contenido se vaya al fondo */
}

.table-container {
  position: absolute;
  top: 10%;
  left: 15%;
  width: 80%;
  max-height: 60vh; /* Limita la altura máxima */
  overflow-y: auto; /* Añade desplazamiento vertical */
  background-color: #fff;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  margin-top: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #007BFF;
  color: white;
  position: sticky;
  top: 0;
  z-index: 1;
}

tr:hover {
  background-color: #f1f1f1;
}

.mensaje {
  text-align: center;
  color: #666;
  font-size: 16px;
  margin-top: 20px;
}

/* Ajustes para la barra lateral activa */
.main-content.sidebar-active .header,
.main-content.sidebar-active .table-container {
  width: 85%; /* Ajusta el ancho cuando la barra lateral está activa */
  margin-left: auto;
  margin-right: auto;
}
</style>