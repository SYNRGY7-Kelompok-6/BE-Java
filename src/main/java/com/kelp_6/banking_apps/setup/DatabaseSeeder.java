package com.kelp_6.banking_apps.setup;

import com.kelp_6.banking_apps.entity.Account;
import com.kelp_6.banking_apps.entity.ETransactionType;
import com.kelp_6.banking_apps.entity.Transaction;
import com.kelp_6.banking_apps.entity.User;
import com.kelp_6.banking_apps.repository.AccountRepository;
import com.kelp_6.banking_apps.repository.TransactionRepository;
import com.kelp_6.banking_apps.repository.UserRepository;
import com.kelp_6.banking_apps.utils.Generator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final TransactionRepository transactionRepository;

    @Autowired
    private final AccountRepository accountRepository;

    @Transactional
    public void setup(){
        transactionRepository.hardDeleteAll();
        accountRepository.hardDeleteAll();
        userRepository.hardDeleteAll();

        // SEED USERS
        int numUsers = 2;
        List<User> listUsers = new ArrayList<>();

        for(int i=0; i<numUsers; i++){
            User user = User.builder()
                    .name("Test " + i)
                    .email("test" + i + "@test.com")
                    .password("withoutencoder" + i)
                    .isVerified(true)
                    .build();

            listUsers.add(user);
        }

        userRepository.saveAll(listUsers);

        log.info("[SUCCESS] Seeds {} records to users table", listUsers.size());

        // SEED ACCOUNT
        int numAccount = listUsers.size();
        List<Account> listAccounts = new ArrayList<>();

        for(int i=0; i<numAccount; i++){
            Account account = Account.builder()
                    .accountNumber(Generator.tenDigitNumberGenerator())
                    .availableBalance(100000D)
                    .currency("IDR")
                    .user(listUsers.get(i))
                    .build();
            listUsers.get(i).setAccount(account);
            listAccounts.add(account);
        }

        accountRepository.saveAll(listAccounts);

        // SEED TRANSACTION
        int numTransactions = 2;
        List<Transaction> listTransactions = new ArrayList<>();
        Map<Integer, Integer> oppositeUser = new HashMap<>();
        oppositeUser.put(0, 1);
        oppositeUser.put(1,0);

        for(int i=0; i<numTransactions; i++){
            User owner = listUsers.get(i);
            User opposite = listUsers.get(oppositeUser.get(i));

            Transaction creditTransaction = Transaction.builder()
                    .amount(10000D)
                    .currency("IDR")
                    .beneficiaryAccountNumber(opposite.getAccount().getAccountNumber())
                    .beneficiaryEmail(opposite.getEmail())
                    .type(ETransactionType.CREDIT)
                    .remark("dummy transfer")
                    .account(owner.getAccount())
                    .build();

            Transaction debitTransaction = Transaction.builder()
                    .amount(10000D)
                    .currency("IDR")
                    .beneficiaryAccountNumber(owner.getAccount().getAccountNumber())
                    .beneficiaryEmail(owner.getEmail())
                    .type(ETransactionType.DEBIT)
                    .remark("dummy transfer")
                    .account(opposite.getAccount())
                    .build();

            listTransactions.add(creditTransaction);
            listTransactions.add(debitTransaction);
        }

        transactionRepository.saveAll(listTransactions);

        log.info("[SUCCESS] Seeds {} records to transactions table", listTransactions.size());
    }
}
