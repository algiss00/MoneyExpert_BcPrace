<template>
    <v-app id="inspire">
        <v-item-group>
            <v-container>
                <v-row>
                    <v-col
                            v-model="actualInfo"
                            cols="12"
                            md="4"
                    >
                        <v-item>
                            <v-card
                                    class="d-flex align-center"
                                    dark
                                    color="#CFDAF0"
                                    height="200"
                            >
                                <v-card-title class="black--text">
                                    <p id="info" class="font-weight-regular black--text ma-2"/>
                                </v-card-title>
                            </v-card>
                        </v-item>
                    </v-col>
                    <v-col
                            v-for="item in cards"
                            :key="item.title"
                            cols="12"
                            md="4"
                    >
                        <v-item>
                            <v-card
                                    class="d-flex align-center"
                                    dark
                                    height="200"
                                    @click="toPage(item)"
                            >
                                <v-scroll-y-transition>
                                    <div
                                            class="text-h2 flex-grow-1 text-center"
                                    >
                                        {{item.title}}
                                    </div>
                                </v-scroll-y-transition>
                            </v-card>
                        </v-item>
                    </v-col>
                </v-row>
            </v-container>
        </v-item-group>
    </v-app>
</template>

<script>
    import {getAllNotificationBudgets, getAllNotificationDebts, getBankAccById} from "../../api";

    export default {
        name: "Dashboard",
        data: () => {
            return {
                cards: [

                    {title: 'Transakce', link: '/transactions/'},
                    {title: 'Statistiky', link: '/statistic/'},
                    {title: 'Závazky', link: '/debts/'},
                    {title: 'Rozpočty', link: '/budgets/'}
                ],
                actualInfo: {title: 'Aktuální info'},
                balance: ""
            }
        },
        methods: {
            toPage(item) {
                return this.$router.push(item.link + this.$route.params.bankId).catch(() => {
                })
            }
        },
        async mounted() {
            // if user not authenticated route to login page
            if (!this.$store.state.user) {
                return await this.$router.push("/").catch(() => {
                })
            }
            this.$store.commit("setLoading", true)
            let bankAcc = await getBankAccById(this.$route.params.bankId)
            if (bankAcc == null) {
                this.$store.commit("setLoading", false)
                alert("Server error!")
                location.reload()
                return
            }
            let infoEl = document.getElementById("info")
            infoEl.innerText = "Aktuální info o účtu: \n" + bankAcc.name + "\n Aktuální zůstatek: \n" + bankAcc.balance + " " + bankAcc.currency

            // set notify debts
            let debtsNotification = await getAllNotificationDebts(this.$route.params.bankId)
            if (debtsNotification == null) {
                this.$store.commit("setLoading", false)
                return
            }
            this.$store.commit("setNotificationDebt", debtsNotification)

            // set notify budgets
            let budgetsNotification = await getAllNotificationBudgets(this.$route.params.bankId)
            if (budgetsNotification == null) {
                this.$store.commit("setLoading", false)
                return
            }
            this.$store.commit("setNotificationBudget", budgetsNotification)
            this.$store.commit("setLoading", false)
        }
    }
</script>