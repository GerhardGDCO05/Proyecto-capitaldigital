<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUsuarioStore } from 'C:/Users/simon/OneDrive/Escritorio/CapitalDigitalISProyect/Proyecto-capitaldigital/proyecto-bank/src/stores/useUsuarioStore.js';
import axios from 'axios';
import Swal from 'sweetalert2';
import { Chart as ChartJS, ArcElement, LineElement, CategoryScale, LinearScale, PointElement, Filler, Tooltip, Legend } from 'chart.js';
import { Pie, Line } from 'vue-chartjs';
import ChartDataLabels from 'chartjs-plugin-datalabels';

// Registrar componentes de Chart.js
ChartJS.register(ArcElement, LineElement, CategoryScale, LinearScale, PointElement, Filler, Tooltip, Legend, ChartDataLabels);

const router = useRouter();
const usuarioStore = useUsuarioStore();
const documento = usuarioStore.usuario?.numeroDocumento;

const transacciones = ref([]);
const mensaje = ref('');
const cuentasXML = ref([]);
const cuentasConSaldo = ref([]);
const chartPieData = ref({
  labels: [],
  datasets: [{
    data: [],
    backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'],
    hoverOffset: 4
  }]
});
const chartLineData = ref({
  labels: [],
  datasets: [{
    label: 'Patrimonio Neto (Bs)',
    data: [],
    borderColor: '#36A2EB',
    backgroundColor: 'rgba(54, 162, 235, 0.2)',
    fill: true,
    tension: 0.4
  }]
});

// Función para parsear números en formato venezolano (100.000,00 -> 100000.00)
const parseVenezuelanNumber = (str: string) => {
  if (!str) return 0;
  const cleanStr = str.replace(' Bs', '').replace(/\./g, '').replace(',', '.');
  return parseFloat(cleanStr) || 0;
};

// Función para formatear números al formato venezolano (100000.00 -> 100.000,00)
const formatVenezuelanNumber = (num: number) => {
  if (isNaN(num)) return '0,00 Bs';
  return num.toFixed(2).replace('.', ',').replace(/\B(?=(\d{3})+(?!\d))/g, '.') + ' Bs';
};

// Función para obtener cuentas del XML desde Spring Boot
const fetchCuentasXML = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/cuenta/numeroDocumento/${documento}`);
    cuentasXML.value = response.data;
    console.log('Cuentas XML cargadas:', cuentasXML.value);
  } catch (error) {
    console.error('Error al cargar cuentas del XML:', error);
    await Swal.fire({
      title: 'Error',
      text: 'No se pudieron cargar las cuentas. Por favor, intenta de nuevo.',
      icon: 'error',
      confirmButtonText: 'OK'
    });
  }
};

// Función para parsear el archivo TXT
const parseTXT = async () => {
  try {
    const response = await fetch('/data/ListaCuentasDetalladasBancos.txt');
    const texto = await response.text();
    const bloques = texto.split('-*-*-*-*-*').filter(bloque => bloque.trim());
    const datosTXT: { [key: string]: { tipoCuenta: string; saldo: string } } = {};

    bloques.forEach(bloque => {
      const lineas = bloque.trim().split('\n');
      const cuenta: { [key: string]: string } = {};
      lineas.forEach(linea => {
        const [clave, valor] = linea.split(':');
        if (clave && valor) cuenta[clave.trim()] = valor.trim();
      });
      if (cuenta.NCuenta) {
        datosTXT[cuenta.NCuenta] = {
          tipoCuenta: cuenta.TipoCuenta || 'Desconocido',
          saldo: cuenta.Saldo || '0,00 Bs'
        };
      }
    });
    console.log('Datos TXT parseados:', datosTXT);
    return datosTXT;
  } catch (error) {
    console.error('Error al leer cuentas.txt:', error);
    return {};
  }
};

// Función para obtener transacciones
const cargarTransacciones = async () => {
  if (!documento) {
    mensaje.value = 'Por favor, inicia sesión nuevamente.';
    console.error('No se encontró numeroDocumento en el store de usuario');
    router.push('/login');
    return;
  }

  try {
    console.log('Haciendo petición a: http://localhost:8080/transactionhistory/numeroDocumento/' + documento);
    const response = await axios.get(`http://localhost:8080/transactionhistory/numeroDocumento/${documento}`);
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

// Función para preparar datos del pie chart
const preparePieChartData = () => {
  const labels = cuentasConSaldo.value.map(cuenta => `${cuenta.banco} - ${cuenta.numeroCuenta}`);
  const saldos = cuentasConSaldo.value.map(cuenta => parseVenezuelanNumber(cuenta.saldo));
  chartPieData.value.labels = labels;
  chartPieData.value.datasets[0].data = saldos;
  console.log('Datos del pie chart:', chartPieData.value);
};

// Función para generar datos históricos para el gráfico de líneas
const generateHistoricalData = (currentTotal: number) => {
  const data = [];
  for (let i = 0; i < 11; i++) {
    const variation = Math.random() * 0.4 - 0.2; // ±20%
    const value = currentTotal * (1 + variation);
    data.push(Math.max(0, value));
  }
  data.push(currentTotal);
  return data;
};

// Función para generar etiquetas de meses
const generateMonthLabels = () => {
  const months = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
  const labels = [];
  const currentDate = new Date(2025, 6); // Julio 2025
  for (let i = 11; i >= 0; i--) {
    const date = new Date(currentDate.getFullYear(), currentDate.getMonth() - i);
    labels.push(`${months[date.getMonth()]} ${date.getFullYear()}`);
  }
  return labels;
};

// Función para preparar datos del gráfico de líneas
const prepareLineChartData = () => {
  const totalSaldo = cuentasConSaldo.value.reduce((total, cuenta) => total + parseVenezuelanNumber(cuenta.saldo), 0);
  chartLineData.value.labels = generateMonthLabels();
  chartLineData.value.datasets[0].data = generateHistoricalData(totalSaldo);
  console.log('Datos del gráfico de líneas:', chartLineData.value);
};

// Cargar datos al montar el componente
onMounted(async () => {
  if (!documento) {
    await Swal.fire({
      title: 'Error',
      text: 'No hay usuario logueado. Por favor, inicia sesión.',
      icon: 'error',
      confirmButtonText: 'OK'
    });
    router.push('/login');
    return;
  }

  await fetchCuentasXML();
  const datosTXT = await parseTXT();
  cuentasConSaldo.value = cuentasXML.value.map(cuenta => ({
    ...cuenta,
    saldo: datosTXT[cuenta.numeroCuenta]?.saldo || '0,00 Bs',
    tipoCuenta: datosTXT[cuenta.numeroCuenta]?.tipoCuenta || 'Desconocido'
  }));
  console.log('Cuentas con saldo:', cuentasConSaldo.value);

  preparePieChartData();
  prepareLineChartData();
  await cargarTransacciones();
});
</script>

<template>
  <header class="header">
    <h1>Vista General</h1>
  </header>
  <main>
    <div class="main-content">
      <div class="posicion-global">
        <h2 class="title">Distribución de Saldos</h2>
        <div v-if="cuentasConSaldo.length === 0" class="mensaje">No hay cuentas disponibles para mostrar.</div>
        <Pie
            v-else
            id="pieChart"
            :data="chartPieData"
            :options="{
            responsive: true,
            plugins: {
              legend: { position: 'top' },
              title: { display: true, text: 'Distribución de Saldos por Cuenta' },
              datalabels: {
                formatter: (value, ctx) => {
                  const total = ctx.dataset.data.reduce((acc: number, val: number) => acc + val, 0);
                  const percentage = total ? ((value / total) * 100).toFixed(2) + '%' : '0%';
                  return `${percentage}\n${formatVenezuelanNumber(value)}`;
                },
                color: '#fff',
                font: { size: 14 }
              }
            }
          }"
            style="max-width: 400px; max-height: 400px;"
        />
      </div>
      <div class="patrimonio-neto">
        <h2 class="title">Evolución del Patrimonio</h2>
        <div v-if="cuentasConSaldo.length === 0" class="mensaje">No hay datos de patrimonio disponibles.</div>
        <Line
            v-else
            id="lineChart"
            :data="chartLineData"
            :options="{
            responsive: true,
            plugins: {
              legend: { position: 'top' },
              title: { display: true, text: 'Evolución del Patrimonio Neto (Últimos 12 Meses)' },
              datalabels: {
                formatter: (value: number) => formatVenezuelanNumber(value),
                color: '#333',
                font: { size: 12 },
                anchor: 'end',
                align: 'top'
              },
              tooltip: {
                callbacks: {
                  label: (context) => `${context.dataset.label}: ${formatVenezuelanNumber(context.parsed.y)}`
                }
              }
            },
            scales: {
              y: {
                beginAtZero: true,
                ticks: {
                  callback: (value: number) => formatVenezuelanNumber(value)
                }
              }
            }
          }"
            style="max-width: 800px; max-height: 300px;"
        />
        <div class="saldo-total">
          <h3>Patrimonio Actual (Julio 2025): {{ formatVenezuelanNumber(cuentasConSaldo.reduce((total, cuenta) => total + parseVenezuelanNumber(cuenta.saldo), 0)) }}</h3>
        </div>
      </div>
      <div class="historial-de-transacciones">
        <h2 class="title">Historial de Transacciones</h2>
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
              <td>{{ formatVenezuelanNumber(Number(transaccion.monto)) }}</td>
              <td>{{ new Date(transaccion.fecha).toLocaleString('es-VE', { dateStyle: 'short', timeStyle: 'short' }) }}</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
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
  margin: 20px auto 0;
}

main {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  padding: 20px;
  min-height: calc(100vh - 80px);
}

.main-content {
  position: absolute;
  top: 10%;
  left: 15%;
  display: grid;
  grid-template-areas:
    "posicionglobal patrimonioneto patrimonioneto"
    "posicionglobal patrimonioneto patrimonioneto"
    "posicionglobal historialTransferencias historialTransferencias";
  grid-template-columns: 1fr 2fr 2fr;
  gap: 20px;
  width: 80%;
  margin-top: 20px;
}

.posicion-global {
  grid-area: posicionglobal;
  background: #fff;
  border-radius: 20px;
  border: solid 3px #0a3cff;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.patrimonio-neto {
  grid-area: patrimonioneto;
  background: #fff;
  border-radius: 20px;
  border: solid 3px #0a3cff;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.historial-de-transacciones {
  grid-area: historialTransferencias;
  background: #fff;
  border-radius: 20px;
  border: solid 3px #0a3cff;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 1.5rem;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  margin-bottom: 20px;
}

.saldo-total {
  margin-top: 20px;
  font-size: 1.2rem;
  font-weight: bold;
}

.table-container {
  max-height: 40vh;
  overflow-y: auto;
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
.main-content.sidebar-active .main-content {
  width: 85%;
  margin-left: auto;
  margin-right: auto;
}
</style>