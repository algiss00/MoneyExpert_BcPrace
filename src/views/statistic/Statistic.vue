<template>
    <v-app id="inspire">
        <v-container fluid>
            <v-card color="#e7f6ff">
                <v-card-title class="mx-auto text--black">
                    Statistiky
                </v-card-title>
                <v-card-text>
                    <v-menu
                            ref="menu"
                            v-model="menu"
                            :close-on-content-click="false"
                            :return-value.sync="date"
                            transition="scale-transition"
                            offset-y
                            max-width="290px"
                            min-width="auto"
                    >
                        <template v-slot:activator="{ on, attrs }">
                            <v-text-field
                                    v-model="dateRangeText"
                                    label="Období"
                                    prepend-icon="mdi-calendar"
                                    readonly
                                    v-bind="attrs"
                                    v-on="on"
                            />
                        </template>
                        <v-date-picker
                                v-model="date"
                                range
                        >
                            <v-spacer/>
                            <v-btn
                                    text
                                    color="primary"
                                    @click="menu = false"
                            >
                                Cancel
                            </v-btn>
                            <v-btn
                                    text
                                    color="primary"
                                    @click="$refs.menu.save(date), getBetweenDate()"
                            >
                                OK
                            </v-btn>
                        </v-date-picker>
                    </v-menu>
                </v-card-text>

                <v-container fill-height v-if="loading===true">
                    <v-layout row justify-center align-center>
                        <v-progress-circular indeterminate :size="70" :width="7"
                                             color="primary"/>
                    </v-layout>
                </v-container>

                <div class="d-md-inline-flex" v-if="loading===false">
                    <div class="font-weight-medium black--text m-left"
                         v-if="sumOfExpensesCategoryBetweenDate.length === 0
                         && sumExpenseAndIncomeBetweenDate[0].data.length === 0
                         && sumExpenseAndIncomeBetweenDate[1].data.length === 0">
                        Žádné transakce v tomto období :)
                    </div>

                    <div id="chartCategory" v-if="sumOfExpensesCategoryBetweenDate.length !== 0" class="mr-5">
                        <div class="font-weight-light black--text m-left">
                            Koláčový graf - výdaje podle kategorie
                        </div>
                        <apexchart type="pie" width="380" :options="chartOptions"
                                   :series="sumOfExpensesCategoryBetweenDate"/>
                    </div>

                    <div id="chartCashflow" v-if="sumExpenseAndIncomeBetweenDate[0].data.length !== 0
                    || sumExpenseAndIncomeBetweenDate[1].data.length !== 0" class="ml-10">
                        <div class="font-weight-light black--text m-left">
                            Cashflow - utrácím méně než výdělávám?
                        </div>
                        <apexchart type="bar" width="600" height="300" :options="chartOptions2"
                                   :series="sumExpenseAndIncomeBetweenDate"/>
                    </div>
                </div>
            </v-card>
        </v-container>
    </v-app>
</template>

<script>
    import {
        getAllUsersCategories,
        getSumOfExpenseBetweenDate,
        getSumOfExpenseForCategoryBetweenDate,
        getSumOfIncomeBetweenDate
    } from "../../api";

    export default {
        name: "Statistic",
        data: () => {
            return {
                date: [new Date(new Date().getFullYear(), new Date().getMonth(), 2)
                    .toISOString().substr(0, 10),
                    new Date(new Date().getFullYear(), new Date().getMonth() + 1, 1)
                        .toISOString().substr(0, 10)],
                loading: true,
                menu: false,
                sumExpenseAndIncomeBetweenDate: [{
                    data: [],
                    name: "INCOME"
                },
                    {
                        data: [],
                        name: "EXPENSE"
                    }
                ],
                sumOfExpensesCategoryBetweenDate: [],
                chartOptions: {
                    labels: [],
                    chart: {
                        width: 380,
                        type: 'pie',
                    },
                    responsive: [{
                        breakpoint: 480,
                        options: {
                            chart: {
                                width: 200
                            },
                            legend: {
                                position: 'bottom'
                            }
                        }
                    }]
                },
                chartOptions2: {
                    colors: ['#61ff7e', '#fb0018'],
                    chart: {
                        type: 'bar',
                        height: 100
                    },
                    plotOptions: {
                        bar: {
                            borderRadius: 4,
                            horizontal: true,
                        }
                    },
                    dataLabels: {
                        enabled: true
                    },
                    tooltip: {
                        enabled: false
                    },
                    xaxis: {
                        categories: ["INCOME", "EXPENSE"],
                    }
                },
            }
        },
        computed: {
            dateRangeText() {
                return this.date.join(' ~ ')
            },
        },
        methods: {
            async getBetweenDate() {
                let from = this.date[0]
                let to = this.date[1]

                if (from === undefined || to === undefined) {
                    alert("Invalid date period! Must be start date and end date!")
                    return
                }
                if (from > to) {
                    alert("Invalid date period! Start date must be before end date!")
                    return
                }

                this.sumOfExpensesCategoryBetweenDate = []
                this.chartOptions.labels = []

                let categories = await getAllUsersCategories()
                if (categories == null) {
                    alert("Server error! No categories.")
                    return
                }

                this.loading = true
                // get sum of expenses for category
                for (let i = 0; i < categories.length; i++) {
                    let sumExpense = await getSumOfExpenseForCategoryBetweenDate(this.$route.params.bankId, from, to, categories[i].id)
                    if (sumExpense == null) {
                        return
                    }
                    if (sumExpense !== 0) {
                        this.sumOfExpensesCategoryBetweenDate.push(sumExpense)
                        this.chartOptions.labels.push(categories[i].name)
                    }
                }

                this.sumExpenseAndIncomeBetweenDate = [{
                    data: [],
                    name: "INCOME"
                },
                    {
                        data: [],
                        name: "EXPENSE"
                    }
                ]

                // get sum of expenses and incomes from bankAcc between date
                let sumExpense = await getSumOfExpenseBetweenDate(this.$route.params.bankId, from, to)
                let sumIncome = await getSumOfIncomeBetweenDate(this.$route.params.bankId, from, to)
                if (sumIncome !== 0) {
                    this.sumExpenseAndIncomeBetweenDate = [{
                        data: [...this.sumExpenseAndIncomeBetweenDate[0].data, sumIncome],
                        name: "INCOME"
                    },
                        this.sumExpenseAndIncomeBetweenDate[1]
                    ]
                }
                if (sumExpense !== 0) {
                    this.sumExpenseAndIncomeBetweenDate = [
                        this.sumExpenseAndIncomeBetweenDate[0],
                        {
                            data: [...this.sumExpenseAndIncomeBetweenDate[1].data, sumExpense],
                            name: "EXPENSE"
                        }
                    ]
                }
                this.loading = false
            }
        },
        async mounted() {
            // if user not authenticated user route to login page
            if (!this.$store.state.user) {
                return await this.$router.push("/").catch(() => {
                })
            }
            let from = this.date[0]
            let to = this.date[1]

            let categories = await getAllUsersCategories()
            if (categories == null) {
                alert("Server error! No categories.")
                return
            }
            this.loading = true
            // get sum of expenses for category
            for (let i = 0; i < categories.length; i++) {
                let sumExpenseCategory = await getSumOfExpenseForCategoryBetweenDate(this.$route.params.bankId,
                    from, to, categories[i].id)
                if (sumExpenseCategory == null) {
                    return
                }
                if (sumExpenseCategory !== 0) {
                    this.sumOfExpensesCategoryBetweenDate.push(sumExpenseCategory)
                    this.chartOptions.labels.push(categories[i].name)
                }
            }

            // get sum of expenses and incomes from bankAcc between date
            let sumExpense = await getSumOfExpenseBetweenDate(this.$route.params.bankId, from, to)
            let sumIncome = await getSumOfIncomeBetweenDate(this.$route.params.bankId, from, to)
            if (sumIncome !== 0) {
                this.sumExpenseAndIncomeBetweenDate = [{
                    data: [...this.sumExpenseAndIncomeBetweenDate[0].data, sumIncome],
                    name: "INCOME"
                },
                    this.sumExpenseAndIncomeBetweenDate[1]
                ]
            }
            if (sumExpense !== 0) {
                this.sumExpenseAndIncomeBetweenDate = [
                    this.sumExpenseAndIncomeBetweenDate[0],
                    {
                        data: [...this.sumExpenseAndIncomeBetweenDate[1].data, sumExpense],
                        name: "EXPENSE"
                    }
                ]
            }
            this.loading = false
        }
    }
</script>

<style>
    .loading-dialog {
        background-color: #303030;

        height: 600px;
    }
</style>
