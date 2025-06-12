<script>
import axios from 'axios'
import { ref } from 'vue'
import { useUsuarioStore } from '@/stores/useUsuarioStore'
import bbvaLogo from '@/images/BBVAprovinciallogo.png'
import bdvLogo from '@/images/Banco_de_Venezuela_logo.png'
import mercantilLogo from '@/images/Mercantil.png'
export default {
  name: 'AgregarCuenta',
  setup() {

    const usuarioStore = useUsuarioStore()
    const nombreCuenta = ref('')
    const numeroCuenta = ref('')
    const bancoSeleccionado = ref({ nombre: null, imagen: null })

    const documentoUsuario = usuarioStore.usuario.numeroDocumento

    const seleccionarBanco = (nombreBanco, imagenBanco) => {
      bancoSeleccionado.value = {
        nombre: nombreBanco,
        imagen: imagenBanco // Ahora es un objeto válido
      }
    }

    const agregarCuenta = async () => {
      if (!nombreCuenta.value || !numeroCuenta.value || !bancoSeleccionado.value.nombre) {
        alert("Por favor complete todos los campos.")
        return
      }

      const nuevaCuenta = {
        banco: bancoSeleccionado.value.nombre,
        numeroCuenta: numeroCuenta.value,
        nombreCuenta: nombreCuenta.value
      }

      try {
        const response = await axios.post(
            `http://localhost:8080/cuenta/numeroDocumento/${documentoUsuario}`,
            nuevaCuenta
        )

        if (response.status === 200) {
          alert("✅ Cuenta agregada correctamente")
          nombreCuenta.value = ''
          numeroCuenta.value = ''
          bancoSeleccionado.value = { nombre: null, imagen: null }
        } else {
          alert("❌ No se pudo guardar la cuenta")
        }
      } catch (error) {
        console.error("Error al guardar:", error)
        alert(`⚠️ ${
            typeof error.response?.data === 'object'
                ? JSON.stringify(error.response.data, null, 2)
                : error.response?.data || error.message
        }`)
      }
    }

    return {
      nombreCuenta,
      numeroCuenta,
      bancoSeleccionado,
      seleccionarBanco,
      agregarCuenta,
      bbvaLogo,
      bdvLogo,
      mercantilLogo
    }
  }
}
</script>

<template>
  <header class="header">
    <h1>Cuentas</h1>
  </header>

  <main>
    <div class="container">
      <div class="card">
        <h2 class="title">Agregar cuenta</h2>
        <form class="form-agregarcuenta">
          <!-- Campo Nombre -->
          <p class="message">Nombre de cuenta</p>
          <input v-model="nombreCuenta" type="text" class="input" placeholder="Cuenta de banco ejemplo">

          <!-- Campo Número de cuenta -->
          <p class="message">Número de cuenta</p>
          <input v-model="numeroCuenta" type="text" class="input" placeholder="0105-XXXXXXXXXXXXXXXXX">

          <!-- Selección de Banco -->
          <p class="message">Seleccione banco de origen</p>
          <div class="dropdown">
            <input hidden id="state-dropdown" name="state-dropdown" type="checkbox">

            <!-- Aquí se muestra el logo + nombre del banco seleccionado -->
            <label for="state-dropdown" class="trigger">
              <div v-if="bancoSeleccionado.nombre" style="display: flex; align-items: center; gap: 8px;">
                <img :src="bancoSeleccionado.imagen" width="60" height="30" v-if="bancoSeleccionado.imagen">
                <span>{{ bancoSeleccionado.nombre }}</span>
              </div>
              <span v-else>Seleccione un banco...</span>
            </label>

            <!-- Botones del dropdown -->
            <ul class="list webkit-scrollbar" role="list" dir="auto">
              <li class="listitem" role="listitem">
                <button type="button" class="button" @click="seleccionarBanco('BBVA', bbvaLogo)">
                  <img src="@/images/BBVAprovinciallogo.png" width="60" height="30">
                  <span>BBVA Provincial</span>
                </button>
              </li>
              <li class="listitem" role="listitem">
                <button type="button" class="button" @click="seleccionarBanco('BDV', bdvLogo)">
                  <img src="@/images/Banco_de_Venezuela_logo.png" width="100" height="30">
                  <span>Banco de Venezuela</span>
                </button>
              </li>
              <li class="listitem" role="listitem">
                <button type="button" class="button" @click="seleccionarBanco('Mercantil', mercantilLogo)">
                  <img src="@/images/Mercantil.png" width="60" height="60">
                  <span>Banco Mercantil</span>
                </button>
              </li>
            </ul>
          </div>
        </form>

        <!-- Botón Agregar -->
        <button class="continuebtn" @click="agregarCuenta">
          <span>Agregar</span>
        </button>
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

.container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.message {
  margin-bottom: 16px;
  font-size: 24px;
  font-family: "Inter", sans-serif;
}

.card {
  position: absolute;
  left: 60%;
  top: 90%;
  transform: translate(-50%, -100%);
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
  height: 70vh;
  width: 100vh;
}

.title {
  font-size: 32px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
}

.form-agregarcuenta {
  display: flex;
  flex-direction: column;
}

.input {
  background-color: #f7f7f7;
  color: #333;
  width: 60%;
  border: none;
  border-radius: 4px;
  padding: 10px;
  margin-top: 20px;
  margin-bottom: 20px;
  transition: background-color 0.15s ease-in-out;
}

.input:focus {
  background-color: #eaeaea;
  outline: none;
  box-shadow: 0 0 0 1px #007bff;
}

/*Apartado de selección de banco (Drop-Down)*/
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
  content: "";
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

/*-------------------------------------------*/

/*---Boton de agregar cuenta---*/
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
/*-----------------------------*/
</style>