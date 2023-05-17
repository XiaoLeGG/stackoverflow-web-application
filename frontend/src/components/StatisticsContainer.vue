<template>
  <div class="container-main">
    <name-label>
      <slot name="heading"></slot>
    </name-label>
    <container>
      <slot name="content"></slot>
    </container>
  </div>
</template>

<style scoped>
  .container-main {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    flex-basis: min-content;
    -webkit-filter: drop-shadow(10px 10px 10px rgba(0,0,0,.2));
  }
  name-label {
    background-color: var(--color-background-soft-hover);
    caret-color: transparent;
    flex-basis: 40px;
    width: max-content;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0.1em 1em 0.1em 1em;
    border-top: 3px solid var(--color-border);
    border-left: 3px solid var(--color-border);
    border-right: 3px solid var(--color-border);
    font-size: 1.4rem;
    font-weight: 500;
    color: var(--color-heading);
  }

  container {
    background-color: transparent;
    border: 3px solid var(--color-border);
    width: 600px;
    flex-basis: 500px;
    padding: 1.3em 2em 1.3em 2em;
    background-color: var(--color-background-soft-hover);
  }
</style>