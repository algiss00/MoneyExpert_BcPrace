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
                        No budgets :)
                    </div>
                    <v-list v-if="budgets.length !== 0">
                        <v-list-item
                                v-for="item in budgets"
                                :key="item.id"
                                class="borders"
                                @click="toDetailBudget(item)"
                        >
                            <v-list-item-content>
                                <v-list-item-title v-text="item.name"/>
                                <v-list-item-action-text class="text-right text-sm-subtitle-1">
                                    {{item.category[0].name}}
                                    {{item.amount}}
                                </v-list-item-action-text>
                                <v-progress-linear
                                        :value="item.percentOfSumAmount"
                                        height="25"
                                >
                                    <template v-slot:default="{ value }">
                                        <strong>{{ Math.ceil(value) }}%</strong>
                                    </template>
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
    import {getAllBudgetsFromBankAcc} from "../../api";

    function getPercentOfSumAmount(budget) {
        return budget.sumAmount * 100 / budget.amount;
    }

    export default {
        data: () => ({
            budgets: [],
        }),
        methods: {
            toAddBudget() {
                this.$router.push('/budgets/' + this.$route.params.bankId + '/addBudget/')
            },
            toDetailBudget(item) {
                this.$router.push('/budgets/' + this.$route.params.bankId + '/detail/' + item.id)
            },
        },
        async mounted() {
            if (!this.$store.state.user) {
                return await this.$router.push("/")
            }
            let budgets = await getAllBudgetsFromBankAcc(this.$route.params.bankId)
            this.budgets = budgets
            if (this.budgets == null) {
                alert("Invalid bankAcc id")
                return
            }
            for (let budget of budgets) {
                let percentOfSumAmount = getPercentOfSumAmount(budget)
                this.$set(budget, 'percentOfSumAmount', percentOfSumAmount)
            }
        }
    }
</script>

<style>
    .borders {
        border-bottom: 2px solid black;
        margin-bottom: 15px;
    }
</style>