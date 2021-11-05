<template>

    <v-card
            class="mx-auto "
            max-width="344"
            outlined
    >
        <v-list-item two-line>
            <v-list-item-content>
                <div>
                    <v-text-field
                            label="Username"
                            v-model="username"
                            hide-details="auto"
                    />
                    <v-text-field
                            label="Password"
                            v-model="password"
                            type=password
                            hide-details="auto"
                    />
                </div>
            </v-list-item-content>
        </v-list-item>

        <v-card-actions>
            <button @click="login"> Login</button>
        </v-card-actions>
    </v-card>
</template>

<script>
    import axios from "axios"

    export default {
        name: 'App',

        components: {},

        data: () => ({
            username: "",
            password: ""
        }),

        methods: {
            async login() {
                console.log(this.username, this.password)
                axios.post("http://localhost:8080/login", null, {
                    params: {
                        username: this.username,
                        password: this.password
                    }
                })
                    .then(response => {
                        console.log(response)
                        response.status
                    })
                    .catch(err => console.warn(err));
            }
            // async get() {
            //     const res = await axios.get("http://localhost:8080/user/");
            //     console.log(res)
            // },
            //
            // async registr() {
            //     const json = JSON.stringify({
            //         email: "12qwqqwsda@123",
            //         name: "qweqwe",
            //         lastname: "s",
            //         username: "qwe",
            //         password: "qwe"
            //     });
            //     const res = await axios.post("http://localhost:8080/user", json, {
            //         "headers": {
            //             "content-type": "application/json",
            //         }
            //     });
            //     console.log(res)
            // }
        }
    };
</script>
