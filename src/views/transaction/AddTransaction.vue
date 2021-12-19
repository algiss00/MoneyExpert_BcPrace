<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-toolbar color="#e7f6ff">
                            <v-toolbar-title>Přidat transakci</v-toolbar-title>
                        </v-toolbar>
                        <v-card-text>
                            <v-form
                                    ref="form"
                                    v-model="valid"
                                    lazy-validation>
                                <v-menu
                                        ref="menu"
                                        v-model="menu"
                                        :close-on-content-click="false"
                                        :return-value.sync="date"
                                        transition="scale-transition"
                                        offset-y
                                        min-width="auto"
                                >
                                    <template v-slot:activator="{ on, attrs }">
                                        <v-text-field
                                                v-model="dateRangeText"
                                                label="Date"
                                                :rules="rules"
                                                prepend-icon="mdi-calendar"
                                                readonly
                                                v-bind="attrs"
                                                v-on="on"
                                        />
                                    </template>
                                    <v-date-picker
                                            v-model="date"
                                            no-title
                                            scrollable
                                    >
                                        <v-spacer/>
                                        <v-btn
                                                text
                                                color="primary"
                                                @click="menu = false"
                                        >
                                            Cancel
                                        </v-btn>
                                        <v-btn
                                                text
                                                color="primary"
                                                @click="$refs.menu.save(date)"
                                        >
                                            OK
                                        </v-btn>
                                    </v-date-picker>
                                </v-menu>
                                <v-text-field
                                        id="amount"
                                        label="částka"
                                        v-model="amount"
                                        :rules="balanceRules"
                                        hide-details="auto"
                                />
                                <v-text-field
                                        id="jottings"
                                        label="poznámky"
                                        v-model="jottings"
                                        hide-details="auto"
                                />
                                <v-select
                                        id="type"
                                        :items="types"
                                        v-model="type"
                                        :rules="[v => !!v || 'povinný']"
                                        label="typ"
                                />
                                <v-select
                                        id="category"
                                        :items="categories"
                                        v-model="category"
                                        label="Kategorie"
                                        item-text="name"
                                        :rules="[v => !!v|| 'povinný']"
                                        item-value="id"
                                        persistent-hint
                                        return-object
                                />
                                <v-text-field
                                        id="bankAcc"
                                        label="účet"
                                        v-model="bankAcc"
                                        hide-details="auto"
                                        disabled
                                />
                            </v-form>
                        </v-card-text>
                        <v-card-actions>
                            <v-btn color="#e7f6ff" @click="toTransactions" class="m2-position">Zpět</v-btn>
                        </v-card-actions>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn @click="addTransaction($event)" :loading="loading" color="#e7f6ff" :disabled="!valid"
                                   class="m3-position">Přidat
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import {
        getBankAccById,
        getCategoryByName,
        addTransaction,
        getAllUsersCategories,
        getAllNotificationBudgets
    } from "../../api";

    export default {
        name: 'AddTransaction',
        data: () => ({
            types: ['EXPENSE', 'INCOME'],
            categories: [],
            category: "",
            type: "",
            amount: "",
            jottings: "",
            bankAcc: "",
            date: (new Date(Date.now() - (new Date()).getTimezoneOffset() * 60000)).toISOString().substring(0, 19),
            menu: false,
            rules: [
                v => String(v).trim().length > 0 || 'povinný'
            ],
            balanceRules: [
                v => !Number.isNaN(Number(v)) || 'musí být číslo',
                v => String(v).trim().length > 0 || 'povinný',
                v => Number(v) > 0 || 'musí být > 0'
            ],
            valid: true,
            loading: false
        }),
        computed: {
            dateRangeText() {
                return this.date.split('T').join(' ')
            },
        },
        methods: {
            async addTransaction(event) {
                if (!this.$refs.form.validate()) {
                    event.preventDefault()
                    return
                }
                this.loading = true

                let category = await getCategoryByName(this.category.name)
                if (category == null) {
                    this.loading = false
                    this.$store.commit("setSnackbarText", "Server error! Invalid category.")
                    this.$store.commit("setSnackbarError", true)
                    return
                }

                const jsonTransaction = JSON.stringify({
                    amount: this.amount,
                    jottings: this.jottings,
                    typeTransaction: this.type,
                    date: this.date
                });

                let result = await addTransaction(jsonTransaction, this.$route.params.bankId, category.id)

                if (result == null || result.status !== 201) {
                    this.$store.commit("setSnackbarText", "Server error! Cant add transaction.")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.status === 201) {
                    this.$store.commit("setSnackbar", true)
                    let budgetsNotification = await getAllNotificationBudgets(this.$route.params.bankId)
                    this.$store.commit("setNotificationBudget", budgetsNotification)
                    await this.$router.push('/transactions/' + this.$route.params.bankId).catch(() => {
                    })
                }
                this.loading = false
            },
            toTransactions() {
                this.$router.push('/transactions/' + this.$route.params.bankId).catch(() => {
                })
            }
        },
        async mounted() {
            // if user not authenticated user route to login page
            if (!this.$store.state.user) {
                return await this.$router.push("/").catch(() => {
                })
            }
            this.$store.commit("setLoading", true)
            let categories = await getAllUsersCategories()
            if (categories == null) {
                this.$store.commit("setLoading", false)
                alert("Server error!")
                this.$store.commit("setUser", null)
                return await this.$router.push("/").catch(() => {
                })
            }

            let bankAcc = await getBankAccById(this.$route.params.bankId)
            if (bankAcc == null) {
                this.$store.commit("setLoading", false)
                this.$store.commit("setSnackbarText", "Server error! Cant get bankAcc.")
                this.$store.commit("setSnackbarError", true)
                return
            }
            this.bankAcc = bankAcc.name
            this.categories = categories
            this.$store.commit("setLoading", false)
        }
    }
</script>