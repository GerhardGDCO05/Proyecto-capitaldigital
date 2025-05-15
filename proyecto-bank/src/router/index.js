import { createRouter, createWebHistory } from 'vue-router';
import App from '@/App.vue';

const routes = [
  {
    path: '/bank',
    name: 'Bank',
    component: () => import('@/components/bank.vue'),
    meta: { popup: true } // Marca esta ruta como popup cuando se abra en ventana nueva
  },
  {
    path: '/registrar',
    name: 'Registro',
    component: () => import('@/components/registrar.vue'),
    meta: { popup: true } // Marca esta ruta como popup cuando se abra en ventana nueva
  },
  {
    path: '/',
    name: 'App',
    component: App,
  },
];

const router = createRouter({
  history: createWebHistory(), // Sin argumento
  routes,
});


export default router;