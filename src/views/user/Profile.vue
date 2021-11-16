<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-toolbar color="#e7f6ff">
                            <v-toolbar-title>Profile</v-toolbar-title>
                            <v-card-actions>
                                <v-btn @click="logout">Logout</v-btn>
                            </v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn
                                    class="mx-2"
                                    icon
                                    @click="deleteProfile"
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
                                        label="jméno"
                                        v-model="name"
                                        :rules="nameRules"
                                        hide-details="auto"
                                        required
                                />
                                <v-text-field
                                        id="lastname"
                                        label="přijmení"
                                        :rules="nameRules"
                                        v-model="lastname"
                                        hide-details="auto"
                                        required
                                />
                                <v-btn color="#e7f6ff" :disabled="!valid" id="editNameLastname"
                                       @click="editNameLastname($event)" class="m-edit">
                                    Změnit jméno a přijmení
                                </v-btn>
                                <v-text-field
                                        id="email"
                                        label="email"
                                        v-model="email"
                                        :rules="emailRules"
                                        type=email
                                        hide-details="auto"
                                        required
                                />
                                <v-btn color="#e7f6ff" :disabled="!valid" id="editEmail" @click="editEmail($event)"
                                       class="m-edit">
                                    Změnit email
                                </v-btn>
                                <v-text-field
                                        id="username"
                                        label="username"
                                        v-model="username"
                                        :rules="usernameRules"
                                        hide-details="auto"
                                        required
                                />
                                <v-btn color="#e7f6ff" :disabled="!valid" id="editUsername"
                                       @click="editUsername($event)" class="m-edit">
                                    Změnit username
                                </v-btn>
                                <v-text-field
                                        id="passwordOld"
                                        label="původní heslo"
                                        v-model="passwordOld"
                                        type=password
                                        hide-details="auto"
                                        required
                                />
                                <v-text-field
                                        id="passwordNew"
                                        label="nové heslo"
                                        v-model="passwordNew"
                                        type=password
                                        hide-details="auto"
                                        required
                                />
                                <v-btn color="#e7f6ff" :disabled="!valid" id="editPassword"
                                       @click="editPassword($event)" class="m-edit">
                                    Změnit heslo
                                </v-btn>
                            </v-form>
                        </v-card-text>
                        <v-card-actions>
                            <v-btn color="#e7f6ff" to="/banks" class="mBack-position">Zpět</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import {
        editEmail,
        editNameLastname,
        editPassword,
        editUsername,
        getCurrentUserBackEnd,
        logout,
        removeUserProfile
    } from "../../api";

    export default {
        name: 'Profile',
        data: () => ({
            name: "",
            lastname: "",
            email: "",
            username: "",
            passwordOld: "",
            passwordNew: "",
            valid: true,
            nameRules: [
                v => !!v || 'required',
            ],
            usernameRules: [
                v => !!v || 'required',
                v => /^\w{0,20}$/.test(v) || 'invalid data'
            ],
            emailRules: [
                v => !!v.trim() || 'E-mail is required',
                v => /.+@.+/.test(v) || 'E-mail must be valid'
            ],
        }),
        methods: {
            async logout() {
                await logout()
                this.$store.commit("setUser", null)
                await this.$router.push('/')
            },
            async editNameLastname(event) {
                if (!this.$refs.form.validate()) {
                    event.preventDefault()
                    return
                }
                const user = JSON.stringify({
                    name: this.name,
                    lastname: this.lastname
                });

                let result = await editNameLastname(user)
                if (result == null || result.status !== 201) {
                    alert("Invalid data!")
                } else if (result.status === 201) {
                    alert("Success!")
                }
            },
            async editEmail(event) {
                if (!this.$refs.form.validate()) {
                    event.preventDefault()
                    return
                }
                let result = await editEmail(this.email)
                if (result == null || result.status !== 201) {
                    alert("Invalid data!")
                } else if (result.status === 201) {
                    alert("Success!")
                }
            },
            async editUsername(event) {
                if (!this.$refs.form.validate()) {
                    event.preventDefault()
                    return
                }
                let result = await editUsername(this.username)
                if (result == null || result.status !== 201) {
                    alert("Invalid data!")
                } else if (result.status === 201) {
                    alert("Success!")
                }
            },
            async editPassword(event) {
                let oldPasswordEl = document.getElementById("passwordOld")
                let newPasswordEl = document.getElementById("passwordNew")
                if (oldPasswordEl.value.trim().length === 0 || newPasswordEl.value.trim().length === 0) {
                    event.preventDefault()
                    alert("empty fields!")
                    return
                }

                let result = await editPassword(this.passwordOld, this.passwordNew)

                if (result == null || result.status !== 201) {
                    alert("Invalid data!")
                } else if (result.status === 201) {
                    alert("Success!")
                }
            },
            async deleteProfile(event) {
                if (!confirm("Opravdu checete smazat profile?")) {
                    event.preventDefault()
                    return
                }

                let result = await removeUserProfile()
                if (result == null || result.status !== 200) {
                    alert("Invalid delete!")
                } else if (result.status === 200) {
                    alert("Success!")
                    this.$store.commit("setUser", null)
                    await this.$router.push("/")
                }
            }
        },
        async mounted() {
            if (!this.$store.state.user) {
                return await this.$router.push("/")
            }
            let user = await getCurrentUserBackEnd()
            if (user == null) {
                alert("Invalid bankAcc id")
                return
            }
            this.name = user.data.name
            this.lastname = user.data.lastname
            this.email = user.data.email
            this.username = user.data.username
        }
    }
</script>

<style>
    .m-edit {
        margin: 15px;
        right: 20px;
    }

    .mBack-position {
        left: 250px;
        bottom: 20px;
    }
</style>