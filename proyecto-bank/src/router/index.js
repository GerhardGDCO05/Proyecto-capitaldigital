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
        path: 'perfil',
        name: 'Perfil',
        component: () => import('@/components/bankcomponents/perfil/Perfil.vue'),
        query: {popup: 'true'},
        props: route => ({usuario: route.query.usuario ? JSON.parse(route.query.usuario) : {}})
      },
      {
        path: 'agregarcuenta',
        name: 'AgregarCuenta',
        component: () => import('@/components/bankcomponents/cuentas/AgregarCuenta.vue'),
        query: {popup: 'true'},
      },
      {
        path: 'beneficiarios',
        name: 'Beneficiarios',
        component: () => import('@/components/bankcomponents/transacciones/Beneficiarios.vue'),
        query: {popup: 'true'},
      },
      {
        path: 'mostrarcuentas',
        name: 'MostrarCuentas',
        component: () => import('@/components/bankcomponents/cuentas/MostrarCuenta.vue'),
        query: {popup: 'true'},
      },
      {
        path: 'crearmeta',
        name: 'CrearMeta',
        component: () => import('@/components/bankcomponents/metasfinancieras/CrearMetas.vue'),
        query: {popup: 'true'},
      },
      {
        path: 'metas',
        name: 'MostrarMetas',
        component: () => import('@/components/bankcomponents/metasfinancieras/MostrarMetas.vue'),
        query: {popup: 'true'},
      },
      {
        path: 'modificar-beneficiario/:accountNumber',
        name: 'ModificarBeneficiario',
        component: () => import('@/components/bankcomponents/transacciones/crudbeneficiarios/ModificarBeneficiario.vue'),
        props: true
      },
      {
        path: 'editar-cuenta/:numeroCuenta',
        name: 'EditarCuenta',
        component: () => import('@/components/bankcomponents/cuentas/EditarCuenta.vue'),
        query: {popup: 'true'}
      },
      {
        path: 'transferencias',
        name: 'Transferencias',
        component: () => import('@/components/bankcomponents/transacciones/Transferencias.vue'),
        query: {popup: 'true'}
      },
      {
        path: 'historialtransferencias',
        name: 'HistorialTransferencias',
        component: () => import('@/components/bankcomponents/transacciones/transferencias/HistorialTransferencias.vue'),
        query: {popup: 'true'}
      },
      {
        path: '/consultar-cuenta/:numeroCuenta',
        name: 'ConsultarCuentas',
        component: () => import('@/components/bankcomponents/cuentas/ConsultarCuenta.vue'),
        props: true, // Permite pasar params como props al componente
        query: {popup: 'true'}
      },
      {
        path: 'tarjetas',
        name: 'Tarjetas',
        component: () => import('@/components/bankcomponents/cuentas/Tarjetas.vue'),
        query: {popup: 'true'}
      },
      {
        path: 'patrimonioneto',
        name: 'PatrimonioNeto',
        component: () => import('@/components/bankcomponents/transacciones/PatrimonioNeto.vue'),
        query: {popup: 'true'}
      },
      {
        path: 'ayuda',
        name: 'Ayuda',
        component: () => import('@/components/bankcomponents/Ayuda.vue'),
        query: {popup: 'true'}
      },
      {
        path: 'configuracion',
        name: 'Configuracion',
        component: () => import('@/components/bankcomponents/Configuracion.vue'),
        query: {popup: 'true'}
      },
    ]
  },
];

const router = createRouter({
  history: createWebHistory(), // Sin argumento
  routes,
});


export default router;