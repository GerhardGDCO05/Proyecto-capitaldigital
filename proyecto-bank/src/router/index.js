import { createRouter, createWebHistory } from 'vue-router';
import App from '@/App.vue';


const routes = [
  {
    path: '/registrar',
    name: 'Registro',
    component: () => import('@/components/Registrar.vue'),
    meta: { popup: true } // Marca esta ruta como popup cuando se abra en ventana nueva
  },
  {
    path: '/',
    name: 'App',
    component: App,
  },
  {
    path: '/bank',
    name: 'BankLayout',
    component: () => import('@/view/Layout.vue'),
    meta: { popup: true },
    children: [
      {
        path: 'vistageneral',
        name: 'VistaG',
        component: () => import('@/components/bankcomponents/VistaGeneral.vue'),
        query: {popup: 'true'},
      },
      {
        path: 'cuentas',
        name: 'Cuentas',
        component: () => import('@/components/bankcomponents/Cuentas.vue'),
        meta: { popup: true }
      },
      {
        path: 'perfil',
        name: 'Perfil',
        component: () => import('@/components/bankcomponents/perfil/Perfil.vue'),
        query: {popup: 'true'},
      },
    ]
  },
];

const router = createRouter({
  history: createWebHistory(), // Sin argumento
  routes,
});


export default router;