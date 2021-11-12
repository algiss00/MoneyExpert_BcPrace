<template>
    <v-app id="inspire">
        <v-container fluid>
            <v-card color="#e7f6ff">
                <v-card-title class="mx-auto">
                    Transakce
                </v-card-title>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn class="m4-position" @click="toAddTransaction">Přidat transakci</v-btn>
                </v-card-actions>
                <v-simple-table dark>
                    <template v-slot:default>
                        <thead>
                        <tr>
                            <th class="text-left">
                                datum
                            </th>
                            <th class="text-left">
                                Kategorie
                            </th>
                            <th class="text-left">
                                Typ
                            </th>
                            <th class="text-left">
                                Částka
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr
                                v-for="item in transactions"
                                :key="item.id"
                                @click="toDetailTransaction(item)"
                        >
                            <td>{{ new Date(item.date).toDateString() }}</td>
                            <td>{{ item.category.name }}</td>
                            <td>{{ item.typeTransaction }}</td>
                            <td>{{ item.amount }}</td>
                        </tr>
                        </tbody>
                    </template>
                </v-simple-table>
            </v-card>
        </v-container>
    </v-app>
</template>

<script>
    import {getAllTransactions} from "../../api";

    export default {
        name: "Transactions",
        data: () => {
            return {
                transactions: []
            }
        },
        methods: {
            toDetailTransaction(item) {
                this.$router.push('/transactions/' + this.$route.params.bankId + '/detail/' + item.id)
            },
            toAddTransaction() {
                this.$router.push('/transactions/' + this.$route.params.bankId + '/addTransaction/')
            }
        },
        async mounted() {
            this.transactions = await getAllTransactions(this.$route.params.bankId)
            console.log(this.transactions)
        }
    }
</script>