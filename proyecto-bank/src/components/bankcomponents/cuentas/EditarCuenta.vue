<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'
import { useUsuarioStore } from "@/stores/useUsuarioStore.js"
import Swal from 'sweetalert2'

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
    await Swal.fire({
      title: 'Error',
      text: '⚠️ Número de cuenta inválido',
      icon: 'error',
      confirmButtonText: 'OK'
    })
    router.push('/cuentas')
    return
  }

  try {
    // Llamo al endpoint que devuelve TODAS las cuentas del usuario
    const response = await axios.get(`http://localhost:8080/cuenta/numeroDocumento/${documento}`)

    // Buscar la cuenta específica
    const cuentaEncontrada = response.data.find(c => c.numeroCuenta === numeroCuenta.value)

    if (!cuentaEncontrada) {
      await Swal.fire({
        title: 'Error',
        text: '❌ Cuenta no encontrada',
        icon: 'error',
        confirmButtonText: 'OK'
      })
      router.push('/cuentas')
      return
    }

    nombreCuenta.value = cuentaEncontrada.nombreCuenta || 'Sin nombre'
    nombreActual.value = nombreCuenta.value
    bancoSeleccionado.value = cuentaEncontrada.banco

  } catch (error) {
    console.error("❌ Error al cargar cuenta:", error)
    await Swal.fire({
      title: 'Error',
      text: '⚠️ No se pudieron cargar los datos de la cuenta',
      icon: 'error',
      confirmButtonText: 'OK'
    })
    router.push('/cuentas')
  }
})

async function guardarCambios() {
  if (!nombreCuenta.value.trim()) {
    await Swal.fire({
      title: 'Campos incompletos',
      text: '⚠️ El nombre no puede estar vacío',
      icon: 'warning',
      confirmButtonText: 'OK'
    })
    return
  }

  try {
    const payload = {
      banco: bancoSeleccionado.value,
      numeroCuenta: numeroCuenta.value,
      nombreCuenta: nombreCuenta.value
    }

    const response = await axios.put(
        `http://localhost:8080/cuenta/numeroDocumento/${documento}/nombreCuenta/${nombreActual.value}`,
        payload
    )

    if (response.status === 200) {
      await Swal.fire({
        title: '¡Éxito!',
        text: '✅ Nombre actualizado exitosamente',
        icon: 'success',
        confirmButtonText: 'OK'
      })
      router.back()
    }

  } catch (error) {
    console.error("❌ Error al guardar cambios:", error)
    await Swal.fire({
      title: 'Error',
      text: `⚠️ Hubo un error al actualizar el nombre de la cuenta: ${
          typeof error.response?.data === 'object'
              ? JSON.stringify(error.response.data, null, 2)
              : error.response?.data || error.message
      }`,
      icon: 'error',
      confirmButtonText: 'OK'
    })
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

/* Contenedor principal del formulario */
.editar-cuenta {
  position: absolute;
  top: 15%;
  left: 25%;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  min-width: 1280px;
  /*width: 90%;*/
  margin: 2rem auto;
  padding: 2.5rem;
  background: linear-gradient(135deg, #ffffff, #f7f7f7);
  border-radius: 16px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #333;
}

/* Título del formulario */
.editar-cuenta h2 {
  font-size: 1.8rem;
  font-weight: 600;
  color: #1a3c8f;
  text-align: center;
  margin-bottom: 1rem;
}

/* Estilo de las etiquetas */
.editar-cuenta label {
  font-size: 1rem;
  font-weight: 500;
  color: #444;
  margin-bottom: 0.5rem;
  display: block;
}

/* Estilo de los inputs */
.editar-cuenta input {
  width: 100%;
  padding: 0.75rem;
  font-size: 1rem;
  border: 2px solid #ccc;
  border-radius: 8px;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  background-color: #fff;
}

/* Input activo */
.editar-cuenta input:focus {
  outline: none;
  border-color: #3d405b;
  box-shadow: 0 0 8px rgba(61, 64, 91, 0.3);
}

/* Input deshabilitado */
.editar-cuenta input:disabled {
  background-color: #f0f0f0;
  border-color: #ddd;
  color: #888;
  cursor: not-allowed;
}

/* Contenedor de botones */
.botones {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 2rem;
}

/* Estilo de los botones, inspirado en .editar-btn de MostrarCuentas.vue */
.botones button {
  position: relative;
  padding: 0.75rem 1.5rem;
  border: none;
  font-size: 1rem;
  font-weight: 600;
  color: #ffffff;
  background-color: #3d405b;
  border-radius: 50px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

/* Efecto de círculo en hover, similar a .editar-btn */
.botones button span:last-child {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 20px;
  height: 20px;
  background-color: #2196f3;
  border-radius: 50%;
  opacity: 0;
  transition: all 0.6s cubic-bezier(0.23, 1, 0.32, 1);
}

/* Texto del botón */
.botones button span:first-child {
  position: relative;
  z-index: 1;
}

/* Hover del botón */
.botones button:hover {
  background-color: #2f3147;
  box-shadow: 0 6px 12px rgba(33, 150, 243, 0.3);
}

/* Efecto de círculo en hover */
.botones button:hover span:last-child {
  width: 150px;
  height: 150px;
  opacity: 1;
}

/* Botón de Cancelar con estilo diferente */
.botones button:last-child {
  background-color: #ed9eb2;
}

/* Hover del botón Cancelar */
.botones button:last-child:hover {
  background-color: #d88a9d;
  box-shadow: 0 6px 12px rgba(237, 158, 178, 0.3);
}

/* Animación al hacer clic */
.botones button:active {
  transform: scale(0.95);
}

/* Media queries para responsividad */
@media (max-width: 600px) {
  .editar-cuenta {
    width: 95%;
    padding: 1.5rem;
  }

  .editar-cuenta h2 {
    font-size: 1.5rem;
  }

  .botones button {
    padding: 0.6rem 1.2rem;
    font-size: 0.9rem;
  }
}
</style>