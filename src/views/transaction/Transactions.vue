<template>
    <v-app id="inspire">
        <v-container fluid>
            <v-card color="#e7f6ff">
                <v-card-title class="mx-auto">
                    Transakce
                    <v-fab-transition>
                        <v-btn
                                class="m4-position"
                                dark
                                small
                                color="green"
                                fab
                                @click="toAddTransaction"
                        >
                            <v-icon>mdi-plus</v-icon>
                        </v-btn>
                    </v-fab-transition>
                </v-card-title>
                <v-card-text>
                    <v-menu
                            ref="menu"
                            v-model="menu"
                            :close-on-content-click="false"
                            :return-value.sync="date"
                            transition="scale-transition"
                            offset-y
                            max-width="290px"
                            min-width="auto"
                    >
                        <template v-slot:activator="{ on, attrs }">
                            <v-text-field
                                    v-model="dateRangeText"
                                    label="Období"
                                    prepend-icon="mdi-calendar"
                                    readonly
                                    v-bind="attrs"
                                    v-on="on"
                            />
                        </template>
                        <v-date-picker
                                v-model="date"
                                range
                        >
                            <v-spacer></v-spacer>
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
                                    @click="$refs.menu.save(date), getTransactionsBetweenDate()"
                            >
                                OK
                            </v-btn>
                        </v-date-picker>
                    </v-menu>
                    <div class="d-md-inline-flex">
                        <v-select
                                id="type"
                                :items="types"
                                v-model="type"
                                label="Typ"
                                class="float-left"
                                style="width:150px; margin-right: 50px;"
                                v-on:change="getAllTransactionsByTypeAndCategoryMethod"
                        />
                        <v-select
                                id="category"
                                :items="categories"
                                v-model="category"
                                label="Kategorie"
                                item-text="name"
                                item-value="id"
                                style="width:200px;"
                                v-on:change="getAllTransactionsByTypeAndCategoryMethod"
                                persistent-hint
                                return-object
                        />
                    </div>
                </v-card-text>
                <div class="font-weight-medium black--text m-left"
                     v-if="transactions.length === 0">
                    No transactions :)
                </div>
                <v-simple-table dark v-if="transactions.length !== 0">
                    <template v-slot:default>
                        <thead>
                        <tr>
                            <th class="text-left">
                                Datum
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
    import {
        getAllTransactionsBetweenDate,
        getAllTransactionsByCategoryAndType,
        getAllTransactionsByCategory,
        getAllTransactionsByType,
        getAllUsersCategories, getAllNotificationBudgets
    } from "../../api";

    async function getAllTransactionsByTypeAndCategoryMethod(bankId, from, to, type, categoryId) {
        let result = await getAllTransactionsByCategoryAndType(bankId, from, to, type, categoryId)
        if (result == null) {
            alert("Invalid bankAcc id")
            return
        }
        return result
    }

    export default {
        name: "Transactions",
        data: () => {
            return {
                transactions: [],
                date: [new Date(new Date().getFullYear(), new Date().getMonth(), 2)
                    .toISOString().substr(0, 10),
                    new Date(new Date().getFullYear(), new Date().getMonth() + 1, 1)
                        .toISOString().substr(0, 10)],
                menu: false,
                types: ['All', 'EXPENSE', 'INCOME'],
                type: "All",
                categories: [],
                category: {
                    id: -1000,
                    name: "All"
                },
            }
        },
        computed: {
            dateRangeText() {
                return this.date.join(' ~ ')
            },
        },
        methods: {
            toDetailTransaction(item) {
                this.$router.push('/transactions/' + this.$route.params.bankId + '/detail/' + item.id).catch(() => {})
            },
            toAddTransaction() {
                this.$router.push('/transactions/' + this.$route.params.bankId + '/addTransaction/').catch(() => {})
            },
            async getTransactionsBetweenDate() {
                let from = this.date[0]
                let to = this.date[1]

                if (from === undefined || to === undefined) {
                    alert("Invalid date period! Must be start date and end date!")
                    return
                }
                this.type = "All"
                this.category = {
                    id: -1000,
                    name: "All"
                }

                if (from > to) {
                    alert("Invalid date period! Start date must be before end date!")
                    return
                }

                this.transactions = await getAllTransactionsBetweenDate(this.$route.params.bankId, from, to)
                if (this.transactions == null) {
                    alert("Invalid bankAcc id")
                    return
                }
            },
            async getAllTransactionsByTypeAndCategoryMethod() {
                if (this.date.length <= 1) {
                    alert("Invalid date period! Must be start date and end date period!")
                    return
                }
                let from = this.date[0]
                let to = this.date[1]
                if (from > to) {
                    alert("Invalid date period! From date is later then another!")
                    return
                }
                if (this.type === "All" && this.category.name === "All") {
                    this.transactions = await getAllTransactionsBetweenDate(this.$route.params.bankId, from, to)
                    if (this.transactions == null) {
                        alert("Invalid bankAcc id")
                        return
                    }
                } else if (this.type !== "All" && this.category.name !== "All") {
                    this.transactions = await getAllTransactionsByTypeAndCategoryMethod(this.$route.params.bankId, from,
                        to, this.type, this.category.id)
                } else if (this.type !== "All" && this.category.name === "All") {
                    this.transactions = await getAllTransactionsByType(this.$route.params.bankId, from, to, this.type)
                    if (this.transactions == null) {
                        alert("Invalid bankAcc id")
                        return
                    }
                } else if (this.type === "All" && this.category.name !== "All") {
                    this.transactions = await getAllTransactionsByCategory(this.$route.params.bankId, from, to, this.category.id)
                    if (this.transactions == null) {
                        alert("Invalid bankAcc id")
                        return
                    }
                }
            },
        },
        async mounted() {
            if (!this.$store.state.user) {
                return await this.$router.push("/").catch(() => {})
            }
            let categories = await getAllUsersCategories()
            if (categories == null) {
                alert("Invalid data")
                return
            }
            let defaultCategory = {
                id: -1000,
                name: "All"
            }
            categories.push(defaultCategory)
            this.categories = categories

            this.transactions = await getAllTransactionsBetweenDate(this.$route.params.bankId, this.date[0], this.date[1])
            if (this.transactions == null) {
                alert("Invalid bankAcc id")
                return
            }
            let budgetsNotification = await getAllNotificationBudgets(this.$route.params.bankId)
            this.$store.commit("setNotificationBudget", budgetsNotification)
        }
    }
</script>