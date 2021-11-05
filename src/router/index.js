import Vue from 'vue'
import VueRouter from 'vue-router'

import Banks from '../views/Banks.vue'

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'Banks',
        component: Banks
    }
]

const router = new VueRouter({
    routes
})

export default router
