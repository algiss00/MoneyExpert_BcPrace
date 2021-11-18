<template>
    <v-app id="inspire">
        <v-container fluid fill-height>
            <v-layout align-center justify-center>
                <v-flex xs12 sm8 md4>
                    <v-card class="elevation-12">
                        <v-toolbar color="#e7f6ff">
                            <v-toolbar-title>Detail rozpočtu</v-toolbar-title>
                            <v-spacer></v-spacer>
                            <v-btn
                                    class="mx-2"
                                    icon
                                    @click="removeBudget($event)"
                            >
                                <v-icon
                                        color="red"
                                >
                                    mdi-delete
                                </v-icon>
                            </v-btn>
                        </v-toolbar>
                        <v-card-text>
                            <v-form
                                    ref="form"
                                    v-model="valid"
                                    lazy-validation>
                                <v-text-field
                                        id="amount"
                                        label="částka"
                                        v-model="amount"
                                        :rules="balanceRules"
                                        hide-details="auto"
                                />
                                <v-btn color="primary" text @click="editAmountBudget" class="editBtn">Změnit částku
                                </v-btn>
                                <v-text-field
                                        id="name"
                                        label="název"
                                        v-model="name"
                                        :rules="nameRules"
                                        hide-details="auto"
                                />
                                <v-btn color="primary" text @click="editNameBudget" class="editBtn">Změnit název
                                </v-btn>
                                <v-text-field
                                        id="percentNotify"
                                        label="procento upozornění"
                                        v-model="percentNotify"
                                        :rules="percentRules"
                                        hide-details="auto"
                                />
                                <v-btn color="primary" text @click="editPercentBudget" class="editBtn">Změnit
                                    procento upozornění
                                </v-btn>
                                <v-select
                                        id="category"
                                        :items="categories"
                                        v-model="category"
                                        label="Kategorie"
                                        item-text="name"
                                        :rules="[v => !!v|| 'Item is required']"
                                        item-value="id"
                                        persistent-hint
                                        return-object
                                />
                                <v-btn color="primary" text @click="editCategoryBudget" class="editBtn">Změnit
                                    kategorii
                                </v-btn>
                                <v-text-field
                                        id="bankAcc"
                                        label="účet"
                                        v-model="bankAcc"
                                        hide-details="auto"
                                        disabled
                                />
                            </v-form>
                        </v-card-text>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn color="#e7f6ff" @click="toBudgets">Zpět</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-app>
</template>

<script>
    import {
        editCategoryBudget,
        getAllUsersCategories,
        getBankAccById,
        getBudgetById, getCategoryByName,
        removeBudget,
        editNameBudget,
        editAmountBudget,
        editPercentBudget
    } from "../../api";

    function findByNameInArray(array, name) {
        for (let i = 0; i < array.length; i++) {
            if (array[i].name === name) {
                return array[i]
            }
        }
        return null
    }

    export default {
        name: 'DetailBudget',
        data: () => ({
            categories: [],
            category: "",
            bankAcc: "",
            name: "",
            percentNotify: "",
            amount: "",
            nameRules: [
                v => String(v).trim().length > 0 || 'required'
            ],
            balanceRules: [
                v => !Number.isNaN(Number(v)) || 'must be number',
                v => String(v).trim().length > 0 || 'required',
                v => Number(v) > 0 || 'must be > 0'
            ],
            percentRules: [
                v => Number.isInteger(Number(v)) || 'must be integer',
                v => String(v).trim().length > 0 || 'required',
                v => Number(v) > 0 || 'must be > 0',
                v => Number(v) <= 100 || 'must be <= 100'
            ],
            valid: true,
        }),
        methods: {
            toBudgets() {
                this.$router.push('/budgets/' + this.$route.params.bankId)
            },
            async removeBudget(event) {
                if (!confirm("Opravdu checete smazat rozpočet?")) {
                    event.preventDefault()
                    return
                }

                let result = await removeBudget(this.$route.params.budgetId)
                if (result == null || result.status !== 200) {
                    alert("Invalid delete!")
                } else if (result.status === 200) {
                    alert("Success!")
                    await this.$router.push('/budgets/' + this.$route.params.bankId)
                }
            },
            async editCategoryBudget() {
                let category = await getCategoryByName(this.category.name)
                if (category == null) {
                    alert("Invalid category")
                    return
                }

                let result = await editCategoryBudget(this.$route.params.budgetId, category.id)
                if (result == null || result.status !== 201) {
                    alert("Invalid data! Maybe budget for this category already exists.")
                } else if (result.status === 201) {
                    alert("Success!")
                }
            },
            async editNameBudget() {
                let result = await editNameBudget(this.$route.params.budgetId, this.name)
                if (result == null || result.status !== 201) {
                    alert("Invalid data!")
                } else if (result.status === 201) {
                    alert("Success!")
                }
            },
            async editAmountBudget() {
                let result = await editAmountBudget(this.$route.params.budgetId, this.amount)
                if (result == null || result.status !== 201) {
                    alert("Invalid data!")
                } else if (result.status === 201) {
                    alert("Success!")
                }
            },
            async editPercentBudget() {
                let result = await editPercentBudget(this.$route.params.budgetId, this.percentNotify)
                if (result == null || result.status !== 201) {
                    alert("Invalid data!")
                } else if (result.status === 201) {
                    alert("Success!")
                }
            },
        },
        async mounted() {
            if (!this.$store.state.user) {
                return await this.$router.push("/")
            }
            let bankAcc = await getBankAccById(this.$route.params.bankId)
            if (bankAcc == null) {
                alert("Invalid bankAcc id")
                return
            }
            let categories = await getAllUsersCategories()
            if (categories == null) {
                alert("Invalid data")
                return
            }
            this.bankAcc = bankAcc.name
            this.categories = categories
            let budgetById = await getBudgetById(this.$route.params.budgetId)
            if (budgetById == null) {
                alert("Invalid budget id")
                return
            }
            this.category = findByNameInArray(categories, budgetById.category[0].name)
            this.name = budgetById.name
            this.amount = budgetById.amount
            this.percentNotify = budgetById.percentNotify
        }
    }
</script>

<style>
    .editBtn {
        margin: 15px;
        right: 20px;
    }
</style>