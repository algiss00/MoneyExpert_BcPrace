<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-toolbar color="#e7f6ff">
                            <v-toolbar-title>Detail transakce</v-toolbar-title>
                        </v-toolbar>
                        <v-card-text>
                            <v-form>
                                <v-text-field
                                        id="date"
                                        label="datum"
                                        v-model="date"
                                        hide-details="auto"
                                        readonly
                                />
                                <v-text-field
                                        id="amount"
                                        label="částka"
                                        v-model="amount"
                                        hide-details="auto"
                                        readonly
                                />
                                <v-text-field
                                        id="jottings"
                                        label="poznámky"
                                        v-model="jottings"
                                        hide-details="auto"
                                        readonly
                                />
                                <v-btn color="#e7f6ff" id="editBtn">Změnit datum, částku, poznámky</v-btn>
                                <v-select
                                        id="type"
                                        :items="types"
                                        v-model="type"
                                        label="typ"
                                        readonly
                                />
                                <v-btn color="#e7f6ff" id="editBtnType">Změnit typ</v-btn>
                                <v-text-field
                                        id="category"
                                        v-model="category"
                                        label="kategorie"
                                        readonly
                                />
                                <v-btn color="#e7f6ff" id="editBtnCategory">Změnit kategorii</v-btn>
                                <v-text-field
                                        id="bankAcc"
                                        label="účet"
                                        v-model="bankAcc"
                                        hide-details="auto"
                                        readonly
                                />
                            </v-form>
                        </v-card-text>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn color="#e7f6ff" @click="toTransactions">Zpět</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import {getTransactionById, getBankAccById} from "../../api";

    export default {
        name: 'DetailTransaction',
        data: () => ({
            types: ['EXPENSE', 'INCOME'],
            date: "",
            category: "",
            type: "",
            amount: "",
            jottings: "",
            bankAcc: ""
        }),
        methods: {
            toTransactions() {
                this.$router.push('/transactions/' + this.$route.params.bankId)
            },
        },
        async mounted() {
            let bankAcc = await getBankAccById(this.$route.params.bankId)
            let result = await getTransactionById(this.$route.params.transId)
            this.date = new Date(result.date).toDateString()
            this.category = result.category.name
            this.type = result.typeTransaction
            this.amount = result.amount
            this.jottings = result.jottings
            this.bankAcc = bankAcc.name
            console.log(result)
        }
    }
</script>

<style>
    #editBtn {
        margin: 15px;
        right: 20px;
    }

    #editBtnType {
        margin: 15px;
        right: 20px;
    }

    #editBtnCategory {
        margin: 15px;
        right: 20px;
    }
</style>