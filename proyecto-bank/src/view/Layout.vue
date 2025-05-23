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

export default {
  components: { Sidebar },
  data() {
    return {
      isSidebarActive: false,
    };
  },
  mounted() {
    window.addEventListener("toggle-sidebar", this.handleToggle);
  },
  beforeUnmount() {
    window.removeEventListener("toggle-sidebar", this.handleToggle);
  },
  methods: {
    handleToggle(event) {
      this.isSidebarActive = event.detail;
    },
  },
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