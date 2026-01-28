<script setup lang="ts">
import { ref } from 'vue';
import Swal from 'sweetalert2';

// Tipos para la personalización de la interfaz
interface ThemePrefs {
  theme: string;
  dashboardLayout: string;
}

// Tipos para las alertas de cuenta
interface AccountAlerts {
  spendingLimit: number;
  largeTransactionThreshold: number;
}

// Tipos para las preferencias de notificaciones
interface NotificationPrefs {
  email: boolean;
  sms: boolean;
}

// Tipos para la selección de idioma
interface LanguageOption {
  value: string;
  label: string;
}

// Estado reactivo para la personalización de la interfaz
const themePrefs = ref<ThemePrefs>({
  theme: 'light',
  dashboardLayout: 'default'
});

// Estado reactivo para las alertas de cuenta
const accountAlerts = ref<AccountAlerts>({
  spendingLimit: 1000,
  largeTransactionThreshold: 500
});

// Estado reactivo para las preferencias de notificaciones
const notificationPrefs = ref<NotificationPrefs>({
  email: true,
  sms: false
});

// Estado reactivo para el idioma
const selectedLanguage = ref<string>('es');

// Opciones de idioma
const languages = ref<LanguageOption[]>([
  { value: 'es', label: 'Español' },
  { value: 'en', label: 'English' },
  { value: 'fr', label: 'Français' }
]);

// Estado para la autenticación en dos factores
const twoFactorEnabled = ref<boolean>(false);

// Función para simular guardar personalización de la interfaz
const saveThemePrefs = async () => {
  await Swal.fire({
    title: 'No implementado',
    text: 'La funcionalidad de personalización de la interfaz no ha sido implementada.',
    icon: 'info',
    confirmButtonText: 'OK'
  });
};

// Función para simular guardar alertas de cuenta
const saveAccountAlerts = async () => {
  await Swal.fire({
    title: 'No implementado',
    text: 'La funcionalidad de configuración de alertas de cuenta no ha sido implementada.',
    icon: 'info',
    confirmButtonText: 'OK'
  });
};

// Función para simular guardar preferencias de notificaciones
const saveNotificationPrefs = async () => {
  await Swal.fire({
    title: 'No implementado',
    text: 'La funcionalidad de preferencias de notificaciones no ha sido implementada.',
    icon: 'info',
    confirmButtonText: 'OK'
  });
};

// Función para simular cambio de idioma
const changeLanguage = async () => {
  await Swal.fire({
    title: 'No implementado',
    text: 'La funcionalidad de cambio de idioma no ha sido implementada.',
    icon: 'info',
    confirmButtonText: 'OK'
  });
};

// Función para simular activar/desactivar 2FA
const toggleTwoFactor = async () => {
  await Swal.fire({
    title: 'No implementado',
    text: 'La funcionalidad de autenticación en dos factores no ha sido implementada.',
    icon: 'info',
    confirmButtonText: 'OK'
  });
};
</script>

<template>
  <header class="header">
    <h1>Configuración</h1>
  </header>

  <div class="config-container">
    <h1>Configuración</h1>

    <!-- Sección de Personalización de la Interfaz -->
    <section class="section">
      <h2>Personalización de la Interfaz</h2>
      <div class="form-group">
        <label for="theme">Tema</label>
        <select v-model="themePrefs.theme" id="theme" @change="saveThemePrefs">
          <option value="light">Claro</option>
          <option value="dark">Oscuro</option>
        </select>
      </div>
      <div class="form-group">
        <label for="dashboard-layout">Diseño del Dashboard</label>
        <select v-model="themePrefs.dashboardLayout" id="dashboard-layout" @change="saveThemePrefs">
          <option value="default">Predeterminado</option>
          <option value="compact">Compacto</option>
          <option value="detailed">Detallado</option>
        </select>
      </div>
      <button @click="saveThemePrefs">Guardar Personalización</button>
    </section>

    <!-- Sección de Alertas de Cuenta -->
    <section class="section">
      <h2>Alertas de Cuenta</h2>
      <div class="form-group">
        <label for="spending-limit">Límite de Gasto Mensual (€)</label>
        <input
            v-model.number="accountAlerts.spendingLimit"
            id="spending-limit"
            type="number"
            min="0"
            placeholder="Ej. 1000"
        />
      </div>
      <div class="form-group">
        <label for="large-transaction">Umbral para Transacciones Grandes (€)</label>
        <input
            v-model.number="accountAlerts.largeTransactionThreshold"
            id="large-transaction"
            type="number"
            min="0"
            placeholder="Ej. 500"
        />
      </div>
      <button @click="saveAccountAlerts">Guardar Alertas</button>
    </section>

    <!-- Sección de Preferencias de Notificaciones -->
    <section class="section">
      <h2>Preferencias de Notificaciones</h2>
      <div class="form-group">
        <label>
          <input type="checkbox" v-model="notificationPrefs.email" />
          Recibir notificaciones por email
        </label>
      </div>
      <div class="form-group">
        <label>
          <input type="checkbox" v-model="notificationPrefs.sms" />
          Recibir notificaciones por SMS
        </label>
      </div>
      <button @click="saveNotificationPrefs">Guardar Preferencias</button>
    </section>

    <!-- Sección de Idioma -->
    <section class="section">
      <h2>Idioma</h2>
      <div class="form-group">
        <label for="language">Seleccionar Idioma</label>
        <select v-model="selectedLanguage" id="language" @change="changeLanguage">
          <option v-for="lang in languages" :key="lang.value" :value="lang.value">
            {{ lang.label }}
          </option>
        </select>
      </div>
    </section>

    <!-- Sección de Seguridad -->
    <section class="section">
      <h2>Seguridad</h2>
      <div class="form-group">
        <label>
          <input type="checkbox" v-model="twoFactorEnabled" @change="toggleTwoFactor" />
          Habilitar autenticación en dos factores (2FA)
        </label>
      </div>
    </section>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css?family=Inter:100,200,300,regular,500,600,700,800,900');

.header {
  position: absolute;
  top: 0%;
  left: 15%;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  font-size: 16px;
  width: 80%;
  height: 60px;
  background-color: rgb(255, 255, 255);
  border-bottom: 2px solid black;
  display: flex;
  align-items: center;
  padding-left: 20px;
  margin: 20px auto 0; /* Centrado horizontal y margen superior */
}

.config-container {
  position: absolute;
  top: 10%;
  left: 25%;
  min-width: 1280px;
  margin: 0 auto;
  padding: 40px;
  background-color: #f6f6f6;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  font-family: 'Inter', sans-serif;
  transition: margin-left 0.3s ease;
}

h1 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

h2 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
}

.section {
  margin-bottom: 40px;
}

.form-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 15px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 5px;
}

.form-group input,
.form-group select {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 14px;
  font-family: 'Inter', sans-serif;
}

.form-group input[type="checkbox"] {
  width: auto;
  margin-right: 10px;
}

.form-group input[type="number"] {
  width: 200px;
}

.form-group select {
  appearance: none;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24"><path fill="%23757575" d="M7 10l5 5 5-5z"/></svg>');
  background-repeat: no-repeat;
  background-position: right 10px center;
  padding-right: 30px;
}

button {
  padding: 12px;
  background-color: #007BFF;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #0056b3;
}

/* Ajuste para sidebar activo */
@media (max-width: 768px) {
  .config-container {
    padding: 20px;
  }
}

:deep(.main-content.sidebar-active) .config-container {
  margin-left: 120px;
}
</style>