<template>
    <v-app id="inspire">
        <v-container fluid>
            <v-card color="#e7f6ff">
                <v-card-title class="mx-auto">
                    Kategorie
                    <v-fab-transition>
                        <v-btn
                                class="m4-position"
                                dark
                                small
                                color="green"
                                fab
                                title="Přidat kategorii"
                                @click="dialogAddCategory = true"
                        >
                            <v-icon>mdi-plus</v-icon>
                        </v-btn>
                    </v-fab-transition>
                    <v-dialog
                            v-model="dialogAddCategory"
                            max-width="500px"
                    >
                        <v-card>
                            <v-card-title>
                                <span>Přidat kategorii</span>
                            </v-card-title>
                            <v-card-text>
                                <v-text-field
                                        id="nameCategory"
                                        label="název"
                                        :rules="rules"
                                        v-model="nameCategory"
                                        hide-details="auto"
                                />
                            </v-card-text>
                            <v-card-actions>
                                <v-btn
                                        color="primary"
                                        text
                                        @click="dialogAddCategory = false"
                                >
                                    Zavřit
                                </v-btn>
                                <v-spacer/>
                                <v-btn
                                        color="primary"
                                        text
                                        @click="addCategory($event)"
                                        :loading="loading"
                                >
                                    Přidat
                                </v-btn>
                            </v-card-actions>
                        </v-card>
                    </v-dialog>
                </v-card-title>
                <div class="font-weight-medium black--text m-left"
                     v-if="createdCategories.length === 0 && defaultCategories.length === 0">
                    No categories :)
                </div>
                <v-dialog
                        v-model="dialogEditCategory"
                        max-width="500px"
                >
                    <v-card>
                        <v-card-title>
                            <span>Editace kategorii</span>
                        </v-card-title>
                        <v-card-text>
                            <v-text-field
                                    id="nameCategoryEdit"
                                    label="název"
                                    :rules="rules"
                                    v-model="nameCategoryEdit"
                                    hide-details="auto"
                            />
                        </v-card-text>
                        <v-card-actions>
                            <v-btn
                                    color="primary"
                                    text
                                    @click="dialogEditCategory = false"
                            >
                                Zavřit
                            </v-btn>
                            <v-spacer/>
                            <v-btn
                                    color="primary"
                                    text
                                    @click="editCategory($event)"
                            >
                                Změnit nazev
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>
                <v-dialog
                        v-model="dialogConfirm"
                        max-width="330"
                >
                    <v-card>
                        <v-card-title class="text-h5">
                            Opravdu checete smazat kategorii?
                        </v-card-title>

                        <v-card-text>
                            Potvrzením odstranění - kategorie se odstraní, pak se také odstraní vše rozpočty,
                            které patří k dané kategorii. Dále vše transakce s danou kategorii, budou patřit
                            ke kategorii "No category".
                        </v-card-text>

                        <v-card-actions>
                            <v-spacer></v-spacer>

                            <v-btn
                                    color="green darken-1"
                                    text
                                    @click="dialogConfirm = false"
                            >
                                Zrušit
                            </v-btn>

                            <v-btn
                                    color="green darken-1"
                                    text
                                    @click="dialogConfirm = false, removeCategory()"
                            >
                                Potvrdit
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>
                <v-list
                        v-if="createdCategories.length !== 0"
                        subheader
                >
                    <v-subheader class="text-sm-h6">Vytvořené kategorie</v-subheader>

                    <v-list-item
                            v-for="item in createdCategories"
                            :key="item.id"
                            class="borders"
                    >
                        <v-list-item-content>
                            <v-list-item-title v-text="item.name"/>
                        </v-list-item-content>
                        <v-list-item-icon>
                            <v-btn
                                    class="mx-2"
                                    icon
                                    @click="dialogEditCategory = true, selectedItem = item, nameCategoryEdit = item.name"
                                    title="Editace kategorii"
                            >
                                <v-icon
                                >
                                    mdi-pencil
                                </v-icon>
                            </v-btn>
                        </v-list-item-icon>
                        <v-list-item-icon>
                            <v-btn
                                    class="mx-2"
                                    icon
                                    @click="dialogConfirm = true, selectedItem = item"
                                    title="Smazat kategorii"
                            >
                                <v-icon
                                        color="red"
                                >
                                    mdi-delete
                                </v-icon>
                            </v-btn>
                        </v-list-item-icon>
                    </v-list-item>
                </v-list>

                <v-divider/>
                <v-list
                        subheader
                        v-if="defaultCategories.length !== 0"
                >
                    <v-subheader class="text-sm-h6">Výchozí kategorie</v-subheader>

                    <v-list-item
                            v-for="item in defaultCategories"
                            :key="item.id"
                            class="borders"
                    >
                        <v-list-item-content>
                            <v-list-item-title v-text="item.name"/>
                        </v-list-item-content>
                    </v-list-item>
                </v-list>
            </v-card>
        </v-container>
    </v-app>
</template>

<script>
    import {
        addCategory,
        editCategory,
        getAllDefaultCategories,
        getAllUsersCreatedCategories,
        removeCategory
    } from "../../api";

    export default {
        name: 'Categories',
        data: () => ({
            createdCategories: [],
            defaultCategories: [],
            rules: [
                v => String(v).trim().length > 0 || 'required'
            ],
            dialogAddCategory: false,
            nameCategory: "",
            nameCategoryEdit: "",
            dialogEditCategory: false,
            selectedItem: {},
            dialogConfirm: false,
            loading: false
        }),
        methods: {
            async editCategory(event) {
                let nameEditEl = document.getElementById("nameCategoryEdit")
                if (nameEditEl.value.trim().length === 0) {
                    event.preventDefault()
                    this.$store.commit("setSnackbarText", "Empty fields!")
                    this.$store.commit("setSnackbarError", true)
                    return
                }
                this.$store.commit("setLoading", true)
                let result = await editCategory(this.selectedItem.id, this.nameCategoryEdit)
                if (result == null || result.status !== 201) {
                    this.$store.commit("setSnackbarText", "Invalid data! Maybe category with this name already exists.")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.status === 201) {
                    let createdCategories = await getAllUsersCreatedCategories()
                    if (createdCategories == null) {
                        this.$store.commit("setLoading", false)
                        this.$store.commit("setSnackbarText", "Server error! Cant get categories.")
                        this.$store.commit("setSnackbarError", true)
                        return
                    }
                    this.$store.commit("setSnackbar", true)
                    this.createdCategories = createdCategories
                    this.dialogEditCategory = false
                }
                this.$store.commit("setLoading", false)
            },
            async addCategory(event) {
                let nameEl = document.getElementById("nameCategory")
                if (nameEl.value.trim().length === 0) {
                    event.preventDefault()
                    this.$store.commit("setSnackbarText", "Empty fields!")
                    this.$store.commit("setSnackbarError", true)
                    return
                }
                this.loading = true
                const jsonCategory = JSON.stringify({
                    name: this.nameCategory
                });

                let result = await addCategory(jsonCategory)
                if (result == null || result.status !== 201) {
                    this.$store.commit("setSnackbarText", "Invalid data! Maybe category with this name already exists.")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.status === 201) {
                    let createdCategories = await getAllUsersCreatedCategories()
                    if (createdCategories == null) {
                        this.loading = false
                        this.$store.commit("setSnackbarText", "Server error! Cant get categories.")
                        this.$store.commit("setSnackbarError", true)
                        return
                    }
                    this.$store.commit("setSnackbar", true)
                    this.createdCategories = createdCategories
                    this.dialogAddCategory = false
                }
                this.nameCategory = ""
                this.loading = false
            },
            async removeCategory() {
                this.$store.commit("setLoading", true)
                let result = await removeCategory(this.selectedItem.id)
                if (result == null || result.status !== 200) {
                    this.$store.commit("setSnackbarText", "Server error! Cant delete.")
                    this.$store.commit("setSnackbarError", true)
                } else if (result.status === 200) {
                    let createdCategories = await getAllUsersCreatedCategories()
                    if (createdCategories == null) {
                        this.$store.commit("setLoading", false)
                        alert("Server error!")
                        return
                    }
                    this.$store.commit("setSnackbar", true)
                    this.createdCategories = createdCategories
                }
                this.$store.commit("setLoading", false)
            }
        },
        async mounted() {
            // if user not authenticated route to login page
            if (!this.$store.state.user) {
                return await this.$router.push("/").catch(() => {
                })
            }
            this.$store.commit("setLoading", true)
            let createdCategories = await getAllUsersCreatedCategories()
            if (createdCategories == null) {
                this.$store.commit("setLoading", false)
                alert("Server error! Cant get users categories.")
                location.reload()
                return
            }
            this.createdCategories = createdCategories

            let defaultCategories = await getAllDefaultCategories()
            if (defaultCategories == null) {
                this.$store.commit("setLoading", false)
                this.$store.commit("setSnackbarText", "Server error! Cant get default categories.")
                this.$store.commit("setSnackbarError", true)
                return
            }
            this.defaultCategories = defaultCategories
            this.$store.commit("setLoading", false)
        }
    }
</script>