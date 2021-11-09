import axios from "axios";

const url = "http://localhost:8080";
export let bankAccId;

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

export function setCurrentBankAccount(id) {
    bankAccId = id
}

export async function getBankAccById() {
    console.log("ID: " + bankAccId)
    let result = await axios.get(`${url}/bank-account/${bankAccId}`, {
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            alert(error.response.data.message);
            window.location.assign("/#banks");
        }
    });
    return result.data
}

export async function getAllUsersBanks() {
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
    console.log(result.data)
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