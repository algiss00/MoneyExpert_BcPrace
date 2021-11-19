<template>
    <v-app id="inspire">
        <v-container fluid>
            <v-card color="#e7f6ff">
                <v-card-title class="mx-auto">
                    Závazky
                    <v-fab-transition>
                        <v-btn
                                class="m4-position"
                                dark
                                small
                                color="green"
                                fab
                                @click="toAddDebt"
                        >
                            <v-icon>mdi-plus</v-icon>
                        </v-btn>
                    </v-fab-transition>
                </v-card-title>
                <v-simple-table dark>
                    <template v-slot:default>
                        <thead>
                        <tr>
                            <th class="text-left">
                                Název
                            </th>
                            <th class="text-left">
                                Deadline
                            </th>
                            <th class="text-left">
                                Částka
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr
                                v-for="item in debts"
                                :key="item.id"
                                @click="toDetailDebt(item)"
                        >
                            <td>{{ item.name }}</td>
                            <td>{{ new Date(item.deadline).toDateString() }}</td>
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
    import {getAllDebtsFromBankAcc} from "../../api";

    export default {
        name: "Debts",
        data: () => {
            return {
                debts: [],
                date: new Date().toISOString().substr(0, 7),
            }
        },
        methods: {
            toAddDebt() {
                this.$router.push('/debts/' + this.$route.params.bankId + '/addDebt/')
            },
            toDetailDebt(item) {
                this.$router.push('/debts/' + this.$route.params.bankId + '/detail/' + item.id)
            }
        },
        async mounted() {
            if (!this.$store.state.user) {
                return await this.$router.push("/")
            }
            this.debts = await getAllDebtsFromBankAcc(this.$route.params.bankId)
            if (this.debts == null) {
                alert("Invalid bankAcc id")
                return
            }
        }
    }
</script>
