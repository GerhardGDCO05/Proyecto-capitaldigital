<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import { useRoute } from 'vue-router'
import { useUsuarioStore } from '@/stores/useUsuarioStore'
import Modal from "@/view/Modal.vue";
import AgregarBeneficiario from "@/components/bankcomponents/transacciones/crudbeneficiarios/AgregarBeneficiario.vue";

interface BeneficiaryModel {
  beneficiaryName: string;
  accountNumber: string;
  bank: string;
}

const beneficiarioSeleccionado = ref(null)

const router = useRouter()
const route = useRoute()
const beneficiarios = ref<BeneficiaryModel[]>([])
const showModal = ref(false)
const metaSeleccionada = ref(null)

const usuarioStore = useUsuarioStore()
const documento = usuarioStore.usuario.numeroDocumento

onMounted(() => {
  cargarBeneficiarios()
  window.addEventListener('resize', () => {})
})

watch(
    () => route.path,
    () => {
      setTimeout(() => window.dispatchEvent(new Event('resize')), 50)
    }
)

const cargarBeneficiarios = async () => {
  try {
    const response = await axios.get<BeneficiaryModel[]>(`http://localhost:8080/beneficiaries/${documento}`)
    beneficiarios.value = response.data
    console.log("✅ Beneficiarios cargados:", beneficiarios.value)
  } catch (error) {
    console.error("❌ Error al cargar beneficiarios:", error)
    alert("⚠️ No se pudieron cargar tus beneficiarios")
  }
}

function seleccionarBeneficiario(benef) {
  beneficiarioSeleccionado.value = benef
  console.log(' Beneficiario seleccionado:', benef)
}

// Eliminar beneficiario
const eliminarBeneficiario = async (accountNumber: string) => {
  if (!confirm("¿Seguro que deseas eliminar este beneficiario?")) return

  try {
    await axios.delete(`http://localhost:8080/beneficiaries/${documento}/${accountNumber}`)
    beneficiarios.value = beneficiarios.value.filter(b => b.accountNumber !== accountNumber)
    alert("🗑️ Beneficiario eliminado correctamente")
  } catch (error) {
    console.error("Error al eliminar beneficiario:", error)
    alert("❌ Error al eliminar el beneficiario")
  }
}

function abrirFormularioModificacion() {
  if (!beneficiarioSeleccionado.value) {
    alert("⚠️ Selecciona un beneficiario antes de modificar")
    return
  }

  router.push({
    name: 'ModificarBeneficiario',
    params: {
      accountNumber: beneficiarioSeleccionado.value.accountNumber
    },
    query: {
      popup: 'true'
    }
  })
}
</script>

<template>
  <header class="header" :key="$route.fullPath"> <!--BUG FIX: :key="$route.fullPath fuerza a Vue a que el componente se cargue correctamente-->
    <h1>Transferencias</h1>
  </header>

  <button class="agregar-benf" @click="showModal = true">Agregar</button>
  <button class="modificar-benf" @click="abrirFormularioModificacion()">Modificar</button>


  <button class="eliminar-benf">Eliminar</button>
  <div class="card">
    <div class="card__title">Beneficiarios</div>
    <table>
      <thead>
      <tr>
        <th></th>
        <th>N°</th>
        <th>Nombre(s)</th>
        <th>Cuenta</th>
        <th>Banco</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(benef, index) in beneficiarios" :key="benef.accountNumber">
        <td>
          <label class="checkbox">
            <input type="checkbox" @click.stop="seleccionarBeneficiario(benef)">
            <span class="checkmark"></span>
          </label>
        </td>
        <td>{{ index + 1 }}</td>
        <td>{{ benef.beneficiaryName }}</td>
        <td>{{ benef.accountNumber }}</td>
        <td>{{ benef.bank }}</td>
      </tr>
      </tbody>
    </table>
  </div>



  <!--MODAL PARA LA VENTANA EMERGENTE-->
  <Modal :is-open="showModal" @close="showModal = false">
    <AgregarBeneficiario />
  </Modal>

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

.card {
  width: 70%; /* Cambiado a auto para que se ajuste al contenido */
   /* Establecer un ancho máximo */
  position: absolute;
  left: 55%;
  top: 60%;
  transform: translate(-50%, -100%);
  background: rgb(0, 31, 64);
  font-family: "Courier New", Courier, monospace;
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
  border-bottom-left-radius: 4px;
  border-bottom-right-radius: 4px;
  margin: 20px; /* Añadir margen para separar de otros elementos */
}

.card__title {
  color: white;
  font-weight: bold;
  padding: 5px 10px;
  border-bottom: 1px solid rgb(167, 159, 159);
  font-size: 1.6rem;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 1.2rem;
  background: white;
}

thead tr {
  background: rgb(234, 235, 234);
}

thead th {

  padding: 10px 12px;
  border: 1px solid rgb(203, 203, 203);
  text-align: left;
}

tbody td {
  padding: 8px 12px;
  border-right: 1px solid rgb(203, 203, 203);
  border-left: 1px solid rgb(203, 203, 203);
  border-bottom: 1px solid rgb(203, 203, 203);
}

tbody tr:nth-child(even) {
  background: rgb(234, 235, 234);
}

/* Ajustes para la última columna, no tener borde derecho */
tbody td:last-child {
  border-right: none;
  text-align: left;
}

/*---BOTONES AGREGAR, MODIFICAR, ELIMINAR---*/
.agregar-benf {
  position: absolute;
  left: 65%;
  top: 15%;
  background-color: #f3f7fe;
  color: #3b82f6;
  border: none;
  cursor: pointer;
  border-radius: 8px;
  width: 150px;
  height: 50px;
  transition: 0.3s;
}

.agregar-benf:hover {
  background-color: #3b82f6;
  box-shadow: 0 0 0 5px #3b83f65f;
  color: #fff;
}

.modificar-benf {
  position: absolute;
  left: 75%;
  top: 15%;
  background-color: #f3f7fe;
  color: #3b82f6;
  border: none;
  cursor: pointer;
  border-radius: 8px;
  width: 150px;
  height: 50px;
  transition: 0.3s;
}

.modificar-benf:hover {
  background-color: #3b82f6;
  box-shadow: 0 0 0 5px #3b83f65f;
  color: #fff;
}

.eliminar-benf {
  position: absolute;
  left: 85%;
  top: 15%;
  background-color: #f3f7fe;
  color: #3b82f6;
  border: none;
  cursor: pointer;
  border-radius: 8px;
  width: 150px;
  height: 50px;
  transition: 0.3s;
}

.eliminar-benf:hover {
  background-color: #3b82f6;
  box-shadow: 0 0 0 5px #3b83f65f;
  color: #fff;
}

/*---------------------------------------*/

/* From Uiverse.io by JkHuger */
.checkbox {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
}

.checkbox input[type="checkbox"] {
  opacity: 0;
  position: absolute;
}

.checkbox .checkmark {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  width: 20px;
  height: 20px;
  border: 1px solid #ccc;
  border-radius: 50%;
  transition: background-color 0.2s ease;
}

.checkbox input[type="checkbox"]:checked + .checkmark {
  background-color: #0078d4;
  border-color: #0078d4;
}

.checkbox input[type="checkbox"]:checked + .checkmark:after {
  content: "";
  display: block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: #fff;
  transition: transform 0.2s ease;
  transform: scale(1);
}

.checkbox .checkmark:after {
  content: "";
  display: block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: transparent;
  transform: scale(0);
  transition: transform 0.2s ease;
}

</style>
