<script>
import { ref, computed, nextTick } from 'vue';
import usuarioService from "../services/usuarioService";
import "../assets/bank.css";
import "../assets/registro.css";
import { useRoute } from "vue-router";
import { watch } from "vue";

export default {
  name: "Bank",

  setup() {
    const route = useRoute();
    const option = ref("Vista General");
    const userName = ref("Simon Manjoud");
    const numeroDocumento = ref("");
    const cuenta = ref({ banco: "", numeroCuenta: "" });
    const banks = ref(["BBVA", "BDV", "BNC", "Mercantil"]);
    
    // Inicialización más robusta
    const usuario = ref({});
    const cuentas = ref([]);

    // Función para parsear datos de forma segura
    const parseJsonSafely = (jsonString, defaultValue = []) => {
      if (!jsonString) return defaultValue;
      try {
        const parsed = JSON.parse(jsonString);
        return Array.isArray(parsed) ? parsed : defaultValue;
      } catch (error) {
        console.error("Error parsing JSON:", error);
        return defaultValue;
      }
    };

    // Inicializar datos desde route.query
    const initializeData = () => {
      if (route.query.usuario) {
        try {
          usuario.value = JSON.parse(route.query.usuario);
        } catch (error) {
          console.error("Error parsing usuario:", error);
          usuario.value = {};
        }
      }

      if (route.query.cuentas) {
        const parsedCuentas = parseJsonSafely(route.query.cuentas, []);
        cuentas.value = [...parsedCuentas]; // Crear una nueva referencia
        console.log("Cuentas inicializadas:", cuentas.value);
      }
    };

    // Inicializar al montar el componente
    initializeData();

    // Watch mejorado con deep y immediate
    watch(
      () => route.query.cuentas, 
      (newValue) => {
        console.log("Watch activado - nuevo valor:", newValue);
        if (newValue) {
          const parsedCuentas = parseJsonSafely(newValue, []);
          // Forzar reactividad creando una nueva referencia
          cuentas.value = [...parsedCuentas];
          console.log("Cuentas actualizadas en Bank.vue:", cuentas.value);
          
          // Forzar re-render si es necesario
          nextTick(() => {
            console.log("NextTick - cuentas:", cuentas.value);
          });
        } else {
          cuentas.value = [];
        }
      },
      { immediate: true, deep: true }
    );

    // Computed para garantizar reactividad
    const cuentasReactivas = computed(() => {
      console.log("Computed cuentasReactivas ejecutado:", cuentas.value);
      return cuentas.value || [];
    });

    const cambiarOption = (opcion) => {
      option.value = opcion;
    };

    const buscar = async () => {
      try {
        if (!numeroDocumento.value) {
          alert("Debe ingresar un número de documento válido.");
          return;
        }
        const response = await usuarioService.obtenerUsuarioPorNumeroDocumento(numeroDocumento.value);
        usuario.value = response.data;
      } catch (error) {
        console.error("Error al buscar", error);
        alert("Error al buscar " + (error.response?.data || error.message));
      }
    };

    const agregar = async () => {
      try {
        if (!cuenta.value.banco || !cuenta.value.numeroCuenta) {
          alert("Debe seleccionar un banco y ingresar un número de cuenta.");
          return;
        }

        const response = await usuarioService.agregarCuentaPorNumeroDocumento(usuario.value.numeroDocumento, cuenta.value);

        alert("Cuenta registrada correctamente!");

        // Opcional: Actualizar la lista local después de agregar
        if (response.data && response.data.cuentas) {
          cuentas.value = [...response.data.cuentas];
        }

      } catch (error) {
        console.error("Error al registrar cuenta:", error);

        if (error.response && error.response.data) {
          const errores = error.response.data;

          if (errores.numeroCuenta) {
            alert("Error: " + errores.numeroCuenta);
          } else {
            alert("Error al registrar cuenta: " + JSON.stringify(errores));
          }
        } else {
          alert("Error desconocido al registrar la cuenta.");
        }
      }
    };

    return { 
      option, 
      cambiarOption, 
      userName, 
      numeroDocumento, 
      usuario, 
      banks, 
      cuenta, 
      agregar, 
      buscar,
      cuentas,
      cuentasReactivas
    };
  },
};
</script>

<template>
    <article class="bank-layout">
        <header class="encabezado">
            <h2>{{ option }}</h2>
            <div class="perfil">
                <img class="perfil-icon" src="../../public/usuario.png" alt="User Icon">
                <h3>{{ usuario.nombre }}</h3>
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
            <section class="settings">
                <label for="numeroDocumento">Numero de documento</label>
                <input class="number-cuenta datos-regist" type="text" id="correo" v-model="numeroDocumento" />
            </section>
            <Button @click="buscar" class="login-button">Buscar</Button>

            <div class="prueba1 prueba">
                <h3>Información del Usuario</h3>
                
                <p><strong>Nombre:</strong> {{ usuario.nombre }}</p>
                <p><strong>Apellido:</strong> {{ usuario.apellido }}</p>
                <p><strong>Tipo de Documento:</strong> {{ usuario.documento }}</p>
                <p><strong>Número de Documento:</strong> {{ usuario.numeroDocumento }}</p>
                <p><strong>Fecha de Nacimiento:</strong> {{ usuario.fechaNacimiento }}</p>
                <p><strong>Dirección:</strong> {{ usuario.direccion }}</p>
                <p><strong>Código Postal:</strong> {{ usuario.codigoPostal }}</p>
                <p><strong>Email:</strong> {{ usuario.email }}</p>
                <p><strong>Banco:</strong> {{ usuario.banco }}</p>
                <p><strong>Número de Cuenta:</strong> {{ usuario.numeroCuenta }}</p>

                <h4>Cuentas Registradas ({{ cuentasReactivas.length }})</h4>
                
                <!-- Versión con computed -->
                <div class="cuentasmostrar">
                    <ul>
                        <li v-for="(cuenta, index) in cuentasReactivas" :key="`cuenta-${index}-${cuenta.numeroCuenta}`">
                            <strong>Nombre de Cuenta:</strong> {{ cuenta.nombreCuenta }} - <strong>Banco:</strong> {{ cuenta.banco }} - <strong>Número de Cuenta:</strong> {{ cuenta.numeroCuenta }}
                        </li>
                    </ul>
                </div>

            </div>

            <div class="prueba2 prueba">
                <h3>Registrar cuenta</h3>
                
                <!-- Selección de banco -->
                <label for="banco">Banco:</label>
                <select class="cb" id="banco" v-model="cuenta.banco">
                    <option v-for="bank in banks" :key="bank" :value="bank">{{ bank }}</option>
                </select>

                <!-- Número de cuenta -->
                <label for="numeroCuenta">Número de Cuenta:</label>
                <input class="number-cuenta datos-regist" type="text" id="numeroCuenta" v-model="cuenta.numeroCuenta" />

                <div class="continuar-button">
                    <a @click="agregar" class="custom-button">>>> Continuar</a>
                </div>
            </div>
        </main>
    </article>
</template>