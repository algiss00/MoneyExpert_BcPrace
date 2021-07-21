//package cz.cvut.fel.service;
//
//import cz.cvut.fel.dao.*;
//import cz.cvut.fel.model.BankAccount;
//import cz.cvut.fel.model.Budget;
//import cz.cvut.fel.service.exceptions.BankAccountNotFoundException;
//import cz.cvut.fel.service.exceptions.BudgetNotFoundException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@Transactional
//public class RemoveService {
//    private UserDao userDao;
//    private BankAccountDao bankAccountDao;
//    private BudgetDao budgetDao;
//    private CategoryDao categoryDao;
//    private DebtDao debtDao;
//    private TransactionDao transactionDao;
//
//    @Autowired
//    public RemoveService(UserDao userDao, BankAccountDao bankAccountDao,
//                         BudgetDao budgetDao, CategoryDao categoryDao,
//                         DebtDao debtDao, TransactionDao transactionDao) {
//        this.userDao = userDao;
//        this.bankAccountDao = bankAccountDao;
//        this.budgetDao = budgetDao;
//        this.categoryDao = categoryDao;
//        this.debtDao = debtDao;
//        this.transactionDao = transactionDao;
//    }
//
//    public void removeBudgetsFromBankAcc(int accId) throws BankAccountNotFoundException, BudgetNotFoundException {
//        BankAccount bankAccount = bankAccountDao.find(accId);
//        for (Budget budget : bankAccount.getBudgets()) {
//            //budgetDao.removeBudgetFromBankAcc(budget.getId(), accId);
//            Budget bu = budgetDao.find(budget.getId());
//            bankAccount.getBudgets().remove(bu);
//            bu.setBankAccount(null);
//            budgetDao.update(bu);
//            bankAccountDao.update(bankAccount);
//        }
//    }
//}
