<script>
import { ref } from 'vue';
import "../assets/bank.css"

export default {
  name: "Bank",
  setup() {
    const option = ref('Vista General');
    const userName = ref('Simon Manjoud');

    const cambiarOption = (opcion) => {
      option.value = opcion;
    };

    return { option, cambiarOption, userName };
  }
}
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
        </main>
  </article>
</template>

<style scoped>

</style>