<template>
    <v-app>
        <v-app-bar color="#e7f6ff">
            <v-app-bar-nav-icon @click="drawer = true"
                                class="hidden-sm-and-up"/>

            <v-btn icon elevation="2" color="black" v-if="$store.state.user">
                <v-icon>
                    mdi-account
                </v-icon>
            </v-btn>
            <v-spacer/>
            <v-toolbar-title v-if="['Banks', 'Login'].includes($route.name)">
                MoneyExpert
            </v-toolbar-title>
            <v-spacer/>
            <v-toolbar-items v-if="$store.state.user && $route.name !== 'Banks'" class=" hidden-xs-only">
                <v-btn v-for="item in menuItems"
                       :key="item.title"
                       router
                       :to="item.link">
                    {{item.title}}
                </v-btn>
            </v-toolbar-items>
        </v-app-bar>
        <v-navigation-drawer v-model="drawer" absolute temporary>
            <v-list nav dense>
                <v-list-item v-for="item in menuItems" :key="item.title"
                             router
                             :to="item.link">
                    <v-list-item-content>
                        {{item.title}}
                    </v-list-item-content>
                </v-list-item>
            </v-list>
        </v-navigation-drawer>
        <main>
            <router-view/>
        </main>
    </v-app>
</template>

<script>

    import {getCurrentUserBackEnd} from "./api";

    export default {
        data() {
            return {
                drawer: false,
                menuItems: [
                    {title: 'Dashboard', link: '/dashboard'},
                    {title: 'Účty', link: '/banks'},
                    {title: 'Transakce', link: '/transactions'},
                    {title: 'Statistiky', link: '/statistic'},
                    {title: 'Závazky', link: '/debts'},
                    {title: 'Rozpočty', link: '/budgets'},
                    {title: 'Kategorie', link: '/categories'}
                ],
            }
        },
        async beforeMount() {
            let user = await getCurrentUserBackEnd()
            if (user) {
                this.$store.commit("setUser", user)
                await this.$router.push("/banks")
            }
        }
    }
</script>
