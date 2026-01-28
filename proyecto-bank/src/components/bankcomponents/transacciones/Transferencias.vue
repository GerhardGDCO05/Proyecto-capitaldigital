<script>
import axios from 'axios'
import { ref } from 'vue'
import { useUsuarioStore } from '@/stores/useUsuarioStore'
import Swal from 'sweetalert2'
import bbvaLogo from '@/images/BBVAprovinciallogo.png'
import bdvLogo from '@/images/Banco_de_Venezuela_logo.png'
import mercantilLogo from '@/images/Mercantil.png'

export default {
  setup() {
    const usuarioStore = useUsuarioStore()
    const documento = usuarioStore.usuario?.numeroDocumento
    const usuario = ref('') // Número de cuenta de destino
    const beneficiarioSeleccionado = ref(null)
    const cuentaOrigenSeleccionada = ref(null)
    const conceptoTransferencia = ref('')
    const bancoSeleccionado = ref({ nombre: null, imagen: null })
    const internalValue = ref(0)
    const displayValue = ref('0,00 Bs')
    const beneficiarios = ref([])
    const cuentasDisponibles = ref([])
    const usarBeneficiario = ref(false) // Estado del checkbox

    // Mapa de logos de bancos
    const bancoLogos = {
      'BBVA': bbvaLogo,
      'BDV': bdvLogo,
      'Mercantil': mercantilLogo
    }

    // Cargar cuentas desde el backend
    const cargarCuentas = async () => {
      if (!documento) {
        console.error('No hay documento de usuario disponible:', documento)
        await Swal.fire({
          title: 'Error',
          text: 'No hay usuario logueado.',
          icon: 'error',
          confirmButtonText: 'OK',
          confirmButtonColor: '#3d405b'
        })
        return
      }
      try {
        console.log('Haciendo petición a:', `http://localhost:8080/cuenta/numeroDocumento/${documento}`)
        const response = await axios.get(`http://localhost:8080/cuenta/numeroDocumento/${documento}`)
        console.log('Respuesta de cuentas:', response.data)
        if (Array.isArray(response.data)) {
          cuentasDisponibles.value = response.data.map(cuenta => ({
            ...cuenta,
            saldo: cuenta.saldo !== undefined ? parseFloat(cuenta.saldo) : 0
          }))
          console.log('Cuentas cargadas:', cuentasDisponibles.value)
        } else {
          cuentasDisponibles.value = []
          await Swal.fire({
            title: 'Sin cuentas',
            text: 'No tiene cuentas secundarias registradas.',
            icon: 'warning',
            confirmButtonText: 'OK',
            confirmButtonColor: '#3d405b'
          })
        }
      } catch (error) {
        console.error('Error al cargar cuentas:', error.response?.data || error.message)
        await Swal.fire({
          title: 'Error',
          text: 'No se pudieron cargar las cuentas.',
          icon: 'error',
          confirmButtonText: 'OK',
          confirmButtonColor: '#3d405b'
        })
      }
    }

    // Cargar beneficiarios desde el backend
    const cargarBeneficiarios = async () => {
      if (!documento) {
        console.error('No hay documento de usuario disponible:', documento)
        await Swal.fire({
          title: 'Error',
          text: 'No hay usuario logueado.',
          icon: 'error',
          confirmButtonText: 'OK',
          confirmButtonColor: '#3d405b'
        })
        return
      }
      try {
        console.log('Haciendo petición a:', `http://localhost:8080/beneficiaries/${documento}`)
        const response = await axios.get(`http://localhost:8080/beneficiaries/${documento}`)
        console.log('Respuesta de beneficiarios:', response.data)
        if (Array.isArray(response.data)) {
          beneficiarios.value = response.data
          console.log('Beneficiarios cargados:', beneficiarios.value)
        } else {
          beneficiarios.value = []
          await Swal.fire({
            title: 'Sin beneficiarios',
            text: 'No tiene beneficiarios registrados.',
            icon: 'warning',
            confirmButtonText: 'OK',
            confirmButtonColor: '#3d405b'
          })
        }
      } catch (error) {
        console.error('Error al cargar beneficiarios:', error.response?.data || error.message)
        await Swal.fire({
          title: 'Error',
          text: 'No se pudieron cargar los beneficiarios.',
          icon: 'error',
          confirmButtonText: 'OK',
          confirmButtonColor: '#3d405b'
        })
      }
    }

    // Escuchar evento de beneficiario agregado
    const handleBeneficiarioAgregado = () => {
      cargarBeneficiarios()
    }

    // Función para autocompletar banco y número de cuenta al seleccionar beneficiario
    const handleBeneficiarioSeleccionado = (beneficiario) => {
      beneficiarioSeleccionado.value = beneficiario
      if (beneficiario && usarBeneficiario.value) {
        // Autocompletar banco de destino y número de cuenta
        bancoSeleccionado.value = {
          nombre: beneficiario.bank,
          imagen: bancoLogos[beneficiario.bank] || null
        }
        usuario.value = beneficiario.accountNumber
      } else {
        // Limpiar si no hay beneficiario seleccionado o el checkbox está desmarcado
        bancoSeleccionado.value = { nombre: null, imagen: null }
        usuario.value = ''
      }
    }

    // Manejar cambio en el checkbox
    const handleUsarBeneficiario = () => {
      if (!usarBeneficiario.value) {
        // Si se desmarca, limpiar beneficiario y campos relacionados
        beneficiarioSeleccionado.value = null
        bancoSeleccionado.value = { nombre: null, imagen: null }
        usuario.value = ''
      }
    }

    // Seleccionar banco manualmente
    const seleccionarBanco = (nombreBanco, imagenBanco) => {
      if (!usarBeneficiario.value) {
        bancoSeleccionado.value = {
          nombre: nombreBanco,
          imagen: imagenBanco
        }
      }
    }

    // Manejar el input del monto
    const handleInput = (e) => {
      let value = e.target.value.replace(/[^0-9]/g, '')
      if (!value) {
        internalValue.value = 0
        displayValue.value = '0,00 Bs'
        return
      }

      const digits = value.split('').reverse()
      let integerPart = ''
      let decimalPart = ''

      for (let i = 0; i < digits.length; i++) {
        if (i < 2) {
          decimalPart = digits[i] + decimalPart
        } else {
          integerPart = digits[i] + integerPart
        }
      }

      while (decimalPart.length < 2) decimalPart += '0'
      const formattedInteger = parseInt(integerPart || '0', 10).toLocaleString('es-VE')

      displayValue.value = `${formattedInteger},${decimalPart} Bs`
      internalValue.value = parseFloat(`${integerPart}.${decimalPart}`)
    }

    // Enviar formulario al backend
    const enviarFormulario = async () => {
      if (
          !usuario.value ||
          (!beneficiarioSeleccionado.value && usarBeneficiario.value) ||
          (!bancoSeleccionado.value.nombre && !usarBeneficiario.value) ||
          !cuentaOrigenSeleccionada.value ||
          internalValue.value <= 0 ||
          !conceptoTransferencia.value
      ) {
        await Swal.fire({
          title: 'Campos incompletos',
          text: 'Por favor complete todos los campos, incluyendo el concepto.',
          icon: 'warning',
          confirmButtonText: 'OK',
          confirmButtonColor: '#3d405b'
        })
        return
      }

      // Validar saldo suficiente
      const cuenta = cuentasDisponibles.value.find(c => c.numeroCuenta === cuentaOrigenSeleccionada.value.numeroCuenta)
      if (!cuenta || cuenta.saldo === undefined || internalValue.value > cuenta.saldo) {
        await Swal.fire({
          title: 'Error',
          text: 'Saldo insuficiente en la cuenta de origen o saldo no disponible.',
          icon: 'error',
          confirmButtonText: 'OK',
          confirmButtonColor: '#3d405b'
        })
        return
      }

      const datosFormulario = {
        beneficiario: usarBeneficiario.value ? beneficiarioSeleccionado.value.beneficiaryName : 'Desconocido',
        monto: internalValue.value.toString(),
        bancoOrigen: cuentaOrigenSeleccionada.value.banco,
        numCuentaOrigen: cuentaOrigenSeleccionada.value.numeroCuenta,
        bancoDestino: bancoSeleccionado.value.nombre,
        numCuentaDestino: usuario.value,
        concepto: conceptoTransferencia.value
      }

      try {
        console.log('Enviando datos al backend:', datosFormulario)
        const response = await axios.post(
            `http://localhost:8080/transactionhistory/numeroDocumento/${documento}`,
            datosFormulario
        )
        console.log('Respuesta de transacción:', response.data)
        if (response.status === 200) {
          await Swal.fire({
            title: '¡Éxito!',
            text: '✅ Transacción enviada correctamente',
            icon: 'success',
            confirmButtonText: 'OK',
            confirmButtonColor: '#3d405b'
          })
          await cargarCuentas() // Actualizar saldos
          limpiarFormulario()
        } else {
          await Swal.fire({
            title: 'Error',
            text: '❌ No se pudo enviar la transacción',
            icon: 'error',
            confirmButtonText: 'OK',
            confirmButtonColor: '#3d405b'
          })
        }
      } catch (error) {
        console.error('Error al enviar transacción:', error.response?.data || error.message)
        await Swal.fire({
          title: 'Error',
          text: `⚠️ ${error.response?.data || error.message}`,
          icon: 'error',
          confirmButtonText: 'OK',
          confirmButtonColor: '#3d405b'
        })
      }
    }

    // Limpiar formulario
    const limpiarFormulario = () => {
      usuario.value = ''
      beneficiarioSeleccionado.value = null
      cuentaOrigenSeleccionada.value = null
      bancoSeleccionado.value = { nombre: null, imagen: null }
      internalValue.value = 0
      displayValue.value = '0,00 Bs'
      conceptoTransferencia.value = ''
      usarBeneficiario.value = false // Resetear el checkbox
    }

    // Ejecutar al montar el componente
    const onMounted = async () => {
      if (!documento) {
        console.error('No hay usuario logueado, documento es:', documento)
        await Swal.fire({
          title: 'Error',
          text: 'No hay usuario logueado.',
          icon: 'error',
          confirmButtonText: 'OK',
          confirmButtonColor: '#3d405b'
        })
        return
      }
      console.log('Usuario logueado, documento:', documento)
      await cargarCuentas()
      await cargarBeneficiarios()
      window.addEventListener('beneficiario-agregado', handleBeneficiarioAgregado)
    }

    onMounted()

    return {
      usuario,
      beneficiarioSeleccionado,
      cuentaOrigenSeleccionada,
      conceptoTransferencia,
      bancoSeleccionado,
      internalValue,
      displayValue,
      beneficiarios,
      cuentasDisponibles,
      seleccionarBanco,
      handleInput,
      enviarFormulario,
      handleBeneficiarioSeleccionado,
      usarBeneficiario,
      handleUsarBeneficiario,
      bbvaLogo,
      bdvLogo,
      mercantilLogo
    }
  }
}
</script>

<template>
  <header class="header">
    <h1>Transacciones</h1>
  </header>
  <div class="main">
    <div class="transferencia-container">
      <!-- Seleccione una cuenta de origen -->
      <label for="cuentaOrigen" class="form-label">Seleccione una cuenta de origen</label>
      <select id="cuentaOrigen" v-model="cuentaOrigenSeleccionada" class="input-style" style="margin-bottom: 16px;">
        <option value="" disabled selected>Seleccione una cuenta...</option>
        <option v-if="cuentasDisponibles.length === 0" disabled>No hay cuentas disponibles</option>
        <option v-for="(cuenta, index) in cuentasDisponibles" :key="index" :value="cuenta">
          {{ cuenta.banco }} - {{ cuenta.numeroCuenta }} - {{ cuenta.saldo ? cuenta.saldo.toLocaleString('es-VE', { style: 'currency', currency: 'VEF' }) : 'Saldo no disponible' }}
        </option>
      </select>

      <!-- Ingrese el monto requerido -->
      <p class="message">Ingrese el monto requerido</p>
      <div class="currency-wrapper">
        <input
            type="text"
            :value="displayValue"
            @input="handleInput"
            placeholder="0,00 Bs"
            class="currency-input"
            autocomplete="off"
        />
      </div>

      <!-- Checkbox para seleccionar beneficiario -->
      <div class="checkbox-container">
        <input
            type="checkbox"
            id="usarBeneficiario"
            v-model="usarBeneficiario"
            @change="handleUsarBeneficiario"
        />
        <label for="usarBeneficiario" class="checkbox-label">Seleccionar beneficiario</label>
      </div>

      <!-- Seleccione un beneficiario -->
      <label for="beneficiario" class="form-label">Seleccione un beneficiario</label>
      <select
          id="beneficiario"
          v-model="beneficiarioSeleccionado"
          class="input-style"
          style="margin-bottom: 16px;"
          @change="handleBeneficiarioSeleccionado(beneficiarioSeleccionado)"
          :disabled="!usarBeneficiario"
      >
        <option value="" disabled selected>Seleccione un beneficiario...</option>
        <option v-if="beneficiarios.length === 0" disabled>No hay beneficiarios disponibles</option>
        <option v-for="(beneficiario, index) in beneficiarios" :key="index" :value="beneficiario">
          {{ beneficiario.beneficiaryName }}
        </option>
      </select>

      <!-- Selección de Banco -->
      <p class="message">Banco de destino</p>
      <div class="dropdown" :class="{ 'disabled': usarBeneficiario }">
        <input hidden id="state-dropdown" name="state-dropdown" type="checkbox" :disabled="usarBeneficiario">
        <label for="state-dropdown" class="trigger">
          <div v-if="bancoSeleccionado.nombre" style="display: flex; align-items: center; gap: 8px;">
            <img :src="bancoSeleccionado.imagen" width="60" height="30" v-if="bancoSeleccionado.imagen">
            <span>{{ bancoSeleccionado.nombre }}</span>
          </div>
          <span v-else>Seleccione un banco...</span>
        </label>
        <ul class="list webkit-scrollbar" role="list" dir="auto">
          <li class="listitem" role="listitem">
            <button type="button" class="button" @click="seleccionarBanco('BBVA', bbvaLogo)" :disabled="usarBeneficiario">
              <img src="@/images/BBVAprovinciallogo.png" width="60" height="30">
              <span>BBVA Provincial</span>
            </button>
          </li>
          <li class="listitem" role="listitem">
            <button type="button" class="button" @click="seleccionarBanco('BDV', bdvLogo)" :disabled="usarBeneficiario">
              <img src="@/images/Banco_de_Venezuela_logo.png" width="100" height="30">
              <span>Banco de Venezuela</span>
            </button>
          </li>
          <li class="listitem" role="listitem">
            <button type="button" class="button" @click="seleccionarBanco('Mercantil', mercantilLogo)" :disabled="usarBeneficiario">
              <img src="@/images/Mercantil.png" width="60" height="60">
              <span>Banco Mercantil</span>
            </button>
          </li>
        </ul>
      </div>

      <label for="numCuentaDestino" class="form-label">Número de cuenta de destino</label>
      <input
          id="numCuentaDestino"
          v-model="usuario"
          placeholder="Ingresa el número de cuenta de destino..."
          class="input-style"
          type="text"
          :disabled="usarBeneficiario"
      >

      <label for="concepto" class="form-label">Concepto de transferencia</label>
      <textarea
          id="concepto"
          v-model="conceptoTransferencia"
          placeholder="Ej: Pago por servicios prestados, compra de producto, etc."
          class="input-style"
          style="height: 100px; resize: none;"
      ></textarea>

      <!-- Botón Enviar -->
      <button class="continuebtn" type="submit" @click="enviarFormulario">
        <span>Enviar</span>
      </button>
    </div>
  </div>
</template>

<style scoped>

.transferencia-container {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
/* Estilos sin cambios */
.input-style {
  padding: 10px;
  border: 2px solid #ccc;
  border-radius: 5px;
  font-size: 16px;
  color: #555;
  outline: none;
  width: 70%;
  margin-bottom: 16px;
}

.input-style:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}

.input-style:disabled {
  background-color: #f0f0f0;
  cursor: not-allowed;
}

/* Label */
.form-label {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 8px;
  display: block;
  color: #333;
}

.message {
  margin-top: 16px;
  margin-bottom: 8px;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

/* Checkbox */
.checkbox-container {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.checkbox-label {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  cursor: pointer;
}

/* Dropdown bancos */
.dropdown {
  border: 1px solid #c1c2c5;
  border-radius: 12px;
  transition: all 300ms;
  display: flex;
  flex-direction: column;
  min-height: 58px;
  background-color: white;
  overflow: hidden;
  position: relative;
  max-width: 398px;
  min-width: 298px;
  margin-bottom: 20px;
}

.dropdown.disabled {
  background-color: #f0f0f0;
  cursor: not-allowed;
}

.dropdown input:where(:checked) ~ .list {
  opacity: 1;
  transform: translateY(-3rem) scale(1);
  transition: all 500ms ease;
  margin-top: 32px;
  padding-top: 4px;
  margin-bottom: -32px;
}

.dropdown input:where(:not(:checked)) ~ .list {
  opacity: 0;
  transform: translateY(3rem);
  margin-top: -100%;
  user-select: none;
  height: 0px;
  max-height: 0px;
  min-height: 0px;
  pointer-events: none;
  transition: all 500ms ease-out;
}

.trigger {
  cursor: pointer;
  font-weight: 600;
  color: inherit;
  width: 100%;
  display: flex;
  align-items: center;
  flex-flow: row;
  gap: 1rem;
  padding: 1rem;
  height: max-content;
  position: relative;
  z-index: 99;
  border-radius: inherit;
  background-color: white;
}

.trigger::after {
  content: "";
}

.trigger:before {
  content: "›";
  rotate: -90deg;
  width: 17px;
  height: 17px;
  color: #262626;
  border-radius: 2px;
  font-size: 26px;
  transition: all 350ms ease;
  transition-delay: 85ms;
}

.dropdown input:where(:checked) + .trigger::after {
  content: "Cerrar selección...";
}

.dropdown input:where(:checked) + .trigger:before {
  rotate: 90deg;
  transition-delay: 0ms;
}

.list {
  height: 100%;
  max-height: 20rem;
  width: calc(100% - calc(var(--w-scrollbar) / 2));
  display: grid;
  grid-auto-flow: row;
  overflow: hidden auto;
  gap: 1rem;
  padding: 0 1rem;
  margin-right: -8px;
  --w-scrollbar: 8px;
}

.listitem {
  height: 100%;
  width: calc(100% + calc(calc(var(--w-scrollbar) / 2) + var(--w-scrollbar)));
  list-style: none;
}

.button {
  padding: 1rem;
  border-radius: 8px;
  font-size: 15px;
  font-weight: bold;
  text-align: justify;
  width: 90%;
  border: 1px solid #c1c2c5;
  background-color: white;
  cursor: pointer;
  display: flex;
  gap: 10px;
  align-items: center;
  transition: background-color 0.3s;
}

.button:disabled {
  background-color: #f0f0f0;
  cursor: not-allowed;
}

.button:hover:not(:disabled) {
  background-color: #f0f0f0;
}

.webkit-scrollbar::-webkit-scrollbar {
  width: var(--w-scrollbar);
  height: var(--w-scrollbar);
  border-radius: 9999px;
}

.webkit-scrollbar::-webkit-scrollbar-track {
  background: #0000;
}

.webkit-scrollbar::-webkit-scrollbar-thumb {
  background: #0000;
  border-radius: 9999px;
}

.webkit-scrollbar:hover::-webkit-scrollbar-thumb {
  background: #c1c2c5;
}

.continuebtn {
  position: absolute;
  left: 85%;
  top: 90%;
  display: inline-block;
  border-radius: 4px;
  background-color: #3d405b;
  border: none;
  color: #FFFFFF;
  text-align: center;
  font-size: 17px;
  padding: 16px;
  width: 130px;
  transition: all 0.5s;
  cursor: pointer;
  margin: 5px;
}

.continuebtn span {
  cursor: pointer;
  display: inline-block;
  position: relative;
  transition: 0.5s;
}

.continuebtn span:after {
  content: '»';
  position: absolute;
  opacity: 0;
  top: 0;
  right: -15px;
  transition: 0.5s;
}

.continuebtn:hover span {
  padding-right: 15px;
}

.continuebtn:hover span:after {
  opacity: 1;
  right: 0;
}

.transferencia-container {
  position: absolute;
  left: 60%;
  top: 90%;
  transform: translate(-50%, -100%);
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
  height: 70vh;
  width: 120vh;
}

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

.currency-input {
  padding: 10px;
  border: 2px solid #ccc;
  border-radius: 5px;
  font-size: 16px;
  color: #555;
  outline: none;
  width: 70%;
  margin-bottom: 16px;
}

.currency-input:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}
</style>