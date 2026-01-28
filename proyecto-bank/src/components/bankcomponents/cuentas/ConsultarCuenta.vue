<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUsuarioStore } from '@/stores/useUsuarioStore';
import axios from 'axios';
import Swal from 'sweetalert2';

const route = useRoute();
const router = useRouter();
const usuarioStore = useUsuarioStore();
const documento = usuarioStore.usuario?.numeroDocumento;

// Props recibidos desde la ruta
const props = defineProps({
  numeroCuenta: {
    type: String,
    required: true
  }
});

// Datos de la cuenta desde query parameters
const cuenta = ref({
  numeroCuenta: route.params.numeroCuenta,
  banco: route.query.banco || 'Desconocido',
  nombreCuenta: route.query.nombreCuenta || 'Sin nombre',
  saldo: route.query.saldo || 'No disponible',
  tipoCuenta: route.query.tipoCuenta || 'Desconocido'
});

// Datos de la tarjeta asociada
const tarjeta = ref(null);

// Logos de bancos
const logosBancos = {
  'BBVA': new URL('@/images/BBVAprovinciallogo.png', import.meta.url).href,
  'BDV': new URL('@/images/Banco_de_Venezuela_logo.png', import.meta.url).href,
  'Mercantil': new URL('@/images/Mercantil.png', import.meta.url).href,
  default: new URL('@/images/bank-default.png', import.meta.url).href
};

// Variables para el historial de transferencias
const transacciones = ref([]);
const mensaje = ref('');
const isLoading = ref(false);

// Computed para determinar la clase CSS según el tipo de cuenta
const claseCuenta = computed(() => {
  return cuenta.value.tipoCuenta === 'Corriente' ? 'cuenta-corriente' : 'cuenta-ahorro';
});

// Función para formatear el monto como "1.234,56 Bs"
const formatMonto = (monto: number | string): string => {
  const numericMonto = typeof monto === 'string' ? parseFloat(monto.replace(',', '.')) : monto;
  if (isNaN(numericMonto)) return '0,00 Bs';
  const value = numericMonto.toFixed(2).replace('.', ',');
  const [integerPart, decimalPart] = value.split(',');
  const formattedInteger = parseInt(integerPart, 10).toLocaleString('es-VE');
  return `${formattedInteger},${decimalPart} Bs`;
};

// Función para cargar la tarjeta asociada
const cargarTarjeta = async () => {
  if (!documento) {
    mensaje.value = 'Por favor, inicia sesión nuevamente.';
    console.error('No se encontró numeroDocumento en el store de usuario');
    router.push('/login');
    return;
  }

  try {
    isLoading.value = true;
    console.log('Haciendo petición para tarjetas: http://localhost:8080/cuenta/tarjetas/numeroDocumento/' + documento);
    const response = await axios.get(`http://localhost:8080/cuenta/tarjetas/numeroDocumento/${documento}`);
    console.log('Respuesta del backend (tarjetas):', response.data);

    // Verificar si response.data es un array
    if (!Array.isArray(response.data)) {
      console.error('La respuesta del backend no es un array:', response.data);
      mensaje.value = 'Error: La respuesta del servidor no contiene datos válidos para las tarjetas.';
      return;
    }

    // Filtrar la tarjeta que coincide con el numeroCuenta
    const tarjetaEncontrada = response.data.find(
        t => t.numeroCuenta === props.numeroCuenta
    );
    console.log('Tarjeta encontrada:', tarjetaEncontrada);

    if (tarjetaEncontrada) {
      tarjeta.value = {
        numeroTarjeta: tarjetaEncontrada.numeroTarjeta || 'N/A',
        validoHasta: tarjetaEncontrada.validoHasta || 'N/A',
        nombreTarjeta: tarjetaEncontrada.nombreTarjeta || 'N/A'
      };
    } else {
      mensaje.value = 'No se encontró una tarjeta asociada a esta cuenta.';
    }
  } catch (error) {
    console.error('Error al cargar la tarjeta:', error);
    mensaje.value = 'Error al cargar la tarjeta: ' + (error.response?.data?.message || error.message);
  } finally {
    isLoading.value = false;
  }
};

// Función para cargar transacciones
const cargarTransacciones = async () => {
  if (!documento) {
    mensaje.value = 'Por favor, inicia sesión nuevamente.';
    console.error('No se encontró numeroDocumento en el store de usuario');
    router.push('/login');
    return;
  }

  try {
    isLoading.value = true;
    console.log('Haciendo petición a: http://localhost:8080/transactionhistory/numeroDocumento/' + documento);
    const response = await axios.get(`http://localhost:8080/transactionhistory/numeroDocumento/${documento}`);
    console.log('Respuesta de transacciones:', response.data);

    // Verificar si response.data es un array
    transacciones.value = Array.isArray(response.data)
        ? response.data.filter(
        transaccion =>
            transaccion.numCuentaOrigen === props.numeroCuenta ||
            transaccion.numCuentaDestino === props.numeroCuenta
    ) || []
        : [];

    if (transacciones.value.length === 0 && !mensaje.value) {
      mensaje.value = 'No hay transferencias disponibles para esta cuenta.';
    }
  } catch (error) {
    console.error('Error al cargar transacciones:', error);
    transacciones.value = [];
    mensaje.value = 'Error al cargar el historial de transacciones: ' + (error.response?.data?.message || error.message);
  } finally {
    isLoading.value = false;
  }
};

// Cargar tarjeta y transacciones al montar el componente
onMounted(() => {
  cargarTarjeta();
  cargarTransacciones();
});

// Función para redirigir a EditarCuenta
const irAEditarCuenta = () => {
  if (!cuenta.value.numeroCuenta) {
    Swal.fire({
      title: 'Error',
      text: 'No se puede editar la cuenta. Datos inválidos.',
      icon: 'error',
      confirmButtonText: 'OK'
    });
    return;
  }

  router.push({
    name: 'EditarCuenta',
    query: { popup: 'true' },
    params: {
      numeroCuenta: cuenta.value.numeroCuenta,
      nombreCuenta: cuenta.value.nombreCuenta
    }
  });
};

// Función para eliminar cuenta
const eliminarCuenta = async () => {
  if (!cuenta.value.numeroCuenta || !documento) {
    await Swal.fire({
      title: 'Error',
      text: 'No se puede eliminar la cuenta. Datos inválidos.',
      icon: 'error',
      confirmButtonText: 'OK'
    });
    return;
  }

  const result = await Swal.fire({
    title: '¿Estás seguro?',
    text: `¿Deseas eliminar la cuenta ${cuenta.value.numeroCuenta}? Esta acción no se puede deshacer.`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Sí, eliminar',
    cancelButtonText: 'Cancelar'
  });

  if (!result.isConfirmed) return;

  try {
    const response = await axios.delete(`http://localhost:8080/cuenta/numeroDocumento/${documento}/numeroCuenta/${cuenta.value.numeroCuenta}`);
    await Swal.fire({
      title: '¡Eliminado!',
      text: response.data || 'Cuenta eliminada correctamente.',
      icon: 'success',
      confirmButtonText: 'OK'
    });
    router.push({
      name: 'MostrarCuentas',
      query: { popup: 'true' }
    });
  } catch (error) {
    console.error('Error al eliminar cuenta:', error);
    await Swal.fire({
      title: 'Error',
      text: error.response?.data?.message || 'Hubo un error al eliminar la cuenta.',
      icon: 'error',
      confirmButtonText: 'OK'
    });
  }
};

// Función para volver a MostrarCuentas
const volver = () => {
  router.push({
    name: 'MostrarCuentas',
    query: { popup: 'true' }
  });
};
</script>

<template>
  <header class="header">
    <h1>Consultar Cuenta</h1>
  </header>
  <main>
    <div class="cuenta-container">
      <div class="nombre-cuenta">
        <h2>{{ cuenta.nombreCuenta }}</h2>
      </div>
      <div class="cuenta-bancaria">
        <div class="contenedor-cuenta">
          <button :class="claseCuenta">
            <img
                :src="logosBancos[cuenta.banco] || logosBancos.default"
                width="150"
                height="100"
            >
            <div class="info-cuenta">
              <span class="numero-cuenta">{{ cuenta.numeroCuenta }}</span>
              <span class="saldo">{{ cuenta.saldo }} Bs</span>
            </div>
            <div class="arrow-wrapper">
              <div class="arrow"></div>
            </div>
            <div class="opt-buttons">
              <button class="editar-btn" @click="irAEditarCuenta">
                <span>Editar</span>
                <span></span>
              </button>
              <button class="editar-btn" @click="eliminarCuenta">
                <span>Borrar</span>
                <span></span>
              </button>
              <button class="editar-btn" @click="volver">
                <span>Volver</span>
                <span></span>
              </button>
            </div>
          </button>
        </div>
      </div>

      <div class="tarjetas-container">
        <div v-if="isLoading" class="mensaje">Cargando tarjeta...</div>
        <div v-else-if="tarjeta" class="flip-card" :key="tarjeta.numeroTarjeta">
          <div class="flip-card-inner">
            <div class="flip-card-front">
              <p class="heading_8264">MASTERCARD</p>
              <svg class="logo" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="36" height="36" viewBox="0 0 48 48">
                <path fill="#ff9800" d="M32 10A14 14 0 1 0 32 38A14 14 0 1 0 32 10Z"></path>
                <path fill="#d50000" d="M16 10A14 14 0 1 0 16 38A14 14 0 1 0 16 10Z"></path>
                <path fill="#ff3d00" d="M18,24c0,4.755,2.376,8.95,6,11.48c3.624-2.53,6-6.725,6-11.48s-2.376-8.95-6-11.48 C20.376,15.05,18,19.245,18,24z"></path>
              </svg>
              <svg version="1.1" class="chip" id="Layer_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" width="30px" height="30px" viewBox="0 0 50 50" xml:space="preserve">  <image id="image0" width="50" height="50" x="0" y="0" href="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAMAAAAp4XiDAAAABGdBTUEAALGPC/xhBQAAACBjSFJN
              AAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAB6VBMVEUAAACNcTiVeUKVeUOY
              fEaafEeUeUSYfEWZfEaykleyklaXe0SWekSZZjOYfEWYe0WXfUWXe0WcgEicfkiXe0SVekSXekSW
              ekKYe0a9nF67m12ZfUWUeEaXfESVekOdgEmVeUWWekSniU+VeUKVeUOrjFKYfEWliE6WeESZe0GS
              e0WYfES7ml2Xe0WXeESUeEOWfEWcf0eWfESXe0SXfEWYekSVeUKXfEWxklawkVaZfEWWekOUekOW
              ekSYfESZe0eXekWYfEWZe0WZe0eVeUSWeETAnmDCoWLJpmbxy4P1zoXwyoLIpWbjvXjivnjgu3bf
              u3beunWvkFWxkle/nmDivXiWekTnwXvkwHrCoWOuj1SXe0TEo2TDo2PlwHratnKZfEbQrWvPrWua
              fUfbt3PJp2agg0v0zYX0zYSfgkvKp2frxX7mwHrlv3rsxn/yzIPgvHfduXWXe0XuyIDzzISsjVO1
              lVm0lFitjVPzzIPqxX7duna0lVncuHTLqGjvyIHeuXXxyYGZfUayk1iyk1e2lln1zYTEomO2llrb
              tnOafkjFpGSbfkfZtXLhvHfkv3nqxH3mwXujhU3KqWizlFilh06khk2fgkqsjlPHpWXJp2erjVOh
              g0yWe0SliE+XekShhEvAn2D///+gx8TWAAAARnRSTlMACVCTtsRl7Pv7+vxkBab7pZv5+ZlL/UnU
              /f3SJCVe+Fx39naA9/75XSMh0/3SSkia+pil/KRj7Pr662JPkrbP7OLQ0JFOijI1MwAAAAFiS0dE
              orDd34wAAAAJcEhZcwAACxMAAAsTAQCanBgAAAAHdElNRQfnAg0IDx2lsiuJAAACLElEQVRIx2Ng
              GAXkAUYmZhZWPICFmYkRVQcbOwenmzse4MbFzc6DpIGXj8PD04sA8PbhF+CFaxEU8iWkAQT8hEVg
              OkTF/InR4eUVICYO1SIhCRMLDAoKDvFDVhUaEhwUFAjjSUlDdMiEhcOEItzdI6OiYxA6YqODIt3d
              I2DcuDBZsBY5eVTr4xMSYcyk5BRUOXkFsBZFJTQnp6alQxgZmVloUkrKYC0qqmji2WE5EEZuWB6a
              lKoKdi35YQUQRkFYPpFaCouKIYzi6EDitJSUlsGY5RWVRGjJLyxNy4ZxqtIqqvOxaVELQwZFZdkI
              JVU1RSiSalAt6rUwUBdWG1CP6pT6gNqwOrgCdQyHNYR5YQFhDXj8MiK1IAeyN6aORiyBjByVTc0F
              qBoKWpqwRCVSgilOaY2OaUPw29qjOzqLvTAchpos47u6EZyYnngUSRwpuTe6D+6qaFQdOPNLRzOM
              1dzhRZyW+CZouHk3dWLXglFcFIflQhj9YWjJGlZcaKAVSvjyPrRQ0oQVKDAQHlYFYUwIm4gqExGm
              BSkutaVQJeomwViTJqPK6OhCy2Q9sQBk8cY0DxjTJw0lAQWK6cOKfgNhpKK7ZMpUeF3jPa28BCET
              amiEqJKM+X1gxvWXpoUjVIVPnwErw71nmpgiqiQGBjNzbgs3j1nus+fMndc+Cwm0T52/oNR9lsdC
              S24ra7Tq1cbWjpXV3sHRCb1idXZ0sGdltXNxRateRwHRAACYHutzk/2I5QAAACV0RVh0ZGF0ZTpj
              cmVhdGUAMjAyMy0wMi0xM1QwODoxNToyOSswMDowMEUnN7UAAAAldEVYdGRhdGU6bW9kaWZ5ADIw
              MjMtMDItMTNUMDg6MTU6MjkrMDA6MDA0eo8JAAAAKHRFWHRkYXRlOnRpbWVzdGFtcAAyMDIzLTAy
              LTEzVDA4OjE1OjI5KzAwOjAwY2+u1gAAAABJRU5ErkJggg=="></image>
            </svg>
              <svg version="1.1" class="contactless" id="Layer_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" width="20px" height="20px" viewBox="0 0 50 50" xml:space="preserve">  <image id="image0" width="50" height="50" x="0" y="0" href="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAQAAAC0NkA6AAAABGdBTUEAALGPC/xhBQAAACBjSFJN
              AAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAAmJLR0QA/4ePzL8AAAAJcEhZ
              cwAACxMAAAsTAQCanBgAAAAHdElNRQfnAg0IEzgIwaKTAAADDklEQVRYw+1XS0iUURQ+f5qPyjQf
              lGRFEEFK76koKGxRbWyVVLSOgsCgwjZBJJYuKogSIoOonUK4q3U0WVBWFPZYiIE6kuArG3VGzK/F
              fPeMM/MLt99/NuHdfPd888/57jn3nvsQWWj/VcMlvMMd5KRTogqx9iCdIjUUmcGR9ImUYowyP3xN
              GQJoRLVaZ2DaZf8kyjEJALhI28ELioyiwC+Rc3QZwRYyO/DH51hQgWm6DMIh10KmD4u9O16K49it
              VoPOAmcGAWWOepXIRScAoJZ2Frro8oN+EyTT6lWkkg6msZfMSR35QTJmjU0g15tIGSJ08ZZMJkJk
              HpNZgSkyXosS13TkJpZ62mPIJvOSzC1bp8vRhhCakEk7G9/o4gmZdbpsTcKu0m63FbnBP9Qrc15z
              bkbemfgNDtEOI8NO5L5O9VYyRYgmJayZ9nPaxZrSjW4+F6Uw9yQqIiIZwhp2huQTf6OIvCZyGM6g
              DJBZbyXifJXr7FZjGXsdxADxI7HUJFB6iWvsIhFpkoiIiGTJfjJfiCuJg2ZEspq9EHGVpYgzKqwJ
              qSAOEwuJQ/pxPvE3cYltJCLdxBLiSKKIE5HxJKcTRNeadxfhDiuYw44zVs1dxKwRk/uCxIiQkxKB
              sSctRVAge9g1E15EHE6yRUaJecRxcWlukdRIbGFOSZCMWQA/iWauIP3slREHXPyliqBcrrD71Amz
              Z+rD1Mt2Yr8TZc/UR4/YtFnbijnHi3UrN9vKQ9rPaJf867ZiaqDB+czeKYmd3pNa6fuI75MiC0uX
              XSR5aEMf7s7a6r/PudVXkjFb/SsrCRfROk0Fx6+H1i9kkTGn/E1vEmt1m089fh+RKdQ5O+xNJPUi
              cUIjO0Dm7HwvErEr0YxeibL1StSh37STafE4I7zcBdRq1DiOkdmlTJVnkQTBTS7X1FYyvfO4piaI
              nKbDCDaT2anLudYXCRFsQBgAcIF2/Okwgvz5+Z4tsw118dzruvIvjhTB+HOuWy8UvovEH6beitBK
              xDyxm9MmISKCWrzB7bSlaqGlsf0FC0gMjzTg6GgAAAAldEVYdGRhdGU6Y3JlYXRlADIwMjMtMDIt
              MTNUMDg6MTk6NTYrMDA6MDCjlq7LAAAAJXRFWHRkYXRlOm1vZGlmeQAyMDIzLTAyLTEzVDA4OjE5
              OjU2KzAwOjAw0ssWdwAAACh0RVh0ZGF0ZTp0aW1lc3RhbXAAMjAyMy0wMi0xM1QwODoxOTo1Nisw
              MDowMIXeN6gAAAAASUVORK5CYII="></image>
            </svg>
              <p class="number">{{ tarjeta.numeroTarjeta }}</p>
              <p class="valid_thru">VALID THRU</p>
              <p class="date_8264">{{ tarjeta.validoHasta }}</p>
              <p class="name">{{ tarjeta.nombreTarjeta }}</p>
            </div>
            <div class="flip-card-back">
              <div class="strip"></div>
              <div class="mstrip"></div>
              <div class="sstrip">
                <p class="code">***</p>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="mensaje">{{ mensaje }}</div>
      </div>

      <div class="historial-de-transferencias">
        <h3>Historial de Transferencias</h3>
        <hr>
        <div v-if="isLoading" class="mensaje">Cargando transacciones...</div>
        <div v-else-if="transacciones.value?.length === 0" class="mensaje">{{ mensaje || 'No hay transferencias disponibles para esta cuenta.' }}</div>
        <table v-else>
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
    </div>
  </main>
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

.cuenta-container {
  position: absolute;
  top: 10%;
  left: 30%;
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.nombre-cuenta {
  text-align: center;
  margin-bottom: 20px;
}

.nombre-cuenta h2 {
  font-size: 24px;
  color: #333;
}

.contenedor-cuenta {
  display: flex;
  flex-direction: column;
  align-items: center;
}

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

.cuenta-corriente .arrow-wrapper,
.cuenta-ahorro .arrow-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
}

.cuenta-corriente .arrow,
.cuenta-ahorro .arrow {
  margin-top: 1px;
  width: var(--arrow-width);
  background: var(--primary-color);
  height: var(--arrow-stroke);
  position: relative;
  transition: 0.2s;
}

.cuenta-corriente .arrow::before,
.cuenta-ahorro .arrow::before {
  content: '';
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

.cuenta-corriente:hover,
.cuenta-ahorro:hover {
  background-color: rgba(1, 61, 163, 0.7);
}

.cuenta-corriente:hover .arrow,
.cuenta-ahorro:hover .arrow {
  background: var(--secondary-color);
}

.cuenta-corriente:hover .arrow:before,
.cuenta-ahorro:hover .arrow:before {
  right: 0;
}

.opt-buttons {
  display: flex;
  gap: 10px;
}

.editar-btn {
  padding: 5px 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.editar-btn:hover {
  background-color: #0056b3;
}

.historial-de-transferencias {
  margin-top: 40px;
}

.historial-de-transferencias h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 10px;
}

.historial-de-transferencias hr {
  border: 1px solid #ccc;
  margin-bottom: 20px;
}

.mensaje {
  text-align: center;
  color: #666;
  font-size: 16px;
  margin-top: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

th, td {
  border: 1px solid #ddd;
  padding: 12px;
  text-align: left;
}

th {
  font-weight: bold;
  background-color: #007BFF;
  color: white;
}

tr:nth-child(even) {
  background-color: #f9f9f9;
}

tr:hover {
  background-color: #f1f1f1;
}

.tarjetas-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 80px;
}

.tarjeta-info {
  background-color: #f7f7f7;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.tarjeta-info h3 {
  font-size: 1.2em;
  margin-bottom: 10px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.tarjeta-info p {
  font-size: 1em;
  margin: 5px 0;
  font-family: 'Inter', sans-serif;
}

.flip-card {
  background-color: transparent;
  width: 240px;
  height: 154px;
  perspective: 1000px;
  color: white;
}

.heading_8264 {
  position: absolute;
  letter-spacing: .2em;
  font-size: 0.5em;
  top: 2em;
  left: 18.6em;
}

.logo {
  position: absolute;
  top: 6.8em;
  left: 11.7em;
}

.chip {
  position: absolute;
  top: 2.3em;
  left: 1.5em;
}

.contactless {
  position: absolute;
  top: 3.5em;
  left: 12.4em;
}

.number {
  color: whitesmoke;
  position: absolute;
  font-weight: bold;
  font-size: .6em;
  top: 8.3em;
  left: 1.6em;
}

.valid_thru {
  position: absolute;
  color: white;
  font-weight: bold;
  top: 635.8em;
  font-size: .01em;
  left: 140.3em;
}

.date_8264 {
  position: absolute;
  font-weight: bold;
  font-size: 0.5em;
  top: 13.6em;
  left: 3.2em;
}

.name {
  position: absolute;
  font-weight: bold;
  font-size: 0.5em;
  top: 16.1em;
  left: 2em;
}

.strip {
  position: absolute;
  background-color: black;
  width: 15em;
  height: 1.5em;
  top: 2.4em;
  background: repeating-linear-gradient(
      45deg,
      #303030,
      #303030 10px,
      #202020 10px,
      #202020 20px
  );
}

.mstrip {
  position: absolute;
  background-color: rgb(255, 255, 255);
  width: 8em;
  height: 0.8em;
  top: 5em;
  left: .8em;
  border-radius: 2.5px;
}

.sstrip {
  position: absolute;
  background-color: rgb(255, 255, 255);
  width: 4.1em;
  height: 0.8em;
  top: 5em;
  left: 10em;
  border-radius: 2.5px;
}

.code {
  font-weight: bold;
  text-align: center;
  margin: .2em;
  color: black;
}

.flip-card-inner {
  position: relative;
  width: 100%;
  height: 100%;
  text-align: center;
  transition: transform 0.8s;
  transform-style: preserve-3d;
}

.flip-card:hover .flip-card-inner {
  transform: rotateY(180deg);
}

.flip-card-front, .flip-card-back {
  box-shadow: 0 8px 14px 0 rgba(0,0,0,0.2);
  position: absolute;
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 100%;
  height: 100%;
  -webkit-backface-visibility: hidden;
  backface-visibility: hidden;
  border-radius: 1rem;
}

.flip-card-front {
  box-shadow: rgba(0, 0, 0, 0.4) 0px 2px 2px, rgba(0, 0, 0, 0.3) 0px 7px 13px -3px, rgba(0, 0, 0, 0.2) 0px -1px 0px inset;
  background-color: #171717;
}

.flip-card-back {
  box-shadow: rgba(0, 0, 0, 0.4) 0px 2px 2px, rgba(0, 0, 0, 0.3) 0px 7px 13px -3px, rgba(0, 0, 0, 0.2) 0px -1px 0px inset;
  background-color: #171717;
  transform: rotateY(180deg);
}
</style>