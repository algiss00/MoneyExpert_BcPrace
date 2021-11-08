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
                            <v-btn @click="registr($event)" color="#e7f6ff">Registrace</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import {registration} from "../api";

    function validate() {
        let usernameEl = document.getElementById("username")
        let passwordEl = document.getElementById("password")
        let emailEl = document.getElementById("email")
        let nameEl = document.getElementById("name")
        let lasnameEl = document.getElementById("lastname")

        if (usernameEl.value.trim().length === 0) {
            markAsError("username", true);
        } else {
            markAsError("username", false);
        }
        if (passwordEl.value.trim().length === 0) {
            markAsError("password", true);
        } else {
            markAsError("password", false);
        }
        if (emailEl.value.trim().length === 0 || !ValidateEmail(emailEl.value)) {
            markAsError("email", true);
        } else {
            markAsError("email", false);
        }
        if (nameEl.value.trim().length === 0) {
            markAsError("name", true);
        } else {
            markAsError("name", false);
        }
        if (lasnameEl.value.trim().length === 0) {
            markAsError("lastname", true);
        } else {
            markAsError("lastname", false);
        }

        return !(usernameEl.classList.value === "error" || passwordEl.classList.value === "error" || emailEl.classList.value === "error"
            || nameEl.classList.value === "error" || lasnameEl.classList.value === "error");
    }

    function markAsError(id, add_remove) {
        let element = document.getElementById(id);
        if (element == null) {
            return;
        }
        if (add_remove) {
            element.classList.add("error");
        } else {
            element.classList.remove("error");
        }
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