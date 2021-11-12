import axios from "axios";

const url = "http://localhost:8080";
let currentUser = null;

export function setCurrentUser(jsonUser) {
    currentUser = jsonUser;
}

export function getCurrentUser() {
    return currentUser;
}

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
            alert(error.response.data.message);
        }
    });
    return result.data
}

export async function getBankAccById(id) {
    let result = await axios.get(`${url}/bank-account/${id}`, {
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            alert(error.response.data.message);
        }
    });
    return result.data
}

export async function getTransactionById(id) {
    let result = await axios.get(`${url}/transaction/${id}`, {
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            alert(error.response.data.message);
        }
    });
    return result.data
}

export async function getAllTransactions(bankAccId) {
    let result = await axios.get(`${url}/bank-account/transactions/${bankAccId}`, {
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            if (error.response.data.message === "Not authenticated client") {
                alert(error.response.data.message);
                window.location.replace("/");
            }
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
            alert(error.response.data.message);
        }
    });
    return result.data
}

export async function getAllUsersCreatedBanks() {
    let result = await axios.get(`${url}/user/created-accounts`, {
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            if (error.response.data.message === "Not authenticated client") {
                alert(error.response.data.message);
                window.location.replace("/");
            }
        }
    });
    return result.data
}

export async function getAllUsersAvailableBanks() {
    let result = await axios.get(`${url}/user/available-accounts`, {
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            if (error.response.data.message === "Not authenticated client") {
                alert(error.response.data.message);
                window.location.replace("/");
            }
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
                alert(error.response.data.message);
                window.location.replace("/");
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
            if (error.response) {
                console.log(error.response)
                if (error.response.data.errorMessage) {
                    alert("Error! Maybe this username or email already exists!")
                }
            }
        });
    return result.data
}

export async function logout() {
    let result = await axios.post(`${url}/logout`, null, {
        withCredentials: true
    })
        .catch(function (error) {
            if (error.response) {
                console.log(error.response)
                if (error.response.data.errorMessage) {
                    alert("Error!")
                }
            }
        });
    setCurrentUser(null)
    return result.data
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
                if (error.response.data.status === 400) {
                    alert("Error! Maybe this username or email already exists!")
                }
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
                if (error.response.data.status === 400) {
                    alert("Not valid data!")
                }
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
                if (error.response.data.status === 400) {
                    alert("Not valid data!")
                }
            }
        })
}