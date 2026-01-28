<script>
import { ref, reactive, computed } from 'vue';
import axios from 'axios';
import Swal from 'sweetalert2';

export default {
  name: 'ModificarMeta',
  props: ['meta'],
  emits: ['meta-updated', 'meta-deleted', 'close'],
  setup(props, { emit }) {
    const metaEdit = ref(JSON.parse(JSON.stringify(props.meta)));

    // Estado reactivo para controlar si los inputs están habilitados
    const inputStates = reactive({
      nombre: false,
      fechaInicio: false,
      fechaFin: false,
      montoRequerido: false,
    });

    // Fecha mínima (hoy)
    const minDate = computed(() => {
      const today = new Date();
      return today.toISOString().split('T')[0]; // Formato YYYY-MM-DD
    });

    // Función para alternar el estado de habilitación de un input
    const toggleInput = (field) => {
      inputStates[field] = !inputStates[field];
    };

    // Formatear entrada de monto como moneda
    const handleInput = (e) => {
      let value = e.target.value.replace(/[^0-9]/g, '');

      if (!value) {
        metaEdit.value.montoRequerido = 0;
        metaEdit.value.displayMonto = '0,00 Bs';
        return;
      }

      const digits = value.split('').reverse();
      let integerPart = '';
      let decimalPart = '';

      for (let i = 0; i < digits.length; i++) {
        if (i < 2) {
          decimalPart = digits[i] + decimalPart;
        } else {
          integerPart = digits[i] + integerPart;
        }
      }

      while (decimalPart.length < 2) decimalPart += '0';

      const formattedInteger = parseInt(integerPart || '0', 10).toLocaleString('es-VE');
      const parsedValue = parseFloat(`${integerPart || 0}.${decimalPart || '00'}`);

      metaEdit.value.montoRequerido = parsedValue;
      metaEdit.value.displayMonto = `${formattedInteger},${decimalPart} Bs`;
    };

    // Enviar modificaciones al backend
    const submitModificacion = async () => {
      // Validar que las fechas no sean anteriores a hoy
      const today = new Date();
      today.setHours(0, 0, 0, 0); // Normalizar a medianoche
      const fechaInicio = new Date(metaEdit.value.fechaInicio);
      const fechaFin = new Date(metaEdit.value.fechaFin);

      if (fechaInicio < today || fechaFin < today) {
        await Swal.fire({
          title: 'Fecha inválida',
          text: 'Las fechas de inicio y fin no pueden ser anteriores a hoy.',
          icon: 'error',
          confirmButtonText: 'OK',
          customClass: {
            popup: 'swal2-custom-zindex',
          },
        });
        return;
      }

      try {
        await axios.put(
            `http://localhost:8080/meta/numeroDocumento/${props.meta.numeroDocumento}/meta/${props.meta.nombre}`,
            {
              nombre: metaEdit.value.nombre,
              fechaInicio: metaEdit.value.fechaInicio,
              fechaFin: metaEdit.value.fechaFin,
              montoRequerido: metaEdit.value.montoRequerido,
            }
        );

        await Swal.fire({
          title: '¡Éxito!',
          text: 'Meta modificada correctamente.',
          icon: 'success',
          confirmButtonText: 'OK',
          customClass: {
            popup: 'swal2-custom-zindex',
          },
        });

        emit('meta-updated', metaEdit.value);
        emit('close'); // Cerrar el modal tras éxito
      } catch (error) {
        console.error('Error al modificar meta:', error);
        await Swal.fire({
          title: 'Error',
          text: error.response?.data || 'Hubo un error al modificar la meta.',
          icon: 'error',
          confirmButtonText: 'OK',
          customClass: {
            popup: 'swal2-custom-zindex',
          },
        });
        // El modal permanece abierto en caso de error
      }
    };

    // Eliminar meta
    const borrarMeta = async () => {
      const result = await Swal.fire({
        title: '¿Estás seguro?',
        text: `¿Deseas eliminar la meta ${metaEdit.value.nombre}? Esta acción no se puede deshacer.`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar',
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        customClass: {
          popup: 'swal2-custom-zindex',
        },
      });

      if (!result.isConfirmed) return; // El modal permanece abierto si el usuario cancela

      try {
        await axios.delete(
            `http://localhost:8080/meta/${props.meta.numeroDocumento}/${props.meta.nombre}`
        );

        await Swal.fire({
          title: '¡Eliminado!',
          text: 'Meta eliminada correctamente.',
          icon: 'success',
          confirmButtonText: 'OK',
          customClass: {
            popup: 'swal2-custom-zindex',
          },
        });

        emit('meta-deleted', props.meta.nombre);
        emit('close'); // Cerrar el modal tras éxito
      } catch (error) {
        console.error('Error al eliminar meta:', error);
        let errorMessage = 'Hubo un error al eliminar la meta.';
        if (error.response) {
          errorMessage = typeof error.response.data === 'string'
              ? error.response.data
              : error.response.data.message || JSON.stringify(error.response.data);
        }
        await Swal.fire({
          title: 'Error',
          text: errorMessage,
          icon: 'error',
          confirmButtonText: 'OK',
          customClass: {
            popup: 'swal2-custom-zindex',
          },
        });
        // El modal permanece abierto en caso de error
      }
    };

    return {
      metaEdit,
      inputStates,
      minDate,
      toggleInput,
      handleInput,
      submitModificacion,
      borrarMeta,
    };
  },
};
</script>

<template>
  <div class="modal-content">
    <h2>Modificar Meta Financiera</h2>
    <form @submit.prevent="submitModificacion">
      <!-- Campo Nombre -->
      <div class="input-edit">
        <label>
          Nombre de la meta:
          <input v-model="metaEdit.nombre" type="text" required :disabled="!inputStates.nombre" />
        </label>
        <button type="button" class="edit-input" @click="toggleInput('nombre')">
          <svg class="css-i6dzq1" stroke-linejoin="round" stroke-linecap="round" fill="none" stroke-width="2" stroke="#FFFFFF" height="24" width="24" viewBox="0 0 24 24">
            <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"></path>
          </svg>
        </button>
      </div>

      <!-- Fecha Inicio -->
      <div class="input-edit">
        <label>
          Fecha de inicio:
          <input v-model="metaEdit.fechaInicio" type="date" required :disabled="!inputStates.fechaInicio" :min="minDate" />
        </label>
        <button type="button" class="edit-input" @click="toggleInput('fechaInicio')">
          <svg class="css-i6dzq1" stroke-linejoin="round" stroke-linecap="round" fill="none" stroke-width="2" stroke="#FFFFFF" height="24" width="24" viewBox="0 0 24 24">
            <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"></path>
          </svg>
        </button>
      </div>

      <!-- Fecha Fin -->
      <div class="input-edit">
        <label>
          Fecha de fin:
          <input v-model="metaEdit.fechaFin" type="date" required :disabled="!inputStates.fechaFin" :min="minDate" />
        </label>
        <button type="button" class="edit-input" @click="toggleInput('fechaFin')">
          <svg class="css-i6dzq1" stroke-linejoin="round" stroke-linecap="round" fill="none" stroke-width="2" stroke="#FFFFFF" height="24" width="24" viewBox="0 0 24 24">
            <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"></path>
          </svg>
        </button>
      </div>

      <!-- Monto requerido -->
      <div class="input-edit">
        <label>
          Monto requerido:
          <input
              :value="metaEdit.displayMonto || metaEdit.montoRequerido.toLocaleString('es-VE') + ',00 Bs'"
              @input="handleInput"
              type="text"
              :disabled="!inputStates.montoRequerido"
          />
        </label>
        <button type="button" class="edit-input" @click="toggleInput('montoRequerido')">
          <svg class="css-i6dzq1" stroke-linejoin="round" stroke-linecap="round" fill="none" stroke-width="2" stroke="#FFFFFF" height="24" width="24" viewBox="0 0 24 24">
            <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"></path>
          </svg>
        </button>
      </div>

      <!-- Botones -->
      <div class="opt-btns">
        <button type="submit">Guardar Cambios</button>
        <button type="button" class="delete-btn" @click="borrarMeta">
          <svg viewBox="0 0 448 512" class="svgIcon">
            <path d="M135.2 17.7L128 32H32C14.3 32 0 46.3 0 64S14.3 96 32 96H416c17.7 0 32-14.3 32-32s-14.3-32-32-32H320l-7.2-14.3C307.4 6.8 296.3 0 284.2 0H163.8c-12.1 0-23.2 6.8-28.6 17.7zM416 128H32L53.2 467c1.6 25.3 22.6 45 47.9 45H346.9c25.3 0 46.3-19.7 47.9-45L416 128z"></path>
          </svg>
        </button>
      </div>
    </form>
  </div>
</template>

<style scoped>
.input-edit {
  display: flex;
  align-items: center;
  column-gap: 30px;
  margin-bottom: 1rem;
}

.input-edit label {
  flex-grow: 1;
  margin: 0;
}

.modal-content {
  padding: 20px;
  background-color: white;
  border-radius: 10px;
  max-width: 400px;
  margin: auto;
  text-align: left;
  z-index: 1000;
}

.modal-content label {
  display: block;
  margin-bottom: 1rem;
}

.modal-content input {
  width: 100%;
  padding: 8px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin-top: 4px;
}

.modal-content button {
  background-color: #424faa;
  color: white;
  border: none;
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  border-radius: 4px;
}

.modal-content button:hover {
  background-color: #2f3147;
}

.edit-input {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background-image: linear-gradient(to top, #30cfd0 0%, #330867 100%);
  border: solid 3px transparent;
  background-clip: padding-box;
  box-shadow: 0px 0px 0px 3px #ffffff00;
  color: white;
  max-height: 43px;
  padding: 0 13px 0 13px;
  border-radius: 100px;
  text-transform: uppercase;
  letter-spacing: 2px;
  transition: all .5s ease;
}

.edit-input:active {
  transform: scale(.9);
  transition: all 100ms ease;
}

.edit-input:hover {
  box-shadow: 0px 0px 0px 3px #30a1b8;
}

.edit-input svg {
  width: 20px;
}

.opt-btns {
  display: flex;
  justify-content: center;
  flex-direction: row;
  column-gap: 10px;
}

.delete-btn {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: rgb(20, 20, 20);
  border: none;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0px 0px 20px rgba(0, 0, 0, 0.164);
  cursor: pointer;
  transition-duration: .3s;
  overflow: hidden;
  position: relative;
}

.svgIcon {
  width: 12px;
  transition-duration: .3s;
}

.svgIcon path {
  fill: white;
}

.delete-btn:hover {
  width: 140px;
  border-radius: 50px;
  transition-duration: .3s;
  background-color: rgb(255, 69, 69);
  align-items: center;
}

.delete-btn:hover .svgIcon {
  width: 50px;
  transition-duration: .3s;
  transform: translateY(60%);
}

.delete-btn::before {
  position: absolute;
  top: -20px;
  content: "Borrar";
  color: white;
  transition-duration: .3s;
  font-size: 2px;
}

.delete-btn:hover::before {
  font-size: 13px;
  opacity: 1;
  transform: translateY(30px);
  transition-duration: .3s;
}

/* Ensure SweetAlert2 modals appear on top */
.swal2-custom-zindex {
  z-index: 2000 !important;
}
</style>