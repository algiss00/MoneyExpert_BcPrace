<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-toolbar color="#e7f6ff">
                            <v-toolbar-title>Detail účtu</v-toolbar-title>
                        </v-toolbar>
                        <v-card-text>
                            <v-form>
                                <v-text-field
                                        id="nameBankAcc"
                                        label="název"
                                        v-model="name"
                                        hide-details="auto"
                                        readonly
                                />
                                <v-select
                                        id="currencyBankAcc"
                                        :items="items"
                                        v-model="currency"
                                        label="měna"
                                        readonly
                                />
                                <v-text-field
                                        id="balanceBankAcc"
                                        label="balance"
                                        v-model="balance"
                                        hide-details="auto"
                                        readonly
                                />
                            </v-form>
                        </v-card-text>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn color="#e7f6ff" to="/banks">Zpět</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import {getBankAccById} from "../api";

    export default {
        name: 'DetailBankAcc',
        data: () => ({
            name: "",
            currency: "",
            balance: "",
            items: ['CZK', 'EUR']
        }),
        methods: {},
        async mounted() {
            let result = await getBankAccById()
            this.name = result.name
            this.currency = result.currency
            this.balance = result.balance
            console.log(result)
        }
    }
</script>