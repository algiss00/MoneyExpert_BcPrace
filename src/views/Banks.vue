<template>
    <v-container fluid>
        <v-card>
            <v-card-title class="mx-auto">
                Přehled účtů
            </v-card-title>
            <div class="font-weight-medium grey--text m-left">
                Created accounts
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
                            v-for="item in banks"
                            :key="item.id"
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
        text-align: center;
    }
</style>

<script>
    import {getAllUsersBanks} from "../api";

    export default {
        name: "banks",
        data: () => {
            return {
                banks: []
            }
        },

        async mounted() {
            let createdBanks = await getAllUsersBanks()
            this.banks = createdBanks
            console.log(this.banks)
        }
    }
</script>
