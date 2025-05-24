<script >
    import { ref, onMounted } from 'vue';
    import { useRoute } from 'vue-router';

    export default {
    setup() {
        const route = useRoute(); 
        
        // Variables para almacenar los datos recibidos
        const usuario = ref({});
        const cuentas = ref([]);

        // Capturar los datos al montar el componente
        onMounted(() => {
            usuario.value = JSON.parse(route.query.usuario || '{}');
            cuentas.value = JSON.parse(route.query.cuentas || '[]');

            //console.log("Emitiendo evento desde VistaG:", usuario.value, cuentas.value); // Verifica que se emite correctamente

            window.dispatchEvent(new CustomEvent("datosUsuario", {
                detail: { usuario: usuario.value, cuentas: cuentas.value }
            }));
            });


        return { usuario, cuentas };
    }
    };
</script>

<template>

</template>

<style scoped>

</style>