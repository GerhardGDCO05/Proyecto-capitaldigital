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
import { useRoute, useRouter } from 'vue-router';
import { ref, onMounted, onUnmounted } from 'vue';
import { useUsuarioStore } from '@/stores/useUsuarioStore';

export default {
  components: { Sidebar },
  setup() {
    const route = useRoute();
    const router = useRouter(); // Añadir router
    const isSidebarActive = ref(false);
    const usuarioStore = useUsuarioStore();
    
    // Configuración del detector de actividad
    const TIEMPO_INACTIVIDAD = 180000; // 5 segundos para alert
    const TIEMPO_CIERRE_SESION = 300000; // 8 segundos para cierre
    
    // Estados reactivos
    const alertaMostrada = ref(false);
    const sesionCerrada = ref(false);
    
    // Variables para temporizadores
    let temporizadorAlert = null;
    let temporizadorCierre = null;

    // Función para limpiar todos los temporizadores
    const limpiarTemporizadores = () => {
      if (temporizadorAlert) {
        clearTimeout(temporizadorAlert);
        temporizadorAlert = null;
      }
      if (temporizadorCierre) {
        clearTimeout(temporizadorCierre);
        temporizadorCierre = null;
      }
    };

    // Función para cerrar sesión
    const cerrarSesion = () => {
      // Evitar múltiples ejecuciones
      if (sesionCerrada.value) {
        return;
      }
      
      sesionCerrada.value = true;
      limpiarTemporizadores();
      
      // Limpiar store del usuario
      try {
        if (usuarioStore.limpiarSesion) {
          usuarioStore.limpiarSesion();
        }
      } catch (error) {
        console.error('Error al limpiar sesión:', error);
      }
      
      // Redirección segura a App.vue
      try {
        router.push('/');
      } catch (error) {
        console.error('Error con router.push:', error);
        window.location.href = '/';
      }
      
      console.log('Sesión cerrada');
    };

    // Función para mostrar alerta de inactividad
    const mostrarAlertaInactividad = () => {
      if (!alertaMostrada.value && !sesionCerrada.value) {
        alertaMostrada.value = true;
        alert('Tu sesión se cerrará por inactividad en 3 segundos');
      }
    };

    // Función para resetear el temporizador de sesión
    const resetearTemporizador = () => {
      // No resetear si la sesión ya está cerrada
      if (sesionCerrada.value) {
        return;
      }
      
      // Resetear estados
      alertaMostrada.value = false;
      
      // Limpiar temporizadores existentes
      limpiarTemporizadores();
      
      // Configurar nuevo temporizador para alerta
      temporizadorAlert = setTimeout(() => {
        mostrarAlertaInactividad();
      }, TIEMPO_INACTIVIDAD);
      
      // Configurar nuevo temporizador para cierre de sesión
      temporizadorCierre = setTimeout(() => {
        cerrarSesion();
      }, TIEMPO_CIERRE_SESION);
    };

    // Función para manejar la actividad del usuario
    const manejarActividad = (event) => {
      // Evitar bucles infinitos si la sesión ya está cerrada
      if (sesionCerrada.value) {
        return;
      }
      
      // Throttle para evitar demasiadas llamadas
      if (event.type === 'mousemove') {
        // Limitar eventos de mousemove cada 100ms
        const now = Date.now();
        if (manejarActividad.lastMouseMove && now - manejarActividad.lastMouseMove < 100) {
          return;
        }
        manejarActividad.lastMouseMove = now;
      }
      
      resetearTemporizador();
    };

    // Función para manejar el toggle del sidebar
    const handleSidebarToggle = (event) => {
      isSidebarActive.value = event.detail;
    };

    onMounted(() => {
      // Configurar eventos del sidebar
      window.addEventListener("toggle-sidebar", handleSidebarToggle);
      
      // Configurar eventos de actividad del usuario
      const eventos = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click'];
      eventos.forEach(evento => {
        document.addEventListener(evento, manejarActividad, { passive: true });
      });
      
      // Iniciar el temporizador de sesión
      resetearTemporizador();
    });

    onUnmounted(() => {
      // Limpiar eventos del sidebar
      window.removeEventListener("toggle-sidebar", handleSidebarToggle);
      
      // Limpiar eventos de actividad del usuario
      const eventos = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click'];
      eventos.forEach(evento => {
        document.removeEventListener(evento, manejarActividad);
      });
      
      // Limpiar temporizadores
      limpiarTemporizadores();
    });

    return { 
      usuario: usuarioStore.usuario, 
      cuentas: usuarioStore.cuentas, 
      isSidebarActive 
    };
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