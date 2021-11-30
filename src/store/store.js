import Vuex from 'vuex'
import Vue from 'vue'

Vue.use(Vuex)

/**
 * this js file contains shared data
 */

const store = new Vuex.Store({
    state: {
        user: null,
        snackbar: false,
        snackbarError: false,
        notificationDebt: [],
        notificationBudget: [],
        loading: false,
        textSnack: ""
    },
    mutations: {
        setUser(state, user) {
            state.user = user
        },
        setSnackbar(state, status) {
            state.snackbar = status
        },
        setSnackbarError(state, status) {
            state.snackbarError = status
        },
        setSnackbarText(state, status) {
            state.textSnack = status
        },
        setLoading(state, status) {
            state.loading = status
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