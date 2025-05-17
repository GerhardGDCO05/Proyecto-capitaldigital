<script>
  import '../assets/registro.css';
  import { ref } from "vue";
  import usuarioService from "../services/usuarioService"; 
      export default{
        name: 'Registro',
        data() {
            return {
              aceptaTerminos: false,
              recordarme: false,
              options: ['Cedula', 'Pasaporte', 'Option 3', 'Option 4'],
              states: ['Miranda', 'Dtto Capital', 'Aragua', 'Carabobo'],
              banks: ['Provincial', 'BDV', 'BNC', 'Mercantil']
            }
        },
        setup() {
          const usuario = ref({
            nombre: "",
            apellido: "",
            numeroDocumento: "",
            direccion: "",
            codigoPostal: "",
            email: "",
            password: "",
            cuentas: [],  // Aquí se guardarán todas las cuentas
            estado: "",
            documento: ""
          });

          // Objeto intermedio que vincula los campos del formulario para la cuenta bancaria.
          const nuevaCuenta = ref({
            banco: "",
            numeroCuenta: ""
          });

          const aceptaTerminos = ref(false);
          const erroresBackend = ref({});

          const registrarUsuario = async () => {
            // Si no se aceptan términos, no se continúa.
            if (!aceptaTerminos.value) {
              alert("Debe aceptar los términos y condiciones.");
              return;
            }

            // Si existen datos en nuevaCuenta (es decir, los campos de banco y cuenta fueron completados),
            // se agregan a la lista 'cuentas' del usuario.
            if (nuevaCuenta.value.banco && nuevaCuenta.value.numeroCuenta) {
              usuario.value.cuentas.push({
                banco: nuevaCuenta.value.banco,
                numeroCuenta: nuevaCuenta.value.numeroCuenta
              });
              // Limpiar el objeto intermedio después de agregar la cuenta.
              nuevaCuenta.value.banco = "";
              nuevaCuenta.value.numeroCuenta = "";
            }

            // Verificar que se haya agregado al menos una cuenta en la lista 'cuentas'
            if (usuario.value.cuentas.length === 0) {
              alert("Debes proporcionar al menos un banco y un número de cuenta antes de registrarte.");
              return;
            }

            console.log("Datos enviados al backend:", JSON.stringify(usuario.value));

            try {
              const response = await usuarioService.guardarUsuario(usuario.value);
              console.log("Usuario registrado:", response.data);
              alert("¡Registro exitoso!");
              this.$router.push('/bank?popup=true');
            } catch (error) {
              console.error("Error al registrar usuario:", error);
              if (error.response && error.response.data) {
                erroresBackend.value = error.response.data;
                const mensajesError = Object.values(erroresBackend.value).join("\n"); // Convierte errores en texto legible
                alert("Error al registrar usuario:\n" + mensajesError); 
              } else {
                alert("Error desconocido al registrar usuario.");
              }
            }
          };

          return { usuario, nuevaCuenta, aceptaTerminos, registrarUsuario, erroresBackend };
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
            <label for="estado">Estado</label>
            <select class=" cb" id="combo-box" v-model="usuario.estado">
              <option class="datos-regist" v-for="state in states" :key="state" :value="state">
                {{ state }}
              </option>
            </select>
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
              <select class="cb" id="combo-box" v-model="nuevaCuenta.banco">
                <option class="datos-regist" v-for="bank in banks" :key="bank" :value="bank">
                  {{ bank }}
                </option>
              </select>
              <div class="Num-cuenta">
                <label for="numero_cuenta">Numero de Cuenta</label>
                <input class="number-cuenta datos-regist" type="text" id="numero_cuenta" v-model="nuevaCuenta.numeroCuenta" />
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
