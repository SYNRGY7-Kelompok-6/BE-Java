package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.entity.Account;
import com.kelp_6.banking_apps.entity.ETransactionType;
import com.kelp_6.banking_apps.entity.Transaction;
import com.kelp_6.banking_apps.entity.User;
import com.kelp_6.banking_apps.model.transfer.intrabank.Amount;
import com.kelp_6.banking_apps.model.transfer.intrabank.TransferRequest;
import com.kelp_6.banking_apps.model.transfer.intrabank.TransferResponse;
import com.kelp_6.banking_apps.repository.AccountRepository;
import com.kelp_6.banking_apps.repository.TransactionRepository;
import com.kelp_6.banking_apps.repository.UserRepository;
import com.kelp_6.banking_apps.utils.CurrencyUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TransactionIntrabankServiceImpl implements TransactionIntrabankService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ValidationService validationService;

    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        this.validationService.validate(request);

        User user = userRepository.findByUserID(request.getUserID()).orElseThrow(() -> new UsernameNotFoundException(
                String.format(" %s doesn't exists", request.getUserID())
        ));

        Account srcAccount = accountRepository.findByUser(user.getId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "source account number doesn't exists"));
        if (request.getBeneficiaryAccountNumber().equals(srcAccount.getAccountNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "can't transfer to oneself account");
        }
        Account benAccount = accountRepository.findByAccountNumber(request.getBeneficiaryAccountNumber())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "beneficiary account number doesn't exists"));

        // original source amount before transfer
        Amount srcAmount = Amount.builder()
                .value(srcAccount.getAvailableBalance())
                .currency(srcAccount.getAvailableBalanceCurr())
                .build();
        // original beneficiary amount before transfer
        Amount benAmount = Amount.builder()
                .value(benAccount.getAvailableBalance())
                .currency(benAccount.getAvailableBalanceCurr())
                .build();
        // requested amount in IDR
        Amount reqAmountIDR = CurrencyUtil.convertAmountCurrency(request.getAmount(), "IDR")
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "unsupported amount currency"));
        // requested amount in source account currency
        Amount reqAmountSRC = CurrencyUtil.convertAmountCurrency(request.getAmount(), srcAmount.getCurrency())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "unsupported amount currency"));
        // requested amount in beneficiary account currency
        Amount reqAmountBEN = CurrencyUtil.convertAmountCurrency(request.getAmount(), benAmount.getCurrency())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "unsupported amount currency"));

        // notes: transferLimit is IDR by default
        if (reqAmountIDR.getValue() > srcAccount.getAccountType().getTransferLimit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "transfer limit exceed");
        }
        if (srcAmount.getValue() - reqAmountSRC.getValue() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "insufficient balance");
        }

        double srcAccRemainingBalance = srcAccount.getAvailableBalance() - reqAmountSRC.getValue();
        double benAccRemainingBalance = benAccount.getAvailableBalance() + reqAmountBEN.getValue();
        Date transactionDate = new Date();

        if (request.getRemark() == null || request.getRemark().isEmpty()) {
            request.setDescription("TRANSFER INTRABANK");
        }

        // source account transaction record
        Transaction srcAccountTransaction = Transaction.builder()
                .beneficiaryAccountNumber(benAccount.getAccountNumber())
                .beneficiaryEmail(benAccount.getUser().getUsername())
                .beneficiaryName(benAccount.getUser().getName())
                .amount(request.getAmount().getValue())
                .currency(request.getAmount().getCurrency())
                .remainingBalance(srcAccount.getAvailableBalance())
                .remark(request.getRemark())
                .description(request.getDescription())
                .transactionDate(transactionDate)
                .account(srcAccount)
                .type(ETransactionType.DEBIT)
                .build();
        this.transactionRepository.save(srcAccountTransaction);
        // beneficiary account transaction record
        Transaction benAccountTransaction = Transaction.builder()
                .beneficiaryAccountNumber(srcAccount.getAccountNumber())
                .beneficiaryEmail(srcAccount.getUser().getUsername())
                .beneficiaryName(srcAccount.getUser().getName())
                .amount(request.getAmount().getValue())
                .currency(request.getAmount().getCurrency())
                .remainingBalance(benAccRemainingBalance)
                .remark(request.getRemark())
                .description(request.getDescription())
                .transactionDate(transactionDate)
                .account(benAccount)
                .type(ETransactionType.CREDIT)
                .build();
        this.transactionRepository.save(benAccountTransaction);

        srcAccount.setAvailableBalance(srcAccRemainingBalance);
        this.accountRepository.save(srcAccount);
        benAccount.setAvailableBalance(benAccRemainingBalance);
        this.accountRepository.save(benAccount);

        return TransferResponse.builder()
                .transactionId(srcAccountTransaction.getId().toString()) // TODO: should replace with reference number
                .amount(request.getAmount())
                .transactionDate(transactionDate)
                .beneficiaryAccountNumber(srcAccountTransaction.getBeneficiaryAccountNumber())
                .beneficiaryName(srcAccountTransaction.getBeneficiaryName())
                .sourceAccountNumber(srcAccount.getAccountNumber())
                .sourceName(srcAccount.getUser().getName())
                .build();
    }
}
