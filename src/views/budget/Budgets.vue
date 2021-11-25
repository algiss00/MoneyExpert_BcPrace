<template>
    <v-app id="inspire">
        <v-container fluid>
            <v-card
                    color="#e7f6ff"
            >
                <v-card-title class="mx-auto">
                    Rozpočty
                    <v-fab-transition>
                        <v-btn
                                class="m4-position"
                                dark
                                small
                                color="green"
                                fab
                                @click="toAddBudget"
                        >
                            <v-icon>mdi-plus</v-icon>
                        </v-btn>
                    </v-fab-transition>
                </v-card-title>
                <v-card-text>
                    <div class="font-weight-medium black--text m-left" v-if="budgets.length === 0">
                        Žádné rozpočty :)
                    </div>
                    <v-list v-if="budgets.length !== 0">
                        <v-list-item
                                v-for="item in budgets"
                                :key="item.id"
                                class="borders"
                                @click="toDetailBudget(item)"
                        >
                            <v-list-item-content>
                                <v-alert
                                        dense
                                        type="error"
                                        v-if="isBudgetInPercentNotification(item) === true
                                        && isBudgetInAmountNotification(item) === false"
                                >
                                    Nastalo procento upozornění!
                                </v-alert>

                                <v-alert
                                        dense
                                        type="error"
                                        v-if="isBudgetInAmountNotification(item) === true"
                                >
                                    Nastal limit rozpočtu!
                                </v-alert>

                                <v-list-item-title v-text="item.name"/>
                                <v-list-item-action-text class="text-right text-sm-subtitle-1">
                                    {{item.category[0].name}}
                                    {{item.amount}}
                                </v-list-item-action-text>
                                <v-progress-linear
                                        :value="item.percentOfSumAmount"
                                        height="25"
                                        :color="item.percentOfSumAmount >= item.percentNotify ? 'red' : 'green'"
                                >
                                    <strong>{{ Math.ceil(item.sumAmount) }}</strong>
                                </v-progress-linear>
                            </v-list-item-content>
                        </v-list-item>
                    </v-list>
                </v-card-text>
            </v-card>
        </v-container>
    </v-app>
</template>

<script>
    import {getAllBudgetsFromBankAcc, getAllNotificationBudgets, getAllNotificationBudgetsByType} from "../../api";

    /**
     * get percent of budget.sumAmount
     * @param budget
     * @returns {number}
     */
    function getPercentOfSumAmount(budget) {
        return budget.sumAmount * 100 / budget.amount;
    }

    export default {
        data: () => ({
            budgets: [],
            alertPercentBudgets: [],
            alertAmountBudgets: [],
        }),
        methods: {
            toAddBudget() {
                this.$router.push('/budgets/' + this.$route.params.bankId + '/addBudget/').catch(() => {
                })
            },
            toDetailBudget(item) {
                this.$router.push('/budgets/' + this.$route.params.bankId + '/detail/' + item.id).catch(() => {
                })
            },
            /**
             * check if budget have notification with percent type
             * @param budget
             * @returns {boolean}
             */
            isBudgetInPercentNotification(budget) {
                for (let i = 0; i < this.alertPercentBudgets.length; i++) {
                    if (this.alertPercentBudgets[i].budget.id === budget.id) {
                        return true
                    }
                }
                return false
            },
            /**
             * check if budget have notification with amount type
             * @param budget
             * @returns {boolean}
             */
            isBudgetInAmountNotification(budget) {
                for (let i = 0; i < this.alertAmountBudgets.length; i++) {
                    if (this.alertAmountBudgets[i].budget.id === budget.id) {
                        return true
                    }
                }
                return false
            }
        },
        async mounted() {
            // if user not authenticated route to login page
            if (!this.$store.state.user) {
                return await this.$router.push("/").catch(() => {
                })
            }
            let budgets = await getAllBudgetsFromBankAcc(this.$route.params.bankId)
            this.budgets = budgets
            if (this.budgets == null) {
                alert("Invalid bankAcc id")
                return
            }

            // set notifications for Budget
            let notifyBudgets = await getAllNotificationBudgets(this.$route.params.bankId)
            this.$store.commit("setNotificationBudget", notifyBudgets)

            // set for each budget percentOfSumAmount and set alerts
            for (let budget of budgets) {
                let percentOfSumAmount = getPercentOfSumAmount(budget)
                this.$set(budget, 'percentOfSumAmount', percentOfSumAmount)
            }

            this.alertPercentBudgets = await getAllNotificationBudgetsByType(this.$route.params.bankId, "BUDGET_PERCENT")
            this.alertAmountBudgets = await getAllNotificationBudgetsByType(this.$route.params.bankId, "BUDGET_AMOUNT")
        }
    }
</script>

<style>
    .borders {
        border-bottom: 2px solid black;
        margin-bottom: 15px;
    }
</style>