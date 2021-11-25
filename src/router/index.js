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
import AddBudget from '../views/budget/AddBudget.vue'
import DetailBudget from '../views/budget/DetailBudget.vue'
import AddDebt from '../views/debt/AddDebt.vue'
import DetailDebt from '../views/debt/DetailDebt.vue'

Vue.use(VueRouter)

/**
 * this js file contains routes for web application
 */

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
        path: '/banks/detail/:bankId',
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
        path: '/budgets/:bankId/addBudget',
        name: 'AddBudget',
        component: AddBudget
    },
    {
        path: '/budgets/:bankId/detail/:budgetId',
        name: 'DetailBudget',
        component: DetailBudget
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
        path: '/debts/:bankId/addDebt',
        name: 'AddDebt',
        component: AddDebt
    },
    {
        path: '/debts/:bankId/detail/:debtId',
        name: 'DetailDebt',
        component: DetailDebt
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
    {
        path: '*',
        redirect: '/'
    }
]


const router = new VueRouter({
    routes,
    mode: 'history'
})

export default router
