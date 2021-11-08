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
                            <v-btn color="#e7f6ff" to="/signup" class="m2-position">Registrace</v-btn>
                        </v-card-actions>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn @click="login" color="#e7f6ff" class="m3-position">Login</v-btn>
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
                    await this.$router.push('/banks')
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