<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useUsuarioStore } from '@/stores/useUsuarioStore';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import Chart from 'chart.js/auto';
import ChartDataLabels from 'chartjs-plugin-datalabels';

const router = useRouter();
const usuarioStore = useUsuarioStore();
const documento = usuarioStore.usuario?.numeroDocumento;

const cuentasXML = ref([]);
const cuentasConSaldo = ref([]);
const chartData = ref({
  labels: [],
  datasets: [{
    label: 'Patrimonio Neto (Bs)',
    data: [],
    borderColor: '#36A2EB',
    backgroundColor: 'rgba(54, 162, 235, 0.2)',
    fill: true,
    tension: 0.4 // Suaviza las líneas
  }]
});

// Función para parsear números en formato venezolano (100.000,00 -> 100000.00)
const parseVenezuelanNumber = (str) => {
  if (!str) return 0;
  const cleanStr = str.replace(' Bs', '').replace(/\./g, '').replace(',', '.');
  return parseFloat(cleanStr) || 0;
};

// Función para formatear números al formato venezolano (100000.00 -> 100.000,00)
const formatVenezuelanNumber = (num) => {
  if (isNaN(num)) return '0,00';
  return num.toFixed(2).replace('.', ',').replace(/\B(?=(\d{3})+(?!\d))/g, '.') + ' Bs';
};

// Función para obtener cuentas del XML desde Spring Boot
const fetchCuentasXML = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/cuenta/numeroDocumento/${documento}`);
    cuentasXML.value = response.data;
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
    const datosTXT = {};

    bloques.forEach(bloque => {
      const lineas = bloque.trim().split('\n');
      const cuenta = {};
      lineas.forEach(linea => {
        const [clave, valor] = linea.split(':');
        if (clave && valor) {
          cuenta[clave.trim()] = valor.trim();
        }
      });
      if (cuenta.NCuenta) {
        datosTXT[cuenta.NCuenta] = {
          tipoCuenta: cuenta.TipoCuenta || 'Desconocido',
          saldo: cuenta.Saldo || '0,00 Bs'
        };
      }
    });

    return datosTXT;
  } catch (error) {
    console.error('Error al leer cuentas.txt:', error);
    return {};
  }
};

// Función para generar datos históricos aleatorios
const generateHistoricalData = (currentTotal) => {
  const data = [];
  // Generar datos para los 11 meses anteriores
  for (let i = 0; i < 11; i++) {
    // Generar un valor aleatorio entre el 80% y el 120% del saldo total actual
    const variation = Math.random() * 0.4 - 0.2; // ±20%
    const value = currentTotal * (1 + variation);
    data.push(Math.max(0, value)); // Asegurar que no sea negativo
  }
  // Añadir el saldo total actual para julio 2025
  data.push(currentTotal);
  return data;
};

// Función para generar etiquetas de meses (de Ago 2024 a Jul 2025)
const generateMonthLabels = () => {
  const months = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
  const labels = [];
  const currentDate = new Date(2025, 6); // Julio 2025
  for (let i = 11; i >= 0; i--) {
    const date = new Date(currentDate.getFullYear(), currentDate.getMonth() - i);
    const monthName = months[date.getMonth()];
    const year = date.getFullYear();
    labels.push(`${monthName} ${year}`);
  }
  return labels;
};

// Función para preparar datos para el gráfico
const prepareChartData = () => {
  const totalSaldo = cuentasConSaldo.value.reduce((total, cuenta) => {
    return total + parseVenezuelanNumber(cuenta.saldo);
  }, 0);

  chartData.value.labels = generateMonthLabels();
  chartData.value.datasets[0].data = generateHistoricalData(totalSaldo);
};

// Inicializar el gráfico de líneas
const initChart = () => {
  const ctx = document.getElementById('lineChart').getContext('2d');
  new Chart(ctx, {
    type: 'line',
    data: chartData.value,
    options: {
      responsive: true,
      plugins: {
        legend: {
          position: 'top',
        },
        title: {
          display: true,
          text: 'Evolución del Patrimonio Neto (Últimos 12 Meses)'
        },
        datalabels: {
          formatter: (value) => formatVenezuelanNumber(value),
          color: '#333',
          font: {
            size: 12
          },
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
            callback: (value) => formatVenezuelanNumber(value)
          }
        }
      }
    },
    plugins: [ChartDataLabels]
  });
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

  prepareChartData();
  initChart();
});
</script>

<template>
  <header class="header">
    <h1>Patrimonio Neto</h1>
  </header>

  <div class="patrimonio-container">
    <div class="card-line-chart">
      <h2 class="title">Evolución del Patrimonio</h2>
      <canvas id="lineChart" style="max-width: 800px; max-height: 400px;"></canvas>
      <div class="saldo-total">
        <h3>Patrimonio Actual (Julio 2025): {{ formatVenezuelanNumber(cuentasConSaldo.reduce((total, cuenta) => {
          return total + parseVenezuelanNumber(cuenta.saldo);
        }, 0)) }}</h3>
      </div>
    </div>
  </div>
</template>

<style scoped>
.header {
  display: flex;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  font-size: 16px;
  position: relative;
  left: 17%;
  top: -100vh;
  width: 80%;
  height: 100%;
  background-color: rgb(255, 255, 255);
  border-bottom: 2px solid black;
}

.patrimonio-container {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  max-width: 1100px; /* Aumentado para un card más grande */
  margin: 30px auto;
  padding: 30px;
}

.card-line-chart {
  background: #fff;
  border-radius: 20px;
  padding: 30px; /* Más padding para espacio interno */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 1280px; /* Card más grande */
  min-height: 600px; /* Card más alto */
}

.title {
  font-size: 2.5rem; /* Título más grande */
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  margin-bottom: 30px;
}

.saldo-total {
  margin-top: 30px;
  font-size: 2rem; /* Texto del saldo total más grande */
  font-weight: bold;
}
</style>