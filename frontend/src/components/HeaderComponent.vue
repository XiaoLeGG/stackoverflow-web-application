
<style scoped>

header {
  background-color: var(--color-background-soft);
  padding: 10px 20px 10px 20px;
  display: flex;
  justify-content: start;
  align-items: center;
  flex-direction: row;
  caret-color: transparent;
  user-select: none;
  top: 0px;
  position: fixed;
  min-width: 100%;
  width: max-content;
  z-index: 2002;
}

header > items:nth-child(3) {
  flex-grow: 1;
}

items {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

logo-header {
  display: flex;
  margin-right: 40px;
}

logo-header > a > img {
  width: 62px;
  height: 62px;
}

logo-text-block {
  display: flex;
  align-items: baseline;
}

logo-text {
  font-size: 2.6rem;
  font-weight: 500;
  color: var(--color-heading);
}

logo-text-amp {
  font-size: 1.6rem;
  font-weight: 400;
  color: var(--color-heading-sub);
  margin-left: 10px;
  font-style: italic;
}

update-time-box {
  height: 100%;
  width: 200px;
  border: solid;
  border-width: 2px;
  border-color: var(--color-border);
  background-color: transparent;
  padding: 10px 10px 10px 10px;
  text-align: center;
  border-radius: 10px;
  transition: 0.3s;
}

link-icon {
  padding: 7px 7px 7px 7px;
  border-radius: 10px;
  transition: 0.2s;
}

link-icon:hover {
  background-color: rgb(255, 255, 255, 0.2);
}


update-time-box:hover {
  background-color: var(--color-background-soft-hover);
  color: var(--color-text-hover);
  border-color: var(--color-border-hover);
}

</style>

<template>
  <header ref="header">
    <logo-header>
      <a href="https://stackoverflow.com/">
        <img alt="sof logo" src="../assets/logo.svg"/>
      </a>
      <logo-text-block>
        <logo-text>Stack Overflow</logo-text>
        <logo-text-amp>web application</logo-text-amp>
      </logo-text-block>
    </logo-header>
    <items>
      <update-time-box>
        <h3>Last updated at: 123</h3>
      </update-time-box>
    </items>
    <items>
      <link-icon>
        <a href="https://github.com/XiaoLeGG/stackoverflow-web-application">
          <img alt="github logo" src="../assets/github-mark-white.svg" width="31" height="31"/>
        </a>
      </link-icon>
    </items>
  </header>
</template>

<script>
export default {
  name: "HeaderComponent",
  data() {
    return {
      height: 40
    }
  },
  mounted() {
    this.height = this.$refs.header.clientHeight;
  }
}
</script>

<style scoped>

</style>