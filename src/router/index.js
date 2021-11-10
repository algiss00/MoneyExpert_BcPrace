import Vue from 'vue'
import VueRouter from 'vue-router'

import Banks from '../views/Banks.vue'
import Login from '../views/Login.vue'
import SignUp from '../views/SignUp.vue'
import AddBankAcc from '../views/AddBankAcc.vue'
import DetailBankAcc from '../views/DetailBankAcc.vue'

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
    },
    {
        path: '/addBankAcc',
        name: 'AddBankAcc',
        component: AddBankAcc
    },
    {
        path: '/detailBankAcc/:id',
        name: 'DetailBankAcc',
        component: DetailBankAcc
    }
]

const router = new VueRouter({
    routes
})

export default router
