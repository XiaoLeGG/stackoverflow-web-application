<!--<script setup>-->
<!--import HelloWorld from './components/HelloWorld.vue'-->
<!--import TheWelcome from './components/TheWelcome.vue'-->
<!--</script>-->

<!--<template>-->
<!--  <header>-->
<!--    <img alt="Vue logo" class="logo" src="./assets/logo.svg" width="125" height="125" />-->

<!--    <div class="wrapper">-->
<!--      <HelloWorld msg="You did it!" />-->
<!--    </div>-->
<!--  </header>-->

<!--  <main>-->
<!--    <TheWelcome />-->
<!--  </main>-->
<!--</template>-->

<!--<style scoped>-->
<!--header {-->
<!--  line-height: 1.5;-->
<!--}-->

<!--.logo {-->
<!--  display: block;-->
<!--  margin: 0 auto 2rem;-->
<!--}-->

<!--@media (min-width: 1024px) {-->
<!--  header {-->
<!--    display: flex;-->
<!--    place-items: center;-->
<!--    padding-right: calc(var(&#45;&#45;section-gap) / 2);-->
<!--  }-->

<!--  .logo {-->
<!--    margin: 0 2rem 0 0;-->
<!--  }-->

<!--  header .wrapper {-->
<!--    display: flex;-->
<!--    place-items: flex-start;-->
<!--    flex-wrap: wrap;-->
<!--  }-->
<!--}-->
<!--</style>-->

<script>
  import HeaderComponent from "@/components/HeaderComponent.vue";
  import IntroductionComponent from "@/components/IntroductionComponent.vue";
  import ContentComponent from "@/components/ContentComponent.vue";


  export default {
    name: 'App',
    components: {ContentComponent, IntroductionComponent, HeaderComponent},
    data() {
      return {
        headerMargin: {
          marginTop: '0px'
        }
      }
    },
    mounted() {
      this.headerMargin.marginTop = this.$refs.header.$data.height + 'px'
    }
  }
</script>

<template>
  <div>
    <HeaderComponent ref="header"/>
  </div>
  <div :style="headerMargin">
    <IntroductionComponent/>
  </div>
  <div>
    <ContentComponent/>
  </div>
</template>