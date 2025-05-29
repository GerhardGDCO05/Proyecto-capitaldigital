<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'
import { useUsuarioStore } from '@/stores/useUsuarioStore'

// Datos del beneficiario
const usuarioStore = useUsuarioStore()
const documento = usuarioStore.usuario.numeroDocumento

const nombre = ref('')
const cedula = ref('')
const numeroCuenta = ref('')
const bancoSeleccionado = ref<{ nombre: string | null; imagen: string | null }>({
  nombre: null,
  imagen: null
})

// Lista de bancos disponibles
const bancos = [
  { nombre: 'BBVA', logo: '/src/images/BBVAprovinciallogo.png' },
  { nombre: 'BDV', logo: '/src/images/Banco_de_Venezuela_logo.png' },
  { nombre: 'Mercantil', logo: '/src/images/Mercantil.png' }
]

// Función para seleccionar banco
function seleccionarBanco(nombreBanco: string) {
  const banco = bancos.filter(b => b.nombre === nombreBanco)[0]
  if (banco) {
    bancoSeleccionado.value = {
      nombre: banco.nombre,
      imagen: banco.logo
    }
  }
}

// Agregar beneficiario al backend
function agregarBeneficiario() {
  if (!nombre.value || !cedula.value || !numeroCuenta.value || !bancoSeleccionado.value.nombre) {
    alert("⚠️ Completa todos los campos")
    return
  }

  const nuevo = {
    beneficiaryName: nombre.value,
    ID: cedula.value.trim() || 'N/A',
    accountNumber: numeroCuenta.value,
    bank: bancoSeleccionado.value.nombre
  }

  // Enviar datos al backend como promesa normal
  axios.post(`http://localhost:8080/beneficiaries/${documento}`, nuevo)
      .then(response => {
        console.log("Respuesta del servidor:", response.data)

        alert("Beneficiario agregado exitosamente")

        // Limpiar formulario
        nombre.value = ''
        cedula.value = ''
        numeroCuenta.value = ''
        bancoSeleccionado.value = { nombre: '', imagen: '' }

        // Lanzar evento global para recargar la lista
        window.dispatchEvent(new CustomEvent("beneficiario-agregado"))

      })
      .catch(error => {
        console.error("❌ Error al guardar:", error)
        alert(`⚠️ ${error.response?.data || error.message}`)
      })
}
</script>

<template>
  <form class="form" @submit.prevent="agregarBeneficiario">
    <p class="form-title">Agregar beneficiario</p>

    <!-- Campo Nombre -->
    <div class="input-container">
      <input v-model="nombre" type="text" placeholder="Nombre del beneficiario" required />
    </div>

    <!-- Campo Cédula (ID) -->
    <div class="input-container">
      <input v-model="cedula" type="text" placeholder="Cédula del beneficiario" required />
    </div>

    <!-- Campo Número de cuenta -->
    <div class="input-container">
      <input v-model="numeroCuenta" type="text" placeholder="Número de cuenta bancaria" required />
    </div>

    <!-- Selección de Banco -->
    <div class="dropdown">
      <input hidden id="state-dropdown" name="state-dropdown" type="checkbox" />

      <label for="state-dropdown" class="trigger">
        <span v-if="bancoSeleccionado.nombre">{{ bancoSeleccionado.nombre }}</span>
        <span v-else>Seleccione un banco...</span>
      </label>

      <ul class="list webkit-scrollbar" role="list" dir="auto">
        <li class="listitem" role="listitem">
          <button type="button" class="button" @click="seleccionarBanco('BBVA')">
            <img src="@/images/BBVAprovinciallogo.png" width="60" height="30" />
            <span>BBVA Provincial</span>
          </button>
        </li>
        <li class="listitem" role="listitem">
          <button type="button" class="button" @click="seleccionarBanco('BDV')">
            <img src="@/images/Banco_de_Venezuela_logo.png" width="100" height="30" />
            <span>Banco de Venezuela</span>
          </button>
        </li>
        <li class="listitem" role="listitem">
          <button type="button" class="button" @click="seleccionarBanco('Mercantil')">
            <img src="@/images/Mercantil.png" width="60" height="60" />
            <span>Banco Mercantil</span>
          </button>
        </li>
      </ul>
    </div>

    <!-- Botón submit -->
    <button type="submit" class="submit">Agregar beneficiario</button>
  </form>
</template>

<style scoped>
/*---FORM PARA AGREGAR BENEFICIARIO---*/
.form {
  background-color: #fff;
  display: block;
  padding: 1rem;
  max-width: 700px;
  border-radius: 0.5rem;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

.form-title {
  font-family: "Inter", sans-serif;
  font-size: 2.75rem;
  line-height: 1.75rem;
  font-weight: 600;
  text-align: left;
  color: #000;
}

.input-container {
  position: relative;
}

.input-container input, .form button {
  outline: none;
  border: 1px solid #e5e7eb;
  margin: 8px 0;
}

.input-container input {
  background-color: #fff;
  padding: 1rem;
  padding-right: 3rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  width: 500px;
  border-radius: 0.5rem;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.submit {
  display: block;
  padding-top: 0.75rem;
  padding-bottom: 0.75rem;
  padding-left: 1.25rem;
  padding-right: 1.25rem;
  background-color: #4F46E5;
  color: #ffffff;
  font-size: 0.875rem;
  line-height: 1.25rem;
  font-weight: 500;
  width: 100%;
  border-radius: 0.5rem;
  text-transform: uppercase;
}

.submit:hover {
  background-color: #3730a3;
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
}

.submit:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.5);
}

/*-----------------------------------------------*/

/*---SELECCION DE BANCO (Drop-Down)---*/
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
  inset-inline: auto;
  max-width: 398px;
  min-width: 298px;
  margin-top: 15px;
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
  list-style: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  user-select: none;
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

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}

.dropdown input:where(:checked) + .trigger {
  margin-bottom: 1rem;
}

.dropdown input:where(:checked) + .trigger:before {
  rotate: 90deg;
  transition-delay: 0ms;
}

.dropdown input:where(:checked) + .trigger::after {
  content: "Cerrar selección...";
}

.trigger:before,
.trigger::after {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
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

.trigger::after {
  content: "Seleccione un banco...";
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

.button:hover {
  background-color: #f0f0f0; /* Cambiar el color de fondo al pasar el mouse */
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
/*----------------------------*/
</style>