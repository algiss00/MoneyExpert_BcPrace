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
    export default {
        data() {
            return {
                drawer: false,
                menuItems: [
                    // todo - maybe add some icons in future
                    {icon: '', title: 'Profile', link: '/profile'},
                    {icon: '', title: 'Dashboard', link: '/dashboard'},
                    {icon: '', title: 'Účty', link: '/banks'},
                    {icon: '', title: 'Transakce', link: '/transactions'},
                    {icon: '', title: 'Statistiky', link: '/statistic'},
                    {icon: '', title: 'Závazky', link: '/debts'},
                    {icon: '', title: 'Rozpočty', link: '/budgets'},
                    {icon: '', title: 'Kategorie', link: '/categories'}
                ]
            }
        }
    }
</script>
