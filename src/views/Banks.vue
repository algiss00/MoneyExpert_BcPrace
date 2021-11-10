<template>
    <v-container fluid>
        <v-card color="#e7f6ff">
            <v-card-title class="mx-auto">
                Přehled účtů
            </v-card-title>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn class="m4-position" to="/addBankAcc">Přidat účet</v-btn>
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
                    </tr>
                    </thead>
                    <tbody>
                    <tr
                            v-for="item in createdBanks"
                            :key="item.id"
                            @click="detailBankAcc(item)"
                    >
                        <td>{{ item.name }}</td>
                        <td>{{ item.balance }}</td>
                        <td>{{ item.currency }}</td>
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
                            @click="detailBankAcc(item)"
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
    import {getAllUsersCreatedBanks, getAllUsersAvailableBanks} from "../api";

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
                this.$router.push('/detailBankAcc/' + item.id)
            }
        },
        async mounted() {
            // TODO - add profil ICON
            let createdBanks = await getAllUsersCreatedBanks()
            let availableBanks = await getAllUsersAvailableBanks()
            this.createdBanks = createdBanks
            this.availableBanks = availableBanks
        }
    }
</script>
