<script setup lang="ts">
import { ref } from 'vue'
import { onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import AgregarBeneficiario from "@/components/bankcomponents/transacciones/crudbeneficiarios/AgregarBeneficiario.vue";
const route = useRoute()
import Modal from "@/view/Modal.vue";
const showModal = ref(false)
// Ejecutar una vez al montar
onMounted(() => {
  window.dispatchEvent(new Event('resize'))
})

// Ejecutar cada vez que cambie la ruta
watch(
    () => route.path,
    () => {
      setTimeout(() => {
        window.dispatchEvent(new Event('resize'))
      }, 50) // Pequeño delay para asegurar que el DOM esté listo
    }
)
</script>

<template>
  <header class="header" :key="$route.fullPath"> <!--BUG FIX: :key="$route.fullPath fuerza a Vue a que el componente se cargue correctamente-->
    <h1>Transferencias</h1>
  </header>

  <button class="agregar-benf" @click="showModal = true">Agregar</button>
  <button class="modificar-benf">Modificar</button>
  <button class="eliminar-benf">Eliminar</button>
  <div class="card">
    <div class="card__title">Beneficiarios</div>
    <table>
      <thead>
      <tr>
        <th>N°</th>
        <th>Nombre(s)</th>
        <th>Cuenta</th>
        <th>Banco</th>
      </tr>
      </thead>
      <tbody>
      <tr>
        <td>1</td>
        <td>José Pérez</td>
        <td>0108-1564152689</td>
        <td>BBVA Provincial</td>
      </tr>
      <tr>
        <td>2</td>
        <td>John Doe</td>
        <td>0105-1523324667</td>
        <td>Banco de Venezuela</td>
      </tr>
      <tr>
        <td>3</td>
        <td>Jane Smith</td>
        <td>0107-214214551135</td>
        <td>Banco Mercantil</td>
      </tr>
      </tbody>
    </table>
  </div>

  <!--MODAL PARA LA VENTANA EMERGENTE-->
  <Modal :is-open="showModal" @close="showModal = false">
    <AgregarBeneficiario />
  </Modal>
</template>

<style>
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
  top: 40%;
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
</style>
