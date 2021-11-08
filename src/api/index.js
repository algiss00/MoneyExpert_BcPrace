import axios from "axios";

const url = "http://localhost:8080"

export async function getAllUsersBanks() {
    let result = await axios.get(`${url}/user/created-accounts`, {
        withCredentials: true
    }).catch(function (error) {
        if (error.response) {
            if (error.response.data.status === 400) {
                // todo move to /
                alert(error.response.data.message)
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
    return result.data
}

export async function registration(jsonUser) {
    return await axios.post("http://localhost:8080/user", jsonUser, {
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