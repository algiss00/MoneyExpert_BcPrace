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
                                    v-model="date"
                                    label="Měsíc"
                                    prepend-icon="mdi-calendar"
                                    readonly
                                    v-bind="attrs"
                                    v-on="on"
                            ></v-text-field>
                        </template>
                        <v-date-picker
                                v-model="date"
                                type="month"
                                no-title
                                scrollable
                        >
                            <v-spacer></v-spacer>
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
                                    @click="$refs.menu.save(date), getByMonth()"
                            >
                                OK
                            </v-btn>
                        </v-date-picker>
                    </v-menu>
                </v-card-text>
                <div class="font-weight-medium black--text m-left"
                     v-if="sumOfExpensesCategoryByMonth.length === 0">
                    No transactions in this period :)
                </div>
                <div class="font-weight-light black--text m-left"
                     v-if="sumOfExpensesCategoryByMonth.length !== 0">
                    Koláčový graf - výdaje podle kategorie
                </div>
                <div id="chartCategory" v-if="sumOfExpensesCategoryByMonth.length !== 0">
                    <apexchart type="pie" width="380" :options="chartOptions" :series="sumOfExpensesCategoryByMonth"/>
                </div>
                <v-spacer></v-spacer>

                <div id="chartCashflow" v-if="sumExpenseAndIncomeByMonth.length !== 0">
                    <apexchart type="bar" width="800" height="300" :options="chartOptions2"
                               :series="sumExpenseAndIncomeByMonth"/>
                </div>
            </v-card>
        </v-container>
    </v-app>
</template>

<script>
    import {
        getAllUsersCategories,
        getSumOfExpenseByMonth,
        getSumOfExpenseForCategoryByMonth,
        getSumOfIncomeByMonth
    } from "../../api";

    export default {
        name: "Statistic",
        data: () => {
            return {
                date: new Date().toISOString().substr(0, 7),
                menu: false,
                sumExpenseAndIncomeByMonth: [{
                    data: []
                }],
                sumOfExpensesCategoryByMonth: [],
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
                    colors: ['#00FF00', '#fb0018'],
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
                        enabled: false
                    },
                    xaxis: {
                        categories: ["INCOME", "EXPENSE"],
                    }
                },
            }
        },
        methods: {
            async getByMonth() {
                let month = this.date.substr(5, 8)
                let year = this.date.substr(0, 4)
                this.sumOfExpensesCategoryByMonth = []
                this.chartOptions.labels = []

                let categories = await getAllUsersCategories()
                if (categories == null) {
                    alert("Invalid data")
                    return
                }
                for (let i = 0; i < categories.length; i++) {
                    let sumExpense = await getSumOfExpenseForCategoryByMonth(this.$route.params.bankId, month, year, categories[i].id)
                    if (sumExpense == null) {
                        alert("Invalid data!")
                        return
                    }
                    if (sumExpense !== 0) {
                        this.sumOfExpensesCategoryByMonth.push(sumExpense)
                        this.chartOptions.labels.push(categories[i].name)
                    }
                }
            }
        },
        async mounted() {
            if (!this.$store.state.user) {
                return await this.$router.push("/")
            }
            let month = this.date.substr(5, 8)
            let year = this.date.substr(0, 4)

            let categories = await getAllUsersCategories()
            if (categories == null) {
                alert("Invalid data")
                return
            }
            for (let i = 0; i < categories.length; i++) {
                let sumExpenseCategory = await getSumOfExpenseForCategoryByMonth(this.$route.params.bankId, month, year, categories[i].id)
                if (sumExpenseCategory == null) {
                    alert("Invalid data!")
                    return
                }
                if (sumExpenseCategory !== 0) {
                    this.sumOfExpensesCategoryByMonth.push(sumExpenseCategory)
                    this.chartOptions.labels.push(categories[i].name)
                }
            }

            let sumExpense = await getSumOfExpenseByMonth(this.$route.params.bankId, month, year)
            let sumIncome = await getSumOfIncomeByMonth(this.$route.params.bankId, month, year)
            if (sumIncome !== 0) {
                this.sumExpenseAndIncomeByMonth[0].data.push(sumIncome)
            }
            if (sumExpense !== 0) {
                this.sumExpenseAndIncomeByMonth[0].data.push(sumExpense)
            }
        }
    }
</script>
