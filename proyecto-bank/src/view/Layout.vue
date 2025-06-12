<!-- Layout.vue -->
<template>
  <div class="layout">
    <Sidebar :usuario="usuario" :cuentas="cuentas" />
    <div class="main-content" :class="{ 'sidebar-active': isSidebarActive }">

      <router-view /> <!-- Aquí se cargan los distintos componentes -->
    </div>
  </div>
</template>

<script>
import Sidebar from "@/components/bankcomponents/Sidebar.vue";
import { useRoute } from 'vue-router';
import { ref, onMounted, onUnmounted } from 'vue';
import { useUsuarioStore } from '@/stores/useUsuarioStore';

export default {
  components: { Sidebar },
  setup() {
    const route = useRoute();
    const isSidebarActive = ref(false);
    const usuarioStore = useUsuarioStore();

    onMounted(() => {
      window.addEventListener("toggle-sidebar", (event) => {
        isSidebarActive.value = event.detail;
      });
    });

    onUnmounted(() => {
      window.removeEventListener("toggle-sidebar", (event) => {
        isSidebarActive.value = false;
      });
    });

    return { usuario: usuarioStore.usuario, cuentas: usuarioStore.cuentas, isSidebarActive };
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