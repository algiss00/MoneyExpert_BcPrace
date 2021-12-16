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
                                title="Přidat bank. účet"
                        >
                            <v-icon>mdi-plus</v-icon>
                        </v-btn>
                    </v-fab-transition>
                </v-card-title>

                <div class="font-weight-medium black--text m-left"
                     v-if="createdBanks.length === 0 && availableBanks.length === 0">
                    Žádné bankovní účty :)
                </div>

                <div class="font-weight-medium black--text m-left" v-if="createdBanks.length !== 0">
                    Vytvořené účty
                </div>

                <v-data-table
                        id="createdTab"
                        v-if="createdBanks.length !== 0"
                        dark
                        :headers="headersCreated"
                        :items-per-page="5"
                        :items="createdBanks"
                        item-key="id"
                        class="elevation-1"
                        :search="searchCreated"
                        :custom-filter="filterText"
                        @click:row="toDashboard"
                >
                    <template v-slot:item.detail="props">
                        <v-btn icon @click="detailBankAcc(props.item)">
                            <v-icon dark>mdi-wrench</v-icon>
                        </v-btn>
                    </template>
                    <template v-slot:top>
                        <v-text-field
                                v-model="searchCreated"
                                label="Vyhledej podle názvu"
                                class="mx-4"
                        />
                    </template>
                </v-data-table>

                <div class="font-weight-medium black--text m-left" v-if="availableBanks.length !== 0">
                    Dostupné účty
                </div>

                <v-data-table
                        id="availableTab"
                        v-if="availableBanks.length !== 0"
                        dark
                        :headers="headersAvailable"
                        :items-per-page="5"
                        :items="availableBanks"
                        item-key="id"
                        class="elevation-1"
                        :search="searchAvailable"
                        :custom-filter="filterText"
                        @click:row="toDashboard"
                >
                    <template v-slot:item.detail="props">
                        <v-btn icon @click.stop="dialog = !dialog, detailAvailableBank(props.item)">
                            <v-icon>mdi-dots-horizontal</v-icon>
                        </v-btn>
                    </template>
                    <template v-slot:top>
                        <v-text-field
                                v-model="searchAvailable"
                                label="Vyhledej podle názvu"
                                class="mx-4"
                        />
                    </template>
                </v-data-table>

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
                                    label="zůstatek"
                                    v-model="balance"
                                    hide-details="auto"
                                    readonly
                            />
                            <v-text-field
                                    id="creator"
                                    label="tvůrce"
                                    v-model="creator"
                                    hide-details="auto"
                                    readonly
                            />
                        </v-card-text>
                        <v-card-actions>
                            <v-spacer></v-spacer>
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
                dialog: false,
                headersCreated: [
                    {text: 'Nazev', value: 'name'},
                    {text: 'Zůstatek', value: 'balance'},
                    {text: 'Měna', value: 'currency'},
                    {text: 'Nastavení', value: 'detail'},
                ],
                searchCreated: '',
                headersAvailable: [
                    {text: 'Nazev', value: 'name'},
                    {text: 'Zůstatek', value: 'balance'},
                    {text: 'Měna', value: 'currency'},
                    {text: 'Detail', value: 'detail'},
                ],
                searchAvailable: '',
            }
        },
        methods: {
            // search in data-table by string
            filterText(value, search) {
                return value != null && search && typeof value === 'string' &&
                    value.toString().toLocaleUpperCase().indexOf(search.toUpperCase()) !== -1
            },
            detailBankAcc(item) {
                this.$router.push('/banks/detail/' + item.id).catch(() => {
                })
            },
            toDashboard(item) {
                this.$router.push('/dashboard/' + item.id).catch(() => {
                })
            },
            async detailAvailableBank(item) {
                this.$store.commit("setLoading", true)
                let creator = await getCreatorOfBankAcc(item.id)
                if (creator == null) {
                    this.$store.commit("setLoading", false)
                    this.$store.commit("setSnackbarText", "Server error!")
                    this.$store.commit("setSnackbarError", true)
                    return
                }
                this.name = item.name
                this.currency = item.currency
                this.balance = item.balance
                this.creator = creator.username
                this.$store.commit("setLoading", false)
            }
        },
        async mounted() {
            // if user not authenticated user route to login page
            if (!this.$store.state.user) {
                return await this.$router.push("/").catch(() => {
                })
            }
            this.$store.commit("setLoading", true)
            let createdBanks = await getAllUsersCreatedBanks()
            if (createdBanks == null) {
                this.$store.commit("setLoading", false)
                alert("Server error!")
                this.$store.commit("setUser", null)
                return await this.$router.push("/").catch(() => {
                })
            }
            let availableBanks = await getAllUsersAvailableBanks()
            if (availableBanks == null) {
                this.$store.commit("setLoading", false)
                this.$store.commit("setSnackbarText", "Server error! Cant get available bank accounts.")
                this.$store.commit("setSnackbarError", true)
                return
            }
            this.createdBanks = createdBanks
            this.availableBanks = availableBanks
            this.$store.commit("setLoading", false)
        }
    }
</script>
