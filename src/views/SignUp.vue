<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-toolbar dark color="primary">
                            <v-toolbar-title>Registrace</v-toolbar-title>
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
                                <v-text-field
                                        id="email"
                                        label="email"
                                        v-model="email"
                                        type=email
                                        hide-details="auto"
                                />
                                <v-text-field
                                        id="name"
                                        label="name"
                                        v-model="name"
                                        hide-details="auto"
                                />
                                <v-text-field
                                        id="lastname"
                                        label="lastname"
                                        v-model="lastname"
                                        hide-details="auto"
                                />
                            </v-form>
                        </v-card-text>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn @click="registr" color="primary">Registrace</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import {registration} from "../api";

    export default {
        name: 'SignUp',
        data: () => ({
            username: "",
            password: "",
            email: "",
            name: "",
            lastname: ""
        }),
        methods: {
            async registr() {
                // let usernameEl = document.getElementById("username")
                // let passwordEl = document.getElementById("password")

                const jsonUser = JSON.stringify({
                    email: this.email,
                    name: this.name,
                    lastname: this.lastname,
                    username: this.username,
                    password: this.password
                });

                let result = await registration(jsonUser)
                // todo Validace result and clear fileds
                console.log(result)
            }
        }
    };
</script>

<style>
    .error {
        border: 3px solid red !important;
    }
</style>