<script setup lang="ts">

</script>

<template>
  <header class="header" :key="$route.fullPath">
    <h1>Cuentas</h1>
  </header>

  <div class="contenedor-cuentas-globales">
    <div class="contenedor-cuentascorrientes">
      <h2 class="title">Cuentas Corrientes</h2>
      <div class="contenedor-cuentas">
        <button class="cuenta-corriente">
          <img src="@/images/BBVAprovinciallogo.png" width="150" height="100">
          <div class="info-cuenta">
            <span class="numero-cuenta">0105-***********************</span>
            <span class="saldo">20.000,00 Bs</span>
          </div>
          <div class="arrow-wrapper">
            <div class="arrow"></div>
          </div>
        </button>
        <button class="cuenta-corriente">
          <img src="@/images/Banco_de_Venezuela_logo.png" width="150" height="50">
          <div class="info-cuenta">
            <span class="numero-cuenta">0105-***********************</span>
            <span class="saldo">20.000,00 Bs</span>
          </div>
          <div class="arrow-wrapper">
            <div class="arrow"></div>
          </div>
        </button>
      </div>
    </div>

    <div class="contenedor-cuentasahorro">
      <h2 class="title">Cuentas de Ahorro</h2>
      <div class="contenedor-cuentas">
        <button class="cuenta-ahorro">
          <img src="@/images/BBVAprovinciallogo.png" width="150" height="100">
          <div class="info-cuenta">
            <span class="numero-cuenta">0105-***********************</span>
            <span class="saldo">20.000,00 Bs</span>
          </div>
          <div class="arrow-wrapper">
            <div class="arrow"></div>
          </div>
        </button>
        <button class="cuenta-ahorro">
          <img src="@/images/Banco_de_Venezuela_logo.png" width="150" height="50">
          <div class="info-cuenta">
            <span class="numero-cuenta">0105-***********************</span>
            <span class="saldo">20.000,00 Bs</span>
          </div>
          <div class="arrow-wrapper">
            <div class="arrow"></div>
          </div>
        </button>
      </div>
    </div>
  </div>
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

.title {
  font-size: 2rem;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.contenedor-cuentas-globales {
  display: flex;
  min-width: 1500px;
  min-height: 700px;
  gap: 15px;
}

.contenedor-cuentascorrientes {
  position: absolute;
  display: flex;
  flex-direction: column;
  top: 50%;
  left: 60%;
  transform: translate(-50%, -50%);
  min-width: 1500px;
  min-height: 700px;
}

.contenedor-cuentasahorro {
  position: absolute;
  display: flex;
  flex-direction: column;
  top: 100%;
  left: 60%;
  transform: translate(-50%, -50%);
  min-width: 1500px;
  min-height: 700px;
}

/*Botones de representacion de las cuentas*/
.cuenta-corriente {
  --primary-color: rgba(195, 181, 229, 1);
  --secondary-color: #fff;
  --hover-color: #111;
  --arrow-width: 10px;
  --arrow-stroke: 2px;
  box-sizing: border-box;
  border: 0;
  border-radius: 20px;
  color: var(--secondary-color);
  padding: 1em 1.8em;
  background: var(--primary-color);
  display: flex;
  transition: 0.2s background;
  align-items: center;
  justify-content: flex-start;
  gap: 5em;
  font-weight: bold;
  min-width: 800px;
  min-height: 100px;
  margin-bottom: 15px;
}

.info-cuenta {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  flex-grow: 1;
}
.numero-cuenta,
.saldo {
  font-size: 1.5rem;
}

.cuenta-corriente .arrow-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
}

.cuenta-corriente .arrow {
  margin-top: 1px;
  width: var(--arrow-width);
  background: var(--primary-color);
  height: var(--arrow-stroke);
  position: relative;
  transition: 0.2s;
}

.cuenta-corriente .arrow::before {
  content: "";
  box-sizing: border-box;
  position: absolute;
  border: solid var(--secondary-color);
  border-width: 0 var(--arrow-stroke) var(--arrow-stroke) 0;
  display: inline-block;
  top: -3px;
  right: 3px;
  transition: 0.2s;
  padding: 3px;
  transform: rotate(-45deg);
}

.cuenta-corriente:hover {
  background-color: rgba(1, 61, 163, 0.7);
}

.cuenta-corriente:hover .arrow {
  background: var(--secondary-color);
}

.cuenta-corriente:hover .arrow:before {
  right: 0;
}


.cuenta-ahorro {
  --primary-color: rgba(237, 158, 178, 1);
  --secondary-color: #fff;
  --hover-color: #111;
  --arrow-width: 10px;
  --arrow-stroke: 2px;
  box-sizing: border-box;
  border: 0;
  border-radius: 20px;
  color: var(--secondary-color);
  padding: 1em 1.8em;
  background: var(--primary-color);
  display: flex;
  transition: 0.2s background;
  align-items: center;
  justify-content: flex-start;
  gap: 5em;
  font-weight: bold;
  min-width: 800px;
  min-height: 100px;
  margin-bottom: 15px;
}

.cuenta-ahorro .arrow-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
}

.cuenta-ahorro .arrow {
  margin-top: 1px;
  width: var(--arrow-width);
  background: var(--primary-color);
  height: var(--arrow-stroke);
  position: relative;
  transition: 0.2s;
}

.cuenta-ahorro .arrow::before {
  content: "";
  box-sizing: border-box;
  position: absolute;
  border: solid var(--secondary-color);
  border-width: 0 var(--arrow-stroke) var(--arrow-stroke) 0;
  display: inline-block;
  top: -3px;
  right: 3px;
  transition: 0.2s;
  padding: 3px;
  transform: rotate(-45deg);
}

.cuenta-ahorro:hover {
  background-color: rgba(1, 61, 163, 0.7);
}

.cuenta-ahorro:hover .arrow {
  background: var(--secondary-color);
}

.cuenta-ahorro:hover .arrow:before {
  right: 0;
}
/*-----------------------------------------*/
</style>