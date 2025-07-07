<script setup lang="ts">
import { ref } from 'vue';
import Swal from 'sweetalert2';

// Tipos para el formulario de contacto
interface ContactForm {
  nombre: string;
  email: string;
  mensaje: string;
}

// Estado reactivo para el formulario
const form = ref<ContactForm>({
  nombre: '',
  email: '',
  mensaje: ''
});

// Estado para las preguntas frecuentes (FAQ)
interface FAQ {
  pregunta: string;
  respuesta: string;
  isOpen: boolean;
}

const faqs = ref<FAQ[]>([
  {
    pregunta: '¿Cómo inicio sesión en el sistema?',
    respuesta: 'Ingresa tu número de documento y contraseña en la página principal. Si no tienes cuenta, haz clic en "Regístrate" para crear una.',
    isOpen: false
  },
  {
    pregunta: '¿Cómo agrego una nueva cuenta bancaria?',
    respuesta: 'Ve a la sección "Agregar Cuenta" en el sidebar, completa el formulario con los detalles de la cuenta y confirma.',
    isOpen: false
  },
  {
    pregunta: '¿Cómo realizo una transferencia?',
    respuesta: 'Dirígete a "Transferencias" en el sidebar, selecciona la cuenta de origen, el beneficiario, el monto y confirma la operación.',
    isOpen: false
  },
  {
    pregunta: '¿Qué hago si olvidé mi contraseña?',
    respuesta: 'Contacta al soporte a través del formulario de abajo o llama a nuestra línea de atención al cliente.',
    isOpen: false
  }
]);

// Función para alternar la visibilidad de las respuestas en el FAQ
const toggleFAQ = (index: number) => {
  faqs.value[index].isOpen = !faqs.value[index].isOpen;
};

// Función para enviar el formulario de contacto
const enviarMensaje = async () => {
  if (!form.value.nombre || !form.value.email || !form.value.mensaje) {
    await Swal.fire({
      title: 'Error',
      text: 'Por favor, completa todos los campos del formulario.',
      icon: 'error',
      confirmButtonText: 'OK'
    });
    return;
  }

  try {
    // Simular envío al backend
    console.log('Enviando mensaje:', form.value);
    await Swal.fire({
      title: '¡Mensaje enviado!',
      text: 'Hemos recibido tu mensaje. Te contactaremos pronto.',
      icon: 'success',
      confirmButtonText: 'OK'
    });
    // Resetear formulario
    form.value = { nombre: '', email: '', mensaje: '' };
  } catch (error) {
    console.error('Error al enviar el mensaje:', error);
    await Swal.fire({
      title: 'Error',
      text: 'Hubo un error al enviar el mensaje. Intenta de nuevo.',
      icon: 'error',
      confirmButtonText: 'OK'
    });
  }
};
</script>

<template>
  <header class="header">
    <h1>Ayuda</h1>
  </header>
  <div class="ayuda-container">
    <h1>Ayuda y Soporte</h1>

    <!-- Sección de Preguntas Frecuentes -->
    <section class="faq-section">
      <h2>Preguntas Frecuentes</h2>
      <div class="faq-list">
        <div v-for="(faq, index) in faqs" :key="index" class="faq-item">
          <div class="faq-question" @click="toggleFAQ(index)">
            <span>{{ faq.pregunta }}</span>
            <i :class="['ri-arrow-down-s-line', { 'rotate': faq.isOpen }]"></i>
          </div>
          <div v-if="faq.isOpen" class="faq-answer">
            <p>{{ faq.respuesta }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Sección de Contacto -->
    <section class="contact-section">
      <h2>Contáctanos</h2>
      <p>Si necesitas ayuda adicional, envíanos un mensaje o contáctanos en:</p>
      <ul>
        <li><strong>Teléfono:</strong> 1-800-BANCO-123</li>
        <li><strong>Email:</strong> soporte@bancoapp.com</li>
        <li><strong>Horario:</strong> Lunes a Viernes, 8:00 AM - 6:00 PM</li>
      </ul>
      <form @submit.prevent="enviarMensaje" class="contact-form">
        <div class="form-group">
          <label for="nombre">Nombre</label>
          <input v-model="form.nombre" id="nombre" type="text" placeholder="Tu nombre" required />
        </div>
        <div class="form-group">
          <label for="email">Email</label>
          <input v-model="form.email" id="email" type="email" placeholder="Tu email" required />
        </div>
        <div class="form-group">
          <label for="mensaje">Mensaje</label>
          <textarea v-model="form.mensaje" id="mensaje" placeholder="Escribe tu mensaje" required></textarea>
        </div>
        <button type="submit">Enviar Mensaje</button>
      </form>
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

.ayuda-container {
  position: absolute;
  top: 10%;
  left: 40%;
  max-width: 800px;
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

/* FAQ Section */
.faq-section {
  margin-bottom: 40px;
}

.faq-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.faq-item {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.faq-question {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  color: #757575;
}

.faq-question:hover {
  background-color: #e9ecef;
}

.faq-question i {
  font-size: 18px;
  transition: transform 0.3s ease;
}

.faq-question i.rotate {
  transform: rotate(180deg);
}

.faq-answer {
  padding: 15px;
  font-size: 14px;
  color: #333;
  background-color: #f8f9fa;
}

/* Contact Section */
.contact-section {
  margin-bottom: 40px;
}

.contact-section ul {
  list-style: none;
  margin-bottom: 20px;
  font-size: 14px;
  color: #333;
}

.contact-section ul li {
  margin-bottom: 10px;
}

.contact-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 5px;
}

.form-group input,
.form-group textarea {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 14px;
  font-family: 'Inter', sans-serif;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
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
  .ayuda-container {
    padding: 20px;
  }
}

:deep(.main-content.sidebar-active) .ayuda-container {
  margin-left: 120px;
}
</style>