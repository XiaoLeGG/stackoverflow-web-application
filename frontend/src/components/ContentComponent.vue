<template>
  <div class="title">
    <h1>Statistics Content</h1>
  </div>
  <div class="content-main">
    <StatisticsContainer>
      <template v-slot:heading>
        <p>test</p>
      </template>
      <template v-slot:content>
        <TestChart/>
      </template>
    </StatisticsContainer>

  </div>
</template>

<script>
import StatisticsContainer from "@/components/StatisticsContainer.vue";
import TestChart from "@/components/TestChart.vue";
export default {
  name: "ContentComponent",
  components: {TestChart, StatisticsContainer}
}
</script>

<style scoped>
  .title {
    margin-top: 50px;
    caret-color: transparent;
    display: flex;
    justify-content: center;
  }

  .title > h1 {
    font-size: 2.6rem;
    font-weight: 500;
    color: var(--color-heading);
  }
  .content-main {
    margin: 100px 100px 100px 100px;
    display: flex;
    justify-content: start;
    flex-direction: row;
    align-items: flex-start;
  }
</style>