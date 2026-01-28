<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useUsuarioStore } from '../stores/useUsuarioStore';
import usuarioService from '@/services/usuarioService';
import Swal from 'sweetalert2';

const router = useRouter();
const usuarioStore = useUsuarioStore();

const usuario = ref({
  nombre: '',
  apellido: '',
  documento: '',
  numeroDocumento: '',
  fechaNacimiento: '',
  direccion: '',
  codigoPostal: '',
  email: '',
  password: '',
  banco: '',
  numeroCuenta: ''
});

const confirmaEmail = ref('');
const confirmaPassword = ref('');
const aceptaTerminos = ref(false);

const options = ['Cedula Nacional', 'Cédula Extranjera'];
const banks = ['BBVA', 'BDV', 'Mercantil'];

// Calcular la fecha máxima para que el usuario tenga al menos 18 años
const maxFechaNacimiento = computed(() => {
  const today = new Date();
  const maxDate = new Date(today.getFullYear() - 18, today.getMonth(), today.getDate());
  return maxDate.toISOString().split('T')[0]; // Formato YYYY-MM-DD
});

const registrarUsuario = async () => {
  // Validación de edad
  const fechaNacimiento = new Date(usuario.value.fechaNacimiento);
  const hoy = new Date();
  const edad = hoy.getFullYear() - fechaNacimiento.getFullYear();
  const mesDiff = hoy.getMonth() - fechaNacimiento.getMonth();
  const diaDiff = hoy.getDate() - fechaNacimiento.getDate();

  // Ajustar edad si el cumpleaños aún no ha ocurrido este año
  const esMenorDe18 = edad < 18 || (edad === 18 && (mesDiff < 0 || (mesDiff === 0 && diaDiff < 0)));

  if (esMenorDe18) {
    await Swal.fire({
      title: 'Error',
      text: 'Debes tener al menos 18 años para registrarte.',
      icon: 'error',
      confirmButtonText: 'OK'
    });
    return;
  }

  // Validaciones existentes
  if (
      !usuario.value.nombre ||
      !usuario.value.apellido ||
      !usuario.value.documento ||
      !usuario.value.numeroDocumento ||
      !usuario.value.fechaNacimiento ||
      !usuario.value.direccion ||
      !usuario.value.codigoPostal ||
      !usuario.value.email ||
      !usuario.value.password ||
      !usuario.value.banco ||
      !usuario.value.numeroCuenta
  ) {
    await Swal.fire({
      title: 'Error',
      text: 'Por favor, completa todos los campos.',
      icon: 'error',
      confirmButtonText: 'OK'
    });
    return;
  }

  if (usuario.value.email !== confirmaEmail.value) {
    await Swal.fire({
      title: 'Error',
      text: 'Los correos electrónicos no coinciden.',
      icon: 'error',
      confirmButtonText: 'OK'
    });
    return;
  }

  if (usuario.value.password !== confirmaPassword.value) {
    await Swal.fire({
      title: 'Error',
      text: 'Las contraseñas no coinciden.',
      icon: 'error',
      confirmButtonText: 'OK'
    });
    return;
  }

  if (!aceptaTerminos.value) {
    await Swal.fire({
      title: 'Error',
      text: 'Debes aceptar los términos y condiciones.',
      icon: 'error',
      confirmButtonText: 'OK'
    });
    return;
  }

  try {
    console.log('Enviando usuario al servidor:', usuario.value);
    const response = await usuarioService.guardarUsuario(usuario.value);
    console.log('Respuesta del servidor (usuario):', response.data);

    const cuentaData = {
      banco: usuario.value.banco,
      numeroCuenta: usuario.value.numeroCuenta,
      nombreCuenta: 'Cuenta Principal'
    };
    console.log('Enviando cuenta al servidor:', cuentaData);
    const cuentaResponse = await usuarioService.agregarCuentaPorNumeroDocumento(
        usuario.value.numeroDocumento,
        cuentaData
    );
    console.log('Respuesta del servidor (cuenta):', cuentaResponse.data);

    usuarioStore.setUsuario({
      numeroDocumento: usuario.value.numeroDocumento,
      nombre: usuario.value.nombre,
      apellido: usuario.value.apellido,
      email: usuario.value.email
    });
    usuarioStore.setCuentas([cuentaData]);

    await Swal.fire({
      title: '¡Éxito!',
      text: 'Usuario y cuenta registrados correctamente.',
      icon: 'success',
      confirmButtonText: 'OK'
    });

    router.push('/login');
  } catch (error) {
    console.error('Error al registrar usuario o cuenta:', error);
    const errorMessage = error.response?.data?.error || 'Hubo un error al registrar el usuario o la cuenta.';
    await Swal.fire({
      title: 'Error',
      text: errorMessage,
      icon: 'error',
      confirmButtonText: 'OK'
    });
  }
};
</script>

<template>
  <div class="grid-container">
    <header class="header">
      <nav class="nav-bar">
        <div class="cap-social">
          <img class="bank-icon" src="../images/bankicon.ico" alt="logo banco">
          <p class="nav-text cap-social-text">Capital</p>
          <p class="nav-text cap-social-text">Digital</p>
        </div>
        <a @click="registrarUsuario" class="links">Registrar</a>
        <a href="#" class="links">Conócenos</a>
        <a href="#" class="links preguntas-frecuentes">Preguntas Frecuentes</a>
      </nav>
    </header>

    <main>
      <div class="register-container">
        <h3 class="title">Datos Personales</h3>
        <div class="datos-personales">
          <div class="input-group">
            <label class="label">Nombre(s)</label>
            <input class="input" type="text" v-model="usuario.nombre" placeholder="Ingrese su nombre" autocomplete="off">
          </div>
          <div class="input-group">
            <label class="label">Apellido(s)</label>
            <input class="input" type="text" v-model="usuario.apellido" placeholder="Ingrese su apellido" autocomplete="off">
          </div>
          <div class="input-group">
            <label class="label">Tipo de Documento</label>
            <select class="input" v-model="usuario.documento">
              <option value="" disabled selected>Selecciona una opción</option>
              <option v-for="option in options" :key="option" :value="option">{{ option }}</option>
            </select>
          </div>
          <div class="input-group">
            <label class="label">Documento</label>
            <input class="input" type="text" v-model="usuario.numeroDocumento" placeholder="N° de documento" autocomplete="off">
          </div>
          <div class="input-group">
            <label class="label">Fecha de Nacimiento</label>
            <input
                class="input"
                type="date"
                v-model="usuario.fechaNacimiento"
                :max="maxFechaNacimiento"
            >
          </div>
          <div class="input-group">
            <label class="label">Dirección</label>
            <input class="input" type="text" v-model="usuario.direccion" placeholder="Ingrese su dirección" autocomplete="off">
          </div>
          <div class="input-group">
            <label class="label">Código Postal</label>
            <input class="input" type="text" v-model="usuario.codigoPostal" placeholder="Ingrese su código postal" autocomplete="off">
          </div>
        </div>
        <div class="segunda-parte-register">
          <div class="informacion-cuenta">
            <h3>Información de Cuenta</h3>
            <div class="inputs-grid">
              <label class="label">Correo electrónico</label>
              <label class="label">Confirmar correo electrónico</label>
              <input class="input" type="email" v-model="usuario.email" placeholder="Correo electrónico">
              <input class="input" type="email" v-model="confirmaEmail" placeholder="Confirmar correo electrónico">
              <label class="label">Contraseña</label>
              <label class="label">Confirmar contraseña</label>
              <input class="input" type="password" v-model="usuario.password" placeholder="Contraseña">
              <input class="input" type="password" v-model="confirmaPassword" placeholder="Confirmar contraseña">
            </div>
          </div>
          <div class="datos-bancarios">
            <h3>Datos Bancarios</h3>
            <div class="inputs-grid">
              <label class="label">Número de cuenta</label>
              <label class="label">Nombre del banco</label>
              <input class="input" type="text" v-model="usuario.numeroCuenta" placeholder="Número de cuenta">
              <select class="input" v-model="usuario.banco">
                <option value="" disabled selected>Selecciona un banco</option>
                <option v-for="bank in banks" :key="bank" :value="bank">{{ bank }}</option>
              </select>
            </div>
            <div class="aceptar-terms-condiciones">
              <div class="checkbox-container">
                <input type="checkbox" id="terms" v-model="aceptaTerminos">
                <label for="terms">He leído y acepto los <a href="#" target="_blank">términos y condiciones</a></label>
              </div>
              <label>Por favor, asegúrese de que todos los campos estén completos antes de registrarse.</label>
              <button class="registerbtn" @click="registrarUsuario">Registrar</button>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
/* Estilos del segundo componente */
header {
  width: 100%;
  height: 85vh;
  margin: 0;
  padding: 0;
}

main {
  position: fixed;
  top: 68%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-image: url("../images/licensed-image.jpg");
  background-size: cover;
  background-position: center center;
  min-height: 100vh;
  width: 100%;
}

main::before {
  content: "";
  position: absolute;
  top: 0%;
  left: 0%;
  width: 100%;
  height: 100%;
  background-color: rgb(216, 221, 221);
  opacity: 0.3;
  z-index: -1;
}

.nav-bar {
  width: 100%;
  height: 20%;
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  align-items: center;
  border-bottom: 2px solid black;
}

.cap-social {
  max-width: 100%;
  max-height: 100%;
  margin-left: 2vh;
  border-left: 2px solid #000;
  display: flex;
  align-items: center;
}

.cap-social-text {
  margin-left: 0.8vh;
  color: rgb(14, 119, 224);
  font-size: 3vh;
  font-weight: bold;
  margin-top: 3vh;
}

.bank-icon {
  margin-top: 1vh;
  width: 10%;
  height: 70%;
}

.links {
  font-family: Arial, Helvetica, sans-serif;
  color: black;
  width: 90%;
  height: 70%;
  text-align: center;
  margin: 0 0.6em;
  font-size: 3vh;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: opacity 0.3s ease;
}

.links:hover {
  opacity: 0.5;
}

.preguntas-frecuentes {
  background-color: rgb(78, 131, 245);
  border-radius: 50px;
  opacity: 1;
  transition: opacity 0.3s ease, color 0.3s ease;
  color: #fff;
}

.preguntas-frecuentes:hover {
  opacity: 0.8;
  color: black;
}

.register-container {
  position: fixed;
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
  min-width: 1700px;
  min-height: 750px;
  width: 60%;
  height: auto;
  max-width: 2000px;
  max-height: 600px;
  background-color: rgb(236, 231, 231);
  color: white;
  padding: 50px;
  box-sizing: border-box;
  z-index: 1000;
  border-radius: 7px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.7);
}

.datos-personales {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-template-rows: repeat(2, auto);
  gap: 10px;
  margin-top: 40px;
  row-gap: 30px;
  column-gap: 20px;
}

.input[type="date"] {
  color: #2f3147;
  padding: 10px;
  font-size: 1rem;
  border-radius: .5rem;
  border: 2px solid transparent;
  background-color: #05060f0a;
  transition: all 0.3s ease;
}

.input[type="date"]:focus {
  outline: none;
  border-color: #05060f;
}

.input-group {
  display: flex;
  flex-direction: column;
}

.input {
  max-width: 190px;
  height: 44px;
  background-color: #05060f0a;
  color: #05060f99;
  border-radius: .5rem;
  padding: 0 1rem;
  border: 2px solid transparent;
  font-size: 1rem;
  transition: border-color .3s cubic-bezier(.25,.01,.25,1) 0s, color .3s cubic-bezier(.25,.01,.25,1) 0s, background .2s cubic-bezier(.25,.01,.25,1) 0s;
}

.label {
  display: block;
  margin-bottom: .3rem;
  font-size: .9rem;
  font-weight: bold;
  color: #05060f99;
  transition: color .3s cubic-bezier(.25,.01,.25,1) 0s;
}

.input:hover, .input:focus, .input-group:hover .input {
  outline: none;
  border-color: #05060f;
}

.input-group:hover .label, .input:focus {
  color: #05060fc2;
}

.segunda-parte-register {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin-top: 40px;
}

.informacion-cuenta {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.inputs-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: repeat(4, auto);
  gap: 15px;
}

.datos-bancarios {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.registerbtn {
  font-size: 17px;
  padding: 0.5em 2em;
  border: transparent;
  box-shadow: 2px 2px 4px rgba(0,0,0,0.4);
  background: dodgerblue;
  color: white;
  border-radius: 4px;
}

.registerbtn:hover {
  background: rgb(2,0,36);
  background: linear-gradient(90deg, rgba(30,144,255,1) 0%, rgba(0,212,255,1) 100%);
}

.registerbtn:active {
  transform: translate(0em, 0.2em);
}

.aceptar-terms-condiciones {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 15px;
}

.aceptar-terms-condiciones label {
  font-size: 0.9rem;
  color: #333;
}

.aceptar-terms-condiciones a {
  color: rgb(78, 131, 245);
  text-decoration: underline;
}

.checkbox-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.checkbox-container input[type="checkbox"] {
  margin: 0;
}

h3 {
  color: cornflowerblue;
  font-weight: 900;
  font-size: 24px;
}

.label {
  color: #000;
}
</style>