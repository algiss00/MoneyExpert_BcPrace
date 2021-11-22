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
                <v-list
                        v-if="createdCategories.length !== 0"
                        subheader
                >
                    <v-subheader class="text-sm-h6">Created categories</v-subheader>

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
                                    @click="removeCategory($event, item)"
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
                    <v-subheader class="text-sm-h6">Default categories</v-subheader>

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
            selectedItem: {}
        }),
        methods: {
            async editCategory(event) {
                let nameEditEl = document.getElementById("nameCategoryEdit")
                if (nameEditEl.value.trim().length === 0) {
                    event.preventDefault()
                    alert("empty field!")
                    return
                }
                let result = await editCategory(this.selectedItem.id, this.nameCategoryEdit)
                if (result == null || result.status !== 201) {
                    alert("Invalid data! Maybe category with this name already exists.")
                } else if (result.status === 201) {
                    let createdCategories = await getAllUsersCreatedCategories()
                    if (createdCategories == null) {
                        alert("Invalid data!")
                        return
                    }
                    this.$store.commit("setSnackbar", true)
                    this.createdCategories = createdCategories
                    this.dialogEditCategory = false
                }
            },
            async addCategory(event) {
                let nameEl = document.getElementById("nameCategory")
                if (nameEl.value.trim().length === 0) {
                    event.preventDefault()
                    alert("empty field!")
                    return
                }
                const jsonCategory = JSON.stringify({
                    name: this.nameCategory
                });

                let result = await addCategory(jsonCategory)
                if (result == null || result.status !== 201) {
                    alert("Invalid data! Maybe category with this name already exists.")
                } else if (result.status === 201) {
                    let createdCategories = await getAllUsersCreatedCategories()
                    if (createdCategories == null) {
                        alert("Invalid data!")
                        return
                    }
                    this.$store.commit("setSnackbar", true)
                    this.createdCategories = createdCategories
                    this.nameCategory = ""
                    this.dialogAddCategory = false
                }
            },
            async removeCategory(event, item) {
                if (!confirm("Opravdu checete smazat kategorii?")) {
                    event.preventDefault()
                    return
                }

                let result = await removeCategory(item.id)
                if (result == null || result.status !== 200) {
                    alert("Invalid delete!")
                } else if (result.status === 200) {
                    let createdCategories = await getAllUsersCreatedCategories()
                    if (createdCategories == null) {
                        alert("Invalid data!")
                        return
                    }
                    this.$store.commit("setSnackbar", true)
                    this.createdCategories = createdCategories
                }
            }
        }
        ,
        async mounted() {
            if (!this.$store.state.user) {
                return await this.$router.push("/")
            }
            let createdCategories = await getAllUsersCreatedCategories()
            if (createdCategories == null) {
                alert("Invalid data")
                return
            }
            this.createdCategories = createdCategories

            let defaultCategories = await getAllDefaultCategories()
            if (defaultCategories == null) {
                alert("Invalid data")
                return
            }
            this.defaultCategories = defaultCategories
        }
    }
</script>