<script>
import { Calendar } from 'v-calendar'
import axios from 'axios'
import { ref } from 'vue'
import { useUsuarioStore } from '@/stores/useUsuarioStore'

export default {
  name: 'CrearMetas',
  components: {
    Calendar
  },
  setup() {
    const usuarioStore = useUsuarioStore()
    const documento = usuarioStore.usuario.numeroDocumento

    // Datos del formulario
    const nombreMeta = ref('')
    const selectedRange = ref(null)
    const displayValue = ref('0,00 Bs')
    const internalValue = ref(0)

    // Atributos del calendario
    const attributes = ref([
      {
        key: 'selectedRange',
        highlight: true,
        dates: null
      }
    ])

    // Manejar selección de fecha
    const handleDayClick = (day) => {
      if (!selectedRange.value || selectedRange.value.end) {
        selectedRange.value = {
          start: day.date,
          end: null
        }
      } else {
        const start = selectedRange.value.start
        const end = day.date

        selectedRange.value = {
          start: start < end ? start : end,
          end: start < end ? end : start
        }

        attributes.value = [
          {
            key: 'selectedRange',
            highlight: true,
            dates: selectedRange.value
          }
        ]
      }
    }

    const formatDate = (date) => {
      return new Intl.DateTimeFormat('es-ES').format(date)
    }

    // Formatear entrada de monto como moneda
    const handleInput = (e) => {
      let value = e.target.value.replace(/[^0-9]/g, '')
      if (!value) {
        internalValue.value = 0
        displayValue.value = '0,00 Bs'
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

      displayValue.value = `${formattedInteger},${decimalPart} Bs`
      internalValue.value = parseFloat(`${integerPart}.${decimalPart}`)
    }

    // Enviar al backend
    const submitMeta = async () => {
      if (
          !nombreMeta.value ||
          !selectedRange.value?.start ||
          !selectedRange.value?.end ||
          !internalValue.value
      ) {
        alert("Por favor complete todos los campos.")
        return
      }

      const nuevaMeta = {
        nombre: nombreMeta.value,
        fechaInicio: selectedRange.value.start.toISOString().split('T')[0],
        fechaFin: selectedRange.value.end.toISOString().split('T')[0],
        montoRequerido: internalValue.value
      }

      try {
        const response = await axios.post(
            `http://localhost:8080/meta/numeroDocumento/${documento}`,
            nuevaMeta
        )

        if (response.status === 200) {
          alert("✅ Meta guardada exitosamente")
          // Limpiar formulario
          nombreMeta.value = ''
          selectedRange.value = null
          internalValue.value = 0
          displayValue.value = '0,00 Bs'
          attributes.value = []
        } else {
          alert("❌ Error al guardar la meta.")
        }
      } catch (error) {
        console.error("Error al enviar meta:", error)
        alert(`⚠️ ${
            typeof error.response?.data === 'object'
                ? JSON.stringify(error.response.data, null, 2)
                : error.response?.data || error.message
        }`)
      }
    }

    return {
      nombreMeta,
      selectedRange,
      displayValue,
      internalValue,
      attributes,
      handleDayClick,
      formatDate,
      handleInput,
      submitMeta
    }
  }
}
</script>

<template>
  <header class="header">
    <h1>Metas financieras</h1>
  </header>

  <main>
    <div class="container">
      <div class="card">
        <h2 class="title">Agregar meta financiera</h2>
        <form class="form-agregarcuenta" @submit.prevent="submitMeta">

          <!-- Campo Nombre -->
          <p class="message">Nombre de la meta financiera</p>
          <input v-model="nombreMeta" type="text" class="input" placeholder="Compra de carro eléctrico">

          <!-- Selector de fechas -->
          <p class="message">Seleccione un rango de fechas</p>
          <div class="calendar-wrapper">
            <calendar
                :attributes="attributes"
                is-range
                @dayclick="handleDayClick"
            />
            <p v-if="selectedRange && selectedRange.end" class="range-display">
              Rango seleccionado: {{ formatDate(selectedRange.start) }} - {{ formatDate(selectedRange.end) }}
            </p>
          </div>

          <!-- Monto requerido -->
          <p class="message">Ingrese el monto requerido</p>
          <div class="currency-wrapper">
            <input
                type="text"
                :value="displayValue"
                @input="handleInput"
                placeholder="0,00 Bs"
                class="currency-input"
                autocomplete="off"
            />
          </div>

          <!-- Botón Agregar -->
          <button class="continuebtn" type="submit">
            <span>Agregar</span>
          </button>
        </form>
      </div>
    </div>
  </main>
</template>

<style scoped>

.header {
  display: flex;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  font-size: 16px;
  position: relative;
  left: 17%;
  top: -100vh;
  width: 80%;
  height: 100%;
  background-color: rgb(255, 255, 255);
  border-bottom: 2px solid black;
}

.container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.message {
  margin-bottom: 16px;
  font-size: 24px;
  font-family: "Inter", sans-serif;
}

.card {
  position: absolute;
  left: 60%;
  top: 100%;
  transform: translate(-50%, -100%);
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
  height: 80vh;
  width: 100vh;
}

.title {
  font-size: 32px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
}

.form-agregarcuenta {
  display: flex;
  flex-direction: column;
}

.input {
  background-color: #f7f7f7;
  color: #333;
  width: 60%;
  border: none;
  border-radius: 4px;
  padding: 10px;
  margin-top: 20px;
  margin-bottom: 20px;
  transition: background-color 0.15s ease-in-out;
}

.input:focus {
  background-color: #eaeaea;
  outline: none;
  box-shadow: 0 0 0 1px #007bff;
}

.currency-wrapper {
  max-width: 300px;
  margin: 1rem auto;
}

.currency-input {
  position: relative;
  left: -120%;
  top: 100%;
  font-size: 1.4rem;
  padding: 10px 14px;
  border: 1px solid #ccc;
  border-radius: 6px;
  text-align: right;
  width: 100%;
  box-sizing: border-box;
}


/*---Boton de agregar meta---*/
.continuebtn {
  position: absolute;
  left: 85%;
  top: 90%;
  display: inline-block;
  border-radius: 4px;
  background-color: #3d405b;
  border: none;
  color: #FFFFFF;
  text-align: center;
  font-size: 17px;
  padding: 16px;
  width: 130px;
  transition: all 0.5s;
  cursor: pointer;
  margin: 5px;
}

.continuebtn span {
  cursor: pointer;
  display: inline-block;
  position: relative;
  transition: 0.5s;
}

.continuebtn span:after {
  content: '»';
  position: absolute;
  opacity: 0;
  top: 0;
  right: -15px;
  transition: 0.5s;
}

.continuebtn:hover span {
  padding-right: 15px;
}

.continuebtn:hover span:after {
  opacity: 1;
  right: 0;
}
/*-----------------------------*/


</style>