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
                            <v-form
                                    ref="form"
                                    v-model="valid"
                                    lazy-validation>
                                <v-text-field
                                        id="usernameLogin"
                                        label="username"
                                        v-model="usernameLogin"
                                        :rules="usernameRules"
                                        hide-details="auto"
                                        required
                                />
                                <v-text-field
                                        id="passwordLogin"
                                        label="password"
                                        v-model="passwordLogin"
                                        type=password
                                        :rules="passRules"
                                        required
                                        hide-details="auto"
                                />
                            </v-form>
                        </v-card-text>
                        <v-card-actions>
                            <v-btn color="#e7f6ff" to="/signup" class="m2-position">Registrace</v-btn>
                        </v-card-actions>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn @click="login($event)" :disabled="!valid" color="#e7f6ff" class="m3-position">Login
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import {login, getUserByUsername} from "../api";

    export default {
        name: 'login',
        data: () => ({
            usernameLogin: "",
            passwordLogin: "",
            usernameRules: [
                v => String(v).trim().length > 0 || 'username is required',
                v => /^\w{0,20}$/.test(v) || 'invalid username'// todo only english letters, cisla a underscore
            ],
            passRules: [
                v => !!v || 'password is required'
            ],
            valid: true,
        }),
        methods: {
            async login(event) {
                if (!this.$refs.form.validate()) {
                    event.preventDefault()
                    return
                }

                let result = await login(this.usernameLogin, this.passwordLogin)
                if (result.loggedIn === true && result.success === true && result.username === this.usernameLogin) {
                    let user = await getUserByUsername(this.usernameLogin)
                    this.$store.commit("setUser", user)
                    await this.$router.push('/banks')
                } else {
                    if (result.errorMessage) {
                        alert(result.errorMessage)
                    }
                }
            }
        },
        beforeMount() {
            if (this.$store.state.user) {
                this.$router.push('/banks')
            }
        }
    };
</script>

<style>
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