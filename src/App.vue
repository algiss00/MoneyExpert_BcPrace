<template>
    <v-app>
        <v-app-bar color="#e7f6ff">
            <v-app-bar-nav-icon @click="drawer = true"
                                class="hidden-md-and-up"
                                v-if="$store.state.user && !(['Banks', 'Login', 'SignUp', 'AddBankAcc', 'DetailBankAcc'].includes($route.name))"/>

            <v-btn icon elevation="2" color="black" v-if="$store.state.user"
                   title="profile" @click.stop="profileDrawer = true, startDialogProfile()">
                <v-icon>
                    mdi-account
                </v-icon>
            </v-btn>
            <v-spacer/>
            <v-toolbar-title>
                <v-btn @click="toBanks" text class="text-capitalize text-h6">
                    MoneyExpert
                </v-btn>
            </v-toolbar-title>

            <v-spacer/>
            <v-toolbar-items
                    v-if="$store.state.user && !(['Banks', 'Login', 'SignUp', 'AddBankAcc', 'DetailBankAcc'].includes($route.name))"
                    class="hidden-sm-and-down">
                <v-btn v-for="item in menuItems"
                       :key="item.title"
                       router
                       @click="toPage(item)">
                    {{item.title}}
                    <v-icon
                            v-if="item.title === 'Závazky' && $store.state.notificationDebt.length > 0"
                            color="red"
                    >
                        mdi-alert-circle
                    </v-icon>

                    <v-icon
                            v-if="item.title === 'Rozpočty' && $store.state.notificationBudget.length > 0"
                            color="red"
                    >
                        mdi-alert-circle
                    </v-icon>
                </v-btn>
            </v-toolbar-items>

            <v-progress-linear
                    :active="$store.state.loading"
                    :indeterminate="$store.state.loading"
                    absolute
                    bottom
                    color="deep-purple accent-4"
            />
        </v-app-bar>
        <v-navigation-drawer v-model="drawer" absolute temporary>
            <v-list nav dense>
                <v-list-item v-for="item in menuItems" :key="item.title"
                             router
                             @click="toPage(item)">
                    <v-list-item-content>
                        {{item.title}}
                    </v-list-item-content>
                </v-list-item>
            </v-list>
        </v-navigation-drawer>

        <v-snackbar
                v-model="$store.state.snackbar"
                :timeout="timeout"
                color="success"
        >
            {{ text }}

            <template v-slot:action="{ attrs }">
                <v-btn
                        color="black"
                        icon
                        v-bind="attrs"
                        @click="$store.commit('setSnackbar', false)"
                >
                    <v-icon>
                        mdi-close
                    </v-icon>
                </v-btn>
            </template>
        </v-snackbar>

        <v-snackbar
                v-model="$store.state.snackbarError"
                :timeout="timeoutError"
                centered
                color="red"
        >
            {{ $store.state.textSnack }}

            <template v-slot:action="{ attrs }">
                <v-btn
                        color="black"
                        icon
                        v-bind="attrs"
                        @click="$store.commit('setSnackbarError', false)"
                >
                    <v-icon>
                        mdi-close
                    </v-icon>
                </v-btn>
            </template>
        </v-snackbar>

        <v-dialog
                v-model="profileDrawer"
        >
            <v-card class="elevation-12">
                <v-toolbar color="#e7f6ff">
                    <v-toolbar-title>Profile</v-toolbar-title>
                    <v-btn
                            class="mx-2"
                            icon
                            @click="deleteProfile"
                            title="Smazat profile"
                            :disabled="!valid"
                    >
                        <v-icon
                                color="red"
                        >
                            mdi-delete
                        </v-icon>
                    </v-btn>
                    <v-spacer></v-spacer>
                    <v-card-actions>
                        <v-btn
                                text
                                @click="logout"
                                title="Logout"
                                color="black"
                        >
                            <v-icon
                            >
                                mdi-logout
                            </v-icon>
                            LOGOUT
                        </v-btn>
                    </v-card-actions>
                </v-toolbar>
                <v-card-text>
                    <v-form
                            ref="form"
                            v-model="valid"
                            lazy-validation>
                        <v-text-field
                                class="mt-5"
                                id="nameProfile"
                                label="jméno"
                                v-model="name"
                                :rules="nameRules"
                                hide-details="auto"
                                required
                        />
                        <v-text-field
                                id="lastnameProfile"
                                label="přijmení"
                                :rules="nameRules"
                                v-model="lastname"
                                hide-details="auto"
                                required
                        />
                        <v-btn color="primary" :disabled="!valid" id="editNameLastname"
                               @click="editNameLastname($event)" class="m-edit">
                            Změnit jméno a přijmení
                        </v-btn>
                        <v-text-field
                                id="emailProfile"
                                label="email"
                                v-model="email"
                                :rules="emailRules"
                                type=email
                                hide-details="auto"
                                required
                        />
                        <v-btn color="primary" :disabled="!valid" id="editEmail" @click="editEmail($event)"
                               class="m-edit">
                            Změnit email
                        </v-btn>
                        <v-text-field
                                id="usernameProfile"
                                label="username"
                                v-model="username"
                                :rules="usernameRules"
                                hide-details="auto"
                                required
                        />
                        <v-btn color="primary" :disabled="!valid" id="editUsername"
                               @click="editUsername($event)" class="m-edit">
                            Změnit username
                        </v-btn>
                        <v-text-field
                                id="passwordOldProfile"
                                label="původní heslo"
                                v-model="passwordOld"
                                type=password
                                hide-details="auto"
                                required
                        />
                        <v-text-field
                                id="passwordNewProfile"
                                label="nové heslo"
                                v-model="passwordNew"
                                type=password
                                hide-details="auto"
                                required
                        />
                        <v-btn color="primary" :disabled="!valid" id="editPassword"
                               @click="editPassword($event)" class="m-edit">
                            Změnit heslo
                        </v-btn>
                    </v-form>

                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="#e7f6ff" @click="profileDrawer = false">Zavřit</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <main>
            <router-view/>
        </main>
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
    } from "./api";

    export default {
        data() {
            return {
                drawer: false,
                profileDrawer: false,
                menuItems: [
                    {title: 'Účty', link: '/banks'},
                    {title: 'Dashboard', link: '/dashboard/'},
                    {title: 'Transakce', link: '/transactions/'},
                    {title: 'Statistiky', link: '/statistic/'},
                    {title: 'Závazky', link: '/debts/'},
                    {title: 'Rozpočty', link: '/budgets/'},
                    {title: 'Kategorie', link: '/categories/'}
                ],
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
                timeout: 2000,
                text: "Success",
                timeoutError: 4000,
            }
        },
        methods: {
            toBanks() {
                this.$router.push('/banks/').catch(() => {
                })
            },
            // route to page
            async toPage(item) {
                if (item.title === 'Účty') {
                    return this.$router.push(item.link).catch(() => {
                    })
                }
                return this.$router.push(item.link + this.$route.params.bankId).catch(() => {
                })
            },
            // profile dialog open
            async startDialogProfile() {
                if (!this.$store.state.user) {
                    return await this.$router.push("/").catch(() => {
                    })
                }
                this.$store.commit("setLoading", true)
                let user = await getCurrentUserBackEnd()
                if (user == null) {
                    this.$store.commit("setLoading", false)
                    this.$store.commit("setSnackbarText", "Server error!")
                    this.$store.commit("setSnackbarError", true)
                    return
                }
                this.name = user.data.name
                this.lastname = user.data.lastname
                this.email = user.data.email
                this.username = user.data.username
                this.passwordOld = ""
                this.passwordNew = ""
                this.$store.commit("setLoading", false)
            },
            async logout() {
                await logout()
                this.profileDrawer = false
                this.$store.commit("setUser", null)
                await this.$router.push('/').catch(() => {
                })
            },
            async editNameLastname(event) {
                if (!this.$refs.form.validate()) {
                    event.preventDefault()
                    return
                }
                this.$store.commit("setLoading", true)
                const user = JSON.stringify({
                    name: this.name,
                    lastname: this.lastname
                });

                let result = await editNameLastname(user)
                if (result == null || result.status !== 201) {
                    this.$store.commit("setSnackbarText", "Server error!")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.status === 201) {
                    this.$store.commit("setSnackbar", true)
                }
                this.$store.commit("setLoading", false)
            },
            async editEmail(event) {
                if (!this.$refs.form.validate()) {
                    event.preventDefault()
                    return
                }
                this.$store.commit("setLoading", true)
                let result = await editEmail(this.email)
                if (result == null || result.status !== 201) {
                    this.$store.commit("setSnackbarText", "Server error! Maybe email already exists.")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.status === 201) {
                    this.$store.commit("setSnackbar", true)
                }
                this.$store.commit("setLoading", false)
            },
            async editUsername(event) {
                if (!this.$refs.form.validate()) {
                    event.preventDefault()
                    return
                }
                this.$store.commit("setLoading", true)
                let result = await editUsername(this.username)
                if (result == null || result.status !== 201) {
                    this.$store.commit("setSnackbarText", "Server error! Maybe username already exists.")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.status === 201) {
                    this.$store.commit("setSnackbar", true)
                }
                this.$store.commit("setLoading", false)
            },
            async editPassword(event) {
                if (this.passwordOld.length === 0 || this.passwordNew.length === 0) {
                    event.preventDefault()
                    this.$store.commit("setSnackbarText", "Empty fields!")
                    this.$store.commit("setSnackbarError", true)
                    return
                }
                this.$store.commit("setLoading", true)
                let result = await editPassword(this.passwordOld, this.passwordNew)

                if (result == null || result.status !== 201) {
                    this.$store.commit("setSnackbarText", "Server error! Maybe old password not equal to actual password.")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.status === 201) {
                    this.$store.commit("setSnackbar", true)
                    this.passwordOld = ""
                    this.passwordNew = ""
                }
                this.$store.commit("setLoading", false)
            },
            async deleteProfile(event) {
                if (!confirm("Opravdu checete smazat profile?")) {
                    event.preventDefault()
                    return
                }

                this.$store.commit("setLoading", true)
                let result = await removeUserProfile()
                if (result == null || result.status !== 200) {
                    this.$store.commit("setSnackbarText", "Server error!")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.status === 200) {
                    this.$store.commit("setSnackbar", true)
                    this.profileDrawer = false
                    this.$store.commit("setUser", null)
                    await this.$router.push("/").catch(() => {
                    })
                }
                this.$store.commit("setLoading", false)
            }
        },
        /**
         * get current authenticated user from back-end
         * and set him on $store
         * invoked when the page is reloaded
         * @returns {Promise<void>}
         */
        async beforeMount() {
            let user = await getCurrentUserBackEnd()
            if (user) {
                this.$store.commit("setUser", user)
                await this.$router.push("/banks").catch(() => {
                })
            }
        }
    }
</script>

<style>
    .m-edit {
        margin: 15px;
        right: 20px;
    }
</style>
