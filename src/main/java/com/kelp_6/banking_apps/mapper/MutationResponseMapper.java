package com.kelp_6.banking_apps.mapper;

import com.kelp_6.banking_apps.entity.Account;
import com.kelp_6.banking_apps.entity.ETransactionType;
import com.kelp_6.banking_apps.entity.Transaction;
import com.kelp_6.banking_apps.entity.User;
import com.kelp_6.banking_apps.model.mutation.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MutationResponseMapper {
    public MutationResponse toDataDTO(Account account, List<Transaction> transactions, double startingBalance){
        return MutationResponse.builder()
                .accountInfo(toAccountInfoDTO(account))
                .accountBalance(toAccountBalanceDTO(account, startingBalance))
                .mutations(transactions.stream().map(this::toMutationDTO).collect(Collectors.toList()))
                .build();
    }

    // ACCOUNT INFO MAPPER
    private AccountInfoResponse toAccountInfoDTO(Account account){
        return AccountInfoResponse.builder()
                .accountNo(account.getAccountNumber())
                .accountType(account.getAccountType().getName())
                .accountCardExp(account.getAccountExp())
                .name(account.getUser().getName())
                .cvv(account.getCvv())
                .accountBalance(toAccountBalanceDetailsDTO(account))
                .pinExpiredTimeLeft(calculatePinExpiredTimeLeft(account.getUser().getPinExpiredDate()))
                .build();
    }

    private AccountBalanceDetailsResponse toAccountBalanceDetailsDTO(Account account){
        return AccountBalanceDetailsResponse.builder()
                .availableBalance(toBalanceDTO(account.getAvailableBalance(), account.getAvailableBalanceCurr()))
                .holdAmount(toBalanceDTO(account.getHoldAmount(), account.getHoldAmountCurr()))
                .build();
    }

    public AccountMonthlyResponse calculateMonthlyIncomeOutcome(List<Transaction> transactions){
        double monthlyIncome = transactions.stream()
                .filter(transaction -> transaction.getType() == ETransactionType.CREDIT)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double monthlyOutcome = transactions.stream()
                .filter(transaction -> transaction.getType() == ETransactionType.DEBIT)
                .mapToDouble(Transaction::getAmount)
                .sum();

        return AccountMonthlyResponse.builder()
                .monthlyIncome(toBalanceDTO(monthlyIncome, "IDR"))
                .monthlyOutcome(toBalanceDTO(monthlyOutcome, "IDR"))
                .build();
    }

    private int calculatePinExpiredTimeLeft(Date pinExpiredLeft){
        long diff = pinExpiredLeft.getTime() - new Date().getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    // ACCOUNT BALANCE MAPPER
    private AccountBalanceResponse toAccountBalanceDTO(Account account, double startingBalance){
        return AccountBalanceResponse.builder()
                .startingBalance(toBalanceDetailDTO(startingBalance, account.getAvailableBalanceCurr(), new Date().toString()))
                .endingBalance(toBalanceDetailDTO(account.getAvailableBalance(), account.getAvailableBalanceCurr(), new Date().toString()))
                .build();
    }

    private BalanceDetailsResponse toBalanceDetailDTO(double value, String currency, String dateTime){
        return BalanceDetailsResponse.builder()
                .value(value)
                .currency(currency)
                .dateTime(dateTime)
                .build();
    }

    private BalanceResponse toBalanceDTO(double value, String currency){
        return BalanceResponse.builder()
                .value(value)
                .currency(currency)
                .build();
    }

    // MUTATION MAPPER
    private DetailMutationResponse toMutationDTO(Transaction transaction){
        return DetailMutationResponse.builder()
                .transactionId(transaction.getId().toString())
                .amount(toMutationBalanceDTO(transaction.getAmount(), transaction.getRemainingBalance(), transaction.getCurrency()))
                .transactionDate(transaction.getTransactionDate().toString())
                .remark(transaction.getRemark())
                .desc(transaction.getDescription())
                .type(transaction.getType().name())
                .beneficiaryAccount(toBeneficiaryAccountDetailDTO(transaction.getBeneficiaryAccountNumber(), transaction.getBeneficiaryName()))
                .sourceAccount(toSourceAccountDetailDTO(transaction.getAccount().getAccountNumber(), transaction.getAccount().getUser().getName()))
                .build();
    }

    private BeneficiaryAccountResponse toBeneficiaryAccountDetailDTO(String accountNumber, String accountName){
        return BeneficiaryAccountResponse.builder()
                .beneficiaryAccountNumber(accountNumber)
                .beneficiaryAccountName(accountName)
                .build();
    }

    private SourceAccountResponse toSourceAccountDetailDTO(String accountNumber, String accountName){
        return SourceAccountResponse.builder()
                .sourceAccountNumber(accountNumber)
                .sourceAccountName(accountName)
                .build();
    }

    private MutationBalanceResponse toMutationBalanceDTO(double value, double remainingBalance, String currency){
        return MutationBalanceResponse.builder()
                .value(value)
                .remainingBalance(remainingBalance)
                .currency(currency)
                .build();
    }


    public TransactionDetailResponse toTransactionDetailDTO(User user, Transaction transaction,Account account) {
        return TransactionDetailResponse.builder()
                .transactionId(transaction.getId().toString())
                .amount(transaction.getAmount())
                .sourceName(user.getName())
                .type(transaction.getType().name())
                .sourceAccountNumber(account.getAccountNumber())
                .transactionDate(transaction.getTransactionDate())
                .remark(transaction.getRemark())
                .desc(transaction.getDescription())
                .beneficiaryAccountNumber(transaction.getBeneficiaryAccountNumber())
                .beneficiaryName(transaction.getBeneficiaryName())
                .build();
    }

    public AccountMonthlyResponse toMonthlyMutation(List<Transaction> transactions) {
        BalanceResponse monthlyIncome = new BalanceResponse();
        BalanceResponse monthlyOutcome = new BalanceResponse();
        AccountMonthlyResponse response = new AccountMonthlyResponse();

        double totalIncome = 0;
        double totalOutcome = 0;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == ETransactionType.CREDIT) {
                totalIncome += transaction.getAmount();
            } else if (transaction.getType() == ETransactionType.DEBIT) {
                totalOutcome += transaction.getAmount();
            }
        }

        monthlyIncome.setValue(totalIncome);
        monthlyIncome.setCurrency("IDR");
        monthlyOutcome.setValue(totalOutcome);
        monthlyOutcome.setCurrency("IDR");
        System.out.println(monthlyIncome.getValue());
        System.out.println(monthlyOutcome.getValue());
        response.setMonthlyIncome(monthlyIncome);
        response.setMonthlyOutcome(monthlyOutcome);

        return response;
    }
}
