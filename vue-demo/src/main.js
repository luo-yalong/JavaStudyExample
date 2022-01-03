import Vue from 'vue'
import App from './App.vue'
//按需引入element-ui
import {Button, Row, Input, Col} from 'element-ui';
Vue.config.productionTip = false

//使用element-ui组件
Vue.use(Button)
Vue.use(Row)
Vue.use(Input)
Vue.use(Col)

new Vue({
  render: h => h(App),
}).$mount('#app')
