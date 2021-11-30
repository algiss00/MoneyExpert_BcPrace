<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-toolbar color="#e7f6ff">
                            <v-toolbar-title>Přidat bankovní účet</v-toolbar-title>
                        </v-toolbar>
                        <v-card-text>
                            <v-form
                                    ref="form"
                                    v-model="valid"
                                    lazy-validations>
                                <v-text-field
                                        id="nameBankAcc"
                                        label="název"
                                        v-model="name"
                                        :rules="nameRules"
                                        hide-details="auto"
                                />
                                <v-select
                                        id="currencyBankAcc"
                                        :items="items"
                                        v-model="currency"
                                        :rules="[v => !!v || 'Item is required']"
                                        label="měna"
                                />
                                <v-text-field
                                        id="balanceBankAcc"
                                        label="zůstatek"
                                        v-model="balance"
                                        :rules="balanceRules"
                                        hide-details="auto"
                                />
                            </v-form>
                        </v-card-text>
                        <v-card-actions>
                            <v-btn color="#e7f6ff" to="/banks" class="m2-position">Zpět</v-btn>
                        </v-card-actions>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn @click="addBankAcc($event)" :loading="loading" :disabled="!valid" color="#e7f6ff"
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
    import {addBankAccount} from "../../api";

    export default {
        name: 'AddBankAcc',
        data: () => ({
            name: "",
            currency: "",
            balance: "",
            items: ['CZK', 'EUR'],
            nameRules: [
                v => String(v).trim().length > 0 || 'required'
            ],
            balanceRules: [
                v => !Number.isNaN(Number(v)) || 'must be number',
                v => String(v).trim().length > 0 || 'required'
            ],
            valid: true,
            loading: false
        }),
        methods: {
            /**
             * add bank Acc
             * @param event
             * @returns {Promise<void>}
             */
            async addBankAcc(event) {
                if (!this.$refs.form.validate()) {
                    event.preventDefault()
                    return
                }
                const jsonBankAcc = JSON.stringify({
                    name: this.name,
                    currency: this.currency,
                    balance: this.balance
                });

                this.loading = true
                let result = await addBankAccount(jsonBankAcc)
                if (result == null || result.status !== 201) {
                    this.$store.commit("setSnackbarText", "Server error!")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.data.name == this.name && result.data.balance == this.balance && result.data.currency == this.currency) {
                    await this.$router.push('/banks').catch(() => {
                    })
                    this.$store.commit("setSnackbar", true)
                }
                this.loading = false
            }
        },
        async mounted() {
            /**
             * if not authenticated user route to login page
             */
            if (!this.$store.state.user) {
                return await this.$router.push("/").catch(() => {
                })
            }
        }
    }
</script>