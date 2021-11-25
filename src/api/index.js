import axios from "axios";

/**
 * This file contains the requests to the back-end part
 */

const url = "http://localhost:8080";

/**
 * get users category by name
 * @param name
 * @returns {Promise<null|T>}
 */
export async function getCategoryByName(name) {
    try {
        let result = await axios.get(`${url}/category/user-by-name`, {
            params: {
                name: name,
            },
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

/**
 * get bankAcc by id
 * @param id
 * @returns {Promise<null|T>}
 */
export async function getBankAccById(id) {
    try {
        let result = await axios.get(`${url}/bank-account/${id}`, {
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

/**
 * get transaction by id
 * @param id
 * @returns {Promise<null|T>}
 */
export async function getTransactionById(id) {
    try {
        let result = await axios.get(`${url}/transaction/${id}`, {
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

/**
 * get budget by id
 * @param id
 * @returns {Promise<null|T>}
 */
export async function getBudgetById(id) {
    try {
        let result = await axios.get(`${url}/budget/${id}`, {
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

/**
 * get debt by id
 * @param id
 * @returns {Promise<null|T>}
 */
export async function getDebtById(id) {
    try {
        let result = await axios.get(`${url}/debt/${id}`, {
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

/**
 * get sum of all expenses which in certain category and between date
 * @param bankId
 * @param from
 * @param to
 * @param categoryId
 * @returns {Promise<null|T>}
 */
export async function getSumOfExpenseForCategoryBetweenDate(bankId, from, to, categoryId) {
    try {
        let result = await axios.get(`${url}/transaction/sum-expense-category/${bankId}`, {
            withCredentials: true,
            params: {
                from: from,
                to: to,
                categoryId: categoryId
            },
        })
        return result.data
    } catch (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
        return null
    }
}

/**
 * get sum of all incomes which in certain category and month, year
 * @param bankId
 * @param month
 * @param year
 * @param categoryId
 * @returns {Promise<null|T>}
 */
export async function getSumOfIncomeForCategoryByMonth(bankId, month, year, categoryId) {
    try {
        let result = await axios.get(`${url}/transaction/sum-income-category/${bankId}`, {
            withCredentials: true,
            params: {
                month: month,
                year: year,
                categoryId: categoryId
            },
        })
        return result.data
    } catch (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
        return null
    }
}

/**
 * get sum of all expenses from bankAcc and between date
 * @param bankId
 * @param from
 * @param to
 * @returns {Promise<null|T>}
 */
export async function getSumOfExpenseBetweenDate(bankId, from, to) {
    try {
        let result = await axios.get(`${url}/transaction/sum-expense/${bankId}`, {
            withCredentials: true,
            params: {
                from: from,
                to: to
            },
        })
        return result.data
    } catch (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
        return null
    }
}

/**
 * get sum of all incomes from bankAcc and between date
 * @param bankId
 * @param from
 * @param to
 * @returns {Promise<null|T>}
 */
export async function getSumOfIncomeBetweenDate(bankId, from, to) {
    try {
        let result = await axios.get(`${url}/transaction/sum-income/${bankId}`, {
            withCredentials: true,
            params: {
                from: from,
                to: to
            },
        })
        return result.data
    } catch (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
        return null
    }
}

/**
 * get all notifications for debts
 * @param bankAccId
 * @returns {Promise<null|T>}
 */
export async function getAllNotificationDebts(bankAccId) {
    try {
        let result = await axios.get(`${url}/notify-debt/bank-account/${bankAccId}`, {
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

/**
 * get all notifications for budgets
 * @param bankAccId
 * @returns {Promise<null|T>}
 */
export async function getAllNotificationBudgets(bankAccId) {
    try {
        let result = await axios.get(`${url}/notify-budget/bank-account/${bankAccId}`, {
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

/**
 * get all notifications for budget by type
 * @param bankAccId
 * @param type
 * @returns {Promise<null|T>}
 */
export async function getAllNotificationBudgetsByType(bankAccId, type) {
    try {
        let result = await axios.get(`${url}/notify-budget/bank-account-by-type/${bankAccId}`, {
            withCredentials: true,
            params: {
                type: type
            }
        })
        return result.data
    } catch (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
        return null
    }
}

/**
 * get all transactions from bankAcc
 * @param bankAccId
 * @returns {Promise<null|T>}
 */
export async function getAllTransactions(bankAccId) {
    try {
        let result = await axios.get(`${url}/bank-account/transactions/${bankAccId}`, {
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

/**
 * get all transactions from bankAcc by month and year
 * @param bankAccId
 * @param month
 * @param year
 * @returns {Promise<null|T>}
 */
export async function getAllTransactionsByMonth(bankAccId, month, year) {
    try {
        let result = await axios.get(`${url}/transaction/sorted-month-year/${bankAccId}`, {
            params: {
                month: month,
                year: year
            },
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

/**
 * get all transactions from bankAcc by type between date
 * @param bankAccId
 * @param from
 * @param to
 * @param type
 * @returns {Promise<null|T>}
 */
export async function getAllTransactionsByType(bankAccId, from, to, type) {
    try {
        let result = await axios.get(`${url}/transaction/between-date-type/${bankAccId}`, {
            params: {
                type: type,
                from: from,
                to: to
            },
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

/**
 * get all transactions from bankAcc by category between date
 * @param bankAccId
 * @param from
 * @param to
 * @param categoryId
 * @returns {Promise<null|T>}
 */
export async function getAllTransactionsByCategory(bankAccId, from, to, categoryId) {
    try {
        let result = await axios.get(`${url}/transaction/between-date-category/${bankAccId}`, {
            params: {
                catId: categoryId,
                from: from,
                to: to
            },
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

/**
 * get all transactions from bankAcc between date
 * @param bankAccId
 * @param from
 * @param to
 * @returns {Promise<null|T>}
 */
export async function getAllTransactionsBetweenDate(bankAccId, from, to) {
    try {
        let result = await axios.get(`${url}/transaction/between-date/${bankAccId}`, {
            params: {
                from: from,
                to: to
            },
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

/**
 * get all transactions from bankAcc by type and category between date
 * @param bankAccId
 * @param from
 * @param to
 * @param type
 * @param categoryId
 * @returns {Promise<null|T>}
 */
export async function getAllTransactionsByCategoryAndType(bankAccId, from, to, type, categoryId) {
    try {
        let result = await axios.get(`${url}/transaction/between-date-category-type/${bankAccId}`, {
            params: {
                type: type,
                catId: categoryId,
                from: from,
                to: to
            },
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

/**
 * get user by username
 * @param username
 * @returns {Promise<null|T>}
 */
export async function getUserByUsername(username) {
    try {
        let result = await axios.get(`${url}/user/username`, {
            params: {
                username: username,
            },
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

/**
 * get all users created bankAccs
 * @returns {Promise<null|T>}
 */
export async function getAllUsersCreatedBanks() {
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

/**
 * get all users available bankAccs
 * @returns {Promise<null|T>}
 */
export async function getAllUsersAvailableBanks() {
    try {
        let result = await axios.get(`${url}/user/available-accounts`, {
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

/**
 * get all budgets from bankAcc
 * @param bankId
 * @returns {Promise<null|T>}
 */
export async function getAllBudgetsFromBankAcc(bankId) {
    try {
        let result = await axios.get(`${url}/bank-account/budgets/${bankId}`, {
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

/**
 * get all debts from bankAcc
 * @param bankId
 * @returns {Promise<null|T>}
 */
export async function getAllDebtsFromBankAcc(bankId) {
    try {
        let result = await axios.get(`${url}/bank-account/debts/${bankId}`, {
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

/**
 * get all users bankAcc include created and available
 * @returns {Promise<null|T>}
 */
export async function getAllUsersBanks() {
    try {
        let result = await axios.get(`${url}/user/all-accounts`, {
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

/**
 * get creator of bankAcc
 * @param bankAccId
 * @returns {Promise<null|T>}
 */
export async function getCreatorOfBankAcc(bankAccId) {
    try {
        let result = await axios.get(`${url}/bank-account/creator/${bankAccId}`, {
            withCredentials: true
        })
        return result.data
    } catch (error) {
        if (error.response) {
            console.log(error.response.data.message + " not creator of bankAcc");
        }
        return null
    }
}

/**
 * get current user from back-end
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function getCurrentUserBackEnd() {
    try {
        return await axios.get(`${url}/user/current-user`, {
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log(error.response.data.message);
        }
        return null
    }
}

/**
 * get all owners of bankAcc
 * @param bankId
 * @returns {Promise<null|T>}
 */
export async function getAllOwnersOfBankAcc(bankId) {
    try {
        let result = await axios.get(`${url}/bank-account/owners/${bankId}`, {
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

/**
 * get all users categories include default and created
 * @returns {Promise<null|T>}
 */
export async function getAllUsersCategories() {
    try {
        let result = await axios.get(`${url}/user/categories`, {
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

/**
 * get all users created categories
 * @returns {Promise<null|T>}
 */
export async function getAllUsersCreatedCategories() {
    try {
        let result = await axios.get(`${url}/category/user-created`, {
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

/**
 * get all default categories
 * @returns {Promise<null|T>}
 */
export async function getAllDefaultCategories() {
    try {
        let result = await axios.get(`${url}/category/default`, {
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

/**
 * login
 * @param username
 * @param password
 * @returns {Promise<null|T>}
 */
export async function login(username, password) {
    try {
        let result = await axios.post(`${url}/login`, null, {
            params: {
                username: username,
                password: password
            },
            withCredentials: true
        })
        return result.data
    } catch (error) {
        if (error.response) {
            console.log("Invalid data!")
        }
        return null
    }
}

/**
 * logout
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function logout() {
    try {
        return await axios.post(`${url}/logout`, null, {
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Error logout!")
        }
        return null
    }
}

/**
 * registration
 * @param jsonUser
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function registration(jsonUser) {
    try {
        return await axios.post(`${url}/user`, jsonUser, {
            "headers": {
                "content-type": "application/json",
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Error! Maybe this username or email already exists!")
        }
        return null
    }
}

/**
 * add bankAcc
 * @param jsonBankAcc
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function addBankAccount(jsonBankAcc) {
    try {
        return await axios.post(`${url}/bank-account`, jsonBankAcc, {
            "headers": {
                "content-type": "application/json",
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * add transaction to bankAcc
 * @param jsonTransaction
 * @param accId
 * @param categoryId
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function addTransaction(jsonTransaction, accId, categoryId) {
    try {
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
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * add budget to bankAcc
 * @param jsonBudget
 * @param accId
 * @param categoryId
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function addBudget(jsonBudget, accId, categoryId) {
    try {
        return await axios.post(`${url}/budget`, jsonBudget, {
            "headers": {
                "content-type": "application/json",
            },
            params: {
                accId: accId,
                categoryId: categoryId
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * add debt to bankAcc
 * @param jsonDebt
 * @param accId
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function addDebt(jsonDebt, accId) {
    try {
        return await axios.post(`${url}/debt`, jsonDebt, {
            "headers": {
                "content-type": "application/json",
            },
            params: {
                accId: accId
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * add category
 * @param jsonCategory
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function addCategory(jsonCategory) {
    try {
        return await axios.post(`${url}/category`, jsonCategory, {
            "headers": {
                "content-type": "application/json",
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * transfer transaction to another bankAcc
 * @param fromAccId
 * @param toAccId
 * @param transId
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function transferTransaction(fromAccId, toAccId, transId) {
    try {
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
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * share bankAcc with another user by username
 * @param username
 * @param bankId
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function shareBankAccount(username, bankId) {
    try {
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
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * edit bankAcc
 * @param jsonBankAcc
 * @param bankId
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function editBankAcc(jsonBankAcc, bankId) {
    try {
        return await axios.post(`${url}/bank-account/update`, jsonBankAcc, {
            "headers": {
                "content-type": "application/json",
            },
            params: {
                accId: bankId
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * edit basic info transaction
 * @param jsonTransaction
 * @param transId
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function editBasicTransaction(jsonTransaction, transId) {
    try {
        return await axios.post(`${url}/transaction/update-basic`, jsonTransaction, {
            "headers": {
                "content-type": "application/json",
            },
            params: {
                transId: transId
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * edit category of transaction
 * @param transId
 * @param categoryId
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function editCategoryTransaction(transId, categoryId) {
    try {
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
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * edit type of transaction
 * @param transId
 * @param type
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function editTypeTransaction(transId, type) {
    try {
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
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * edit users name and lastname
 * @param userJson
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function editNameLastname(userJson) {
    try {
        return await axios.post(`${url}/user/basic-info`, userJson, {
            "headers": {
                "content-type": "application/json",
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * edit users email
 * @param email
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function editEmail(email) {
    try {
        return await axios.post(`${url}/user/email`, null, {
            "headers": {
                "content-type": "application/json",
            },
            params: {
                email: email,
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * edit users username
 * @param username
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function editUsername(username) {
    try {
        return await axios.post(`${url}/user/username`, null, {
            "headers": {
                "content-type": "application/json",
            },
            params: {
                username: username,
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * edit users password
 * @param oldPassword
 * @param newPassword
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function editPassword(oldPassword, newPassword) {
    try {
        return await axios.post(`${url}/user/password`, null, {
            "headers": {
                "content-type": "application/json",
            },
            params: {
                oldPassword: oldPassword,
                newPassword: newPassword
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * edit category of budget
 * @param budgetId
 * @param categoryId
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function editCategoryBudget(budgetId, categoryId) {
    try {
        return await axios.post(`${url}/budget/update-category`, null, {
            "headers": {
                "content-type": "application/json",
            },
            params: {
                budId: budgetId,
                categoryId: categoryId
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * edit name of budget
 * @param budgetId
 * @param name
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function editNameBudget(budgetId, name) {
    try {
        return await axios.post(`${url}/budget/update-name`, null, {
            "headers": {
                "content-type": "application/json",
            },
            params: {
                budId: budgetId,
                name: name
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * edit amount of budget
 * @param budgetId
 * @param amount
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function editAmountBudget(budgetId, amount) {
    try {
        return await axios.post(`${url}/budget/update-amount`, null, {
            "headers": {
                "content-type": "application/json",
            },
            params: {
                budId: budgetId,
                amount: amount
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * edit percent of budget
 * @param budgetId
 * @param percent
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function editPercentBudget(budgetId, percent) {
    try {
        return await axios.post(`${url}/budget/update-percent`, null, {
            "headers": {
                "content-type": "application/json",
            },
            params: {
                budId: budgetId,
                percent: percent
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * edit basic info of debt
 * @param debtId
 * @param jsonDebt
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function editBasicDebt(debtId, jsonDebt) {
    try {
        return await axios.post(`${url}/debt/update-basic`, jsonDebt, {
            "headers": {
                "content-type": "application/json",
            },
            params: {
                debtId: debtId,
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * edit notifyDate of debt
 * @param debtId
 * @param notifyDate
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function editNotifyDate(debtId, notifyDate) {
    try {
        return await axios.post(`${url}/debt/update-notify-date`, null, {
            "headers": {
                "content-type": "application/json",
            },
            params: {
                debtId: debtId,
                notifyDate: notifyDate
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * edit deadline of debt
 * @param debtId
 * @param deadline
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function editDeadline(debtId, deadline) {
    try {
        return await axios.post(`${url}/debt/update-deadline-date`, null, {
            "headers": {
                "content-type": "application/json",
            },
            params: {
                debtId: debtId,
                deadline: deadline
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * edit users category
 * @param categoryId
 * @param name
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function editCategory(categoryId, name) {
    try {
        return await axios.post(`${url}/category/update-name`, null, {
            "headers": {
                "content-type": "application/json",
            },
            params: {
                catId: categoryId,
                name: name
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * remove transaction from bankAcc
 * @param transId
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function removeTransactionFromBank(transId) {
    try {
        return await axios.delete(`${url}/bank-account/transaction`, {
            params: {
                transId: transId
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * remove users bankAcc
 * @param bankAccountId
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function removeBankAcc(bankAccountId) {
    try {
        return await axios.delete(`${url}/bank-account/${bankAccountId}`, {
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * remove budget
 * @param budgetId
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function removeBudget(budgetId) {
    try {
        return await axios.delete(`${url}/budget/${budgetId}`, {
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * remove debt
 * @param debtId
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function removeDebt(debtId) {
    try {
        return await axios.delete(`${url}/debt/${debtId}`, {
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * remove users category
 * @param categoryId
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function removeCategory(categoryId) {
    try {
        return await axios.delete(`${url}/category/${categoryId}`, {
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * remove a owner from bankAcc
 * @param userId
 * @param bankAccountId
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function removeOwnerFromBankAcc(userId, bankAccountId) {
    try {
        return await axios.delete(`${url}/bank-account/owner`, {
            params: {
                userId: userId,
                accId: bankAccountId
            },
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}

/**
 * delete user profile
 * @returns {Promise<null|AxiosResponse<T>>}
 */
export async function removeUserProfile() {
    try {
        return await axios.delete(`${url}/user/delete-account`, {
            withCredentials: true
        })
    } catch (error) {
        if (error.response) {
            console.log("Not valid data!")
        }
        return null
    }
}