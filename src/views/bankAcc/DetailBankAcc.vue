<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-toolbar color="#e7f6ff">
                            <v-toolbar-title>Detail účtu</v-toolbar-title>
                            <v-btn
                                    icon
                                    @click="dialog = true"
                            >
                                <v-icon
                                        color="blue"
                                >
                                    mdi-share
                                </v-icon>
                            </v-btn>
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
                                                v-model="username"
                                                hide-details="auto"
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
                            <v-spacer></v-spacer>
                            <v-btn
                                    class="mx-2"
                                    icon
                                    @click="removeBankAcc($event)"
                            >
                                <v-icon
                                        color="red"
                                >
                                    mdi-delete
                                </v-icon>
                            </v-btn>
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
                                <v-btn color="#e7f6ff" @click="editBankAcc($event)" id="editBtnBank">Změnit název, měnu,
                                    balance
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
                                            <v-list>
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
                                                            fab
                                                            dark
                                                            small
                                                            color="white"
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
                                        label="creator"
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
        markAsError,
        removeBankAcc,
        getUserByUsername,
        shareBankAccount,
        getCurrentUserBackEnd, getAllOwnersOfBankAcc, removeOwnerFromBankAcc
    } from "../../api";

    function validateUsername() {
        let username = document.getElementById("username")

        if (username.value.trim().length === 0) {
            markAsError("username", true);
        } else {
            markAsError("username", false);
        }

        return !(username.classList.value === "error");
    }

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
            owners: []
        }),
        methods: {
            async editBankAcc(event) {
                if (!validate()) {
                    event.preventDefault()
                    return
                }

                const jsonBank = JSON.stringify({
                    name: this.name,
                    currency: this.currency,
                    balance: this.balance
                });

                let result = await editBankAcc(jsonBank, this.$route.params.id)
                if (result == null || result.status !== 201) {
                    return
                } else if (result.status === 201) {
                    alert("Success!")
                }
            },
            async removeBankAcc(event) {
                if (!confirm("Opravdu checete smazat účet?")) {
                    event.preventDefault()
                    return
                }

                let result = await removeBankAcc(this.$route.params.id)
                if (result == null || result.status !== 200) {
                    return
                } else if (result.status === 200) {
                    alert("Success!")
                    await this.$router.push('/banks/')
                }
            },
            async shareBankAcc(event) {
                if (!validateUsername()) {
                    event.preventDefault()
                    return
                }

                let user = await getUserByUsername(this.username)
                let result = await shareBankAccount(user.username, this.$route.params.id)
                if (result == null || result.status !== 201) {
                    this.username = ""
                    this.dialog = false
                    return
                } else {
                    alert("Success!")
                    this.username = ""
                    this.dialog = false
                }
            },
            async getOwnersOfBank() {
                let owners = await getAllOwnersOfBankAcc(this.$route.params.id)
                this.owners = owners
                if (this.owners.isEmpty) {
                    //todo
                }
            },
            async removeOwnerFromBankAcc(event, user) {
                if (!confirm("Opravdu checete smazat vlastnika?")) {
                    event.preventDefault()
                    return
                }
                console.log(user)

                let result = await removeOwnerFromBankAcc(user.id, this.$route.params.id)
                if (result == null || result.status !== 200) {
                    return
                } else if (result.status === 200) {
                    alert("Success!")
                    this.ownersDialog = false
                }
            }
        },
        async mounted() {
            // TODO - set Creator! nejak mit nastevneho current usera
            let result = await getBankAccById(this.$route.params.id)
            let currentUser = await getCurrentUserBackEnd()
            this.creator = currentUser.username
            this.name = result.name
            this.currency = result.currency
            this.balance = result.balance
            console.log(result)
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
        border: 2px solid black
    }
</style>