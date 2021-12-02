<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
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
                                            v-model="shareBankAccount"
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
                                        Převod
                                    </v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-dialog>
                        <v-dialog
                                v-model="dialogConfirm"
                                max-width="360"
                        >
                            <v-card>
                                <v-card-title class="text-h5">
                                    Potvrzujete změnu kategorii.
                                </v-card-title>

                                <v-card-text>
                                    Potvrzením změny - u transakci se změní kategorie, má to následky na vytvořené rozpočty.
                                    Pokud máte rozpočet na editovanou kategorii, tak částka transakci se přidá k tomu rozpočtu.
                                </v-card-text>

                                <v-card-actions>
                                    <v-spacer/>

                                    <v-btn
                                            color="green darken-1"
                                            text
                                            @click="dialogConfirm = false"
                                    >
                                        Zrušit
                                    </v-btn>

                                    <v-btn
                                            color="green darken-1"
                                            text
                                            @click="dialogConfirm = false, editCategoryTransaction()"
                                    >
                                        Potvrdit
                                    </v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-dialog>
                        <v-toolbar color="#e7f6ff">
                            <v-toolbar-title>Detail transakce</v-toolbar-title>
                            <v-btn
                                    icon
                                    @click="dialog = true"
                                    title="Převod transakci"
                                    :disabled="!valid"
                            >
                                <v-icon
                                        color="blue"
                                >
                                    mdi-share
                                </v-icon>
                            </v-btn>
                            <v-spacer></v-spacer>
                            <v-btn
                                    class="mx-2"
                                    icon
                                    @click="removeTransaction($event)"
                                    title="Smazat transakci"
                                    :disabled="!valid"
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
                                                label="Date"
                                                :rules="rules"
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
                                <v-btn color="primary" :disabled="!valid" text @click="editTypeTransaction"
                                       id="editBtnType">Změnit typ
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
                                <v-btn color="primary" :disabled="!valid" text @click="dialogConfirm = true"
                                       id="editBtnCategory">Změnit
                                    kategorii
                                </v-btn>
                                <v-text-field
                                        id="bankAcc"
                                        label="účet"
                                        v-model="bankAcc.name"
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

    // find element in array by id
    function findByIdInArray(array, id) {
        for (let i = 0; i < array.length; i++) {
            if (array[i].id === id) {
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
            shareBankAccount: "",
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
            dialogConfirm: false
        }),
        methods: {
            toTransactions() {
                this.$router.push('/transactions/' + this.$route.params.bankId).catch(() => {
                })
            },
            async editBasicInfo(event) {
                if (!this.$refs.form.validate()) {
                    event.preventDefault()
                    return
                }
                this.$store.commit("setLoading", true)
                const jsonTransaction = JSON.stringify({
                    amount: this.amount,
                    jottings: this.jottings,
                    date: this.date
                });

                let result = await editBasicTransaction(jsonTransaction, this.$route.params.transId)
                if (result == null || result.status !== 201) {
                    this.$store.commit("setSnackbarText", "Server error! Cant edit.")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.status === 201) {
                    this.$store.commit("setSnackbar", true)
                }
                this.$store.commit("setLoading", false)
            },
            async editTypeTransaction() {
                this.$store.commit("setLoading", true)
                let result = await editTypeTransaction(this.$route.params.transId, this.type)
                if (result == null || result.status !== 201) {
                    this.$store.commit("setSnackbarText", "Server error! Cant edit.")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.status === 201) {
                    this.$store.commit("setSnackbar", true)
                }
                this.$store.commit("setLoading", false)
            },
            async editCategoryTransaction() {
                let category = await getCategoryByName(this.category.name)
                if (category == null) {
                    this.$store.commit("setSnackbarText", "Server error! Cant category.")
                    this.$store.commit("setSnackbarError", true)
                    return
                }
                this.$store.commit("setLoading", true)
                let result = await editCategoryTransaction(this.$route.params.transId, category.id)
                if (result == null || result.status !== 201) {
                    this.$store.commit("setSnackbarText", "Server error! Cant edit.")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.status === 201) {
                    this.$store.commit("setSnackbar", true)
                }
                this.$store.commit("setLoading", false)
            },
            async removeTransaction(event) {
                if (!confirm("Opravdu checete smazat transakci?")) {
                    event.preventDefault()
                    return
                }

                this.$store.commit("setLoading", true)
                let result = await removeTransactionFromBank(this.$route.params.transId)
                if (result == null || result.status !== 200) {
                    this.$store.commit("setSnackbarText", "Server error! Cant delete.")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.status === 200) {
                    this.$store.commit("setSnackbar", true)
                    await this.$router.push('/transactions/' + this.$route.params.bankId).catch(() => {
                    })
                }
                this.$store.commit("setLoading", false)
            },
            async transferTransaction(event) {
                if (this.shareBankAccount.id === this.bankAcc.id) {
                    event.preventDefault()
                    this.$store.commit("setSnackbarText", "Not valid Transfer! You must transfer to another bank account!")
                    this.$store.commit("setSnackbarError", true)
                    return
                }
                this.$store.commit("setLoading", true)
                let result = await transferTransaction(this.$route.params.bankId, this.shareBankAccount.id, this.$route.params.transId)
                if (result == null || result.status !== 201) {
                    this.$store.commit("setSnackbarText", "Server error! Cant transfer.")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.status === 201) {
                    this.$store.commit("setSnackbar", true)
                    await this.$router.push('/transactions/' + this.$route.params.bankId).catch(() => {
                    })
                }
                this.$store.commit("setLoading", false)
            }
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
                alert("Server error!")
                location.reload()
                return
            }

            let bankAcc = await getBankAccById(this.$route.params.bankId)
            if (bankAcc == null) {
                this.$store.commit("setLoading", false)
                this.$store.commit("setSnackbarText", "Server error! Cant get bankAcc.")
                this.$store.commit("setSnackbarError", true)
                return
            }
            let transactionById = await getTransactionById(this.$route.params.transId)
            if (transactionById == null) {
                this.$store.commit("setLoading", false)
                this.$store.commit("setSnackbarText", "Server error! Cant get transaction.")
                this.$store.commit("setSnackbarError", true)
                return
            }
            // set category to transaction
            // category may be not from users category
            let transactionCategory = {
                id: transactionById.category.id,
                name: transactionById.category.name
            }
            if (findByIdInArray(categories, transactionCategory.id) == null) {
                categories.push(transactionCategory)
            }
            this.categories = categories
            this.category = transactionCategory

            let allUsersBanks = await getAllUsersBanks()
            if (allUsersBanks == null) {
                this.$store.commit("setLoading", false)
                this.$store.commit("setSnackbarText", "Server error! Cant get bank accounts.")
                this.$store.commit("setSnackbarError", true)
                return
            }

            this.allBankAccounts = allUsersBanks
            // set actual bankAcc to shareBankAcc dialog box
            this.shareBankAccount = findByIdInArray(allUsersBanks, bankAcc.id)

            this.date = new Date(transactionById.date).toISOString().substring(0, 19)
            this.type = transactionById.typeTransaction
            this.amount = transactionById.amount
            this.jottings = transactionById.jottings
            this.bankAcc = bankAcc
            this.$store.commit("setLoading", false)
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