<!-- Layout.vue -->
<template>
  <div class="layout">
    <Sidebar />
    <div class="main-content" :class="{ 'sidebar-active': isSidebarActive }">

      <router-view /> <!-- Aquí se cargan los distintos componentes -->
    </div>
  </div>
</template>

<script>
import Sidebar from "@/components/bankcomponents/Sidebar.vue";
import { useRoute } from 'vue-router';
import { ref, onMounted, onUnmounted } from 'vue';

export default {
  components: { Sidebar },

  setup() {
    const route = useRoute();
    const usuario = ref({});
    const cuentas = ref([]);
    const isSidebarActive = ref(false);

    // Capturar datos de la URL al cargar Layout.vue
    onMounted(() => {
      usuario.value = JSON.parse(route.query.usuario || '{}');
      cuentas.value = JSON.parse(route.query.cuentas || '[]');

      console.log("Datos recibidos en Layout.vue:", usuario.value, cuentas.value);
      
      window.addEventListener("toggle-sidebar", (event) => {
        isSidebarActive.value = event.detail;
      });
    });

    // Eliminar el evento al desmontar el componente
    onUnmounted(() => {
      window.removeEventListener("toggle-sidebar", (event) => {
        isSidebarActive.value = false;
      });
    });

    return { usuario, cuentas, isSidebarActive };
  }
};
</script>

<style>
.main-content.sidebar-active .header {
  left: 10%;
  width: 90%;
  transition: .3s;
}
.main-content.sidebar-active .form-perfil {
  left: 55%;
  transition: .3s;
}
</style>