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
import DetailTransaction from '../views/transaction/DetailTransaction'
import AddTransaction from '../views/transaction/AddTransaction.vue'

Vue.use(VueRouter)

const routes = [
    {
        path: '/banks',
        name: 'Banks',
        component: Banks
    },
    {
        path: '/banks/addBankAcc',
        name: 'AddBankAcc',
        component: AddBankAcc
    },
    {
        path: '/banks/detailBankAcc/:bankId',
        name: 'DetailBankAcc',
        component: DetailBankAcc
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
        path: '/budgets/:bankId',
        name: 'Budgets',
        component: Budgets
    },
    {
        path: '/categories/:bankId',
        name: 'Categories',
        component: Categories
    },
    {
        path: '/dashboard/:bankId',
        name: 'Dashboard',
        component: Dashboard
    },
    {
        path: '/debts/:bankId',
        name: 'Debts',
        component: Debts
    },
    {
        path: '/statistic/:bankId',
        name: 'Statistic',
        component: Statistic
    },
    {
        path: '/transactions/:bankId',
        name: 'Transactions',
        component: Transactions
    },
    {
        path: '/transactions/:bankId/detail/:transId',
        name: 'DetailTransaction',
        component: DetailTransaction
    },
    {
        path: '/transactions/:bankId/addTransaction/',
        name: 'AddTransaction',
        component: AddTransaction
    },
]


const router = new VueRouter({
    routes,
    mode: 'history'
})

export default router
