<script>
import '../assets/registro.css';
import { ref } from "vue";
import { useRouter } from 'vue-router';
import usuarioService from "../services/usuarioService";

export default{
  name: 'Registro',
  data() {
    return {
      aceptaTerminos: false,
      recordarme: false,
      options: ['Cedula', 'Pasaporte', 'Option 3', 'Option 4'],
      banks: ['Provincial', 'BDV', 'BNC', 'Mercantil']
    }
  },
  setup() {
    const router = useRouter();

    const usuario = ref({
      nombre: "",
      apellido: "",
      numeroDocumento: "",
      direccion: "",
      codigoPostal: "",
      email: "",
      password: "",
      banco: "",
      numeroCuenta: "",
      fechaNacimiento: "",
      documento: ""
    });

    const aceptaTerminos = ref(false);
    const erroresBackend = ref({});

    const registrarUsuario = async () => {

      // Si no se aceptan términos, no se continúa.
      if (!aceptaTerminos.value) {
        alert("Debe aceptar los términos y condiciones.");
        return;
      }

      // confirmar si son iguales los correos
      const email=document.getElementById("email");
      const password=document.getElementById("password");
      const confirmEmail = document.getElementById("confir-email");
      const confirmPassword=document.getElementById("confirm-password");

      //  Si los correos están vacíos, poner borde rojo
      if (usuario.value.email.trim() === "" || confirmEmail.value.trim() === "") {
        alert("Debe ingresar ambos correos electrónicos.");
        confirmEmail.style.borderBottom = "2px solid #a00d";
        email.style.borderBottom = "2px solid #a00d";
        return;
      }

      //  Si los correos no coinciden, poner borde rojo
      if (usuario.value.email !== confirmEmail.value) {
        alert("Los correos electrónicos no coinciden.");
        confirmEmail.style.borderBottom = "2px solid #a00d";
        email.style.borderBottom = "2px solid #a00d";
        return;
      } else {
        confirmEmail.style.borderBottom = "2px solid green";
        email.style.borderBottom = "2px solid green";
      }

      //  Validación de contraseña vacía
      if (usuario.value.password.trim() === "" || confirmPassword.value.trim() === "") {
        alert("Debe ingresar y confirmar la contraseña.");
        confirmPassword.style.borderBottom = "2px solid red";
        password.style.borderBottom = "2px solid red";
        return;
      }

      // Si las contraseñas no coinciden, poner borde rojo
      if (usuario.value.password !== confirmPassword.value) {
        alert("Las contraseñas deben coincidir.");
        confirmPassword.style.borderBottom = "2px solid red";
        password.style.borderBottom = "2px solid red";
        return;
      } else {
        confirmPassword.style.borderBottom = "2px solid green";
        password.style.borderBottom = "2px solid green";
      }

      console.log("Datos enviados al backend:", JSON.stringify(usuario.value));

      try {
        const response = await usuarioService.guardarUsuario(usuario.value);
        alert("¡Registro exitoso!");
        router.push('/bank?popup=true');
      } catch (error) {
        console.error("Error al registrar usuario:", error);
        if (error.response && error.response.data) {
          erroresBackend.value = error.response.data;
          const mensajesError = Object.values(erroresBackend.value).join("\n");
          alert("Error al registrar usuario:\n" + mensajesError);
        } else {
          alert("Error desconocido al registrar usuario.");
        }
      }
    };

    return {
      usuario,
      aceptaTerminos,
      registrarUsuario,
      erroresBackend
    };
  }
};
</script>
<template>
    <section id="registro-modal" class="registro-modal">
        <!-- Botón para cerrar el modal -->
        <h1>Registrate</h1>
        <p>Datos Personales</p>
        <div class="regist-content-up">
          <!------------------------------------------>
          <div class="camp-regist nombre-container">
            <!-- Campo para el nombre completo -->
            <label for="nombre">Nombre(s)</label>
            <input class="datos-regist" type="text" id="nombre" placeholder="Ingrese su nombre" v-model="usuario.nombre" />
          </div>
            
          <div class="camp-regist apellido-container">
            <!-- Campo para el apellido completo -->
            <label for="apellido">Apellido(s)</label>
            <input class="datos-regist" type="text" id="apellido" placeholder="Ingrese su apellido" v-model="usuario.apellido" />
          </div>
          
          <div class="tipo-doc-container">
            <div class="camp-regist">
              <label for="identificacion">Tipo de Documento</label>
              <select class="cb" id="combo-box" v-model="usuario.documento">
                <option class="datos-regist" v-for="option in options" :key="option" :value="option">
                  {{ option }}
                </option>
              </select>
            </div>
            <div class="camp-regist Documento">
              <label for="documento">Documento</label>
              <input class="datos-regist" type="text" id="documento" placeholder="N° de documento" v-model="usuario.numeroDocumento" />
            </div>
          </div>
          <!------------------------------------------>
          
          <div class="camp-regist">
            <label for="fechaNacimiento">Fecha de Nacimiento</label>
            <input class="cb datos-regist" type="date" id="fechaNacimiento" v-model="usuario.fechaNacimiento" />
          </div>

          <div class="camp-regist direccion-container">
            <!-- Campo para la dirección -->
            <label for="direccion">Dirección</label>
            <input class="datos-regist" type="text" id="direccion" placeholder="Ingrese su dirección" v-model="usuario.direccion" />
          </div>
            
          <div class="camp-regist codigopostal-container">
            <!-- Campo para el código postal -->
            <label for="codigopostal">Código Postal</label>
            <input class="datos-regist" type="text" id="codigopostal" placeholder="Ingrese su código postal" v-model="usuario.codigoPostal" />
          </div>
        </div>
        <div class="regist-content-dowm">

          <fieldset class="info-cuenta">
            <legend>Informacion de la Cuenta</legend>
            <div class="emails">
              <div  class="correo-electronico">
                <label for="correo">Correo Electronico</label>
                <input class="datos-regist" type="email" id="email" placeholder="Ingrese su Email" v-model="usuario.email" />
              </div>

              <div  class="confirmacion-correo-electronico">
                <label for="correo">Confirmar Correo Electronico</label>
                <input class="datos-regist" type="email" id="confir-email" placeholder="Ingrese su Email" />
              </div>
              
            </div>
            <div class="password">
              <div class="contra">
                  <!-- Campo para la contraseña -->
                <label for="password">Contraseña</label>
                <input class="datos-regist" type="password" id="password" placeholder="Ingrese su contraseña" v-model="usuario.password" />
              </div>
              <div class="confirmacion-contra">
                  <!-- Campo para la confirmación de la contraseña -->
                <label for="confirm-password">Confirmar Contraseña</label>
                <input class="datos-regist" type='password' id="confirm-password" placeholder="Confirme su contraseña" />
              </div>
              
            </div>
          </fieldset>

          <fieldset class="data-bank">
            <legend>Datos Bancarios</legend>
            <div class="info-bank">
              <label for="banco">Banco</label>
              <select class="cb" id="combo-box" v-model="usuario.banco">
                <option class="datos-regist" v-for="bank in banks" :key="bank" :value="bank">
                  {{ bank }}
                </option>
              </select>
              <div class="Num-cuenta">
                <label for="numero_cuenta">Numero de Cuenta</label>
                <input class="number-cuenta datos-regist" type="text" id="numero_cuenta" v-model="usuario.numeroCuenta" />
              </div>
            </div>
            <div class="aceptar-terms-condiciones">
              <input type="checkbox" id="terms" v-model="aceptaTerminos" />
              <label for="terms">He leido y acepto los <a href="#" target="_blank">términos y condiciones</a></label>
              <label>Por favor, asegúrese de que todos los campos estén completos antes de registrarse.</label>
              <div class="continuar-button">
                <a @click="registrarUsuario" class="custom-button">>>>Continuar</a>
              </div>
            </div>

          </fieldset>
        </div>
        
      </section>
</template>
