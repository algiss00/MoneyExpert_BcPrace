<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-dialog
                                v-model="dialog"
                                max-width="500px"
                        >
                            <v-card>
                                <v-card-title>
                                    <span>Sdílet bankovní účet</span>
                                </v-card-title>
                                <v-card-text>
                                    <v-text-field
                                            id="username"
                                            label="username"
                                            :rules="rules"
                                            v-model="username"
                                            hide-details="auto"
                                    />
                                </v-card-text>
                                <v-card-actions>
                                    <v-btn
                                            color="primary"
                                            text
                                            @click="dialog = false, username = ''"
                                    >
                                        Zavřit
                                    </v-btn>
                                    <v-spacer/>
                                    <v-btn
                                            color="primary"
                                            text
                                            @click="shareBankAcc($event)"
                                    >
                                        Sdílet
                                    </v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-dialog>
                        <v-toolbar color="#e7f6ff">
                            <v-toolbar-title>Detail účtu</v-toolbar-title>
                            <v-btn
                                    icon
                                    @click="dialog = true"
                                    title="Sdílet bank account"
                                    :disabled="!valid"
                            >
                                <v-icon
                                        color="blue"
                                >
                                    mdi-share
                                </v-icon>
                            </v-btn>
                            <v-spacer/>
                            <v-btn
                                    class="mx-2"
                                    icon
                                    @click="removeBankAcc($event)"
                                    title="Smazat bank account"
                                    :disabled="!valid"
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
                                        id="nameBankAcc"
                                        label="název"
                                        v-model="name"
                                        :rules="rules"
                                        hide-details="auto"
                                        required
                                />
                                <v-select
                                        id="currencyBankAcc"
                                        :items="items"
                                        :rules="[v => !!v || 'Item is required']"
                                        v-model="currency"
                                        label="měna"
                                />
                                <v-text-field
                                        id="balanceBankAcc"
                                        label="zůstatek"
                                        v-model="balance"
                                        :rules="balanceRules"
                                        hide-details="auto"
                                        required
                                />
                                <v-btn color="primary" text @click="editBankAcc($event)" :disabled="!valid"
                                       id="editBtnBank">
                                    Změnit název, měnu,
                                    zůstatek
                                </v-btn>
                                <v-spacer>
                                </v-spacer>
                                <v-btn color="#e7f6ff" id="ownersBtn" @click="ownersDialog = true, getOwnersOfBank()">
                                    Seznam vlastniků
                                </v-btn>
                                <v-dialog
                                        v-model="ownersDialog"
                                        max-width="500px"
                                >
                                    <v-card>
                                        <v-card-title>
                                            <span>Seznam vlastniků</span>
                                        </v-card-title>
                                        <v-card-text>
                                            <div class="font-weight-light black--text"
                                                 v-if="owners.length === 0">
                                                Žádné vlastniky :)
                                            </div>
                                            <v-list v-if="owners.length !== 0">
                                                <v-list-item
                                                        v-for="item in owners"
                                                        :key="item.id"
                                                        id="ownersList"
                                                >
                                                    <v-list-item-content>
                                                        <v-list-item-title v-text="item.username"></v-list-item-title>
                                                    </v-list-item-content>
                                                    <v-btn
                                                            class="mx-2"
                                                            icon
                                                            @click="removeOwnerFromBankAcc($event, item)"
                                                    >
                                                        <v-icon
                                                                color="red"
                                                        >
                                                            mdi-delete
                                                        </v-icon>
                                                    </v-btn>
                                                </v-list-item>
                                            </v-list>
                                        </v-card-text>
                                        <v-card-actions>
                                            <v-spacer></v-spacer>
                                            <v-btn
                                                    color="primary"
                                                    text
                                                    @click="ownersDialog = false"
                                            >
                                                Zavřit
                                            </v-btn>
                                        </v-card-actions>
                                    </v-card>
                                </v-dialog>
                                <v-text-field
                                        id="creator"
                                        label="tvůrce"
                                        v-model="creator"
                                        hide-details="auto"
                                        disabled
                                />
                            </v-form>
                        </v-card-text>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn color="#e7f6ff" to="/banks">Zpět</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import {
        getBankAccById,
        editBankAcc,
        removeBankAcc,
        getUserByUsername,
        shareBankAccount,
        getAllOwnersOfBankAcc, removeOwnerFromBankAcc,
        getCreatorOfBankAcc
    } from "../../api";

    export default {
        name: 'DetailBankAcc',
        data: () => ({
            name: "",
            currency: "",
            balance: "",
            items: ['CZK', 'EUR'],
            dialog: false,
            username: "",
            creator: "",
            ownersDialog: false,
            owners: [],
            rules: [
                v => String(v).trim().length > 0 || 'required'
            ],
            balanceRules: [
                v => !Number.isNaN(Number(v)) || 'must be number',
                v => String(v).trim().length > 0 || 'required'
            ],
            valid: true,
        }),
        methods: {
            async editBankAcc(event) {
                if (!this.$refs.form.validate()) {
                    event.preventDefault()
                    return
                }
                this.$store.commit("setLoading", true)
                const jsonBank = JSON.stringify({
                    name: this.name,
                    currency: this.currency,
                    balance: this.balance
                });

                let result = await editBankAcc(jsonBank, this.$route.params.bankId)
                if (result == null || result.status !== 201) {
                    this.$store.commit("setSnackbarText", "Server error!")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.status === 201) {
                    this.$store.commit("setSnackbar", true)
                }
                this.$store.commit("setLoading", false)
            },
            async removeBankAcc(event) {
                if (!confirm("Opravdu checete smazat účet?")) {
                    event.preventDefault()
                    return
                }

                this.$store.commit("setLoading", true)
                let result = await removeBankAcc(this.$route.params.bankId)
                if (result == null || result.status !== 200) {
                    this.$store.commit("setSnackbarText", "Server error!")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.status === 200) {
                    this.$store.commit("setSnackbar", true)
                    await this.$router.push('/banks/').catch(() => {
                    })
                }
                this.$store.commit("setLoading", false)
            },
            async shareBankAcc(event) {
                let usernameEl = document.getElementById("username")
                if (usernameEl.value.trim().length === 0) {
                    event.preventDefault()
                    this.$store.commit("setSnackbarText", "Empty fields!")
                    this.$store.commit("setSnackbarError", true)
                    return
                }
                this.$store.commit("setLoading", true)
                let user = await getUserByUsername(this.username)
                if (user == null) {
                    this.$store.commit("setLoading", false)
                    this.$store.commit("setSnackbarText", "User not exists!")
                    this.$store.commit("setSnackbarError", true)
                    this.username = ''
                    return
                }
                let result = await shareBankAccount(user.username, this.$route.params.bankId)
                if (result == null || result.status !== 201) {
                    this.username = ""
                    this.dialog = false
                    this.$store.commit("setSnackbarText", "Server error!")
                    this.$store.commit("setSnackbarError", true)
                } else {
                    this.$store.commit("setSnackbar", true)
                    this.username = ""
                    this.dialog = false
                }
                this.$store.commit("setLoading", false)
            },
            async getOwnersOfBank() {
                this.$store.commit("setLoading", true)
                let owners = await getAllOwnersOfBankAcc(this.$route.params.bankId)
                if (owners == null) {
                    this.$store.commit("setLoading", false)
                    this.$store.commit("setSnackbarText", "Server error!")
                    this.$store.commit("setSnackbarError", true)
                    return
                }
                this.owners = owners
                this.$store.commit("setLoading", false)
            },
            async removeOwnerFromBankAcc(event, user) {
                if (!confirm("Opravdu checete smazat vlastnika?")) {
                    event.preventDefault()
                    return
                }
                this.$store.commit("setLoading", true)
                let result = await removeOwnerFromBankAcc(user.id, this.$route.params.bankId)
                if (result == null || result.status !== 200) {
                    this.$store.commit("setSnackbarText", "Server error!")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.status === 200) {
                    this.$store.commit("setSnackbar", true)
                    this.ownersDialog = false
                }
                this.$store.commit("setLoading", false)
            }
        },
        async mounted() {
            // if user not authenticated route to login page
            if (!this.$store.state.user) {
                return await this.$router.push("/").catch(() => {
                })
            }
            this.$store.commit("setLoading", true)
            let result = await getBankAccById(this.$route.params.bankId)
            if (result == null) {
                this.$store.commit("setLoading", false)
                alert("Server error!")
                location.reload()
                return
            }
            let creator = await getCreatorOfBankAcc(this.$route.params.bankId)
            if (creator == null) {
                this.$store.commit("setLoading", false)
                this.$store.commit("setSnackbarText", "Server error!")
                this.$store.commit("setSnackbarError", true)
                return
            }
            this.creator = creator.username
            this.name = result.name
            this.currency = result.currency
            this.balance = result.balance
            this.$store.commit("setLoading", false)
        }
    }
</script>

<style>
    #editBtnBank {
        margin: 15px;
        right: 20px;
    }

    #ownersBtn {
        margin: 15px;
        right: 20px;
    }

    #ownersList {
        margin: 5px;
        border-bottom: 2px solid black
    }
</style>