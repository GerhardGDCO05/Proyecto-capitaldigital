<script>
import { ref } from 'vue'
import axios from 'axios'

export default {
  name: 'ModificarMeta',
  props: ['meta'],
  setup(props) {
    const metaEdit = ref(JSON.parse(JSON.stringify(props.meta)))

    const handleInput = (e) => {
      let value = e.target.value.replace(/[^0-9]/g, '')

      if (!value) {
        metaEdit.value.montoRequerido = 0
        metaEdit.value.displayMonto = '0,00 Bs'
        return
      }

      const digits = value.split('').reverse()
      let integerPart = ''
      let decimalPart = ''

      for (let i = 0; i < digits.length; i++) {
        if (i < 2) {
          decimalPart = digits[i] + decimalPart
        } else {
          integerPart = digits[i] + integerPart
        }
      }

      while (decimalPart.length < 2) decimalPart += '0'

      const formattedInteger = parseInt(integerPart || '0', 10).toLocaleString('es-VE')
      const parsedValue = parseFloat(`${integerPart || 0}.${decimalPart || '00'}`)

      metaEdit.value.montoRequerido = parsedValue
      metaEdit.value.displayMonto = `${formattedInteger},${decimalPart} Bs`
    }

    const submitModificacion = async () => {
      try {
        await axios.put(
            `http://localhost:8080/meta/numeroDocumento/${props.meta.numeroDocumento}/meta/${props.meta.nombre}`,
            {
              nombre: metaEdit.value.nombre,
              fechaInicio: metaEdit.value.fechaInicio,
              fechaFin: metaEdit.value.fechaFin,
              montoRequerido: metaEdit.value.montoRequerido
            }
        )

        alert("✅ Meta modificada correctamente")
      } catch (error) {
        console.error("Error al modificar meta:", error)
        alert(`⚠️ ${
            typeof error.response?.data === 'object'
                ? JSON.stringify(error.response.data, null, 2)
                : error.response?.data || error.message
        }`)
      }
    }

    return {
      metaEdit,
      handleInput,
      submitModificacion
    }
  }
}
</script>

<template>
  <div class="modal-content">
    <h2>Modificar Meta Financiera</h2>
    <form @submit.prevent="submitModificacion">
      <!-- Campo Nombre -->
      <label>
        Nombre de la meta:
        <input v-model="metaEdit.nombre" type="text" required />
      </label>

      <!-- Fecha Inicio -->
      <label>
        Fecha de inicio:
        <input v-model="metaEdit.fechaInicio" type="date" required />
      </label>

      <!-- Fecha Fin -->
      <label>
        Fecha de fin:
        <input v-model="metaEdit.fechaFin" type="date" required />
      </label>

      <!-- Monto requerido -->
      <label>
        Monto requerido:
        <input
            :value="metaEdit.displayMonto || metaEdit.montoRequerido.toLocaleString('es-VE') + ',00 Bs'"
            @input="handleInput"
            type="text"
        />
      </label>

      <!-- Botón Guardar -->
      <button type="submit">Guardar Cambios</button>
    </form>
  </div>
</template>

<style scoped>
.modal-content {
  padding: 20px;
  background-color: white;
  border-radius: 10px;
  max-width: 400px;
  margin: auto;
  text-align: left;
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
  background-color: #3d405b;
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
</style>