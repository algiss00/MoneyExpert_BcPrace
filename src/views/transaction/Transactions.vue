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
                    Žádné transakce :)
                </div>
                <v-data-table
                        v-if="transactions.length !== 0"
                        dark
                        :headers="headers"
                        :items-per-page="10"
                        :items="transactions"
                        item-key="id"
                        class="elevation-1"
                        :search="search"
                        :custom-filter="filterOnlyCapsText"
                        @click:row="toDetailTransaction"
                >
                    <template v-slot:item.date="{ item }">
                        <span>{{ new Date(item.date).toDateString() }}</span>
                    </template>
                    <template v-slot:top>
                        <v-text-field
                                v-model="search"
                                label="Vyhledej podle kategorii nebo typu (POUZE VELKÁ PÍSMENA)"
                                class="mx-4"
                        />
                    </template>
                </v-data-table>
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

    /**
     * get transaction from bankAcc by Type and Category
     * @param bankId
     * @param from
     * @param to
     * @param type
     * @param categoryId
     * @returns {Promise<T>}
     */
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
                search: '',
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
                headers: [
                    {text: 'Datum', value: 'date'},
                    {text: 'Kategorie', value: 'category.name'},
                    {text: 'Typ', value: 'typeTransaction'},
                    {text: 'Částka', value: 'amount'},
                ]
            }
        },
        computed: {
            dateRangeText() {
                return this.date.join(' ~ ')
            },
        },
        methods: {
            filterOnlyCapsText(value, search) {
                return value != null &&
                    search != null &&
                    typeof value === 'string' &&
                    value.toString().toLocaleUpperCase().indexOf(search) !== -1
            },
            toDetailTransaction(item) {
                this.$router.push('/transactions/' + this.$route.params.bankId + '/detail/' + item.id).catch(() => {
                })
            },
            toAddTransaction() {
                this.$router.push('/transactions/' + this.$route.params.bankId + '/addTransaction/').catch(() => {
                })
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
                this.$store.commit("setLoading", true)
                this.transactions = await getAllTransactionsBetweenDate(this.$route.params.bankId, from, to)
                if (this.transactions == null) {
                    this.$store.commit("setLoading", false)
                    alert("Invalid bankAcc id")
                    return
                }
                this.$store.commit("setLoading", false)
            },
            /**
             * get transactions with sorting by category and type
             * @returns {Promise<void>}
             */
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
                this.$store.commit("setLoading", true)
                if (this.type === "All" && this.category.name === "All") {
                    this.transactions = await getAllTransactionsBetweenDate(this.$route.params.bankId, from, to)
                    if (this.transactions == null) {
                        this.$store.commit("setLoading", false)
                        alert("Invalid bankAcc id")
                        return
                    }
                } else if (this.type !== "All" && this.category.name !== "All") {
                    this.transactions = await getAllTransactionsByTypeAndCategoryMethod(this.$route.params.bankId, from,
                        to, this.type, this.category.id)
                } else if (this.type !== "All" && this.category.name === "All") {
                    this.transactions = await getAllTransactionsByType(this.$route.params.bankId, from, to, this.type)
                    if (this.transactions == null) {
                        this.$store.commit("setLoading", false)
                        alert("Invalid bankAcc id")
                        return
                    }
                } else if (this.type === "All" && this.category.name !== "All") {
                    this.transactions = await getAllTransactionsByCategory(this.$route.params.bankId, from, to, this.category.id)
                    if (this.transactions == null) {
                        this.$store.commit("setLoading", false)
                        alert("Invalid bankAcc id")
                        return
                    }
                }
                this.$store.commit("setLoading", false)
            },
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
                this.$store.commit("setLoading", false)
                alert("Invalid bankAcc id")
                return
            }

            // set notifications for budgets
            let budgetsNotification = await getAllNotificationBudgets(this.$route.params.bankId)
            this.$store.commit("setNotificationBudget", budgetsNotification)
            this.$store.commit("setLoading", false)
        }
    }
</script>