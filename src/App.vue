<template>
    <div id="app">
        <v-app-bar
                color="deep-purple accent-4"
                dark
        >
<!--            TODO Az se prihlasi-->
<!--          <v-btn icon>-->
<!--            <v-icon>mdi-account</v-icon>-->
<!--          </v-btn>-->

            <v-spacer></v-spacer>
            <v-toolbar-title>MoneyExpert</v-toolbar-title>
            <v-spacer></v-spacer>
        </v-app-bar>
        <router-view/>
    </div>
</template>

<style lang="scss">
    #app {
        font-family: Avenir, Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-align: center;
        color: #2c3e50;
    }

    #nav {
        padding: 30px;

        a {
            font-weight: bold;
            color: #2c3e50;

            &.router-link-exact-active {
                color: #42b983;
            }
        }
    }
</style>
