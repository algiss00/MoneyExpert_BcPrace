<template>
    <v-app id="inspire">
        <v-container fluid>
            <v-card color="#e7f6ff">
                <v-card-title class="mx-auto text--black">
                    Přehled účtů
                    <v-fab-transition>
                        <v-btn
                                class="m4-position"
                                to="/banks/addBankAcc"
                                dark
                                small
                                color="green"
                                fab
                        >
                            <v-icon>mdi-plus</v-icon>
                        </v-btn>
                    </v-fab-transition>
                </v-card-title>
                <div class="font-weight-medium black--text m-left">
                    Created accounts
                </div>
                <v-simple-table dark id="createdTab">
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
                                Nastavení
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr
                                v-for="item in createdBanks"
                                :key="item.id"
                                @click="toDashboard(item)"
                        >
                            <td>{{ item.name }}</td>
                            <td>{{ item.balance }}</td>
                            <td>{{ item.currency }}</td>
                            <td>
                                <v-btn
                                        icon
                                        @click.stop="detailBankAcc(item)"
                                >
                                    <v-icon>
                                        mdi-wrench
                                    </v-icon>
                                </v-btn>
                            </td>
                        </tr>
                        </tbody>
                    </template>
                </v-simple-table>

                <div class="font-weight-medium black--text m-left">
                    Available accounts
                </div>
                <v-simple-table dark id="availableTab">
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
                                Detail
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
                            <td>
                                <v-btn
                                        icon
                                        @click.stop="dialog = !dialog, detailAvailableBank(item)"
                                >
                                    <v-icon>mdi-dots-horizontal</v-icon>
                                </v-btn>
                            </td>
                        </tr>
                        </tbody>
                    </template>
                </v-simple-table>

                <v-dialog
                        v-model="dialog"
                        max-width="500px"
                >
                    <v-card>
                        <v-card-title>
                            <span>Detail účtu</span>
                        </v-card-title>
                        <v-card-text>
                            <v-text-field
                                    id="name"
                                    label="název"
                                    v-model="name"
                                    hide-details="auto"
                                    readonly
                            />
                            <v-text-field
                                    id="currency"
                                    label="měna"
                                    v-model="currency"
                                    hide-details="auto"
                                    readonly
                            />
                            <v-text-field
                                    id="balance"
                                    label="balance"
                                    v-model="balance"
                                    hide-details="auto"
                                    readonly
                            />
                            <v-text-field
                                    id="creator"
                                    label="creator"
                                    v-model="creator"
                                    hide-details="auto"
                                    readonly
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
                        </v-card-actions>
                    </v-card>
                </v-dialog>

            </v-card>
        </v-container>
    </v-app>
</template>
<style>
    .m-left {
        text-align: left;
        margin-left: 20px;
        font-size: 20px;
    }

    .m4-position {
        left: 20px;
    }

    #createdTab {
        margin-bottom: 30px;
    }

    #availableTab {
        margin-bottom: 10px;
    }
</style>

<script>
    import {getAllUsersCreatedBanks, getAllUsersAvailableBanks, getCreatorOfBankAcc} from "../../api";

    export default {
        name: "banks",
        data: () => {
            return {
                createdBanks: [],
                availableBanks: [],
                show: false,
                name: "",
                currency: "",
                balance: "",
                creator: "",
                dialog: false
            }
        },
        methods: {
            detailBankAcc(item) {
                this.$router.push('/banks/detail/' + item.id)
            },
            toDashboard(item) {
                this.$router.push('/dashboard/' + item.id)
            },
            // toTransactions(item) {
            //     this.$router.push('/transactions/' + item.id)
            // },
            async detailAvailableBank(item) {
                let creator = await getCreatorOfBankAcc(item.id)
                if (creator == null) {
                    alert("Invalid bankAcc id")
                    return
                }
                this.name = item.name
                this.currency = item.currency
                this.balance = item.balance
                this.creator = creator.username
            }
        },
        async mounted() {
            if (!this.$store.state.user) {
                return await this.$router.push("/")
            }
            let createdBanks = await getAllUsersCreatedBanks()
            if (createdBanks == null) {
                alert("Invalid data")
                return
            }
            let availableBanks = await getAllUsersAvailableBanks()
            if (availableBanks == null) {
                alert("Invalid username")
                return
            }
            this.createdBanks = createdBanks
            this.availableBanks = availableBanks
        }
    }
</script>
