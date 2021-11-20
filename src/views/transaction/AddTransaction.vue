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
                                <v-select
                                        id="type"
                                        :items="types"
                                        v-model="type"
                                        :rules="[v => !!v || 'Item is required']"
                                        label="typ"
                                />
                                <v-select
                                        id="category"
                                        :items="categories"
                                        v-model="category"
                                        label="Kategorie"
                                        item-text="name"
                                        :rules="[v => !!v|| 'Item is required']"
                                        item-value="id"
                                        persistent-hint
                                        return-object
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
                            <v-btn @click="addTransaction($event)" color="#e7f6ff" :disabled="!valid"
                                   class="m3-position">Přidat
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import {getBankAccById, getCategoryByName, addTransaction, getAllUsersCategories} from "../../api";

    export default {
        name: 'AddTransaction',
        data: () => ({
            types: ['EXPENSE', 'INCOME'],
            categories: [],
            category: "",
            type: "",
            amount: "",
            jottings: "",
            bankAcc: "",
            date: (new Date(Date.now() - (new Date()).getTimezoneOffset() * 60000)).toISOString().substring(0, 19),
            menu: false,
            rules: [
                v => String(v).trim().length > 0 || 'required'
            ],
            balanceRules: [
                v => !Number.isNaN(Number(v)) || 'must be number',
                v => String(v).trim().length > 0 || 'required',
                v => Number(v) > 0 || 'must be > 0'
            ],
            valid: true,
        }),
        methods: {
            async addTransaction(event) {
                if (!this.$refs.form.validate()) {
                    event.preventDefault()
                    return
                }

                let category = await getCategoryByName(this.category.name)
                if (category == null) {
                    alert("Invalid category")
                    return
                }

                const jsonTransaction = JSON.stringify({
                    amount: this.amount,
                    jottings: this.jottings,
                    typeTransaction: this.type,
                    date: this.date
                });

                let result = await addTransaction(jsonTransaction, this.$route.params.bankId, category.id)

                if (result == null || result.status !== 201) {
                    alert("Invalid data!")
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
            this.bankAcc = bankAcc.name
            this.categories = categories
        }
    }
</script>