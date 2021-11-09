<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-toolbar color="#e7f6ff">
                            <v-toolbar-title>Přidat bankovní účet</v-toolbar-title>
                        </v-toolbar>
                        <v-card-text>
                            <v-form>
                                <v-text-field
                                        id="nameBankAcc"
                                        label="název"
                                        v-model="name"
                                        hide-details="auto"
                                />
                                <v-select
                                        id="currencyBankAcc"
                                        :items="items"
                                        v-model="currency"
                                        label="měna"
                                />
                                <v-text-field
                                        id="balanceBankAcc"
                                        label="balance"
                                        v-model="balance"
                                        hide-details="auto"
                                />
                            </v-form>
                        </v-card-text>
                        <v-card-actions>
                            <v-btn color="#e7f6ff" to="/banks" class="m2-position">Zpět</v-btn>
                        </v-card-actions>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn @click="addBankAcc($event)" color="#e7f6ff" class="m3-position">Přidat</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import {addBankAccount, markAsError} from "../api";

    function validate() {
        let nameEl = document.getElementById("nameBankAcc")
        let balanceEl = document.getElementById("balanceBankAcc")

        if (nameEl.value.trim().length === 0) {
            markAsError("nameBankAcc", true);
        } else {
            markAsError("nameBankAcc", false);
        }
        if (balanceEl.value.trim().length === 0) {
            markAsError("balanceBankAcc", true);
        } else {
            markAsError("balanceBankAcc", false);
        }
        return !(nameEl.classList.value === "error" || balanceEl.classList.value === "error");
    }

    export default {
        name: 'AddBankAcc',
        data: () => ({
            name: "",
            currency: "CZK",
            balance: "",
            items: ['CZK', 'EUR']
        }),
        methods: {
            async addBankAcc(event) {
                if (!validate()) {
                    event.preventDefault()
                    return
                }
                const jsonBankAcc = JSON.stringify({
                    name: this.name,
                    currency: this.currency,
                    balance: this.balance
                });

                let result = await addBankAccount(jsonBankAcc)
                if (result.data.name == this.name && result.data.balance == this.balance && result.data.currency == this.currency) {
                    alert("Success!")
                    await this.$router.push('/banks')
                } else {
                    alert("Not valid response!")
                }
            }
        }
    }
</script>