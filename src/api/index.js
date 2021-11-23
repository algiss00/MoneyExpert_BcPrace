import axios from "axios";

const url = "http://localhost:8080";

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

export async function getSumOfExpenseForCategoryByMonth(bankId, month, year, categoryId) {
    try {
        let result = await axios.get(`${url}/transaction/sum-expense-category/${bankId}`, {
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

export async function getSumOfExpenseByMonth(bankId, month, year) {
    try {
        let result = await axios.get(`${url}/transaction/sum-expense/${bankId}`, {
            withCredentials: true,
            params: {
                month: month,
                year: year
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

export async function getSumOfIncomeByMonth(bankId, month, year) {
    try {
        let result = await axios.get(`${url}/transaction/sum-income/${bankId}`, {
            withCredentials: true,
            params: {
                month: month,
                year: year
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

export async function getAllTransactionsByType(bankAccId, month, year, type) {
    try {
        let result = await axios.get(`${url}/transaction/sorted-type/${bankAccId}`, {
            params: {
                type: type,
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

export async function getAllTransactionsByCategory(bankAccId, month, year, categoryId) {
    try {
        let result = await axios.get(`${url}/transaction/sorted-category/${bankAccId}`, {
            params: {
                catId: categoryId,
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

export async function getAllTransactionsByCategoryAndType(bankAccId, month, year, type, categoryId) {
    try {
        let result = await axios.get(`${url}/transaction/sorted-type-category/${bankAccId}`, {
            params: {
                type: type,
                catId: categoryId,
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