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
                                    v-model="date"
                                    label="Měsíc"
                                    prepend-icon="mdi-calendar"
                                    readonly
                                    v-bind="attrs"
                                    v-on="on"
                            ></v-text-field>
                        </template>
                        <v-date-picker
                                v-model="date"
                                type="month"
                                no-title
                                scrollable
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
                                    @click="$refs.menu.save(date), getTransactionsByMonth()"
                            >
                                OK
                            </v-btn>
                        </v-date-picker>
                    </v-menu>
                    <div id="filtr">
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
                <v-simple-table dark>
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
        getAllTransactionsByCategory, getAllTransactionsByCategoryAndType,
        getAllTransactionsByMonth,
        getAllTransactionsByType,
        getAllUsersCategories
    } from "../../api";

    async function getAllTransactionsByTypeAndCategoryMethod(bankId, month, year, type, categoryId) {
        return await getAllTransactionsByCategoryAndType(bankId, month, year, type, categoryId)
    }

    export default {
        name: "Transactions",
        data: () => {
            return {
                transactions: [],
                date: new Date().toISOString().substr(0, 7),
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
        methods: {
            toDetailTransaction(item) {
                this.$router.push('/transactions/' + this.$route.params.bankId + '/detail/' + item.id)
            },
            toAddTransaction() {
                this.$router.push('/transactions/' + this.$route.params.bankId + '/addTransaction/')
            },
            async getTransactionsByMonth() {
                let month = this.date.substr(5, 8)
                let year = this.date.substr(0, 4)
                this.type = "All"
                this.category = {
                    id: -1000,
                    name: "All"
                }
                this.transactions = await getAllTransactionsByMonth(this.$route.params.bankId, month, year)
            },
            async getAllTransactionsByTypeAndCategoryMethod() {
                let month = this.date.substr(5, 8)
                let year = this.date.substr(0, 4)
                if (this.type === "All" && this.category.name === "All") {
                    this.transactions = await getAllTransactionsByMonth(this.$route.params.bankId, month, year)
                } else if (this.type !== "All" && this.category.name !== "All") {
                    this.transactions = await getAllTransactionsByTypeAndCategoryMethod(this.$route.params.bankId, month,
                        year, this.type, this.category.id)
                } else if (this.type !== "All" && this.category.name === "All") {
                    this.transactions = await getAllTransactionsByType(this.$route.params.bankId, month, year, this.type)
                } else if (this.type === "All" && this.category.name !== "All") {
                    this.transactions = await getAllTransactionsByCategory(this.$route.params.bankId, month, year, this.category.id)
                }
            },
        },
        async mounted() {
            let categories = await getAllUsersCategories()
            let defaultCategory = {
                id: -1000,
                name: "All"
            }
            categories.push(defaultCategory)
            this.categories = categories
            let month = this.date.substr(5, 8)
            let year = this.date.substr(0, 4)
            this.transactions = await getAllTransactionsByMonth(this.$route.params.bankId, month, year)
        }
    }
</script>

<style>
    #filtr {
        display: inline-block;
    }
</style>