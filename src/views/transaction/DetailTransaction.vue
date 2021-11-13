<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-toolbar color="#e7f6ff">
                            <v-toolbar-title>Detail transakce</v-toolbar-title>
                            <v-spacer></v-spacer>
                            <v-btn
                                    class="mx-2"
                                    fab
                                    dark
                                    small
                                    color="#e7f6ff"
                                    @click="removeTransaction($event)"
                            >
                                <v-icon
                                        color="red"
                                >
                                    mdi-delete
                                </v-icon>
                            </v-btn>
                        </v-toolbar>
                        <v-card-text>
                            <v-form>
                                <v-menu
                                        ref="menu"
                                        v-model="menu"
                                        :close-on-content-click="false"
                                        :return-value.sync="date"
                                        transition="scale-transition"
                                        offset-y
                                        min-width="auto"
                                >
                                    <template v-slot:activator="{ on, attrs }">
                                        <v-text-field
                                                v-model="date"
                                                label="Picker in menu"
                                                prepend-icon="mdi-calendar"
                                                readonly
                                                v-bind="attrs"
                                                v-on="on"
                                        />
                                    </template>
                                    <v-date-picker
                                            v-model="date"
                                            no-title
                                            scrollable
                                    >
                                        <v-spacer/>
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
                                                @click="$refs.menu.save(date)"
                                        >
                                            OK
                                        </v-btn>
                                    </v-date-picker>
                                </v-menu>
                                <v-text-field
                                        id="amount"
                                        label="částka"
                                        v-model="amount"
                                        hide-details="auto"
                                />
                                <v-text-field
                                        id="jottings"
                                        label="poznámky"
                                        v-model="jottings"
                                        hide-details="auto"
                                />
                                <v-btn color="#e7f6ff" @click="editBasicInfo($event)" id="editBtn">Změnit datum, částku,
                                    poznámky
                                </v-btn>
                                <v-select
                                        id="type"
                                        :items="types"
                                        v-model="type"
                                        label="typ"
                                />
                                <v-btn color="#e7f6ff" @click="editTypeTransaction" id="editBtnType">Změnit typ</v-btn>
                                <v-select
                                        id="category"
                                        :items="categories"
                                        v-model="category"
                                        label="Vyberte kategorii"
                                        item-text="name"
                                        item-value="id"
                                        persistent-hint
                                        return-object
                                        single-line
                                />
                                <v-btn color="#e7f6ff" @click="editCategoryTransaction" id="editBtnCategory">Změnit
                                    kategorii
                                </v-btn>
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
    import {
        getTransactionById,
        getBankAccById,
        editBasicTransaction,
        editTypeTransaction,
        editCategoryTransaction,
        markAsError,
        getCategoryByName,
        getAllUsersCategories,
        removeTransactionFromBank
    } from "../../api";


    function validate() {
        let amountEl = document.getElementById("amount")

        if (amountEl.value.trim().length === 0 || amountEl.value.trim() <= 0) {
            markAsError("amount", true);
        } else {
            markAsError("amount", false);
        }
        return !(amountEl.classList.value === "error");
    }

    function findCategoryByNameInArray(array, name) {
        for (let i = 0; i < array.length; i++) {
            if (array[i].name === name) {
                return array[i]
            }
        }
        return null
    }

    export default {
        name: 'DetailTransaction',
        data: () => ({
            types: ['EXPENSE', 'INCOME'],
            date: "",
            categories: [],
            category: "",
            type: "",
            amount: "",
            jottings: "",
            bankAcc: "",
            menu: false
        }),
        methods: {
            toTransactions() {
                this.$router.push('/transactions/' + this.$route.params.bankId)
            },
            async editBasicInfo(event) {
                if (!validate()) {
                    event.preventDefault()
                    return
                }

                const jsonTransaction = JSON.stringify({
                    amount: this.amount,
                    jottings: this.jottings,
                    date: this.date
                });

                let result = await editBasicTransaction(jsonTransaction, this.$route.params.transId)
                if (result == null || result.status !== 201) {
                    return
                } else if (result.status === 201) {
                    alert("Success!")
                }
            },
            async editTypeTransaction() {
                let result = await editTypeTransaction(this.$route.params.transId, this.type)
                if (result == null || result.status !== 201) {
                    return
                } else if (result.status === 201) {
                    alert("Success!")
                }
            },
            async editCategoryTransaction() {
                let category = await getCategoryByName(this.category.name)

                let result = await editCategoryTransaction(this.$route.params.transId, category.id)
                if (result == null || result.status !== 201) {
                    return
                } else if (result.status === 201) {
                    alert("Success!")
                }
            },
            async removeTransaction(event) {
                if (!confirm("Opravdu checete smazat transakci?")) {
                    event.preventDefault()
                    return
                }

                let result = await removeTransactionFromBank(this.$route.params.transId, this.$route.params.bankId)
                if (result == null || result.status !== 200) {
                    return
                } else if (result.status === 200) {
                    alert("Success!")
                    await this.$router.push('/transactions/' + this.$route.params.bankId)
                }
            }
        },
        async mounted() {
            let bankAcc = await getBankAccById(this.$route.params.bankId)
            let categories = await getAllUsersCategories()
            this.categories = categories
            let transactionById = await getTransactionById(this.$route.params.transId)
            this.category = findCategoryByNameInArray(categories, transactionById.category.name)
            this.date = new Date(transactionById.date).toISOString().substring(0, 19)
            this.type = transactionById.typeTransaction
            this.amount = transactionById.amount
            this.jottings = transactionById.jottings
            this.bankAcc = bankAcc.name
            console.log(transactionById)
        }
    }
</script>

<style>
    #editBtn {
        margin: 15px;
        right: 25px;
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