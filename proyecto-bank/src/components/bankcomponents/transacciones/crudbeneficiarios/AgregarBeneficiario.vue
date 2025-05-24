<script setup lang="ts">

</script>

<template>
  <!-- From Uiverse.io by nathann09 -->

  <form class="form">
    <p class="form-title">Agregar beneficiario</p>
    <div class="input-container">
      <input type="text" placeholder="Ingresa su nombre completo">
      <span>
          </span>
    </div>
    <div class="input-container">
      <input type="text" placeholder="Ingresa su número de teléfono">
    </div>
    <div class="input-container">
      <input type="text" placeholder="Ingresa su número de cuenta bancaria">
    </div>
    <div class="dropdown">
      <input
          hidden=""
          class="sr-only"
          name="state-dropdown"
          id="state-dropdown"
          type="checkbox"
      />
      <label
          aria-label="dropdown scrollbar"
          for="state-dropdown"
          class="trigger"
      ></label>
      <ul class="list webkit-scrollbar" role="list" dir="auto">
        <li class="listitem" role="listitem">
          <button class="button">
            <img src="@/images/BBVAprovinciallogo.png" width="60" height="30">
            <span>BBVA Provincial</span>
          </button>
        </li>
        <li class="listitem" role="listitem">
          <button class="button">
            <img src="@/images/Banco_de_Venezuela_logo.png" width="100" height="30">
            <span>Banco de Venezuela</span>
          </button>
        </li>
        <li class="listitem" role="listitem">
          <button class="button">
            <img src="@/images/Mercantil.png" width="60" height="60">
            <span>Banco Mercantil</span>
          </button>
        </li>
      </ul>
    </div>
    <button type="submit" class="submit">
      Agregar beneficiario
    </button>
  </form>


</template>

<style scoped>
/*---FORM PARA AGREGAR BENEFICIARIO---*/
/* From Uiverse.io by nathann09 */
.form {
  background-color: #fff;
  display: block;
  padding: 1rem;
  max-width: 700px;
  border-radius: 0.5rem;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

.form-title {
  font-family: "Inter", sans-serif;
  font-size: 2.75rem;
  line-height: 1.75rem;
  font-weight: 600;
  text-align: left;
  color: #000;
}

.input-container {
  position: relative;
}

.input-container input, .form button {
  outline: none;
  border: 1px solid #e5e7eb;
  margin: 8px 0;
}

.input-container input {
  background-color: #fff;
  padding: 1rem;
  padding-right: 3rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  width: 500px;
  border-radius: 0.5rem;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.submit {
  display: block;
  padding-top: 0.75rem;
  padding-bottom: 0.75rem;
  padding-left: 1.25rem;
  padding-right: 1.25rem;
  background-color: #4F46E5;
  color: #ffffff;
  font-size: 0.875rem;
  line-height: 1.25rem;
  font-weight: 500;
  width: 100%;
  border-radius: 0.5rem;
  text-transform: uppercase;
}

.submit:hover {
  background-color: #3730a3;
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
}

.submit:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.5);
}

/*-----------------------------------------------*/

/*---SELECCION DE BANCO (Drop-Down)---*/
.dropdown {
  border: 1px solid #c1c2c5;
  border-radius: 12px;
  transition: all 300ms;
  display: flex;
  flex-direction: column;
  min-height: 58px;
  background-color: white;
  overflow: hidden;
  position: relative;
  inset-inline: auto;
  max-width: 398px;
  min-width: 298px;
  margin-top: 15px;
}

.dropdown input:where(:checked) ~ .list {
  opacity: 1;
  transform: translateY(-3rem) scale(1);
  transition: all 500ms ease;
  margin-top: 32px;
  padding-top: 4px;
  margin-bottom: -32px;
}

.dropdown input:where(:not(:checked)) ~ .list {
  opacity: 0;
  transform: translateY(3rem);
  margin-top: -100%;
  user-select: none;
  height: 0px;
  max-height: 0px;
  min-height: 0px;
  pointer-events: none;
  transition: all 500ms ease-out;
}

.trigger {
  cursor: pointer;
  list-style: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  user-select: none;
  font-weight: 600;
  color: inherit;
  width: 100%;
  display: flex;
  align-items: center;
  flex-flow: row;
  gap: 1rem;
  padding: 1rem;
  height: max-content;
  position: relative;
  z-index: 99;
  border-radius: inherit;
  background-color: white;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}

.dropdown input:where(:checked) + .trigger {
  margin-bottom: 1rem;
}

.dropdown input:where(:checked) + .trigger:before {
  rotate: 90deg;
  transition-delay: 0ms;
}

.dropdown input:where(:checked) + .trigger::after {
  content: "Cerrar selección...";
}

.trigger:before,
.trigger::after {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
}

.trigger:before {
  content: "›";
  rotate: -90deg;
  width: 17px;
  height: 17px;
  color: #262626;
  border-radius: 2px;
  font-size: 26px;
  transition: all 350ms ease;
  transition-delay: 85ms;
}

.trigger::after {
  content: "Seleccione un banco...";
}

.list {
  height: 100%;
  max-height: 20rem;
  width: calc(100% - calc(var(--w-scrollbar) / 2));
  display: grid;
  grid-auto-flow: row;
  overflow: hidden auto;
  gap: 1rem;
  padding: 0 1rem;
  margin-right: -8px;
  --w-scrollbar: 8px;
}

.listitem {
  height: 100%;
  width: calc(100% + calc(calc(var(--w-scrollbar) / 2) + var(--w-scrollbar)));
  list-style: none;
}

.button {

  padding: 1rem;
  border-radius: 8px;
  font-size: 15px;
  font-weight: bold;
  text-align: justify;
  width: 90%;
  border: 1px solid #c1c2c5;
  background-color: white;
  cursor: pointer;
  display: flex;
  gap: 10px;
  align-items: center;
  transition: background-color 0.3s;

}

.button:hover {
  background-color: #f0f0f0; /* Cambiar el color de fondo al pasar el mouse */
}

.webkit-scrollbar::-webkit-scrollbar {
  width: var(--w-scrollbar);
  height: var(--w-scrollbar);
  border-radius: 9999px;
}

.webkit-scrollbar::-webkit-scrollbar-track {
  background: #0000;
}

.webkit-scrollbar::-webkit-scrollbar-thumb {
  background: #0000;
  border-radius: 9999px;
}

.webkit-scrollbar:hover::-webkit-scrollbar-thumb {
  background: #c1c2c5;
}
/*----------------------------*/
</style>