<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-alert
                                dense
                                type="error"
                                v-if="alertDebtNotify"
                        >
                            Nastal datum upozornění!
                        </v-alert>

                        <v-alert
                                dense
                                type="error"
                                v-if="alertDebtDeadline"
                        >
                            Nastal deadline!
                        </v-alert>
                        <v-toolbar color="#e7f6ff">
                            <v-toolbar-title>Detail závazku</v-toolbar-title>
                            <v-btn
                                    class="mx-2"
                                    icon
                                    @click="dialogDebt = true"
                                    title="Ukončení závazku"
                            >
                                <v-icon
                                        color="green"
                                >
                                    mdi-check-bold
                                </v-icon>
                            </v-btn>
                            <v-spacer></v-spacer>
                            <v-btn
                                    class="mx-2"
                                    icon
                                    @click="removeDebt($event)"
                                    title="Smazat závazek"
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
                                <v-textarea
                                        id="description"
                                        name="input-7-1"
                                        v-model="description"
                                        label="popis"
                                        rows="2"
                                />
                                <v-btn color="primary" text class="editBtnDebt" @click="editBasic($event)">Změnit název,
                                    částku, popis
                                </v-btn>
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
                                <v-btn color="primary" text class="editBtnDebt" @click="editDeadline($event)">Změnit
                                    deadline
                                </v-btn>
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
                                <v-btn color="primary" text class="editBtnDebt" @click="editNotifyDate($event)">Změnit
                                    datum upozornění
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
                            <v-btn color="#e7f6ff" @click="toDebts">Zpět</v-btn>
                        </v-card-actions>
                        <v-dialog
                                v-model="dialogDebt"
                                max-width="290"
                        >
                            <v-card>
                                <v-card-title class="text-h5">
                                    Potverzujete ukončení závazku.
                                </v-card-title>

                                <v-card-text>
                                    Potvrzením ukončení - závazek se odstraní, pak se přidá transakce s částkou závazku
                                    a bude mít kategorii 'zavazek'.
                                </v-card-text>

                                <v-card-actions>
                                    <v-spacer></v-spacer>

                                    <v-btn
                                            color="green darken-1"
                                            text
                                            @click="dialogDebt = false"
                                    >
                                        Zrušit
                                    </v-btn>

                                    <v-btn
                                            color="green darken-1"
                                            text
                                            @click="dialogDebt = false, closeDebt()"
                                    >
                                        Potvrdit
                                    </v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-dialog>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import {
        addTransaction,
        editBasicDebt,
        editDeadline,
        editNotifyDate,
        getAllNotificationDebts,
        getBankAccById,
        getDebtById,
        removeDebt
    } from "../../api";

    export default {
        name: 'DetailDebt',
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
            alertDebtNotify: false,
            alertDebtDeadline: false,
            dialogDebt: false
        }),
        methods: {
            isDebtNotify(item) {
                let notifications = this.$store.state.notificationDebt
                for (let i = 0; i < notifications.length; i++) {
                    if (notifications[i].debt.id === item.id && notifications[i].typeNotification === "DEBT_DEADLINE") {
                        return "deadline"
                    } else if (notifications[i].debt.id === item.id && notifications[i].typeNotification === "DEBT_NOTIFY") {
                        return "notify"
                    }
                }
                return "empty"
            },
            toDebts() {
                this.$router.push('/debts/' + this.$route.params.bankId).catch(() => {})
            },
            async closeDebt() {
                let currentDate = (new Date(Date.now() - (new Date()).getTimezoneOffset() * 60000)).toISOString().substring(0, 19)
                const jsonTransaction = JSON.stringify({
                    amount: this.amount,
                    jottings: "Závazek " + this.name,
                    typeTransaction: "EXPENSE",
                    date: currentDate,
                });

                let result = await addTransaction(jsonTransaction, this.$route.params.bankId, -15)

                if (result == null || result.status !== 201) {
                    alert("Invalid data! Transaction is not added")
                }

                let resultRemove = await removeDebt(this.$route.params.debtId)
                if (resultRemove == null || resultRemove.status !== 200) {
                    alert("Invalid delete debt!")
                } else if (resultRemove.status === 200) {
                    this.$store.commit("setSnackbar", true)
                }

                await this.$router.push('/debts/' + this.$route.params.bankId).catch(() => {})
            },
            async removeDebt(event) {
                if (!confirm("Opravdu checete smazat závazek?")) {
                    event.preventDefault()
                    return
                }

                let result = await removeDebt(this.$route.params.debtId)
                if (result == null || result.status !== 200) {
                    alert("Invalid delete!")
                } else if (result.status === 200) {
                    this.$store.commit("setSnackbar", true)
                    await this.$router.push('/debts/' + this.$route.params.bankId).catch(() => {})
                }
            },
            async editBasic(event) {
                if (!this.$refs.form.validate()) {
                    event.preventDefault()
                    return
                }

                const jsonDebt = JSON.stringify({
                    amount: this.amount,
                    description: this.description,
                    name: this.name
                });

                let result = await editBasicDebt(this.$route.params.debtId, jsonDebt)
                if (result == null || result.status !== 201) {
                    alert("Invalid data!")
                } else if (result.status === 201) {
                    this.$store.commit("setSnackbar", true)
                }
            },
            async editDeadline(event) {
                if (!this.$refs.form.validate()) {
                    event.preventDefault()
                    return
                }

                let result = await editDeadline(this.$route.params.debtId, this.deadline)
                if (result == null || result.status !== 201) {
                    alert("Invalid data! Maybe notifyDate is after deadline date.")
                } else if (result.status === 201) {
                    this.$store.commit("setSnackbar", true)
                    let debtsNotification = await getAllNotificationDebts(this.$route.params.bankId)
                    this.$store.commit("setNotificationDebt", debtsNotification)
                }
            },
            async editNotifyDate(event) {
                if (!this.$refs.form.validate()) {
                    event.preventDefault()
                    return
                }

                let result = await editNotifyDate(this.$route.params.debtId, this.notifyDate)
                if (result == null || result.status !== 201) {
                    alert("Invalid data! Maybe notifyDate is after deadline date.")
                } else if (result.status === 201) {
                    this.$store.commit("setSnackbar", true)
                    let debtsNotification = await getAllNotificationDebts(this.$route.params.bankId)
                    this.$store.commit("setNotificationDebt", debtsNotification)
                }
            }
        },
        async mounted() {
            if (!this.$store.state.user) {
                return await this.$router.push("/").catch(() => {})
            }
            let bankAcc = await getBankAccById(this.$route.params.bankId)
            if (bankAcc == null) {
                alert("Invalid bankAcc id")
                return
            }
            this.bankAcc = bankAcc.name

            let debt = await getDebtById(this.$route.params.debtId)
            if (debt == null) {
                alert("Invalid transaction id")
                return
            }
            if (this.isDebtNotify(debt) !== "empty") {
                if (this.isDebtNotify(debt) === "deadline") {
                    this.alertDebtDeadline = true
                    this.alertDebtNotify = false
                } else if (this.isDebtNotify(debt) === "notify") {
                    this.alertDebtNotify = true
                    this.alertDebtDeadline = false
                }
            }

            this.name = debt.name
            this.amount = debt.amount
            this.description = debt.description
            this.notifyDate = new Date(debt.notifyDate).toISOString().substring(0, 10)
            this.deadline = new Date(debt.deadline).toISOString().substring(0, 10)
        }
    }
</script>

<style>
    .editBtnDebt {
        margin: 5px;
        right: 15px;
    }
</style>
