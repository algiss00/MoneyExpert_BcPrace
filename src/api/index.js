import axios from "axios";

const url = "http://localhost:8080"

export async function getAllUsersBanks() {
    let result = await axios.get(`${url}/user/created-accounts`, {
        withCredentials: true
    })
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
    console.log(result)
    return result.data
}