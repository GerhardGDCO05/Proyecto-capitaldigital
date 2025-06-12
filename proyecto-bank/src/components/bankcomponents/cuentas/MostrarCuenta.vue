<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useUsuarioStore } from '@/stores/useUsuarioStore'
import { useRouter } from 'vue-router'
const router = useRouter()

// Estado reactivo
const showModal = ref(false)
const cuentaSeleccionada = ref(null)

const usuarioStore = useUsuarioStore()
const documento = usuarioStore.usuario.numeroDocumento

const cuentasXML = ref([])
const cuentasConSaldo = ref([])

// Logos de bancos
const logosBancos = {
  'BBVA': new URL('@/images/BBVAprovinciallogo.png', import.meta.url).href,
  'BDV': new URL('@/images/Banco_de_Venezuela_logo.png', import.meta.url).href,
  'Mercantil': new URL('@/images/Mercantil.png', import.meta.url).href,
  default: new URL('@/images/bank-default.png', import.meta.url).href
}

// Función para obtener cuentas del XML desde Spring Boot
const fetchCuentasXML = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/cuenta/numeroDocumento/${documento}`)
    cuentasXML.value = response.data
  } catch (error) {
    console.error("Error al cargar cuentas del XML", error)
  }
}

// Función para parsear el archivo TXT
const parseTXT = async () => {
  try {
    const response = await fetch('/data/ListaCuentasDetalladasBancos.txt')
    const texto = await response.text()

    const bloques = texto.split('-*-*-*-*-*').filter(bloque => bloque.trim())
    const datosTXT = {}

    bloques.forEach(bloque => {
      const lineas = bloque.trim().split('\n')
      const cuenta = {}
      lineas.forEach(linea => {
        const [clave, valor] = linea.split(':')
        if (clave && valor) {
          cuenta[clave.trim()] = valor.trim()
        }
      })
      if (cuenta.NCuenta) {
        datosTXT[cuenta.NCuenta] = {
          tipoCuenta: cuenta.TipoCuenta || 'Desconocido',
          saldo: cuenta.Saldo || '0 Bs'
        }
      }
    })

    return datosTXT
  } catch (error) {
    console.error('Error al leer cuentas.txt:', error)
    return {}
  }
}

// Cargar y combinar datos
onMounted(async () => {
  if (!documento) {
    alert("⚠️ No hay usuario logueado")
    return
  }

  await fetchCuentasXML()
  const datosTXT = await parseTXT()

  // Combinar datos del XML con los del TXT
  cuentasConSaldo.value = cuentasXML.value.map(cuenta => ({
    ...cuenta,
    saldo: datosTXT[cuenta.numeroCuenta]?.saldo || 'No disponible',
    tipoCuenta: datosTXT[cuenta.numeroCuenta]?.tipoCuenta || 'Desconocido'
  }))
})

function irAEditarCuenta(cuenta) {
  if (!cuenta) {
    alert("⚠️ Seleccione una cuenta válida antes de continuar.")
    return
  }

  router.push({
    name: 'EditarCuenta',
    query: { popup: 'true' },
    params: {
      numeroCuenta: cuenta.numeroCuenta,
      nombreCuenta: cuenta.nombreCuenta
    }
  })
}
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
        <button v-for="cuenta in cuentasConSaldo.filter(c => c.tipoCuenta === 'Corriente')" :key="cuenta.numeroCuenta" class="cuenta-corriente">
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

          <button class="editar-btn" @click.stop="irAEditarCuenta(cuenta)">
            <span>Editar</span>
            <span></span>
          </button>
        </button>

      </div>
    </div>

    <!-- Cuentas de Ahorro -->
    <div class="contenedor-cuentasahorro">
      <h2 class="title">Cuentas de Ahorro</h2>
      <div class="contenedor-cuentas">
        <button v-for="cuenta in cuentasConSaldo.filter(c => c.tipoCuenta === 'Ahorro')" :key="cuenta.numeroCuenta" class="cuenta-ahorro">
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

          <button class="editar-btn" @click.stop="irAEditarCuenta(cuenta)">
            <span>Editar</span>
            <span></span>
          </button>
        </button>
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

/*Botones de representacion de las cuentas*/
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
/*-----------------------------------------*/

/* From Uiverse.io by gharsh11032000 */
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



</style>