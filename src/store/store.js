import Vuex from 'vuex'
import Vue from 'vue'

Vue.use(Vuex)

const store = new Vuex.Store({
    state: {
        user: null,
        snackbar: false,
        notificationDebt: [],
        notificationBudget: [],
    },
    mutations: {
        setUser(state, user) {
            state.user = user
        },
        setSnackbar(state, status) {
            state.snackbar = status
        },
        setNotificationDebt(state, status) {
            state.notificationDebt = status
        },
        setNotificationBudget(state, status) {
            state.notificationBudget = status
        },
    }
})

export default store