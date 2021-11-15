import axios from "axios";

const url = "http://localhost:8080";

export function markAsError(id, add_remove) {
    let element = document.getElementById(id);
    if (element == null) {
        return;
    }
    if (add_remove) {
        element.classList.add("error");
    } else {
        element.classList.remove("error");
    }
}

export async function getCategoryByName(name) {
    let result = await axios.get(`${url}/category/user-by-name`, {
        params: {
            name: name,
        },
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
    });
    return result.data
}

export async function getBankAccById(id) {
    let result = await axios.get(`${url}/bank-account/${id}`, {
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
    });
    return result.data
}

export async function getTransactionById(id) {
    let result = await axios.get(`${url}/transaction/${id}`, {
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
    });
    return result.data
}

export async function getAllTransactions(bankAccId) {
    let result = await axios.get(`${url}/bank-account/transactions/${bankAccId}`, {
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
    });
    return result.data
}

export async function getAllTransactionsByMonth(bankAccId, month, year) {
    let result = await axios.get(`${url}/transaction/sorted-month-year/${bankAccId}`, {
        params: {
            month: month,
            year: year
        },
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
    });
    return result.data
}

export async function getAllTransactionsByType(bankAccId, month, year, type) {
    let result = await axios.get(`${url}/transaction/sorted-type/${bankAccId}`, {
        params: {
            type: type,
            month: month,
            year: year
        },
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
    });
    return result.data
}

export async function getAllTransactionsByCategory(bankAccId, month, year, categoryId) {
    let result = await axios.get(`${url}/transaction/sorted-category/${bankAccId}`, {
        params: {
            catId: categoryId,
            month: month,
            year: year
        },
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
    });
    return result.data
}

export async function getAllTransactionsByCategoryAndType(bankAccId, month, year, type, categoryId) {
    let result = await axios.get(`${url}/transaction/sorted-type-category/${bankAccId}`, {
        params: {
            type: type,
            catId: categoryId,
            month: month,
            year: year
        },
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
    });
    return result.data
}


export async function getUserByUsername(username) {
    let result = await axios.get(`${url}/user/username`, {
        params: {
            username: username,
        },
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
    });
    return result.data
}

export async function getAllUsersCreatedBanks() {
// TODO - takovy FORMAT!!! try catch
    try {
        let result = await axios.get(`${url}/user/created-accounts`, {
            withCredentials: true
        })
        return result.data
    } catch (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
        return null
    }
}

export async function getAllUsersAvailableBanks() {
    let result = await axios.get(`${url}/user/available-accounts`, {
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
    });
    return result.data
}

export async function getAllUsersBanks() {
    let result = await axios.get(`${url}/user/all-accounts`, {
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
    });
    return result.data
}

export async function getCreatorOfBankAcc(bankAccId) {
    let result = await axios.get(`${url}/bank-account/creator/${bankAccId}`, {
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            console.log(error.response.data.message + " not creator of bankAcc");
        }
    });
    return result.data
}

export async function getCurrentUserBackEnd() {
    return await axios.get(`${url}/user/current-user`, {
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
        return null
    })
}

export async function getAllOwnersOfBankAcc(bankId) {
    let result = await axios.get(`${url}/bank-account/owners/${bankId}`, {
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
    });
    return result.data
}

export async function getAllUsersCategories() {
    let result = await axios.get(`${url}/user/categories`, {
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            if (error.response.data.message === "Not authenticated client") {
                console.log(error.response.data.message);
            }
        }
    });
    return result.data
}

export async function login(username, password) {
    let result = await axios.post(`${url}/login`, null, {
        params: {
            username: username,
            password: password
        },
        withCredentials: true
    })
        .catch(function (error) {
            console.log(error)
            if (error.response) {
                console.log("Invalid data!")
            }
        });
    return result.data
}

export async function logout() {
    return await axios.post(`${url}/logout`, null, {
        withCredentials: true
    })
        .catch(function (error) {
            if (error.response) {
                if (error.response.data.errorMessage) {
                    console.log("Error logout!")
                }
            }
        });
}

export async function registration(jsonUser) {
    return await axios.post(`${url}/user`, jsonUser, {
        "headers": {
            "content-type": "application/json",
        },
        withCredentials: true
    })
        .catch(function (error) {
            if (error.response) {
                console.log("Error! Maybe this username or email already exists!")
            }
        })
}

export async function addBankAccount(jsonBankAcc) {
    return await axios.post(`${url}/bank-account`, jsonBankAcc, {
        "headers": {
            "content-type": "application/json",
        },
        withCredentials: true
    })
        .catch(function (error) {
            if (error.response) {
                console.log("Not valid data!")
            }
        })
}

export async function addTransaction(jsonTransaction, accId, categoryId) {
    return await axios.post(`${url}/transaction`, jsonTransaction, {
        "headers": {
            "content-type": "application/json",
        },
        params: {
            accId: accId,
            categoryId: categoryId
        },
        withCredentials: true
    })
        .catch(function (error) {
            if (error.response) {
                console.log("Not valid data!")
            }
        })
}

export async function transferTransaction(fromAccId, toAccId, transId) {
    return await axios.post(`${url}/transaction/transfer`, null, {
        "headers": {
            "content-type": "application/json",
        },
        params: {
            fromAccId: fromAccId,
            toAccId: toAccId,
            transId: transId
        },
        withCredentials: true
    })
        .catch(function (error) {
            if (error.response) {
                console.log("Not valid data!")
            }
        })
}

export async function shareBankAccount(username, bankId) {
    return await axios.post(`${url}/bank-account/owner`, null, {
        "headers": {
            "content-type": "application/json",
        },
        params: {
            username: username,
            accId: bankId
        },
        withCredentials: true
    })
        .catch(function (error) {
            if (error.response) {
                console.log("Not valid data!")
            }
        })
}

export async function editBankAcc(jsonBankAcc, bankId) {
    return await axios.post(`${url}/bank-account/update`, jsonBankAcc, {
        "headers": {
            "content-type": "application/json",
        },
        params: {
            accId: bankId
        },
        withCredentials: true
    })
        .catch(function (error) {
            if (error.response) {
                console.log("Not valid data!")
            }
        })
}

export async function editBasicTransaction(jsonTransaction, transId) {
    return await axios.post(`${url}/transaction/update-basic`, jsonTransaction, {
        "headers": {
            "content-type": "application/json",
        },
        params: {
            transId: transId
        },
        withCredentials: true
    })
        .catch(function (error) {
            if (error.response) {
                console.log("Not valid data!")
            }
        })
}

export async function editCategoryTransaction(transId, categoryId) {
    return await axios.post(`${url}/transaction/update-category`, null, {
        "headers": {
            "content-type": "application/json",
        },
        params: {
            transId: transId,
            catId: categoryId
        },
        withCredentials: true
    })
        .catch(function (error) {
            if (error.response) {
                console.log("Not valid data!")
            }
        })
}

export async function editTypeTransaction(transId, type) {
    return await axios.post(`${url}/transaction/update-type`, null, {
        "headers": {
            "content-type": "application/json",
        },
        params: {
            transId: transId,
            typeTransaction: type
        },
        withCredentials: true
    })
        .catch(function (error) {
            if (error.response) {
                console.log("Not valid data!")
            }
        })
}

export async function removeTransactionFromBank(transId, bankAccountId) {
    return await axios.delete(`${url}/bank-account/transaction`, {
        params: {
            transId: transId,
            bankAccountId: bankAccountId
        },
        withCredentials: true
    })
        .catch(function (error) {
            if (error.response) {
                console.log("Not valid data!")
            }
        })
}

export async function removeBankAcc(bankAccountId) {
    return await axios.delete(`${url}/bank-account/${bankAccountId}`, {
        withCredentials: true
    })
        .catch(function (error) {
            if (error.response) {
                console.log("Not valid data!")
            }
        })
}


export async function removeOwnerFromBankAcc(userId, bankAccountId) {
    return await axios.delete(`${url}/bank-account/owner`, {
        params: {
            userId: userId,
            accId: bankAccountId
        },
        withCredentials: true
    })
        .catch(function (error) {
            if (error.response) {
                console.log("Not valid data!")
            }
        })
}