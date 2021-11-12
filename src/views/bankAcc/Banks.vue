<template>
    <v-app id="inspire">
        <v-container fluid>
            <v-card color="#e7f6ff">
                <v-card-actions>
                    <v-btn @click="logout">Logout</v-btn>
                </v-card-actions>
                <v-card-title class="mx-auto">
                    Přehled účtů
                </v-card-title>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn class="m4-position" to="/banks/addBankAcc">Přidat účet</v-btn>
                </v-card-actions>
                <div class="font-weight-medium grey--text m-left">
                    Created accounts
                </div>
                <!--            TODO - change color of table!-->
                <v-simple-table dark>
                    <template v-slot:default>
                        <thead>
                        <tr>
                            <th class="text-left">
                                Nazev
                            </th>
                            <th class="text-left">
                                Zůstatek
                            </th>
                            <th class="text-left">
                                Měna
                            </th>
                            <th class="text-left">
                                Editace
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr
                                v-for="item in createdBanks"
                                :key="item.id"
                                @click="toTransactions(item)"
                        >
                            <td>{{ item.name }}</td>
                            <td>{{ item.balance }}</td>
                            <td>{{ item.currency }}</td>
                            <td>
                                <v-btn
                                        color="primary"
                                        fab
                                        small
                                        dark
                                        @click.stop="detailBankAcc(item)"
                                >
                                    <v-icon>
                                        mdi-pencil
                                    </v-icon>
                                </v-btn>
                            </td>
                        </tr>
                        </tbody>
                    </template>
                </v-simple-table>

                <div class="font-weight-medium grey--text m-left">
                    Available accounts
                </div>
                <v-simple-table dark>
                    <template v-slot:default>
                        <thead>
                        <tr>
                            <th class="text-left">
                                Nazev
                            </th>
                            <th class="text-left">
                                Zůstatek
                            </th>
                            <th class="text-left">
                                Měna
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr
                                v-for="item in availableBanks"
                                :key="item.id"
                                @click="toTransactions(item)"
                        >
                            <td>{{ item.name }}</td>
                            <td>{{ item.balance }}</td>
                            <td>{{ item.currency }}</td>
                        </tr>
                        </tbody>
                    </template>
                </v-simple-table>
            </v-card>
        </v-container>
    </v-app>
</template>
<style>
    .m-left {
        text-align: left;
    }

    .m4-position {
        bottom: 50px;
    }
</style>

<script>
    import {getAllUsersCreatedBanks, getAllUsersAvailableBanks, logout, getCurrentUser} from "../../api";

    export default {
        name: "banks",
        data: () => {
            return {
                createdBanks: [],
                availableBanks: []
            }
        },
        methods: {
            detailBankAcc(item) {
                this.$router.push('/banks/detailBankAcc/' + item.id)
            },
            toTransactions(item) {
                this.$router.push('/transactions/' + item.id)
            },
            async logout() {
                let result = await logout()
                if (result.success == true && result.loggedIn == false) {
                    await this.$router.push('/')
                }
            }
        },
        async mounted() {
            // TODO - add profil ICON
            let createdBanks = await getAllUsersCreatedBanks()
            let availableBanks = await getAllUsersAvailableBanks()
            this.createdBanks = createdBanks
            this.availableBanks = availableBanks
            console.log(getCurrentUser())
        }
    }
</script>
