<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-toolbar color="#e7f6ff">
                            <v-toolbar-title>Přidat rozpočet</v-toolbar-title>
                        </v-toolbar>
                        <v-card-text>
                            <v-form
                                    ref="form"
                                    v-model="valid"
                                    lazy-validations>
                                <v-text-field
                                        id="amount"
                                        label="částka"
                                        v-model="amount"
                                        :rules="balanceRules"
                                        hide-details="auto"
                                />
                                <v-text-field
                                        id="name"
                                        label="název"
                                        v-model="name"
                                        :rules="nameRules"
                                        hide-details="auto"
                                />
                                <v-text-field
                                        id="percentNotify"
                                        label="procento upozornění"
                                        v-model="percentNotify"
                                        :rules="percentRules"
                                        hide-details="auto"
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
                            <v-btn color="#e7f6ff" @click="toBudgets" class="m2-position">Zpět</v-btn>
                        </v-card-actions>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn @click="addBudget($event)" :loading="loading" :disabled="!valid" color="#e7f6ff"
                                   class="m3-position">
                                Přidat
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import {addBudget, getAllUsersCategories, getBankAccById, getCategoryByName} from "../../api";

    export default {
        name: 'AddBudget',
        data: () => ({
            categories: [],
            category: "",
            bankAcc: "",
            name: "",
            percentNotify: "",
            amount: "",
            nameRules: [
                v => String(v).trim().length > 0 || 'povinný'
            ],
            balanceRules: [
                v => !Number.isNaN(Number(v)) || 'musí být číslo',
                v => String(v).trim().length > 0 || 'povinný',
                v => Number(v) > 0 || 'musí být > 0'
            ],
            percentRules: [
                v => Number.isInteger(Number(v)) || 'musí být celé číslo',
                v => String(v).trim().length > 0 || 'povinný',
                v => Number(v) > 0 || 'musí být > 0',
                v => Number(v) <= 100 || 'musí být <= 100'
            ],
            valid: true,
            loading: false
        }),
        methods: {
            async addBudget(event) {
                if (!this.$refs.form.validate()) {
                    event.preventDefault()
                    return
                }

                this.loading = true

                let category = await getCategoryByName(this.category.name)
                if (category == null) {
                    this.loading = false
                    this.$store.commit("setSnackbarText", "Server error!")
                    this.$store.commit("setSnackbarError", true)
                    return
                }

                const jsonBudget = JSON.stringify({
                    amount: this.amount,
                    name: this.name,
                    percentNotify: this.percentNotify
                });

                let result = await addBudget(jsonBudget, this.$route.params.bankId, category.id)

                if (result == null || result.status !== 201) {
                    this.$store.commit("setSnackbarText", "Invalid data! Možná rozpočet pro tuhle kategorii již existuje.")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.status === 201) {
                    this.$store.commit("setSnackbar", true)
                    await this.$router.push('/budgets/' + this.$route.params.bankId).catch(() => {
                    })
                }
                this.loading = false
            },
            toBudgets() {
                this.$router.push('/budgets/' + this.$route.params.bankId).catch(() => {
                })
            }
        },
        async mounted() {
            // if user not authenticated route to login page
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
                this.$store.commit("setSnackbarText", "Server error!")
                this.$store.commit("setSnackbarError", true)
                return
            }

            this.bankAcc = bankAcc.name
            this.categories = categories
            this.$store.commit("setLoading", false)
        }
    }
</script>