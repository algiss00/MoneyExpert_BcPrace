-- category table
INSERT INTO category_table VALUES (1, 'Auto');
INSERT INTO category_table VALUES (2, 'Jidlo');
INSERT INTO category_table VALUES (3, 'Mzda');
INSERT INTO category_table VALUES (4, 'Shopping');


-- users table
INSERT INTO user_table VALUES (10,'emailTest1@ads.cz','Sicker','John','123456','john123');
INSERT INTO user_table VALUES (11,'emailTest2@ads.cz','Sky','Ali','123456','ali123');
INSERT INTO user_table VALUES (12,'emailTest3@ads.cz','Nice','Cley','123456','cley123');
INSERT INTO user_table VALUES (13,'emailTest4@ads.cz','True','Petr','123456','petr123');
INSERT INTO user_table VALUES (14,'emailTes5t@ads.cz','Low','Dirtrich','123456','dirtrich123');

-- bankAccount table
INSERT INTO bank_account_table VALUES (15,1000,'CZK','CSOB Ucet');
INSERT INTO bank_account_table VALUES (16,1205,'EUR','SPORKA Ucet');
INSERT INTO bank_account_table VALUES (17,124,'EUR','Pracovni Ucet');
INSERT INTO bank_account_table VALUES (18,10000,'CZK','KB Ucet');
INSERT INTO bank_account_table VALUES (19,1000,'CZK','CSOB Ucet');

-- relation_category_user table
INSERT INTO relation_category_user VALUES (10, 1);
INSERT INTO relation_category_user VALUES (10, 2);
INSERT INTO relation_category_user VALUES (11, 3);
INSERT INTO relation_category_user VALUES (13, 4);

-- relation_bank_account_user table
INSERT INTO relation_bank_account_user VALUES (10, 15);
INSERT INTO relation_bank_account_user VALUES (10, 19);
INSERT INTO relation_bank_account_user VALUES (10, 16);
INSERT INTO relation_bank_account_user VALUES (11, 17);
INSERT INTO relation_bank_account_user VALUES (12, 18);

-- budget table
INSERT INTO budget_table VALUES (19,1000,'Jidlo budget',50,15,-2,10);
INSERT INTO budget_table VALUES (20,1500,'Auto budget',65,16,-1,11);
INSERT INTO budget_table VALUES (21,2000,'Byt budget',80,17,-4,12);

INSERT INTO budget_table VALUES (22,2000,'Byt budget',80,17,-3,12);
INSERT INTO budget_table VALUES (23,2000,'Byt budget',80,17,-7,12);

-- debt table
INSERT INTO debt_table VALUES (22,1000,'2021-10-10','Debt to Honza','Honza dluh','2021-10-09',null,null);
INSERT INTO debt_table VALUES (23,100,'2021-10-11','netflix','Netflix','2021-10-10',null,null);
INSERT INTO debt_table VALUES (24,250,'2021-10-14','debt to Karl','Karl dluh','2021-10-19',null,null);

INSERT INTO debt_table VALUES (25,250,'2022-10-29','debt to Karl','Karl dluh','2021-10-10',null,null);
INSERT INTO debt_table VALUES (26,250,'2022-10-29','debt to Karl','Karl dluh','2021-10-13',null,null);
INSERT INTO debt_table VALUES (27,250,'2022-10-29','debt to Karl','Karl dluh','2021-10-14',null,null);

-- NotifyBudget table
INSERT INTO notify_budget_table VALUES (25,'BUDGET_AMOUNT',19,10);
INSERT INTO notify_budget_table VALUES (26,'BUDGET_PERCENT',20,11);
INSERT INTO notify_budget_table VALUES (27,'BUDGET_AMOUNT',21,12);

INSERT INTO notify_budget_table VALUES (28,'BUDGET_AMOUNT',22,10);
INSERT INTO notify_budget_table VALUES (29,'BUDGET_PERCENT',23,10);

-- NotifyDebt table
INSERT INTO notify_debt_table VALUES (28,'DEBT_NOTIFY',13,22);
INSERT INTO notify_debt_table VALUES (29,'DEBT_DEADLINE',14,23);
INSERT INTO notify_debt_table VALUES (30,'DEBT_NOTIFY',13,24);
INSERT INTO notify_debt_table VALUES (31,'DEBT_DEADLINE',13,26);

-- Transaction table
INSERT INTO transaction_table VALUES (30,100,'2021-10-01','jottings test 1','EXPENSE',15,-4);
INSERT INTO transaction_table VALUES (31,150,'2021-10-02','jottings test 2','INCOME',16,-10);
INSERT INTO transaction_table VALUES (32,200,'2021-10-03','jottings test 3','EXPENSE',17,-1);

INSERT INTO transaction_table VALUES (33,100,'2021-09-01','jottings test 4','EXPENSE',18,-4);
INSERT INTO transaction_table VALUES (34,150,'2021-09-21','jottings test 5','INCOME',18,-10);
INSERT INTO transaction_table VALUES (35,200,'2021-09-30','jottings test 6','EXPENSE',18,-1);
INSERT INTO transaction_table VALUES (36,100,'2021-10-01','jottings test 4','EXPENSE',18,-4);
INSERT INTO transaction_table VALUES (37,150,'2021-10-02','jottings test 5','INCOME',18,-10);
INSERT INTO transaction_table VALUES (38,200,'2021-10-05','jottings test 6','EXPENSE',18,-1);
INSERT INTO transaction_table VALUES (39,200,'2021-10-06','jottings test 6','EXPENSE',18,-1);