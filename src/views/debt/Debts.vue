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
                <div class="font-weight-medium black--text m-left"
                     v-if="debts.length === 0">
                    No debts :)
                </div>
                <v-data-table
                        dark
                        :headers="headers"
                        :items="debts"
                        item-key="id"
                        class="elevation-1"
                        :search="search"
                        :custom-filter="filterOnlyCapsText"
                        @click:row="toDetailDebt"
                        :item-class="statusColor"
                >
                    <template v-slot:item.deadline="{ item }">
                        <span>{{ new Date(item.deadline).toDateString() }}</span>
                    </template>
                    <template v-slot:top>
                        <v-text-field
                                v-model="search"
                                label="Search (UPPER CASE ONLY)"
                                class="mx-4"
                        />
                    </template>
                </v-data-table>
            </v-card>
        </v-container>
    </v-app>
</template>

<script>
    import {getAllDebtsFromBankAcc, getAllNotificationDebts} from "../../api";

    export default {
        name: "Debts",
        data: () => {
            return {
                search: '',
                debts: [],
                headers: [
                    {text: 'Název', value: 'name'},
                    {text: 'Deadline', value: 'deadline'},
                    {text: 'Částka', value: 'amount'},
                ]
            }
        },
        methods: {
            filterOnlyCapsText(value, search) {
                return value != null &&
                    search != null &&
                    typeof value === 'string' &&
                    value.toString().toLocaleUpperCase().indexOf(search) !== -1
            },
            statusColor(item) {
                let notifications = this.$store.state.notificationDebt
                if (this.$store.state.notificationDebt.length > 0) {
                    for (let i = 0; i < notifications.length; i++) {
                        if (notifications[i].debt.id === item.id) {
                            return 'alertColor'
                        }
                    }
                }
                return 'alertColorBlack'
            },
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
            let debtsNotification = await getAllNotificationDebts(this.$route.params.bankId)
            this.$store.commit("setNotificationDebt", debtsNotification)
        }
    }
</script>

<style>
    .alertColor {
        background-color: #DC143C
    }

    .alertColorBlack {
        background-color: black
    }
</style>
