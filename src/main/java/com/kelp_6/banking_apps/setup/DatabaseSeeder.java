package com.kelp_6.banking_apps.setup;

import com.kelp_6.banking_apps.entity.*;
import com.kelp_6.banking_apps.repository.*;
import com.kelp_6.banking_apps.utils.Generator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private final SavedAccountsRespository savedAccountsRespository;
    private final SimpleDateFormat birthFormatter = new SimpleDateFormat("dd-MM-yyyy");

    @Transactional
    public void setup() {
        transactionRepository.hardDeleteAll();
        savedAccountsRespository.hardDeleteAll();
        accountRepository.hardDeleteAll();
        accountTypeRepository.hardDeleteAll();
        loginInfosRepository.hardDeleteAll();
        userRepository.hardDeleteAll();

        /*
            USER SEEDER
        */
        int numUsers = 2;
        List<User> listUsers = new ArrayList<>();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Date birthDate = new Date();

        try {
            birthDate = birthFormatter.parse("04-08-2002");
        }catch (ParseException exception){
            log.error("error while parse birth date");
        }

        for(int i=0; i<numUsers; i++){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
            calendar.setTime(new Date());
            calendar.set(Calendar.YEAR, 2027);

            User user = User.builder()
                    .name((i == 0) ? "Muh. Sabili" : "Abilsabili")
                    .username((i==0) ? "inikel6synrgy@gmail.com" : "abilsabili04@gmail.com")
                    .password(passwordEncoder.encode(String.format("Password_%d", i)))
                    .isVerified(true)
                    .pin(passwordEncoder.encode("123456"))
                    .pinExpiredDate(calendar.getTime())
                    .birth(birthDate)
                    .phone((i==0) ? "0895411255580" : "0895411255582")
                    .address("Jalan Balongsari No.13, Kecamatan Tandes, Kota Surabaya, Prov. Jawa Timur")
                    .imageUrl("https://www.kindpng.com/picc/m/252-2524695_dummy-profile-image-jpg-hd-png-download.png")
                    .build();

            if(i==0) user.setUserID("user001");
            if(i==1) user.setUserID("user002");

            listUsers.add(user);
        }

        // seed additional users
        Calendar calendarAdd = Calendar.getInstance();
        calendarAdd.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        calendarAdd.setTime(new Date());
        calendarAdd.set(Calendar.YEAR, 2028);

        User userAdd1 = User.builder()
                .name("Raisa Winarsih")
                .username("raisa@test.com")
                .password(passwordEncoder.encode("Password654"))
                .isVerified(true)
                .pin(passwordEncoder.encode("654321"))
                .pinExpiredDate(calendarAdd.getTime())
                .userID("raisa654")
                .birth(birthDate)
                .phone("0895411255583")
                .address("Jalan Balongsari No.13, Kecamatan Tandes, Kota Surabaya, Prov. Jawa Timur")
                .imageUrl("https://www.kindpng.com/picc/m/252-2524695_dummy-profile-image-jpg-hd-png-download.png")
                .build();
        listUsers.add(userAdd1);
        User userAdd2 = User.builder()
                .name("Yahya Anggriawan")
                .username("yahya@test.com")
                .password(passwordEncoder.encode("Password456"))
                .isVerified(true)
                .pin(passwordEncoder.encode("654321"))
                .pinExpiredDate(calendarAdd.getTime())
                .userID("yahya456")
                .birth(birthDate)
                .phone("0895411255584")
                .address("Jalan Balongsari No.13, Kecamatan Tandes, Kota Surabaya, Prov. Jawa Timur")
                .imageUrl("https://www.kindpng.com/picc/m/252-2524695_dummy-profile-image-jpg-hd-png-download.png")
                .build();
        listUsers.add(userAdd2);
        User userAdd3 = User.builder()
                .name("Eko Kurniawan")
                .username("kurniawan@test.com")
                .password(passwordEncoder.encode("Password111"))
                .isVerified(true)
                .pin(passwordEncoder.encode("111111"))
                .pinExpiredDate(calendarAdd.getTime())
                .userID("kurniawan111")
                .birth(birthDate)
                .phone("0895411255585")
                .address("Jalan Balongsari No.13, Kecamatan Tandes, Kota Surabaya, Prov. Jawa Timur")
                .imageUrl("https://www.kindpng.com/picc/m/252-2524695_dummy-profile-image-jpg-hd-png-download.png")
                .build();
        listUsers.add(userAdd3);

        userRepository.saveAll(listUsers);
        log.info("[SUCCESS] Seeds {} records to users table", listUsers.size());

        /*
            ACCOUNT TYPE SEEDER
        */
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

        /*
            ACCOUNT SEEDER
        */
        List<Account> listAccounts = new ArrayList<>();
        List<String> listAccountNumber = new ArrayList<>();
        listAccountNumber.add("2859613256");
        listAccountNumber.add("9330903549");
        listAccountNumber.add("2860724357");
        listAccountNumber.add("9341014650");
        listAccountNumber.add("5647282937");

        int counter = 0;
        for (User user : listUsers) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
            calendar.add(Calendar.YEAR, 2);
            calendar.add(Calendar.HOUR_OF_DAY, 10);
            calendar.add(Calendar.MINUTE, 30);

            Account account = Account.builder()
                    .accountNumber(listAccountNumber.get(counter))
                    .availableBalance((double) (Generator.numberRangeGenerator(500, 5000) * 1000))
                    .availableBalanceCurr("IDR")
                    .holdAmount(50000D)
                    .holdAmountCurr("IDR")
                    .cvv("56" + counter)
                    .user(user)
                    .accountType(accountTypes.get(counter % 2 == 0 ? 0 : 1))
                    .accountExp(calendar.getTime())
                    .build();
            user.setAccount(account);
            listAccounts.add(account);
            counter++;
        }

        accountRepository.saveAll(listAccounts);
        log.info("[SUCCESS] Seeds {} records to accounts table", listAccounts.size());

        /*
            LOGIN INFO SEEDER
        */
        List<Date> dates = new ArrayList<>();
        List<LoginInfos> listLoginInfos = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
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
        log.info("[SUCCESS] Seeds {} records to login infos table", listLoginInfos.size());

        /*
            SAVED ACCOUNT SEEDER
        */
        HashMap<Integer, List<Integer>> savedAccIx = new HashMap<>();
        savedAccIx.put(0, List.of(1, 2));
        savedAccIx.put(1, List.of(2, 3));
        savedAccIx.put(2, List.of(3, 0));
        List<SavedAccounts> savedAccounts = new ArrayList<>();

        for (Integer key : savedAccIx.keySet()) {
            for (int item : savedAccIx.get(key)) {
                savedAccounts.add(
                        SavedAccounts.builder()
                                .user(listUsers.get(key))
                                .account(listUsers.get(item).getAccount())
                                .favorite(true)
                                .build()
                );
            }
        }

        savedAccountsRespository.saveAll(savedAccounts);
        log.info("[SUCCESS] Seeds {} records to saved accounts table", savedAccounts.size());

        /*
            TRANSACTION SEEDER
        */
        // "Transfer" type transaction
        List<Transaction> listTransactions = new ArrayList<>();
        Map<Integer, List<Integer>> oppositeUser = new HashMap<>();
        oppositeUser.put(0, List.of(1, 2, 3, 3, 2, 1));
        oppositeUser.put(1, List.of(0, 2, 3, 3, 2, 0));
        oppositeUser.put(2, List.of(0, 1, 3, 3, 1, 0));
        oppositeUser.put(3, List.of(0, 1, 2, 2, 1, 0));

        for(int i=0; i<oppositeUser.size(); i++){
            for(Integer oppositeId : oppositeUser.get(i)){
                double amount = Generator.numberRangeGenerator(10, 100) * 1000;

                User owner = listUsers.get(i);
                User opposite = listUsers.get(oppositeId);
                owner.getAccount().setAvailableBalance(owner.getAccount().getAvailableBalance() - amount);
                opposite.getAccount().setAvailableBalance(opposite.getAccount().getAvailableBalance() + amount);
                String remark = (Generator.numberRangeGenerator(100, 300) < 200) ? "Transfer" : "QRIS Transfer";

                Date transactionDate = new Date();
                Transaction debitTransaction = Transaction.builder()
                        .refNumber(Generator.refNumberGenerator(transactionDate))
                        .amount(amount)
                        .currency("IDR")
                        .remainingBalance(owner.getAccount().getAvailableBalance())
                        .beneficiaryAccountNumber(opposite.getAccount().getAccountNumber())
                        .beneficiaryEmail(opposite.getUsername())
                        .beneficiaryName(opposite.getName())
                        .type(ETransactionType.DEBIT)
                        .remark(remark)
                        .description("dummy transfer")
                        .account(owner.getAccount())
                        .build();

                Transaction creditTransaction = Transaction.builder()
                        .refNumber(Generator.refNumberGenerator(transactionDate))
                        .amount(amount)
                        .currency("IDR")
                        .remainingBalance(opposite.getAccount().getAvailableBalance())
                        .beneficiaryAccountNumber(owner.getAccount().getAccountNumber())
                        .beneficiaryEmail(owner.getUsername())
                        .beneficiaryName(owner.getName())
                        .type(ETransactionType.CREDIT)
                        .remark(remark)
                        .description("dummy transfer")
                        .account(opposite.getAccount())
                        .build();

                listTransactions.add(creditTransaction);
                listTransactions.add(debitTransaction);
            }
        }

        transactionRepository.saveAll(listTransactions);
        log.info("[SUCCESS] Seeds {} records to transactions table", listTransactions.size());
    }
}
