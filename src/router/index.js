import Vue from 'vue'
import VueRouter from 'vue-router'

import Banks from '../views/bankAcc/Banks.vue'
import Login from '../views/Login.vue'
import SignUp from '../views/SignUp.vue'
import AddBankAcc from '../views/bankAcc/AddBankAcc.vue'
import DetailBankAcc from '../views/bankAcc/DetailBankAcc.vue'
import Budgets from '../views/budget/Budgets.vue'
import Categories from '../views/category/Categories.vue'
import Dashboard from '../views/dashboard/Dashboard.vue'
import Debts from '../views/debt/Debts.vue'
import Transactions from '../views/transaction/Transactions.vue'
import Statistic from '../views/statistic/Statistic.vue'
import Profile from '../views/user/Profile.vue'


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
        path: '/detailBankAcc/:bankAccId',
        name: 'DetailBankAcc',
        component: DetailBankAcc
    },
    {
        path: '/budgets/',
        name: 'Budgets',
        component: Budgets
    },    {
        path: '/categories',
        name: 'Categories',
        component: Categories
    },    {
        path: '/dashboard/',
        name: 'Dashboard',
        component: Dashboard
    },    {
        path: '/debts/',
        name: 'Debts',
        component: Debts
    },    {
        path: '/statistic/',
        name: 'Statistic',
        component: Statistic
    },    {
        path: '/transactions/',
        name: 'Transactions',
        component: Transactions
    },    {
        path: '/profile',
        name: 'Profile',
        component: Profile
    },
]


const router = new VueRouter({
    routes,
    mode: 'history'
})

export default router
