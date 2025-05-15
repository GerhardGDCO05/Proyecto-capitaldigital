<script>
  import './assets/main.css';

  export default {
    name: 'App',
    data() {
      return {
        recordarme: false,
        options: ['Cedula', 'Pasaporte', 'Option 3', 'Option 4'],
      }
    },
    methods: {
      abrirbank() {
        this.$router.push({ path: '/bank', query: { popup: 'true' } });
      },
      
      abrirRegistro() {
        this.$router.push({ path: '/registrar', query: { popup: 'true' } });
      }
    },
  };

  document.addEventListener('DOMContentLoaded', function() {
    let currentIndex = 0;
    const items = document.querySelectorAll('.carrusel-item');
    const totalItems = items.length;

    function showNext() {
      currentIndex = (currentIndex + 1) % totalItems;
      updateCarrusel();
    }

    function updateCarrusel() {
      const carruselElement = document.querySelector('.carrusel');
      if (carruselElement) {
        const offset = -currentIndex * 100;
        carruselElement.style.transform = `translateX(${offset}%)`;
      }
    }

    // Automatically change image every 10 seconds
    setInterval(showNext, 10000);
  });



</script>

<template>
  <div id="app">
    <template v-if="$route.query.popup==='true'">
          <router-view />
    </template>


    <!-- Si no es popup, muestra la aplicación normal -->
    <template v-else>

      <main>
        <div class="titulo">
          <img class="bank-icon" src="../public/bankicon.ico" alt="logo banco">
          <h1>MANJOUD BANK</h1>
        </div>
        <header class="header">
          <nav class="nav-bar">
            <div class="cap-social">
              <img class="bank-icon" src="../bankicon.ico" alt="logo banco">
              <p class="nav-text cap-social-text">CAPITAL</p> 
              <p class="nav-text cap-social-text">SOCIAL</p>
            </div>
            <a @click="abrirRegistro" class="links">Registrar</a>
            <a @click="" class="links">Conócenos</a>
            <a href="#" class="links preguntas-frecuentes">Preguntas Frecuentes</a>
          </nav>
          <div class="carrusel-container">
            <div class="login" id="login">
              <h2>Inicio de Sesion</h2>
              <div class="login-data-container">
                <label for="identificacion">Documento</label>
                <select class=" cb cb-login" id="combo-box" v-model="selectedOption">
                  <option class="datos-login" v-for="option in options" :key="option" :value="option">
                    {{ option }}
                  </option>
                </select>
              </div>
              <input type='text' class="login-data" placeholder="N- de documento">
              <input type='password' class="login-data" placeholder="Password">
              <Button @click="abrirbank" class="login-button">INGRESAR</Button>
              <div class="recordarme">
                <a href="#">¿Olvidaste tu contraseña?</a>
                <input class="checkbox-remenber" type="checkbox" id="remenber" v-model="recordarme" />
                <label class="label-remenber" for="remenber">Recordarme</label>
              </div>
            </div>
            <div class="carrusel">
              <div class="carrusel-item"><img src="https://media.istockphoto.com/id/542727462/es/foto/horizonte-de-houston-texas.jpg?s=612x612&w=0&k=20&c=ZtuiuLR6C48V8jUy_Ws4qyoWrhT6F1wlS_hfxQ5DTjE=" alt="paisaje"></div>

                <div class="carrusel-item"><img src="https://media.istockphoto.com/id/517188688/es/foto/paisaje-de-monta%C3%B1a.jpg?s=612x612&w=0&k=20&c=EnSd5sJdxih_svZHscQ5Hfzr3RSOdXO9MpdmKK4CMTs=" alt="Image 2"></div>

                <div class="carrusel-item"><img src="https://thumbs.dreamstime.com/b/paisajes-de-yosemite-46208063.jpg" alt="Image 3"></div>
            </div>
          </div>
        </header>

        
        <div class="container">
            <h2 class="subtitle">TU PATRIMONIO EN UN SITIO</h2>
            <nav class="nav-container">
              <a href="#" class="link-container">Posicion Global de tus Cuentas</a>
              <a href="#" class="link-container">Historial de transacciones</a>
              <a href="#" class="link-container">Control Financiero</a>
            </nav>
            <div class="fotos-container-first">
              <div class="img-mujer">
                <img class="foto-mujer" src="https://st.depositphotos.com/2117297/4993/i/450/depositphotos_49933079-stock-photo-hipster-woman-using-laptop.jpg" alt="">
              </div>
              <div class="feature">
                <div class="img-feature-container">
                  <img src="../public/seguridad-informatica.png" alt="consultas">
                  <img src="../public/agente-de-servicio-al-cliente.png" alt="servicio-al-cliente">
                  <img src="../public/ciberseguridad.png" alt="antifraude">
                </div>
                <div class="regist">
                  <a @click="abrirRegistro" class="custom-button">Registrate</a>
                </div>
              </div>
          </div>

          <div class="title-second-container"><h2>PRINCIPALES OPERACIONES DE CAPITAL DIGITAL</h2></div>
          <div class="fotos-container-second">
                <div class="second">
                  <img src="../public/lupa.png" alt="consultas">
                  <h2>CONSULTAS</h2>
                  <p>Posicion global,</p>
                  <p> Tarjetas, </p>
                  <p>Cuentas, Entre Otras más.</p>
                </div>
                <div class="second">
                  <img src="../public/banco.png" alt="banco">
                  <h2>TRANSFERENCIAS Y PAGOS</h2>
                  <p>Transferencia de dinero a terceros,</p>
                  <p>soporte a multiples a bancos.</p>
                  <p>Pagos de servicios como electricidad,</p>
                  <p>internet y agua</p>
                </div>
                <div class="second ">
                  <img src="../public/registro.png" alt="registros">
                  <h2>REGISTROS</h2>
                  <p>Registros de todas las transferencias,</p>
                  <p>pagos y monitoreo de cuentas.</p>
                </div>
          </div>
          <div class="extra">
            <div class="extra-banks">
            <img class="bdv" src="https://laverdaddemonagas.com/wp-content/uploads/2022/05/quieres-abrir-una-cuenta-en-bdv-aqui-te-ensenamos-laverdaddemonagas.com-1621287095-937796-1621287161-noticia-normal.jpg" alt="">
            <img class="mercantil" src="../public/MERCANTIL.jpg" alt="">
            <img class="bbva" src="../public/BBVA.png" alt="">
            <img class="bnc" src="../public/bnc.png" alt="">

            </div>
            <div class="otros-extras">
              <h2>Multi-Soporte de Bancos</h2>
              <p>Compatibilidad para todos los bancos</p>
              <p>nacionales y gestiona tu patrimonio,</p>
              <p>todo en una pagina</p>
              <a href="#">Como Funciona >></a>

            </div>

          </div>
        </div>
        <footer class="barra-final">
          <div class="cap-digital">
            
            <div class="cap-digital-title">
              <img class="bank-icon icon-cap-digital" src="../public/bankicon.ico" alt="logo banco">
              <h2>CAPITAL DIGITAL</h2>
            </div>        
            <div class="info-terminos-condiciones">
              <div>
                <p>Legal Terminos y Condiciones</p>
              </div>
              <div>
                <p>¿Donde nos encontramos?</p>
                <p>Sedes</p>
              </div>
              <div>
                <a href="#login"><p>Iniciar Sesion</p></a>
              </div>
            </div>
    <!-- informacion adicional-->
            <div class="info-adicional">
              <div>
                <p>0414-2326879</p>
              </div>
              <div>
                <p>Caracas, Venezuela</p>
              </div>
              <div>
                <p>capitaldigital@e-banking.net</p>
              </div>
            </div>
          </div>
          <div class="derechos-reservados">
            <p>© 2025-Actua Capital Digital, S.A. Todos los derechos reservados</p>
          </div>

        </footer>
      </main>
    </template>
  </div>
</template>



