<template>
  <div id="app">
    <Home/>
  </div>
</template>

<script>
import Home from "@/pages/Home";
export default {
  name: 'App',
  components: {Home},
  data() {
    return {
      msg: 'hello,world'
    }
  },
}
</script>

<style>
#app {
  text-align: center;
  margin-top: 60px;
}
</style>
