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
    data: [],
    backgroundColor: [
      '#FF6384',
      '#36A2EB',
      '#FFCE56',
      '#4BC0C0',
      '#9966FF',
      '#FF9F40'
    ],
    hoverOffset: 4
  }]
});
const pieChartInstance = ref(null); // Almacenar la instancia del gráfico

// Logos de bancos
const logosBancos = {
  'BBVA': new URL('@/images/BBVAprovinciallogo.png', import.meta.url).href,
  'BDV': new URL('@/images/Banco_de_Venezuela_logo.png', import.meta.url).href,
  'Mercantil': new URL('@/images/Mercantil.png', import.meta.url).href,
  default: new URL('@/images/bank-default.png', import.meta.url).href
};

// Función para parsear números (100.000,00 -> 100000.00)
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

// Función para consultar una cuenta específica
const consultarCuenta = (cuenta) => {
  router.push({
    name: 'ConsultarCuentas',
    params: { numeroCuenta: cuenta.numeroCuenta },
    query: {
      popup: 'true',
      banco: cuenta.banco,
      nombreCuenta: cuenta.nombreCuenta,
      saldo: cuenta.saldo,
      tipoCuenta: cuenta.tipoCuenta
    }
  });
};

// Función para calcular el saldo total y preparar datos para el pie chart
const prepareChartData = (cuentas) => {
  const labels = cuentas.map(cuenta => `${cuenta.banco} - ${cuenta.numeroCuenta}`);
  const saldos = cuentas.map(cuenta => parseVenezuelanNumber(cuenta.saldo));
  chartData.value.labels = labels;
  chartData.value.datasets[0].data = saldos;
};

// Inicializar el pie chart
const initChart = () => {
  const ctx = document.getElementById('pieChart').getContext('2d');
  // Destruir el gráfico existente si existe
  if (pieChartInstance.value) {
    pieChartInstance.value.destroy();
  }
  // Crear un nuevo gráfico
  pieChartInstance.value = new Chart(ctx, {
    type: 'pie',
    data: chartData.value,
    options: {
      responsive: true,
      plugins: {
        legend: {
          position: 'top',
        },
        title: {
          display: true,
          text: 'Distribución de Saldos por Cuenta'
        },
        datalabels: {
          formatter: (value, ctx) => {
            const total = ctx.dataset.data.reduce((acc, val) => acc + val, 0);
            const percentage = total ? ((value / total) * 100).toFixed(2) + '%' : '0%';
            return `${percentage}\n${formatVenezuelanNumber(value)}`;
          },
          color: '#fff',
          font: {
            size: 14
          }
        }
      }
    },
    plugins: [ChartDataLabels]
  });
};

// Cargar y combinar datos
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

  prepareChartData(cuentasConSaldo.value);
  initChart();
});

// Función para redirigir a EditarCuenta
const irAEditarCuenta = (cuenta) => {
  if (!cuenta) {
    Swal.fire({
      title: 'Error',
      text: 'Seleccione una cuenta válida antes de continuar.',
      icon: 'error',
      confirmButtonText: 'OK'
    });
    return;
  }

  router.push({
    name: 'EditarCuenta',
    query: { popup: 'true' },
    params: {
      numeroCuenta: cuenta.numeroCuenta,
      nombreCuenta: cuenta.nombreCuenta
    }
  });
};

// Función para eliminar cuenta
const eliminarCuenta = async (cuenta) => {
  if (!cuenta || !cuenta.numeroCuenta) {
    await Swal.fire({
      title: 'Error',
      text: 'Seleccione una cuenta válida antes de continuar.',
      icon: 'error',
      confirmButtonText: 'OK'
    });
    return;
  }

  const result = await Swal.fire({
    title: '¿Estás seguro?',
    text: `¿Deseas eliminar la cuenta ${cuenta.numeroCuenta}? Esta acción no se puede deshacer.`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Sí, eliminar',
    cancelButtonText: 'Cancelar'
  });

  if (!result.isConfirmed) return;

  try {
    const response = await axios.delete(`http://localhost:8080/cuenta/numeroDocumento/${documento}/numeroCuenta/${cuenta.numeroCuenta}`);
    cuentasConSaldo.value = cuentasConSaldo.value.filter(c => c.numeroCuenta !== cuenta.numeroCuenta);
    await Swal.fire({
      title: '¡Eliminado!',
      text: response.data || 'Cuenta eliminada correctamente.',
      icon: 'success',
      confirmButtonText: 'OK'
    });
    // Actualizar el pie chart después de eliminar
    prepareChartData(cuentasConSaldo.value);
    initChart();
  } catch (error) {
    console.error('Error al eliminar cuenta:', error);
    await Swal.fire({
      title: 'Error',
      text: error.response?.data || 'Hubo un error al eliminar la cuenta.',
      icon: 'error',
      confirmButtonText: 'OK'
    });
  }
};
</script>

<template>
  <header class="header">
    <h1>Cuentas</h1>
  </header>

  <div class="contenedor-cuentas-globales">
    <!-- Cuentas Corrientes -->
    <div class="contenedor-cuentascorrientes">
      <h2 class="title">Cuentas Corrientes</h2>
      <div class="contenedor-cuentas">
        <button
            v-for="cuenta in cuentasConSaldo.filter(c => c.tipoCuenta === 'Corriente')"
            :key="cuenta.numeroCuenta"
            class="cuenta-corriente"
            @click="consultarCuenta(cuenta)"
        >
          <img
              :src="logosBancos[cuenta.banco] || logosBancos.default"
              width="150"
              height="100"
          >
          <div class="info-cuenta">
            <span class="numero-cuenta">{{ cuenta.numeroCuenta }}</span>
            <span class="saldo">{{ cuenta.saldo }}</span>
          </div>
          <div class="arrow-wrapper">
            <div class="arrow"></div>
          </div>
          <div class="opt-buttons">
            <button class="editar-btn" @click.stop="irAEditarCuenta(cuenta)">
              <span>Editar</span>
              <span></span>
            </button>
            <button class="editar-btn" @click.stop="eliminarCuenta(cuenta)" :disabled="cuenta.nombreCuenta === 'Cuenta Principal'">
              <span>Borrar</span>
              <span></span>
            </button>
          </div>
        </button>
      </div>
    </div>

    <!-- Cuentas de Ahorro -->
    <div class="contenedor-cuentasahorro">
      <h2 class="title">Cuentas de Ahorro</h2>
      <div class="contenedor-cuentas">
        <button
            v-for="cuenta in cuentasConSaldo.filter(c => c.tipoCuenta === 'Ahorro')"
            :key="cuenta.numeroCuenta"
            class="cuenta-ahorro"
            @click="consultarCuenta(cuenta)"
        >
          <img
              :src="logosBancos[cuenta.banco] || logosBancos.default"
              width="150"
              height="100"
          >
          <div class="info-cuenta">
            <span class="numero-cuenta">{{ cuenta.numeroCuenta }}</span>
            <span class="saldo">{{ cuenta.saldo }}</span>
          </div>
          <div class="arrow-wrapper">
            <div class="arrow"></div>
          </div>
          <div class="opt-buttons">
            <button class="editar-btn" @click.stop="irAEditarCuenta(cuenta)">
              <span>Editar</span>
              <span></span>
            </button>
            <button class="editar-btn" @click.stop="eliminarCuenta(cuenta)" :disabled="cuenta.nombreCuenta === 'Cuenta Principal'">
              <span>Borrar</span>
              <span></span>
            </button>
          </div>
        </button>
      </div>
    </div>

    <div class="posicion-global">
      <div class="card-pie-chart">
        <h2 class="title">Distribución de Saldos</h2>
        <canvas id="pieChart" style="max-width: 400px; max-height: 400px;"></canvas>
        <div class="saldo-total">
          <h3>Saldo Total: {{ formatVenezuelanNumber(cuentasConSaldo.reduce((total, cuenta) => {
            return total + parseVenezuelanNumber(cuenta.saldo);
          }, 0)) }}</h3>
        </div>
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

.title {
  font-size: 2rem;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.contenedor-cuentas-globales {
  display: flex;
  min-width: 1500px;
  min-height: 700px;
  gap: 15px;
}

.contenedor-cuentascorrientes {
  position: absolute;
  display: flex;
  flex-direction: column;
  top: 50%;
  left: 60%;
  transform: translate(-50%, -50%);
  min-width: 1500px;
  min-height: 700px;
}

.contenedor-cuentasahorro {
  position: absolute;
  display: flex;
  flex-direction: column;
  top: 100%;
  left: 60%;
  transform: translate(-50%, -50%);
  min-width: 1500px;
  min-height: 700px;
}

.posicion-global {
  position: absolute;
  top: 50%;
  left: 88%;
  transform: translate(-50%, -50%);
  min-width: 400px;
  min-height: 400px;
}

.card-pie-chart {
  background: #fff;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.saldo-total {
  margin-top: 20px;
  font-size: 1.5rem;
  font-weight: bold;
}

/* Botones de representación de las cuentas */
.cuenta-corriente {
  --primary-color: rgba(195, 181, 229, 1);
  --secondary-color: #fff;
  --hover-color: #111;
  --arrow-width: 10px;
  --arrow-stroke: 2px;
  box-sizing: border-box;
  border: 0;
  border-radius: 20px;
  color: var(--secondary-color);
  padding: 1em 1.8em;
  background: var(--primary-color);
  display: flex;
  transition: 0.2s background;
  align-items: center;
  justify-content: flex-start;
  gap: 5em;
  font-weight: bold;
  min-width: 800px;
  min-height: 100px;
  margin-bottom: 15px;
}

.info-cuenta {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  flex-grow: 1;
  gap: 20px;
}

.numero-cuenta,
.saldo {
  font-size: 1.5rem;
}

.cuenta-corriente .arrow-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
}

.cuenta-corriente .arrow {
  margin-top: 1px;
  width: var(--arrow-width);
  background: var(--primary-color);
  height: var(--arrow-stroke);
  position: relative;
  transition: 0.2s;
}

.cuenta-corriente .arrow::before {
  content: "";
  box-sizing: border-box;
  position: absolute;
  border: solid var(--secondary-color);
  border-width: 0 var(--arrow-stroke) var(--arrow-stroke) 0;
  display: inline-block;
  top: -3px;
  right: 3px;
  transition: 0.2s;
  padding: 3px;
  transform: rotate(-45deg);
}

.cuenta-corriente:hover {
  background-color: rgba(1, 61, 163, 0.7);
}

.cuenta-corriente:hover .arrow {
  background: var(--secondary-color);
}

.cuenta-corriente:hover .arrow:before {
  right: 0;
}

.cuenta-ahorro {
  --primary-color: rgba(237, 158, 178, 1);
  --secondary-color: #fff;
  --hover-color: #111;
  --arrow-width: 10px;
  --arrow-stroke: 2px;
  box-sizing: border-box;
  border: 0;
  border-radius: 20px;
  color: var(--secondary-color);
  padding: 1em 1.8em;
  background: var(--primary-color);
  display: flex;
  transition: 0.2s background;
  align-items: center;
  justify-content: flex-start;
  gap: 5em;
  font-weight: bold;
  min-width: 800px;
  min-height: 100px;
  margin-bottom: 15px;
}

.cuenta-ahorro .arrow-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
}

.cuenta-ahorro .arrow {
  margin-top: 1px;
  width: var(--arrow-width);
  background: var(--primary-color);
  height: var(--arrow-stroke);
  position: relative;
  transition: 0.2s;
}

.cuenta-ahorro .arrow::before {
  content: "";
  box-sizing: border-box;
  position: absolute;
  border: solid var(--secondary-color);
  border-width: 0 var(--arrow-stroke) var(--arrow-stroke) 0;
  display: inline-block;
  top: -3px;
  right: 3px;
  transition: 0.2s;
  padding: 3px;
  transform: rotate(-45deg);
}

.cuenta-ahorro:hover {
  background-color: rgba(1, 61, 163, 0.7);
}

.cuenta-ahorro:hover .arrow {
  background: var(--secondary-color);
}

.cuenta-ahorro:hover .arrow:before {
  right: 0;
}

/* Estilos para los botones de edición y eliminación */
.editar-btn {
  position: relative;
  display: inline-block;
  padding: 12px 24px;
  border: none;
  font-size: 16px;
  background-color: inherit;
  border-radius: 100px;
  font-weight: 600;
  color: #ffffff40;
  box-shadow: 0 0 0 2px #ffffff20;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.6s cubic-bezier(0.23, 1, 0.320, 1);
}

.editar-btn span:last-child {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 20px;
  height: 20px;
  background-color: #2196F3;
  border-radius: 50%;
  opacity: 0;
  transition: all 0.8s cubic-bezier(0.23, 1, 0.320, 1);
}

.editar-btn span:first-child {
  position: relative;
  z-index: 1;
}

.editar-btn:hover {
  box-shadow: 0 0 0 5px #2195f360;
  color: #ffffff;
}

.editar-btn:active {
  scale: 0.95;
}

.editar-btn:hover span:last-child {
  width: 150px;
  height: 150px;
  opacity: 1;
}

.opt-buttons {
  display: flex;
  column-gap: 10px;
}
</style>