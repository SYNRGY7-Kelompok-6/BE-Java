package com.kelp_6.banking_apps.setup;

import com.kelp_6.banking_apps.entity.ETransactionType;
import com.kelp_6.banking_apps.entity.Transaction;
import com.kelp_6.banking_apps.entity.User;
import com.kelp_6.banking_apps.repository.TransactionRepository;
import com.kelp_6.banking_apps.repository.UserRepository;
import com.kelp_6.banking_apps.utils.Generator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final TransactionRepository transactionRepository;

    @Transactional
    public void setup(){
        transactionRepository.hardDeleteAll();
        userRepository.hardDeleteAll();

        // SEED USERS
        int numUsers = 2;
        List<User> listUsers = new ArrayList<>();

        for(int i=0; i<numUsers; i++){
//            User user = new User();
//            user.setName("Test " + i);
//            user.setEmail("test" + i + "@test.com");
//            user.setPassword("withoutencoder" + i);
            User user = User.builder()
                    .name("Test " + i)
                    .email("test" + i + "@test.com")
                    .password("withoutencoder" + i)
                    .isVerified(true)
                    .accountNumber(Generator.tenDigitNumberGenerator())
                    .balance(100000L)
                    .build();

            listUsers.add(user);
        }

        userRepository.saveAllAndFlush(listUsers);

        log.info("[SUCCESS] Seeds {} records to users table", listUsers.size());

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
                    .amount(10000L)
                    .description("dummy transfer")
                    .type(ETransactionType.CREDIT)
                    .oppositeAccNumber(opposite.getAccountNumber())
                    .user(owner)
                    .build();

            Transaction debitTransaction = Transaction.builder()
                    .amount(10000L)
                    .description("dummy transfer")
                    .type(ETransactionType.DEBIT)
                    .oppositeAccNumber(owner.getAccountNumber())
                    .user(opposite)
                    .build();

            listTransactions.add(creditTransaction);
            listTransactions.add(debitTransaction);
        }

        transactionRepository.saveAllAndFlush(listTransactions);

        log.info("[SUCCESS] Seeds {} records to transactions table", listTransactions.size());
    }
}
