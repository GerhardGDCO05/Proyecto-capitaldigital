<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'
import { useUsuarioStore } from '@/stores/useUsuarioStore'
import Swal from 'sweetalert2'

interface BeneficiaryModel {
  beneficiaryName: string;
  ID: string;
  accountNumber: string;
  bank: string;
}

const route = useRoute()
const router = useRouter()

// Datos reactivos del formulario
const nombre = ref('')
const numeroCuenta = ref('')
const bancoSeleccionado = ref('')

// Lista de bancos disponibles
const bancos = ['BBVA', 'BDV', 'Mercantil']

const usuarioStore = useUsuarioStore()
const documento = usuarioStore.usuario.numeroDocumento

onMounted(async () => {
  const accountNumber = route.params.accountNumber as string

  if (!accountNumber) {
    await Swal.fire({
      title: 'Error',
      text: '⚠️ Número de cuenta inválido',
      icon: 'error',
      confirmButtonText: 'OK',
      confirmButtonColor: '#3d405b'
    })
    router.push('/beneficiarios')
    return
  }

  try {
    const response = await axios.get<BeneficiaryModel[]>(
        `http://localhost:8080/beneficiaries/${documento}`
    )

    const beneficiario = response.data.find(
        b => b.accountNumber === accountNumber
    )

    if (!beneficiario) {
      await Swal.fire({
        title: 'Error',
        text: '❌ No se encontró el beneficiario',
        icon: 'error',
        confirmButtonText: 'OK',
        confirmButtonColor: '#3d405b'
      })
      router.push('/beneficiarios')
      return
    }

    nombre.value = beneficiario.beneficiaryName
    numeroCuenta.value = beneficiario.accountNumber
    bancoSeleccionado.value = beneficiario.bank

  } catch (error) {
    console.error("❌ Error al cargar beneficiario:", error)
    await Swal.fire({
      title: 'Error',
      text: '⚠️ No se pudieron cargar los datos del beneficiario',
      icon: 'error',
      confirmButtonText: 'OK',
      confirmButtonColor: '#3d405b'
    })
    router.push('/beneficiarios')
  }
})

async function guardarCambios() {
  if (!nombre.value || !bancoSeleccionado.value || !numeroCuenta.value) {
    await Swal.fire({
      title: 'Campos incompletos',
      text: '⚠️ Completa todos los campos',
      icon: 'warning',
      confirmButtonText: 'OK',
      confirmButtonColor: '#3d405b'
    })
    return
  }

  const payload = {
    beneficiaryName: nombre.value,
    ID: '10336111',
    accountNumber: numeroCuenta.value,
    bank: bancoSeleccionado.value
  }

  try {
    const response = await axios.put(
        `http://localhost:8080/beneficiaries/${documento}/${route.params.accountNumber}`,
        payload
    )

    if (response.status === 200) {
      await Swal.fire({
        title: '¡Éxito!',
        text: '✅ Beneficiario modificado exitosamente',
        icon: 'success',
        confirmButtonText: 'OK',
        confirmButtonColor: '#3d405b'
      })
      router.back()
    }
  } catch (error) {
    console.error("❌ Error al guardar cambios:", error)
    await Swal.fire({
      title: 'Error',
      text: `⚠️ ${
          typeof error.response?.data === 'object'
              ? JSON.stringify(error.response.data, null, 2)
              : error.response?.data || error.message
      }`,
      icon: 'error',
      confirmButtonText: 'OK',
      confirmButtonColor: '#3d405b'
    })
  }
}
</script>

<template>

  <header class="header">
    <h1>Beneficiarios</h1>
  </header>

  <div class="modificar-beneficiario">
    <h2>Modificar Beneficiario</h2>

    <form @submit.prevent="guardarCambios" class="form">
      <!-- Campo Nombre -->
      <label for="nombre">Nombre</label>
      <input id="nombre" v-model="nombre" type="text" placeholder="Nombre del beneficiario" required />

      <!-- Campo Cuenta -->
      <label>Número de cuenta</label>
      <input v-model="numeroCuenta" type="text" placeholder="Ingrese nuevo número de cuenta" required />

      <!-- Selección de Banco -->
      <label>Banco</label>
      <select v-model="bancoSeleccionado" required>
        <option v-for="banco in bancos" :key="banco" :value="banco">
          {{ banco }}
        </option>
      </select>

      <!-- Botones -->
      <div class="botones">
        <button type="submit">Guardar cambios</button>
        <button type="button" @click="router.back()">Cancelar</button>
      </div>
    </form>
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
  background-color: #fff;
  border-bottom: 2px solid black;
}

.modificar-beneficiario {
  display: flex;
  flex-direction: column;
  position: absolute;
  top: 20%;
  left: 40%;
  max-width: 500px;
  width: 100%;
  padding: 2rem;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  color: black;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 1rem; /* Espaciado automático entre campos */
}

.form label {
  font-weight: bold;
  margin-bottom: 4px;
}

.form input,
.form select {
  width: 100%;
  padding: 10px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.form select {
  height: 40px;
}

.botones {
  margin-top: 1.5rem;
  display: flex;
  gap: 10px;
}

.botones button {
  background-color: #3d405b;
  color: white;
  border: none;
  padding: 10px 20px;
  cursor: pointer;
  border-radius: 4px;
}

.botones button:hover {
  background-color: #2f3147;
}

pre {
  background: #f0f0f0;
  padding: 10px;
  overflow-x: auto;
}
</style>