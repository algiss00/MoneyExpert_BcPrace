import Vue from 'vue'
import VueRouter from 'vue-router'

import Banks from '../views/Banks.vue'
import Login from '../views/Login.vue'
import SignUp from '../views/SignUp.vue'

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'Banks',
        component: Banks
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/signUp',
        name: 'SignUp',
        component: SignUp
    }
]

const router = new VueRouter({
    routes
})

export default router
