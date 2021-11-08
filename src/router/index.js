import Vue from 'vue'
import VueRouter from 'vue-router'

import Banks from '../views/Banks.vue'
import Login from '../views/Login.vue'
import SignUp from '../views/SignUp.vue'

Vue.use(VueRouter)

const routes = [
    {
        path: '/banks',
        name: 'Banks',
        component: Banks
    },
    {
        path: '/',
        name: 'Login',
        component: Login
    },
    {
        path: '/signup',
        name: 'SignUp',
        component: SignUp
    }
]

const router = new VueRouter({
    routes
})

export default router
