<script>
import axios from 'axios'
import { ref } from 'vue'
import { useUsuarioStore } from '@/stores/useUsuarioStore'
import Modal from "@/view/Modal.vue"
import ModificarMeta from "@/components/bankcomponents/metasfinancieras/ModificarMeta.vue";

export default {
  name: 'MostrarMetas',
  components: {ModificarMeta, Modal },
  setup() {
    const usuarioStore = useUsuarioStore()
    const documento = usuarioStore.usuario.numeroDocumento
    const showModal = ref(false)
    const metas = ref([])
    const metaSeleccionada = ref(null)

    const cargarMetas = async () => {
      if (!documento) {
        alert("⚠️ No hay usuario logueado")
        return
      }

      try {
        const response = await axios.get(`http://localhost:8080/meta/numeroDocumento/${documento}`)
        metas.value = response.data.map(meta => ({
          ...meta,
          numeroDocumento: documento,
          displayMonto: meta.montoRequerido.toLocaleString('es-VE') + ',00 Bs'
        }))
      } catch (error) {
        console.error("Error al cargar metas:", error)
        alert("❌ Error al cargar tus metas financieras")
      }
    }

    const abrirModal = (meta) => {
      metaSeleccionada.value = meta
      showModal.value = true
    }

    cargarMetas()

    return {
      metas,
      documento,
      showModal,
      metaSeleccionada,
      abrirModal
    }
  }
}
</script>

<template>
  <header class="header">
    <h1>Metas financieras</h1>
  </header>

  <main>
    <div class="goals-container">
      <!-- Mostrar tarjetas dinámicas -->
      <div v-for="meta in metas" :key="meta.nombre" class="card" @click="abrirModal(meta)">
        <a href="#" class="content">
          <p class="heading">{{ meta.nombre }}</p>
          <p class="para">Fecha: {{ meta.fechaInicio }} - {{ meta.fechaFin }}</p>
          <p class="para">Monto: {{ meta.displayMonto || `${meta.montoRequerido},00 Bs` }}</p>
        </a>
      </div>

      <!-- Tarjeta vacía si no hay metas -->
      <div v-if="metas.length === 0" class="card empty-card">
        <a href="#" class="content">
          <p class="heading">No tienes metas aún</p>
          <p class="para">Crea una nueva meta financiera para comenzar a ahorrar</p>
        </a>
      </div>
    </div>
  </main>

  <!-- Modal -->
  <Modal :is-open="showModal" @close="showModal = false">
    <ModificarMeta :meta="metaSeleccionada" />
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

.goals-container {
  display: grid;
  grid-template-columns: 50px 50px 50px;
  gap: 500px;
  row-gap: 30px;
  position: absolute;
  top: 20%;
  left: 20%;
}


/* From Uiverse.io by gharsh11032000 */
.card {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 320px;
  padding: 2px;
  border-radius: 24px;
  overflow: hidden;
  line-height: 1.6;
  transition: all 0.48s cubic-bezier(0.23, 1, 0.32, 1);
}

.content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 24px;
  padding: 34px;
  border-radius: 22px;
  color: #ffffff;
  overflow: hidden;
  background: #ffffff;
  transition: all 0.48s cubic-bezier(0.23, 1, 0.32, 1);
}

.content .heading {
  font-weight: 700;
  font-size: 36px;
  line-height: 1.3;
  z-index: 1;
  transition: all 0.48s cubic-bezier(0.23, 1, 0.32, 1);
}

.content .para {
  z-index: 1;
  opacity: 0.8;
  font-size: 18px;
  transition: all 0.48s cubic-bezier(0.23, 1, 0.32, 1);
}

.card::before {
  content: "";
  position: absolute;
  height: 160%;
  width: 160%;
  border-radius: inherit;
  background: #0a3cff;
  background: linear-gradient(to right, #0a3cff, #0a3cff);
  transform-origin: center;
  animation: moving 4.8s linear infinite paused;
  transition: all 0.88s cubic-bezier(0.23, 1, 0.32, 1);
}

.card:hover::before {
  animation-play-state: running;
  z-index: -1;
  width: 20%;
}

.card:hover .content .heading,
.card:hover .content .para {
  color: #000000;
}

.card:hover {
  box-shadow: 0rem 6px 13px rgba(10, 60, 255, 0.1),
  0rem 24px 24px rgba(10, 60, 255, 0.09),
  0rem 55px 33px rgba(10, 60, 255, 0.05),
  0rem 97px 39px rgba(10, 60, 255, 0.01), 0rem 152px 43px rgba(10, 60, 255, 0);
  scale: 1.05;
  color: #000000;
}

@keyframes moving {
  0% {
    transform: rotate(0);
  }

  100% {
    transform: rotate(360deg);
  }
}


/*------------*/
</style>