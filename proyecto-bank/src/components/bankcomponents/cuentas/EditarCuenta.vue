<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'
import {useUsuarioStore} from "@/stores/useUsuarioStore.js";

const route = useRoute()
const router = useRouter()

// Datos reactivos
const documento = useUsuarioStore().usuario.numeroDocumento
const numeroCuenta = ref(route.params.numeroCuenta)
const nombreCuenta = ref('')
const nombreActual = ref('')
const bancoSeleccionado = ref('')

onMounted(async () => {
  if (!numeroCuenta.value) {
    alert("⚠️ Número de cuenta inválido")
    router.push('/cuentas')
    return
  }

  try {
    // Llamo al endpoint que devuelve TODAS las cuentas del usuario
    const response = await axios.get(`http://localhost:8080/cuenta/numeroDocumento/${documento}`)

    // Buscar la cuenta específica
    const cuentaEncontrada = response.data.find(c => c.numeroCuenta === numeroCuenta.value)

    if (!cuentaEncontrada) {
      alert("❌ Cuenta no encontrada")
      router.push('/cuentas')
      return
    }

    nombreCuenta.value = cuentaEncontrada.nombreCuenta || 'Sin nombre'
    nombreActual.value = nombreCuenta.value
    bancoSeleccionado.value = cuentaEncontrada.banco

  } catch (error) {
    console.error("❌ Error al cargar cuenta:", error)
    alert("⚠️ No se pudieron cargar los datos de la cuenta")
    router.push('/cuentas')
  }
})
async function guardarCambios() {
  if (!nombreCuenta.value.trim()) {
    alert("⚠️ El nombre no puede estar vacío")
    return
  }

  try {
    const payload = {
      banco: bancoSeleccionado.value,
      numeroCuenta: numeroCuenta.value,
      nombreCuenta: nombreCuenta.value
    }

    const response = await axios.put(
        `http://localhost:8080/cuenta/numeroDocumento/${documento}/nombreCuenta/${nombreActual.value}`, // ✅ Nombre original
        payload
    )

    if (response.status === 200) {
      alert("✅ Nombre actualizado exitosamente")
      router.back()
    }

  } catch (error) {
    console.error("❌ Error al guardar cambios:", error)
    alert(`Hubo un error al actualizar el nombre de la cuenta: ${
        typeof error.response?.data === 'object'
            ? JSON.stringify(error.response.data, null, 2)
            : error.response?.data || error.message
    }`)
  }
}

</script>

<template>
  <header class="header">
    <h1>Editar Cuenta</h1>
  </header>

  <main class="editar-cuenta">
    <h2>Modificar nombre de cuenta</h2>

    <!-- Campo Número de cuenta -->
    <label>Número de cuenta:</label>
    <input :value="numeroCuenta" disabled />

    <!-- Campo Nombre actual -->
    <label>Nombre actual:</label>
    <input :value="nombreActual" disabled />

    <!-- Campo Nuevo nombre -->
    <label>Nuevo nombre:</label>
    <input v-model="nombreCuenta" type="text" placeholder="Ej: Mi cuenta personal" required />

    <!-- Botones -->
    <div class="botones">
      <button @click="guardarCambios">Guardar cambios</button>
      <button @click="router.back()">Cancelar</button>
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

.editar-cuenta {
  display: flex;
  flex-direction: column;
  position: absolute;
  top: 50%;
  left: 50%;
  max-width: 500px;
  height: 500px;
  margin: 2rem auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  color: black;
}

.form label {
  display: block;
  margin-top: 1rem;
  font-weight: bold;
}

.form input[type="text"] {
  width: 100%;
  padding: 10px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 4px;
  display: block;
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
</style>