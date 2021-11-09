<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-toolbar color="#e7f6ff">
                            <v-toolbar-title>Registrace</v-toolbar-title>
                        </v-toolbar>
                        <v-card-text>
                            <v-form>
                                <v-text-field
                                        id="usernameRegistr"
                                        label="username"
                                        v-model="username"
                                        hide-details="auto"
                                />
                                <v-text-field
                                        id="passwordRegistr"
                                        label="password"
                                        v-model="password"
                                        type=password
                                        hide-details="auto"
                                />
                                <v-text-field
                                        id="emailRegistr"
                                        label="email"
                                        v-model="email"
                                        type=email
                                        hide-details="auto"
                                />
                                <v-text-field
                                        id="nameRegistr"
                                        label="name"
                                        v-model="name"
                                        hide-details="auto"
                                />
                                <v-text-field
                                        id="lastnameRegistr"
                                        label="lastname"
                                        v-model="lastname"
                                        hide-details="auto"
                                />
                            </v-form>
                        </v-card-text>
                        <v-card-actions>
                            <v-btn color="#e7f6ff" to="/" class="m2-position">Zpět</v-btn>
                        </v-card-actions>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn @click="registr($event)" color="#e7f6ff" class="m3-position">Registrace</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import {registration, markAsError} from "../api";

    function validate() {
        let usernameEl = document.getElementById("usernameRegistr")
        let passwordEl = document.getElementById("passwordRegistr")
        let emailEl = document.getElementById("emailRegistr")
        let nameEl = document.getElementById("nameRegistr")
        let lasnameEl = document.getElementById("lastnameRegistr")

        if (usernameEl.value.trim().length === 0) {
            markAsError("usernameRegistr", true);
        } else {
            markAsError("usernameRegistr", false);
        }
        if (passwordEl.value.trim().length === 0) {
            markAsError("passwordRegistr", true);
        } else {
            markAsError("passwordRegistr", false);
        }
        if (emailEl.value.trim().length === 0 || !ValidateEmail(emailEl.value)) {
            markAsError("emailRegistr", true);
        } else {
            markAsError("emailRegistr", false);
        }
        if (nameEl.value.trim().length === 0) {
            markAsError("nameRegistr", true);
        } else {
            markAsError("nameRegistr", false);
        }
        if (lasnameEl.value.trim().length === 0) {
            markAsError("lastnameRegistr", true);
        } else {
            markAsError("lastnameRegistr", false);
        }

        return !(usernameEl.classList.value === "error" || passwordEl.classList.value === "error" || emailEl.classList.value === "error"
            || nameEl.classList.value === "error" || lasnameEl.classList.value === "error");
    }

    /**
     * @return {boolean}
     */
    function ValidateEmail(mail) {
        if (/^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/.test(mail)) {
            return true
        }
        alert("You have entered an invalid email address!")
        return false
    }

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
            async registr(event) {
                if (!validate()) {
                    event.preventDefault()
                    return
                }
                const jsonUser = JSON.stringify({
                    email: this.email,
                    name: this.name,
                    lastname: this.lastname,
                    username: this.username,
                    password: this.password
                });

                let result = await registration(jsonUser)

                if (result.data.username === this.username && result.data.email === this.email) {
                    alert("Success!")
                    await this.$router.push('/')
                }
            }
        }
    }
</script>

<style>
    .error {
        border: 3px solid red !important;
    }
</style>