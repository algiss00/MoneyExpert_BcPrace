<template>
    <v-app>
        <v-app-bar color="#e7f6ff">
            <v-app-bar-nav-icon @click="drawer = true"
                                class="hidden-sm-and-up"></v-app-bar-nav-icon>
            <v-toolbar-title>
                <router-link to="/" tag="span" style="cursor: pointer">
                    MoneyExpert
                </router-link>
            </v-toolbar-title>
            <v-spacer></v-spacer>
            <v-toolbar-items class="hidden-xs-only">
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
    // import {getCurrentUser} from "../src/api";

    export default {
        data() {
            return {
                drawer: false,
            }
        },
        computed: {
            menuItems() {
                let menuItems = [
                    {title: 'Profile', link: '/profile'},
                    {title: 'Dashboard', link: '/dashboard'},
                    {title: 'Účty', link: '/banks'},
                    {title: 'Transakce', link: '/transactions'},
                    {title: 'Statistiky', link: '/statistic'},
                    {title: 'Závazky', link: '/debts'},
                    {title: 'Rozpočty', link: '/budgets'},
                    {title: 'Kategorie', link: '/categories'}
                ];
                // if (getCurrentUser() != null) {
                //     menuItems = [
                //         {title: 'Profile', link: '/profile'},
                //         {title: 'Dashboard', link: '/dashboard'},
                //         {title: 'Účty', link: '/banks'},
                //         {title: 'Transakce', link: '/transactions'},
                //         {title: 'Statistiky', link: '/statistic'},
                //         {title: 'Závazky', link: '/debts'},
                //         {title: 'Rozpočty', link: '/budgets'},
                //         {title: 'Kategorie', link: '/categories'}
                //     ]
                // }
                return menuItems
            }
        }
    }
</script>
