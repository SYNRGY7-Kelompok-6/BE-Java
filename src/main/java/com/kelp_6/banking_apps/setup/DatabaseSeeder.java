package com.kelp_6.banking_apps.setup;

import com.kelp_6.banking_apps.entity.*;
import com.kelp_6.banking_apps.repository.*;
import com.kelp_6.banking_apps.utils.Generator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final LoginInfosRepository loginInfosRepository;

    @Transactional
    public void setup(){
        transactionRepository.hardDeleteAll();
        accountRepository.hardDeleteAll();
        accountTypeRepository.hardDeleteAll();
        loginInfosRepository.hardDeleteAll();
        userRepository.hardDeleteAll();

        // SEED USERS
        int numUsers = 2;
        List<User> listUsers = new ArrayList<>();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        for(int i=0; i<numUsers; i++){
            User user = User.builder()
                    .name("Test " + i)
                    .username("test" + i + "@test.com")
                    .password(passwordEncoder.encode(String.format("Password_%d", i)))
                    .isVerified(true)
                    .pin(passwordEncoder.encode("123456"))
                    .pinExpiredDate(new Date())
                    .build();

            if(i==0) user.setUserID("zg6kx3FFrDatGLHG");
            if(i==1) user.setUserID("i4rnpOL3iIQZnIkj");

            listUsers.add(user);
        }

        userRepository.saveAll(listUsers);

        log.info("[SUCCESS] Seeds {} records to users table", listUsers.size());

        // SEED ACCOUNT TYPE

        List<AccountType> accountTypes = new ArrayList<>();
        Map<String, Double> accountType = new HashMap<>();
        accountType.put("Ekspresi", 10000000D);
        accountType.put("Master", 100000000D);

        for(Map.Entry<String, Double> entry : accountType.entrySet()){
            AccountType newAccountType = AccountType.builder()
                    .name(entry.getKey())
                    .transferLimit(entry.getValue())
                    .build();
            accountTypes.add(newAccountType);
        }

        accountTypeRepository.saveAll(accountTypes);

        log.info("[SUCCESS] Seeds {} records to account_types table", accountTypes.size());

        // SEED ACCOUNT
        List<Account> listAccounts = new ArrayList<>();

        int counter = 0;
        for (User listUser : listUsers) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 2);
            calendar.add(Calendar.HOUR_OF_DAY, 10);
            calendar.add(Calendar.MINUTE, 30);

            Account account = Account.builder()
                    .accountNumber(Generator.tenDigitNumberGenerator())
                    .availableBalance(100000D)
                    .availableBalanceCurr("IDR")
                    .holdAmount(0D)
                    .holdAmountCurr("IDR")
                    .user(listUser)
                    .accountType(accountTypes.get(counter))
                    .accountExp(calendar.getTime())
                    .build();
            listUser.setAccount(account);
            listAccounts.add(account);
            counter++;
        }

        accountRepository.saveAll(listAccounts);

        // SEED LOGIN INFO
        List<Date> dates = new ArrayList<>();
        List<LoginInfos> listLoginInfos = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendar.getTime();
        dates.add(yesterday);
        dates.add(new Date());

        for(User curUser : listUsers){
            counter = 0;
            for(Date curDate : dates){
                LoginInfos infos = LoginInfos.builder()
                        .ipAddress("17.172.224.47")
                        .isSuccess(counter != 0)
                        .location("Surabaya, Indonesia")
                        .timestamp(curDate)
                        .user(curUser)
                        .build();
                counter++;
                listLoginInfos.add(infos);
            }
        }

        loginInfosRepository.saveAll(listLoginInfos);

        // SEED TRANSACTION
        int numTransactions = 2;
        List<Transaction> listTransactions = new ArrayList<>();
        Map<Integer, Integer> oppositeUser = new HashMap<>();
        oppositeUser.put(0, 1);
        oppositeUser.put(1, 0);

        for(int i=0; i<numTransactions; i++){
            User owner = listUsers.get(i);
            User opposite = listUsers.get(oppositeUser.get(i));
            owner.getAccount().setAvailableBalance(owner.getAccount().getAvailableBalance() + 10000);
            opposite.getAccount().setAvailableBalance(opposite.getAccount().getAvailableBalance() - 10000);

            Transaction creditTransaction = Transaction.builder()
                    .amount(10000D)
                    .currency("IDR")
                    .remainingBalance(owner.getAccount().getAvailableBalance())
                    .beneficiaryAccountNumber(opposite.getAccount().getAccountNumber())
                    .beneficiaryEmail(opposite.getUsername())
                    .beneficiaryName(opposite.getName())
                    .type(ETransactionType.CREDIT)
                    .remark("dummy transfer")
                    .account(owner.getAccount())
                    .build();

            Transaction debitTransaction = Transaction.builder()
                    .amount(10000D)
                    .currency("IDR")
                    .remainingBalance(opposite.getAccount().getAvailableBalance())
                    .beneficiaryAccountNumber(owner.getAccount().getAccountNumber())
                    .beneficiaryEmail(owner.getUsername())
                    .beneficiaryName(owner.getName())
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
