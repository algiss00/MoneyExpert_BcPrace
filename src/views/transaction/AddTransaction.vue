<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-toolbar color="#e7f6ff">
                            <v-toolbar-title>Přidat transakci</v-toolbar-title>
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
                                <v-select
                                        id="type"
                                        :items="types"
                                        v-model="type"
                                        label="typ"
                                />
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
                            <v-btn color="#e7f6ff" @click="toTransactions" class="m2-position">Zpět</v-btn>
                        </v-card-actions>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn @click="addTransaction($event)" color="#e7f6ff" class="m3-position">Přidat</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import {markAsError, getBankAccById, getCategoryByName, addTransaction, getAllUsersCategories} from "../../api";

    function validate() {
        let amountEl = document.getElementById("amount")

        if (amountEl.value.trim().length === 0) {
            markAsError("amount", true);
        } else {
            markAsError("amount", false);
        }
        return !(amountEl.classList.value === "error");
    }

    export default {
        name: 'AddTransaction',
        data: () => ({
            types: ['EXPENSE', 'INCOME'],
            categories: [],
            category: "",
            type: "EXPENSE",
            amount: "",
            jottings: "",
            bankAcc: "",
            date: (new Date(Date.now() - (new Date()).getTimezoneOffset() * 60000)).toISOString().substring(0, 19),
            menu: false
        }),
        methods: {
            async addTransaction(event) {
                if (!validate()) {
                    event.preventDefault()
                    return
                }

                let category = await getCategoryByName(this.category.name)

                const jsonTransaction = JSON.stringify({
                    amount: this.amount,
                    jottings: this.jottings,
                    typeTransaction: this.type,
                    date: this.date
                });

                let result = await addTransaction(jsonTransaction, this.$route.params.bankId, category.id)

                if (result == null || result.status !== 201) {
                    return
                } else if (result.status === 201) {
                    alert("Success!")
                    await this.$router.push('/transactions/' + this.$route.params.bankId)
                }
            },
            toTransactions() {
                this.$router.push('/transactions/' + this.$route.params.bankId)
            }
        },
        async mounted() {
            let bankAcc = await getBankAccById(this.$route.params.bankId)
            let categories = await getAllUsersCategories()
            this.bankAcc = bankAcc.name
            this.categories = categories
            this.category = categories[0]
        }
    }
</script>