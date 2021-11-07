<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-toolbar dark color="primary">
                            <v-toolbar-title>Přihlášení</v-toolbar-title>
                        </v-toolbar>
                        <v-card-text>
                            <v-form>
                                <v-text-field
                                        id="username"
                                        label="username"
                                        v-model="username"
                                        hide-details="auto"
                                />
                                <v-text-field
                                        id="password"
                                        label="password"
                                        v-model="password"
                                        type=password
                                        hide-details="auto"
                                />
                            </v-form>
                        </v-card-text>
                        <v-card-actions>
                            <v-btn color="primary" to="/SignUp">Registrace</v-btn>
                        </v-card-actions>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn @click="login" color="primary">Login</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import {login} from "../api";

    export default {
        name: 'login',
        data: () => ({
            username: "",
            password: ""
        }),
        methods: {
            async login() {
                let usernameEl = document.getElementById("username")
                let passwordEl = document.getElementById("password")

                let result = await login(this.username, this.password)
                console.log(result)
                if (result.loggedIn === true && result.success === true && result.username === this.username) {
                    usernameEl.classList.remove("error");
                    passwordEl.classList.remove("error");
                    window.location = "./"
                } else {
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
</style>