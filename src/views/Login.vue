<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-toolbar color="#e7f6ff">
                            <v-toolbar-title>Přihlášení</v-toolbar-title>
                        </v-toolbar>
                        <v-card-text>
                            <v-form>
                                <v-text-field
                                        id="usernameLogin"
                                        label="username"
                                        v-model="usernameLogin"
                                        hide-details="auto"
                                />
                                <v-text-field
                                        id="passwordLogin"
                                        label="password"
                                        v-model="passwordLogin"
                                        type=password
                                        hide-details="auto"
                                />
                            </v-form>
                        </v-card-text>
                        <v-card-actions>
                            <v-btn color="#e7f6ff" to="/signup" class="m2-position">Registrace</v-btn>
                        </v-card-actions>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn @click="login($event)" color="#e7f6ff" class="m3-position">Login</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import {login, markAsError, setCurrentUser, getUserByUsername} from "../api";


    function validate() {
        let usernameEl = document.getElementById("usernameLogin")
        let passwordEl = document.getElementById("passwordLogin")

        if (usernameEl.value.trim().length === 0) {
            markAsError("usernameLogin", true);
        } else {
            markAsError("usernameLogin", false);
        }
        if (passwordEl.value.trim().length === 0) {
            markAsError("passwordLogin", true);
        } else {
            markAsError("passwordLogin", false);
        }

        return !(usernameEl.classList.value === "error" || passwordEl.classList.value === "error")
    }

    export default {
        name: 'login',
        data: () => ({
            usernameLogin: "",
            passwordLogin: ""
        }),
        methods: {
            async login(event) {
                if (!validate()) {
                    event.preventDefault()
                    alert("Empty fields!")
                    return
                }
                let usernameEl = document.getElementById("usernameLogin")
                let passwordEl = document.getElementById("passwordLogin")

                let result = await login(this.usernameLogin, this.passwordLogin)
                if (result.loggedIn === true && result.success === true && result.username === this.usernameLogin) {
                    usernameEl.classList.remove("error");
                    passwordEl.classList.remove("error");
                    let user = await getUserByUsername(this.usernameLogin)

                    setCurrentUser(user)
                    await this.$router.push('/banks')
                } else {
                    if (result.errorMessage) {
                        alert(result.errorMessage)
                    }
                    usernameEl.classList.add("error")
                    passwordEl.classList.add("error")
                }
            }
        }
    };
</script>

<style>
    .error {
        border: 3px solid red !important;
    }

    .m2-position {
        position: relative;
        left: 10px;
        top: 20px;
    }

    .m3-position {
        position: relative;
        bottom: 33px;
        right: 10px;
    }
</style>