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
                            <v-spacer></v-spacer>
                            <v-btn color="#e7f6ff" to="/banks" class="m3-position">Zpět</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>

    export default {
        name: 'DetailBankAcc',
        data: () => ({
            name: "",
            currency: "",
            balance: "",
            items: ['CZK', 'EUR']
        }),
        async mounted() {
            // TODO
        }
    }
</script>