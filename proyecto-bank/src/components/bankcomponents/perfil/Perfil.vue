<script>
import '@/assets/perfil.css';
</script>

<template>
<header class="header">
    <h1>Perfil</h1>
</header>
<main>
    <form class="form-perfil">
      <div class="flex-1-perfil">
        <div class="column">
          <p class="message">Información personal</p>
          <!-- Campo Nombre -->
          <div class="form-field">
            <span>Nombre</span>
            <div class="input-group">
              <input class="input" type="text" placeholder="Nombre" disabled>
            </div>
          </div>

          <div class="form-field">
            <span>Apellido</span>
            <div class="input-group">
              <input class="input" type="text" placeholder="Apellido" disabled>

            </div>
          </div>

          <div class="form-field">
            <span>Email</span>
            <div class="input-group">
              <input class="input" type="email" placeholder="Email" disabled>
              <button class="editBtn">
                <svg height="1em" viewBox="0 0 512 512">
                  <path
                      d="M410.3 231l11.3-11.3-33.9-33.9-62.1-62.1L291.7 89.8l-11.3 11.3-22.6 22.6L58.6 322.9c-10.4 10.4-18 23.3-22.2 37.4L1 480.7c-2.5 8.4-.2 17.5 6.1 23.7s15.3 8.5 23.7 6.1l120.3-35.4c14.1-4.2 27-11.8 37.4-22.2L387.7 253.7 410.3 231zM160 399.4l-9.1 22.7c-4 3.1-8.5 5.4-13.3 6.9L59.4 452l23-78.1c1.4-4.9 3.8-9.4 6.9-13.3l22.7-9.1v32c0 8.8 7.2 16 16 16h32zM362.7 18.7L348.3 33.2 325.7 55.8 314.3 67.1l33.9 33.9 62.1 62.1 33.9 33.9 11.3-11.3 22.6-22.6 14.5-14.5c25-25 25-65.5 0-90.5L453.3 18.7c-25-25-65.5-25-90.5 0zm-47.4 168l-144 144c-6.2 6.2-16.4 6.2-22.6 0s-6.2-16.4 0-22.6l144-144c6.2-6.2 16.4-6.2 22.6 0s6.2 16.4 0 22.6z"
                  ></path>
                </svg>
              </button>

            </div>
          </div>
        </div>

        <div class="column">
          <p class="message">&nbsp;</p>
          <div class="form-field">
            <span>Fecha de nacimiento</span>
            <div class="input-group">
              <input class="input" type="tel" placeholder="Fecha" disabled>
            </div>
          </div>

          <div class="form-field">
            <span>Documento</span>
            <div class="input-group">
              <input class="input" type="tel" placeholder="documento" disabled>
              <button class="editBtn">
                <svg height="1em" viewBox="0 0 512 512">
                  <path
                      d="M410.3 231l11.3-11.3-33.9-33.9-62.1-62.1L291.7 89.8l-11.3 11.3-22.6 22.6L58.6 322.9c-10.4 10.4-18 23.3-22.2 37.4L1 480.7c-2.5 8.4-.2 17.5 6.1 23.7s15.3 8.5 23.7 6.1l120.3-35.4c14.1-4.2 27-11.8 37.4-22.2L387.7 253.7 410.3 231zM160 399.4l-9.1 22.7c-4 3.1-8.5 5.4-13.3 6.9L59.4 452l23-78.1c1.4-4.9 3.8-9.4 6.9-13.3l22.7-9.1v32c0 8.8 7.2 16 16 16h32zM362.7 18.7L348.3 33.2 325.7 55.8 314.3 67.1l33.9 33.9 62.1 62.1 33.9 33.9 11.3-11.3 22.6-22.6 14.5-14.5c25-25 25-65.5 0-90.5L453.3 18.7c-25-25-65.5-25-90.5 0zm-47.4 168l-144 144c-6.2 6.2-16.4 6.2-22.6 0s-6.2-16.4 0-22.6l144-144c6.2-6.2 16.4-6.2 22.6 0s6.2 16.4 0 22.6z"
                  ></path>
                </svg>
              </button>
            </div>
          </div>

          <div class="form-field">
            <span>Número de documento</span>
            <div class="input-group">
              <input class="input" type="tel" placeholder="N. Documento" disabled>
              <button class="editBtn">
                <svg height="1em" viewBox="0 0 512 512">
                  <path
                      d="M410.3 231l11.3-11.3-33.9-33.9-62.1-62.1L291.7 89.8l-11.3 11.3-22.6 22.6L58.6 322.9c-10.4 10.4-18 23.3-22.2 37.4L1 480.7c-2.5 8.4-.2 17.5 6.1 23.7s15.3 8.5 23.7 6.1l120.3-35.4c14.1-4.2 27-11.8 37.4-22.2L387.7 253.7 410.3 231zM160 399.4l-9.1 22.7c-4 3.1-8.5 5.4-13.3 6.9L59.4 452l23-78.1c1.4-4.9 3.8-9.4 6.9-13.3l22.7-9.1v32c0 8.8 7.2 16 16 16h32zM362.7 18.7L348.3 33.2 325.7 55.8 314.3 67.1l33.9 33.9 62.1 62.1 33.9 33.9 11.3-11.3 22.6-22.6 14.5-14.5c25-25 25-65.5 0-90.5L453.3 18.7c-25-25-65.5-25-90.5 0zm-47.4 168l-144 144c-6.2 6.2-16.4 6.2-22.6 0s-6.2-16.4 0-22.6l144-144c6.2-6.2 16.4-6.2 22.6 0s6.2 16.4 0 22.6z"
                  ></path>
                </svg>
              </button>
            </div>
          </div>
        </div>

        <div class="column">
          <p class="message">&nbsp;</p> <!-- Para alinear las columnas -->
          <div class="form-field">
            <span>Dirección</span>
            <div class="input-group">
              <input class="input" type="tel" placeholder="Dirección" disabled>
              <button class="editBtn">
                <svg height="1em" viewBox="0 0 512 512">
                  <path
                      d="M410.3 231l11.3-11.3-33.9-33.9-62.1-62.1L291.7 89.8l-11.3 11.3-22.6 22.6L58.6 322.9c-10.4 10.4-18 23.3-22.2 37.4L1 480.7c-2.5 8.4-.2 17.5 6.1 23.7s15.3 8.5 23.7 6.1l120.3-35.4c14.1-4.2 27-11.8 37.4-22.2L387.7 253.7 410.3 231zM160 399.4l-9.1 22.7c-4 3.1-8.5 5.4-13.3 6.9L59.4 452l23-78.1c1.4-4.9 3.8-9.4 6.9-13.3l22.7-9.1v32c0 8.8 7.2 16 16 16h32zM362.7 18.7L348.3 33.2 325.7 55.8 314.3 67.1l33.9 33.9 62.1 62.1 33.9 33.9 11.3-11.3 22.6-22.6 14.5-14.5c25-25 25-65.5 0-90.5L453.3 18.7c-25-25-65.5-25-90.5 0zm-47.4 168l-144 144c-6.2 6.2-16.4 6.2-22.6 0s-6.2-16.4 0-22.6l144-144c6.2-6.2 16.4-6.2 22.6 0s6.2 16.4 0 22.6z"
                  ></path>
                </svg>
              </button>
            </div>
          </div>

          <div class="form-field">
            <span>Estado</span>
            <div class="input-group">
              <input class="input" type="tel" placeholder="Estado" disabled>
              <button class="editBtn">
                <svg height="1em" viewBox="0 0 512 512">
                  <path
                      d="M410.3 231l11.3-11.3-33.9-33.9-62.1-62.1L291.7 89.8l-11.3 11.3-22.6 22.6L58.6 322.9c-10.4 10.4-18 23.3-22.2 37.4L1 480.7c-2.5 8.4-.2 17.5 6.1 23.7s15.3 8.5 23.7 6.1l120.3-35.4c14.1-4.2 27-11.8 37.4-22.2L387.7 253.7 410.3 231zM160 399.4l-9.1 22.7c-4 3.1-8.5 5.4-13.3 6.9L59.4 452l23-78.1c1.4-4.9 3.8-9.4 6.9-13.3l22.7-9.1v32c0 8.8 7.2 16 16 16h32zM362.7 18.7L348.3 33.2 325.7 55.8 314.3 67.1l33.9 33.9 62.1 62.1 33.9 33.9 11.3-11.3 22.6-22.6 14.5-14.5c25-25 25-65.5 0-90.5L453.3 18.7c-25-25-65.5-25-90.5 0zm-47.4 168l-144 144c-6.2 6.2-16.4 6.2-22.6 0s-6.2-16.4 0-22.6l144-144c6.2-6.2 16.4-6.2 22.6 0s6.2 16.4 0 22.6z"
                  ></path>
                </svg>
              </button>
            </div>
          </div>

          <div class="form-field">
            <span>Código Postal</span>
            <div class="input-group">
              <input class="input" type="tel" placeholder="Código Postal" disabled>
              <button class="editBtn">
                <svg height="1em" viewBox="0 0 512 512">
                  <path
                      d="M410.3 231l11.3-11.3-33.9-33.9-62.1-62.1L291.7 89.8l-11.3 11.3-22.6 22.6L58.6 322.9c-10.4 10.4-18 23.3-22.2 37.4L1 480.7c-2.5 8.4-.2 17.5 6.1 23.7s15.3 8.5 23.7 6.1l120.3-35.4c14.1-4.2 27-11.8 37.4-22.2L387.7 253.7 410.3 231zM160 399.4l-9.1 22.7c-4 3.1-8.5 5.4-13.3 6.9L59.4 452l23-78.1c1.4-4.9 3.8-9.4 6.9-13.3l22.7-9.1v32c0 8.8 7.2 16 16 16h32zM362.7 18.7L348.3 33.2 325.7 55.8 314.3 67.1l33.9 33.9 62.1 62.1 33.9 33.9 11.3-11.3 22.6-22.6 14.5-14.5c25-25 25-65.5 0-90.5L453.3 18.7c-25-25-65.5-25-90.5 0zm-47.4 168l-144 144c-6.2 6.2-16.4 6.2-22.6 0s-6.2-16.4 0-22.6l144-144c6.2-6.2 16.4-6.2 22.6 0s6.2 16.4 0 22.6z"
                  ></path>
                </svg>
              </button>
            </div>
          </div>
        </div>

      </div>

      <div class="flex-2-perfil">
        <p>Información de cuenta</p>
        <div class="new-row">
          <div class="form-field">
            <span>Correo electrónico</span>
            <div class="input-group">
              <input class="input" type="text" placeholder="Correo" disabled>
              <button class="editBtn">
                <svg height="1em" viewBox="0 0 512 512">
                  <path
                      d="M410.3 231l11.3-11.3-33.9-33.9-62.1-62.1L291.7 89.8l-11.3 11.3-22.6 22.6L58.6 322.9c-10.4 10.4-18 23.3-22.2 37.4L1 480.7c-2.5 8.4-.2 17.5 6.1 23.7s15.3 8.5 23.7 6.1l120.3-35.4c14.1-4.2 27-11.8 37.4-22.2L387.7 253.7 410.3 231zM160 399.4l-9.1 22.7c-4 3.1-8.5 5.4-13.3 6.9L59.4 452l23-78.1c1.4-4.9 3.8-9.4 6.9-13.3l22.7-9.1v32c0 8.8 7.2 16 16 16h32zM362.7 18.7L348.3 33.2 325.7 55.8 314.3 67.1l33.9 33.9 62.1 62.1 33.9 33.9 11.3-11.3 22.6-22.6 14.5-14.5c25-25 25-65.5 0-90.5L453.3 18.7c-25-25-65.5-25-90.5 0zm-47.4 168l-144 144c-6.2 6.2-16.4 6.2-22.6 0s-6.2-16.4 0-22.6l144-144c6.2-6.2 16.4-6.2 22.6 0s6.2 16.4 0 22.6z"
                  ></path>
                </svg>
              </button>
            </div>
          </div>
          <div class="form-field">
            <span>Contraseña</span>
            <div class="input-group">
              <input class="input" type="text" placeholder="Contraseña" disabled>
              <button class="editBtn">
                <svg height="1em" viewBox="0 0 512 512">
                  <path
                      d="M410.3 231l11.3-11.3-33.9-33.9-62.1-62.1L291.7 89.8l-11.3 11.3-22.6 22.6L58.6 322.9c-10.4 10.4-18 23.3-22.2 37.4L1 480.7c-2.5 8.4-.2 17.5 6.1 23.7s15.3 8.5 23.7 6.1l120.3-35.4c14.1-4.2 27-11.8 37.4-22.2L387.7 253.7 410.3 231zM160 399.4l-9.1 22.7c-4 3.1-8.5 5.4-13.3 6.9L59.4 452l23-78.1c1.4-4.9 3.8-9.4 6.9-13.3l22.7-9.1v32c0 8.8 7.2 16 16 16h32zM362.7 18.7L348.3 33.2 325.7 55.8 314.3 67.1l33.9 33.9 62.1 62.1 33.9 33.9 11.3-11.3 22.6-22.6 14.5-14.5c25-25 25-65.5 0-90.5L453.3 18.7c-25-25-65.5-25-90.5 0zm-47.4 168l-144 144c-6.2 6.2-16.4 6.2-22.6 0s-6.2-16.4 0-22.6l144-144c6.2-6.2 16.4-6.2 22.6 0s6.2 16.4 0 22.6z"
                  ></path>
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>

      <button class="continuebtn">
        <span>Guardar</span>
      </button>



    </form>
</main>
</template>
<style scoped>
@import url('https://fonts.googleapis.com/css?family=Inter:100,200,300,regular,500,600,700,800,900');

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

/* From Uiverse.io by ammarsaa */
.form-perfil {
  position: absolute;
  display: flex;
  flex-direction: column;
  top: 50%;
  left: 60%;
  transform: translate(-50%, -50%);
  min-width: 1500px;
  min-height: 700px;
  gap: 10px;
  padding: 20px;
  border-radius: 10px;
  background-color: #3A5F9B;
  color: #fff;
  border: 1px solid #333;
}

p {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  font-size: 24px;
}

.flex-1-perfil {
  display: flex;
  gap: 30px;
  padding: 20px;
}

.flex-2-perfil {
  gap: 30px;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.new-row {
  display: flex;
  flex-direction: row;
  margin-top: -10px; /* Espacio entre secciones */
  gap: 15px;
}

.column {
  display: flex;
  flex-direction: column;
  gap: 20px; /* Espacio entre campos */
  flex: 1; /* Ambas columnas son iguales */
}

.input-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/*---BOTON DE EDITAR---*/

/* From Uiverse.io by vinodjangid07 */
.editBtn {
  width: 55px;
  height: 55px;
  border-radius: 20px;
  border: none;
  background-color: rgb(93, 93, 116);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0px 5px 10px rgba(0, 0, 0, 0.123);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}
.editBtn::before {
  content: "";
  width: 200%;
  height: 200%;
  background-color: rgb(102, 102, 141);
  position: absolute;
  z-index: 1;
  transform: scale(0);
  transition: all 0.3s;
  border-radius: 50%;
  filter: blur(10px);
}
.editBtn:hover::before {
  transform: scale(1);
}
.editBtn:hover {
  box-shadow: 0px 5px 10px rgba(0, 0, 0, 0.336);
}

.editBtn svg {
  height: 17px;
  fill: white;
  z-index: 3;
  transition: all 0.2s;
  transform-origin: bottom;
}
.editBtn:hover svg {
  transform: rotate(-15deg) translateX(5px);
}
.editBtn::after {
  content: "";
  width: 25px;
  height: 1.5px;
  position: absolute;
  bottom: 19px;
  left: -5px;
  background-color: white;
  border-radius: 2px;
  z-index: 2;
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.5s ease-out;
}
.editBtn:hover::after {
  transform: scaleX(1);
  left: 0px;
  transform-origin: right;
}


/*----------------------*/

/*---BOTON GUARDAR CAMBIOS---*/
/* From Uiverse.io by NorthFishHasNa */
.continuebtn {
  position: relative;
  left: 88%;
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
/*----------------------------*/

</style>