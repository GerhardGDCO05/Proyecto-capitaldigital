// main.js

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedState from 'pinia-plugin-persistedstate'
import { setupCalendar, Calendar, DatePicker } from 'v-calendar';
import 'v-calendar/style.css';
// Importa tu componente principal App.vue
import App from './App.vue'

// Importa el router si lo usas
import router from './router'

// Crea la instancia de Pinia
const pinia = createPinia()
pinia.use(piniaPluginPersistedState) // Activa persistencia

// Crea la aplicación Vue
const app = createApp(App)

// Usa Pinia y el Router
app.use(pinia)
app.use(router)

// Monta la aplicación
app.mount('#app')

app.component('VCalendar', Calendar)
app.component('VDatePicker', DatePicker)