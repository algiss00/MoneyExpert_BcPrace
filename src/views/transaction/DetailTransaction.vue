<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-toolbar color="#e7f6ff">
                            <v-toolbar-title>Detail transakce</v-toolbar-title>
                            <v-btn
                                    icon
                                    @click="dialog = true"
                            >
                                <v-icon
                                        color="blue"
                                >
                                    mdi-share
                                </v-icon>
                            </v-btn>
                            <v-dialog
                                    v-model="dialog"
                                    max-width="500px"
                            >
                                <v-card>
                                    <v-card-title>
                                        <span>Převod transakci</span>
                                    </v-card-title>
                                    <v-card-text>
                                        <v-select
                                                id="bankAccounts"
                                                :items="allBankAccounts"
                                                v-model="bankAccount"
                                                label="dostupné bankovní účty"
                                                item-text="name"
                                                item-value="id"
                                                persistent-hint
                                                return-object
                                        />
                                    </v-card-text>
                                    <v-card-actions>
                                        <v-btn
                                                color="primary"
                                                text
                                                @click="dialog = false"
                                        >
                                            Zavřit
                                        </v-btn>
                                        <v-spacer/>
                                        <v-btn
                                                color="primary"
                                                text
                                                @click="transferTransaction($event)"
                                        >
                                            Transfer
                                        </v-btn>
                                    </v-card-actions>
                                </v-card>
                            </v-dialog>

                            <v-spacer></v-spacer>
                            <v-btn
                                    class="mx-2"
                                    icon
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
                            <v-form
                                    ref="form"
                                    v-model="valid"
                                    lazy-validation>
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
                                        :rules="balanceRules"
                                        hide-details="auto"
                                />
                                <v-text-field
                                        id="jottings"
                                        label="poznámky"
                                        v-model="jottings"
                                        hide-details="auto"
                                />
                                <v-btn color="primary" text @click="editBasicInfo($event)" :disabled="!valid"
                                       id="editBtn">
                                    Změnit datum, částku,
                                    poznámky
                                </v-btn>
                                <v-select
                                        id="type"
                                        :items="types"
                                        :rules="[v => !!v || 'Item is required']"
                                        v-model="type"
                                        label="typ"
                                />
                                <v-btn color="primary" text @click="editTypeTransaction" id="editBtnType">Změnit typ
                                </v-btn>
                                <v-select
                                        id="category"
                                        :items="categories"
                                        v-model="category"
                                        label="Kategorie"
                                        item-text="name"
                                        :rules="[v => !!v || 'Item is required']"
                                        item-value="id"
                                        persistent-hint
                                        return-object
                                />
                                <v-btn color="primary" text @click="editCategoryTransaction" id="editBtnCategory">Změnit
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
        getCategoryByName,
        getAllUsersCategories,
        removeTransactionFromBank,
        getAllUsersBanks,
        transferTransaction
    } from "../../api";

    function findByNameInArray(array, name) {
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
            allBankAccounts: [],
            bankAccount: "",
            category: "",
            type: "",
            amount: "",
            jottings: "",
            bankAcc: "",
            menu: false,
            dialog: false,
            rules: [
                v => String(v).trim().length > 0 || 'required',
            ],
            balanceRules: [
                v => !Number.isNaN(Number(v)) || 'must be number',
                v => String(v).trim().length > 0 || 'required',
                v => Number(v) > 0 || 'must be > 0'
            ],
            valid: true,
        }),
        methods: {
            toTransactions() {
                this.$router.push('/transactions/' + this.$route.params.bankId)
            },
            async editBasicInfo(event) {
                if (!this.$refs.form.validate()) {
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
                    alert("Invalid data!")
                } else if (result.status === 201) {
                    alert("Success!")
                }
            },
            async editTypeTransaction() {
                let result = await editTypeTransaction(this.$route.params.transId, this.type)
                if (result == null || result.status !== 201) {
                    alert("Invalid data!")
                } else if (result.status === 201) {
                    alert("Success!")
                }
            },
            async editCategoryTransaction() {
                let category = await getCategoryByName(this.category.name)
                if (category == null) {
                    alert("Invalid category")
                    return
                }

                let result = await editCategoryTransaction(this.$route.params.transId, category.id)
                if (result == null || result.status !== 201) {
                    alert("Invalid data!")
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
                    alert("Invalid delete!")
                } else if (result.status === 200) {
                    alert("Success!")
                    await this.$router.push('/transactions/' + this.$route.params.bankId)
                }
            },
            async transferTransaction(event) {
                if (this.bankAccount.name === this.bankAcc) {
                    event.preventDefault()
                    alert("Not valid Trasfer! You must transfer to another bank account!")
                    return
                }

                let result = await transferTransaction(this.$route.params.bankId, this.bankAccount.id, this.$route.params.transId)
                if (result == null || result.status !== 201) {
                    alert("Invalid data!")
                } else if (result.status === 201) {
                    alert("Success!")
                    await this.$router.push('/transactions/' + this.$route.params.bankId)
                }
            }
        },
        async mounted() {
            if (!this.$store.state.user) {
                return await this.$router.push("/")
            }
            let bankAcc = await getBankAccById(this.$route.params.bankId)
            if (bankAcc == null) {
                alert("Invalid bankAcc id")
                return
            }
            let categories = await getAllUsersCategories()
            if (categories == null) {
                alert("Invalid data")
                return
            }
            let allUsersBanks = await getAllUsersBanks()
            if (allUsersBanks == null) {
                alert("Invalid data")
                return
            }

            this.allBankAccounts = allUsersBanks
            this.bankAccount = findByNameInArray(allUsersBanks, bankAcc.name)
            this.categories = categories
            let transactionById = await getTransactionById(this.$route.params.transId)
            if (transactionById == null) {
                alert("Invalid transaction id")
                return
            }
            this.category = findByNameInArray(categories, transactionById.category.name)
            this.date = new Date(transactionById.date).toISOString().substring(0, 19)
            this.type = transactionById.typeTransaction
            this.amount = transactionById.amount
            this.jottings = transactionById.jottings
            this.bankAcc = bankAcc.name
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