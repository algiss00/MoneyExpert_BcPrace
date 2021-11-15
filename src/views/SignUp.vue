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
                            <v-form
                                    ref="form"
                                    v-model="valid"
                                    lazy-validation>
                                <v-text-field
                                        id="usernameRegistr"
                                        label="username"
                                        v-model="username"
                                        :rules="nameRules"
                                        hide-details="auto"
                                        required
                                />
                                <v-text-field
                                        id="passwordRegistr"
                                        label="password"
                                        v-model="password"
                                        :rules="passRules"
                                        type=password
                                        hide-details="auto"
                                        required
                                />
                                <v-text-field
                                        id="emailRegistr"
                                        label="email"
                                        v-model="email"
                                        type=email
                                        :rules="emailRules"
                                        hide-details="auto"
                                        required
                                />
                                <v-text-field
                                        id="nameRegistr"
                                        label="name"
                                        v-model="name"
                                        :rules="nameRules"
                                        hide-details="auto"
                                        required
                                />
                                <v-text-field
                                        id="lastnameRegistr"
                                        label="lastname"
                                        v-model="lastname"
                                        :rules="nameRules"
                                        hide-details="auto"
                                        required
                                />
                            </v-form>
                        </v-card-text>
                        <v-card-actions>
                            <v-btn color="#e7f6ff" to="/" class="m2-position">Zpět</v-btn>
                        </v-card-actions>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn @click="registr($event)" color="#e7f6ff" :disabled="!valid"
                                   class="m3-position">Registrace
                            </v-btn>
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
            lastname: "",
            nameRules: [
                v => !!v.trim() || 'Name is required'
            ],
            passRules: [
                v => !!v || 'Name is required'
            ],
            emailRules: [
                v => !!v.trim() || 'E-mail is required',
                v => /.+@.+/.test(v) || 'E-mail must be valid',
            ],
            valid: true,
        }),
        methods: {
            async registr(event) {
                if (!this.$refs.form.validate()) {
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