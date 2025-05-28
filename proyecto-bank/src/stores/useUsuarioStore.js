import { defineStore } from 'pinia';

export const useUsuarioStore = defineStore('usuario', {
    state: () => ({
        usuario: null,
        cuentas: [],
    }),
    actions: {
        setUsuario(usuario) {
            this.usuario = usuario;
        },
        setCuentas(cuentas) {
            this.cuentas = cuentas;
        },
        clearUsuario() {
            this.usuario = null;
            this.cuentas = [];
        }
    },
    // Persistencia con localStorage
    persist: {
        enabled: true,
        strategies: [
            {
                key: 'usuario-store',
                storage: localStorage,
            },
        ],
    },
});