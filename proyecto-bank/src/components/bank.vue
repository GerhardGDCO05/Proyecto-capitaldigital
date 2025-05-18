<script>
import { ref } from 'vue';
import usuarioService from "../services/usuarioService";
import "../assets/bank.css";
import "../assets/registro.css";


export default {
  name: "Bank",

  data() {
    return {
      aceptaTerminos: false,
      recordarme: false,
      options: ['Cedula', 'Pasaporte', 'Option 3', 'Option 4'],
      states: ['Miranda', 'Dtto Capital', 'Aragua', 'Carabobo'],
      banks: ['Provincial', 'BDV', 'BNC', 'Mercantil'] 
    };
  },

  setup() {
    const option = ref('Vista General');
    const userName = ref('Simon Manjoud');

    const cambiarOption = (opcion) => {
      option.value = opcion;
    };

    const usuarioCuenta = ref({
      correo: "",
      cuentas: []
    });

    const addCuenta = ref({
      banco: "",
      numeroCuenta: ""
    });

    const agregar = async () => {
      try {
        if (!usuarioCuenta.value.correo) {
          alert("Debe ingresar un email válido.");
          return;
        }
        if (!addCuenta.value.banco || !addCuenta.value.numeroCuenta) {
          alert("Debe ingresar un banco y número de cuenta.");
          return;
        }

        const response = await usuarioService.agregarCuentaPorEmail(usuarioCuenta.value.correo, addCuenta.value);
        alert("Cuenta agregada exitosamente!");

        // Limpia los datos después de agregar la cuenta
        addCuenta.value.banco = "";
        addCuenta.value.numeroCuenta = "";

      } catch (error) {
        console.error("Error al agregar cuenta:", error);
        alert("Error al agregar la cuenta: " + (error.response?.data || error.message));
      }
    };

    return { option, cambiarOption, userName, usuarioCuenta, addCuenta, agregar };
  }
};
</script>


<template>
    <article class="bank-layout">
        <header class="encabezado">
            <h2>{{ option }}</h2>
            <div class="perfil">
                <img class="perfil-icon" src="../../public/usuario.png" alt="User Icon">
                <h3>{{ userName }}</h3>
            </div>
        </header>
        <aside class="sidebar">
            <section class="logo">
                <img src="../../public/bankicon.ico" alt="Logo Banco">
                <h1>CAPITAL DIGITAL</h1>
            </section>
            <section class="principal-options">
                <div class="option-container" @click="cambiarOption('Vista General')">
                    <img class="icon " src="../../public/ojo.png" alt="Dashboard Icon">
                    <p>Vista General</p>
                </div>
                <div class="option-container" @click="cambiarOption('Posición Global')">
                    <img class="icon " src="../../public/grafica.png" alt="Globe Icon">
                    <p>Posición Global</p>
                </div>
                <div class="option-container" @click="cambiarOption('Cuentas')">
                    <img class="icon " src="../../public/cuenta.png" alt="Accounts Icon">
                    <p>Cuentas</p>
                </div>
                <div class="option-container" @click="cambiarOption('Tarjetas')">
                    <img class="icon " src="../../public/tarjeta-de-credito.png" alt="Cards Icon">
                    <p>Tarjetas</p>
                </div>
                <div class="option-container" @click="cambiarOption('Transacciones')">
                    <img class="icon " src="../../public/transaccion.png" alt="Transactions Icon">
                    <p>Transacciones</p>
                </div>
                <div class="option-container" @click="cambiarOption('Patrimonio Neto')">
                    <img class="icon " src="../../public/patrimonio-neto.png" alt="Wealth Icon">
                    <p>Patrimonio Neto</p>
                </div>
            </section>
            <section class="extra-options">
                <div class="option-container" @click="cambiarOption('Configuración')">
                    <img class="icon " src="../../public/configuracion.png" alt="Settings Icon">
                    <p>Configuración</p>
                </div>
                <div class="option-container">
                    <img class="icon" src="../../public/cerrar-sesion.png" alt="Logout Icon">
                    <p>Cerrar Sesión</p>
                </div>
                <div class="switch-container">
                    <div class="switch">
                        <div class="switch-circle"></div>
                    </div>
                    <p>Modo Vista</p>
                </div>
            </section>
        </aside>

        <main class="contenido">
        <!-- Contenido principal -->
            <section class="settings">
                <h3>Agregar cuenta</h3>
                <div class="info-bank">
                    <label for="banco">Banco</label>
                    <select class="cb" id="combo-box" v-model="addCuenta.banco">
                        <option class="datos-regist" v-for="bank in banks" :key="bank" :value="bank">
                        {{ bank }}
                        </option>
                    </select>
                    <div class="Num-cuenta">
                        <label for="numero_cuenta">Numero de Cuenta</label>
                        <input class="number-cuenta datos-regist" type="text" id="numero_cuenta" v-model="addCuenta.numeroCuenta" />
                    </div>
                </div>
                <label for="email">email</label>
                <input class="number-cuenta datos-regist" type="email" id="correo" v-model="usuarioCuenta.correo" />
            </section>
            <Button @click="agregar" class="login-button">Guardar</Button>
        </main>
    </article>
</template>

<style scoped>

</style>