<script>

import { ref } from 'vue';
import usuarioService from "../services/usuarioService";
import "../assets/bank.css";
import "../assets/registro.css";
import $ from 'jquery';

export default {
  name: "Bank",

  data() {
    return {
      aceptaTerminos: false,
      recordarme: false,
      options: ['Cedula', 'Pasaporte', 'Option 3', 'Option 4'],
      states: ['Miranda', 'Dtto Capital', 'Aragua', 'Carabobo'],
      banks: ['Provincial', 'BDV', 'BNC', 'Mercantil']
    };
  },

  setup() {
    const option = ref('Vista General');
    const userName = ref('Simon Manjoud');

    const cambiarOption = (opcion) => {
      option.value = opcion;
    };

    const usuarioCuenta = ref({
      correo: "",
      cuentas: []
    });

    const addCuenta = ref({
      banco: "",
      numeroCuenta: ""
    });

    const agregar = async () => {
      try {
        if (!usuarioCuenta.value.correo) {
          alert("Debe ingresar un email válido.");
          return;
        }
        if (!addCuenta.value.banco || !addCuenta.value.numeroCuenta) {
          alert("Debe ingresar un banco y número de cuenta.");
          return;
        }

        const response = await usuarioService.agregarCuentaPorEmail(usuarioCuenta.value.correo, addCuenta.value);
        alert("Cuenta agregada exitosamente!");

        // Limpia los datos después de agregar la cuenta
        addCuenta.value.banco = "";
        addCuenta.value.numeroCuenta = "";

      } catch (error) {
        console.error("Error al agregar cuenta:", error);
        alert("Error al agregar la cuenta: " + (error.response?.data || error.message));
      }
    };

    return { option, cambiarOption, userName, usuarioCuenta, addCuenta, agregar };
  },


  mounted() {
    // Agregar eventos jQuery
    $(".menu > ul > li").click(function (e) {
      // Remover clase "active" de los elementos hermanos
      $(this).siblings().removeClass("active");
      // Alternar clase "active" en el elemento clicado
      $(this).toggleClass("active");
      // Mostrar/ocultar submenú
      $(this).find("ul").slideToggle();
      // Cerrar otros submenús si están abiertos
      $(this).siblings().find("ul").slideUp();
      // Remover clase "active" de los elementos del submenú
      $(this).siblings().find("ul").find("li").removeClass("active");
    });

    // Alternar clase "active" en el sidebar
    $(".menu-btn").click(function () {
      $(".sidebar").toggleClass("active");
    });
  }
};
</script>



<!--librerias usadas: remixicon-->
<template>
  <link href="https://cdn.jsdelivr.net/npm/remixicon@3.5.0/fonts/remixicon.css" rel="stylesheet">
  <div class="container">
    <div class="sidebar">
      <div class="menu-btn">
        <i class="ri-menu-line"></i>
      </div>
      <div class="head">
        <div class="logo-ent">
          <!--LOGO CDIGITAL-->
          <img src='C:/Users/Flia Manjoud/Desktop/Proyecto-capitaldigital/proyecto-bank/src/image-removebg-preview.png' alt="">
        </div>
      </div>
      <div class="nav">
        <div class="menu">
          <p class="title">Principal</p>
          <ul>
            <li class="active">
              <a href="#">
                <i class="ri-home-fill"></i>
                <span class="text">Vista General</span>
              </a>
            </li>
            <li>
              <a href="#">
                <i class="ri-pie-chart-2-fill"></i>
                <span class="text">Posición Global</span>
              </a>
            </li>
          </ul>
          <p class="title">Administrar</p>
          <ul>
            <li>
              <a href="#">
                <i class="ri-account-box-fill"></i>
                <span class="text">Cuentas</span>
                <i class="ri-arrow-down-s-line"></i>
              </a>
              <ul class="sub-menu">
                <li>
                  <a href="#">
                    <span>Mostrar Cuentas</span>
                  </a>
                </li>
                <li>
                  <a href="#">
                    <span>Añadir Cuenta</span>
                  </a>
                </li>
                <li>
                  <a href="#">
                    <span>Eliminar Cuenta</span>
                  </a>
                </li>
              </ul>
            </li>
            <li>
              <a href="#">
                <i class="ri-bank-card-fill"></i>
                <span class="text">Tarjetas</span>
              </a>
            </li>
          </ul>
          <p class="title">Movimientos</p>
          <ul>
            <li>
              <a href="#">
                <i class="ri-exchange-funds-line"></i>
                <span class="text">Transferencias</span>
                <i class="ri-arrow-down-s-line"></i>
              </a>
              <ul class="sub-menu">
                <li>
                  <a href="#">
                    <span>Realizar transferencia</span>
                  </a>
                </li>
                <li>
                  <a href="#">
                    <span>Historial de transferencias</span>
                  </a>
                </li>
              </ul>
            </li>
            <li>
              <a href="#">
                <i class="ri-line-chart-fill"></i>
                <span class="text">Patrimonio Neto</span>
              </a>
            </li>
          </ul>
        </div>
        <div class="menu">
          <p class="title">Configuración</p>
          <ul>
            <li>
              <a href="#">
                <i class="ri-settings-5-fill"></i>
                <span class="text">Configuración</span>
              </a>
            </li>
          </ul>
        </div>
      </div>
      <div class="menu menu-acc">
        <p class="title">Cuenta</p>
        <ul>
          <li>
            <a href="#">
              <i class="ri-question-fill"></i>
              <span class="text">Ayuda</span>
            </a>
            <a href="#">
              <i class="ri-account-circle-fill"></i>
              <span class="text">Perfil</span>
            </a>
            <a href="#">
              <i class="ri-logout-box-fill"></i>
              <span class="text">Cerrar Sesión</span>
            </a>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>
<!-- transition: all 0.3s; en el apartado de .sidebar.active .logo-ent-->
<style scoped>
@import url('https://fonts.googleapis.com/css?family=Inter:100,200,300,regular,500,600,700,800,900');
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: "Inter", sans-serif;
}

body {
  background-color: rgba(242, 242, 242, 1);
}

.container {
  display: flex;
  align-items: center;
  width: 100%;
  min-height: 100vh;
}

.sidebar {
  position: fixed;
  left: 0;
  width: 270px;
  height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background-color: rgb(8, 96, 164);
  padding: 24px;
  border-radius: 0 30px 30px 0;
  transition: all 0.3s;
}

.sidebar .head {
  display: flex;
  gap: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #fff8e7;
}

.logo-ent {
  width: 200px;
  height: 70px;
  overflow: hidden;
}

.logo-ent img {
  width: 100%;
  object-fit: cover;
}

.menu ul li {
  position: relative;
  list-style: none;
  margin-bottom: 5px;
}

.menu ul li a {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  font-weight: 500;
  color: #757575;
  text-decoration: none;
  padding: 12px 8px;
  border-radius: 8px;
  transition: all 0.3s;
}

.menu ul li > a:hover, .menu ul li.active > a {
  color: #000;
  background-color: #f6f6f6;
}

.menu ul li.active .ri-arrow-down-s-line {
  transform: rotate(180deg);
}

.menu-btn {
  position: absolute;
  right: 10px;
  top: 4.5%;
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #757575;
  border: 2px solid #f6f6f6;
}

.menu-btn:hover i{
  color: #000;
}

.menu-btn i {
  transition: all 0.3s;
}

.menu ul li .text {
  flex: 1;
}

.menu .sub-menu {
  display: none;
  margin-left: 20px;
  padding-left: 20px;
  padding-top: 5px;
  border-left: 1px solid #f6f6f6;
}

.menu .sub-menu li a {
  padding: 10px 8px;
  font-size: 12px;
}

.menu:not(:last-child){
  padding-bottom: 10px;
  margin-bottom: 20px;
  border-bottom: 2px solid #f6f6f6;
}

.sidebar.active {
  width: 120px;
}

.menu .title {
  font-size: 10px;
  font-weight: 500;
  color: #757575;
  text-transform: uppercase;
  margin-bottom: 10px;
}

.sidebar.active .menu .title {
  text-align: center;
}

.sidebar.active .menu-btn i {
  transform: rotate(180deg);
}

.sidebar.active .menu > ul > li > a {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.sidebar.active .menu > ul > li > a .text {
  position: absolute;
  left: 70px;
  top: 50%;
  transform: translateY(-50%);
  padding: 10px;
  border-radius: 4px;
  color: #fff;
  background-color: #000;
  opacity: 0;
  visibility: hidden;
}

.sidebar.active .menu > ul > li > a .text::after {
  content: "";
  position: absolute;
  left: -5px;
  top: 20px;
  width: 20px;
  height: 20px;
  border-radius: 2px;
  background-color: #000;
  transform: rotate(45deg);
  z-index: -1;
}

.sidebar.active .menu > ul > li > a:hover .text {
  left: 60px;
  opacity: 1;
  visibility: visible;
}

.sidebar.active .menu .sub-menu {
  position: absolute;
  top: 0;
  left: 20px;
  width: 200px;
  border-radius: 20px;
  padding: 10px 20px;
  border: 1px solid #f6f6f6;
  background-color: #fff;
  box-shadow: 0 10px 8px rgba(0, 0, 0, 0.1);
}

.menu-acc {
  position: absolute;
  bottom: 20px;
  left: 0;
  width: 100%;
  padding: 10px;
}


.sidebar .logo-ent img {
  transition: opacity 0.3s ease;
}

.sidebar.active .logo-ent img {
  opacity: 0; /* Oculta la imagen actual */
}

.sidebar.active .logo-ent {
  background-image: url('C:/Users/simon/OneDrive/Escritorio/Proyecto-capitaldigital/proyecto-bank/src/thumbnail_large.png');
  background-size: contain;
  background-repeat: no-repeat;
  width: 100%;
  height: 70px; /* Ajusta según tu imagen */
}

</style>

