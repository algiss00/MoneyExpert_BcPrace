<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-toolbar color="#e7f6ff">
                            <v-toolbar-title>Přidat závazek</v-toolbar-title>
                        </v-toolbar>
                        <v-card-text>
                            <v-form
                                    ref="form"
                                    v-model="valid"
                                    lazy-validations>
                                <v-text-field
                                        id="name"
                                        label="název"
                                        v-model="name"
                                        :rules="nameRules"
                                        hide-details="auto"
                                />
                                <v-text-field
                                        id="amount"
                                        label="částka"
                                        v-model="amount"
                                        :rules="balanceRules"
                                        hide-details="auto"
                                />
                                <v-menu
                                        ref="menu"
                                        v-model="menu"
                                        :close-on-content-click="false"
                                        :return-value.sync="deadline"
                                        transition="scale-transition"
                                        offset-y
                                        min-width="auto"
                                >
                                    <template v-slot:activator="{ on, attrs }">
                                        <v-text-field
                                                v-model="deadline"
                                                label="Deadline"
                                                prepend-icon="mdi-calendar"
                                                readonly
                                                :rules="nameRules"
                                                v-bind="attrs"
                                                v-on="on"
                                        />
                                    </template>
                                    <v-date-picker
                                            v-model="deadline"
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
                                                @click="$refs.menu.save(deadline)"
                                        >
                                            OK
                                        </v-btn>
                                    </v-date-picker>
                                </v-menu>
                                <v-menu
                                        ref="menu2"
                                        v-model="menu2"
                                        :close-on-content-click="false"
                                        :return-value.sync="notifyDate"
                                        transition="scale-transition"
                                        offset-y
                                        min-width="auto"
                                >
                                    <template v-slot:activator="{ on, attrs }">
                                        <v-text-field
                                                v-model="notifyDate"
                                                label="Datum upozornění"
                                                prepend-icon="mdi-calendar"
                                                readonly
                                                :rules="nameRules"
                                                v-bind="attrs"
                                                v-on="on"
                                        />
                                    </template>
                                    <v-date-picker
                                            v-model="notifyDate"
                                            no-title
                                            scrollable
                                    >
                                        <v-spacer/>
                                        <v-btn
                                                text
                                                color="primary"
                                                @click="menu2 = false"
                                        >
                                            Cancel
                                        </v-btn>
                                        <v-btn
                                                text
                                                color="primary"
                                                @click="$refs.menu2.save(notifyDate)"
                                        >
                                            OK
                                        </v-btn>
                                    </v-date-picker>
                                </v-menu>
                                <v-textarea
                                        id="description"
                                        name="input-7-1"
                                        v-model="description"
                                        label="popis"
                                        rows="2"
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
                            <v-btn color="#e7f6ff" @click="toDebts" class="m2-position">Zpět</v-btn>
                        </v-card-actions>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn @click="addDebt($event)" :loading="loading" :disabled="!valid" color="#e7f6ff"
                                   class="m3-position">
                                Přidat
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import {addDebt, getBankAccById} from "../../api";

    export default {
        name: 'AddDebt',
        data: () => ({
            bankAcc: "",
            name: "",
            deadline: "",
            amount: "",
            notifyDate: "",
            description: "",
            menu: false,
            menu2: false,
            nameRules: [
                v => String(v).trim().length > 0 || 'required'
            ],
            balanceRules: [
                v => !Number.isNaN(Number(v)) || 'must be number',
                v => String(v).trim().length > 0 || 'required',
                v => Number(v) > 0 || 'must be > 0'
            ],
            valid: true,
            loading: false
        }),
        methods: {
            async addDebt(event) {
                if (!this.$refs.form.validate()) {
                    event.preventDefault()
                    return
                }
                const jsonDebt = JSON.stringify({
                    amount: this.amount,
                    deadline: this.deadline,
                    description: this.description,
                    name: this.name,
                    notifyDate: this.notifyDate
                });
                this.loading = true
                let result = await addDebt(jsonDebt, this.$route.params.bankId)

                if (result == null || result.status !== 201) {
                    alert("Invalid data! Maybe notifyDate is after deadline date.")
                } else if (result.status === 201) {
                    this.$store.commit("setSnackbar", true)
                    await this.$router.push('/debts/' + this.$route.params.bankId).catch(() => {
                    })
                }
                this.loading = false
            },
            toDebts() {
                this.$router.push('/debts/' + this.$route.params.bankId).catch(() => {
                })
            }
        },
        async mounted() {
            // if user not authenticated user route to login page
            if (!this.$store.state.user) {
                return await this.$router.push("/").catch(() => {
                })
            }
            this.$store.commit("setLoading", true)
            let bankAcc = await getBankAccById(this.$route.params.bankId)
            if (bankAcc == null) {
                this.$store.commit("setLoading", false)
                alert("Invalid bankAcc id")
                return
            }
            this.bankAcc = bankAcc.name
            this.$store.commit("setLoading", false)
        }
    }
</script>