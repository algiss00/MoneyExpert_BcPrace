import Vuex from 'vuex'
import Vue from 'vue'

Vue.use(Vuex)

const store = new Vuex.Store({
    state: {
        user: null,
        snackbar: false,
        notificationBudget: [{
            id: 10,
            typeNotification: "BUDGET_AMOUNT",
            budget: 34
        }],
        notificationDebt: []
    },
    mutations: {
        setUser(state, user) {
            state.user = user
        },
        setSnackbar(state, status) {
            state.snackbar = status
        },
        setNotificationBudget(state, status) {
            state.notificationBudget = status
        }
    }
})

export default store