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
import Swal from 'sweetalert2';

export default {
  components: { Sidebar },
  setup() {
    const route = useRoute();
    const router = useRouter();
    const isSidebarActive = ref(false);
    const usuarioStore = useUsuarioStore();

    // Configuración del detector de inactividad
    const TIEMPO_INACTIVIDAD = 180000; // 3 minutos para alerta
    const TIEMPO_CIERRE_SESION = 300000; // 5 minutos para cierre

    // Estados reactivos
    const alertaMostrada = ref(false);
    const sesionCerrada = ref(false);

    // Variables para temporizadores
    let temporizadorAlert = null;
    let temporizadorCierre = null;

    // Función para limpiar todos los temporizadores
    const limpiarTemporizadores = () => {
      console.log('Limpiando temporizadores');
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
    const cerrarSesion = async () => {
      if (sesionCerrada.value) {
        console.log('Sesión ya cerrada, evitando ejecución');
        return;
      }

      sesionCerrada.value = true;
      console.log('Cerrando sesión por inactividad');
      limpiarTemporizadores();

      try {
        usuarioStore.$reset(); // Resetea el store a su estado inicial
        console.log('Estado del usuario después de cerrar sesión:', usuarioStore.usuario);
        await Swal.fire({
          title: '¡Sesión cerrada!',
          text: 'Tu sesión se cerró por inactividad.',
          icon: 'info',
          confirmButtonText: 'OK'
        });
        router.push('/').catch(err => {
          console.error('Error en router.push:', err);
          window.location.href = '/';
        });
      } catch (error) {
        console.error('Error al cerrar sesión:', error);
        await Swal.fire({
          title: 'Error',
          text: 'Hubo un error al cerrar la sesión. Por favor, intenta de nuevo.',
          icon: 'error',
          confirmButtonText: 'OK'
        });
      }
    };

    // Función para mostrar alerta de inactividad
    const mostrarAlertaInactividad = async () => {
      if (!alertaMostrada.value && !sesionCerrada.value) {
        alertaMostrada.value = true;
        console.log('Mostrando alerta de inactividad');
        await Swal.fire({
          title: 'Inactividad detectada',
          text: 'Tu sesión se cerrará en 2 minutos si no hay actividad.',
          icon: 'warning',
          confirmButtonText: 'OK'
        });
      }
    };

    // Función para resetear el temporizador de sesión
    const resetearTemporizador = () => {
      if (sesionCerrada.value) {
        console.log('Sesión cerrada, no se resetea el temporizador');
        return;
      }

      console.log('Reseteando temporizador de inactividad');
      alertaMostrada.value = false;
      limpiarTemporizadores();

      temporizadorAlert = setTimeout(() => {
        mostrarAlertaInactividad();
      }, TIEMPO_INACTIVIDAD);

      temporizadorCierre = setTimeout(() => {
        cerrarSesion();
      }, TIEMPO_CIERRE_SESION);
    };

    // Función para manejar la actividad del usuario
    const manejarActividad = (event) => {
      if (sesionCerrada.value) {
        console.log('Sesión cerrada, ignorando evento:', event.type);
        return;
      }

      // Throttle para mousemove
      if (event.type === 'mousemove') {
        const now = Date.now();
        if (manejarActividad.lastMouseMove && now - manejarActividad.lastMouseMove < 500) {
          return;
        }
        manejarActividad.lastMouseMove = now;
      }

      console.log('Actividad detectada:', event.type);
      resetearTemporizador();
    };
    manejarActividad.lastMouseMove = 0; // Inicializar throttle

    // Función para manejar el toggle del sidebar
    const handleSidebarToggle = (event) => {
      isSidebarActive.value = event.detail;
      console.log('Sidebar toggled:', isSidebarActive.value);
    };

    onMounted(() => {
      console.log('Layout.vue montado, iniciando detección de inactividad');
      window.addEventListener('toggle-sidebar', handleSidebarToggle);

      const eventos = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click'];
      eventos.forEach(evento => {
        document.addEventListener(evento, manejarActividad, { passive: true });
      });

      resetearTemporizador();
    });

    onUnmounted(() => {
      console.log('Layout.vue desmontado, limpiando eventos y temporizadores');
      window.removeEventListener('toggle-sidebar', handleSidebarToggle);

      const eventos = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click'];
      eventos.forEach(evento => {
        document.removeEventListener(evento, manejarActividad);
      });

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