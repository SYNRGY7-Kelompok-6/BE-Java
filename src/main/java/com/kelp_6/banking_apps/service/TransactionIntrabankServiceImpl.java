package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.entity.*;
import com.kelp_6.banking_apps.model.email.EmailModel;
import com.kelp_6.banking_apps.model.transfer.intrabank.Amount;
import com.kelp_6.banking_apps.model.transfer.intrabank.TransferRequest;
import com.kelp_6.banking_apps.model.transfer.intrabank.TransferResponse;
import com.kelp_6.banking_apps.repository.*;
import com.kelp_6.banking_apps.service.email.EmailService;
import com.kelp_6.banking_apps.utils.CurrencyUtil;
import com.kelp_6.banking_apps.utils.Generator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TransactionIntrabankServiceImpl implements TransactionIntrabankService {
    private final static Logger LOGGER = LoggerFactory.getLogger(TransactionIntrabankServiceImpl.class);

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final SavedAccountsRespository savedAccountsRespository;
    private final TransactionRepository transactionRepository;
    private final ValidationService validationService;
    private final TransactionTokenService transactionTokenService;
    private final BlacklistedUserPinTokenRepository blacklistedUserPinTokenRepository;

    @Autowired
    private EmailService emailService;
    @Transactional
    public TransferResponse transfer(TransferRequest request, Boolean isSchedule) {
        LOGGER.info("accessed");

        if(request.getRemark() == null || (!request.getRemark().equalsIgnoreCase("Transfer") && !request.getRemark().equalsIgnoreCase("Scheduled Transfer"))){
            if(request.getRemark() != null && (!request.getRemark().equalsIgnoreCase("QRIS Transfer") && !request.getRemark().equalsIgnoreCase("QRIS Pay"))){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown Remark");
            }
        }

        User user = userRepository.findByUserID(request.getUserID()).orElseThrow(() -> new UsernameNotFoundException(
                String.format(" %s doesn't exists", request.getUserID())
        ));

        Account srcAccount = accountRepository.findByUser(user.getId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "source account number doesn't exists"));
        if(!isSchedule && (request.getPinToken() == null || request.getPinToken().isEmpty())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid pin credential");
        }

        boolean pinTokenChecker = !isSchedule && (!this.transactionTokenService.validateTransactionToken(request.getPinToken(), srcAccount.getAccountNumber()) || this.blacklistedUserPinTokenRepository.existsByUser_IdAndPinToken(user.getId(), request.getPinToken()));
        if (pinTokenChecker) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid pin credential");
        }
        if (request.getBeneficiaryAccountNumber().equals(srcAccount.getAccountNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "can't transfer to oneself account");
        }

        if (request.getRemark().equalsIgnoreCase("Transfer") && !savedAccountsRespository.existsByUser_IdAndAccount_AccountNumber(user.getId(), request.getBeneficiaryAccountNumber())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "account number not saved");
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

        if(request.getDesc() == null || request.getDesc().isEmpty()){
            request.setDesc(request.getRemark());
        }

        // source account transaction record
        Transaction srcAccountTransaction = Transaction.builder()
                .refNumber(Generator.refNumberGenerator(transactionDate))
                .beneficiaryAccountNumber(benAccount.getAccountNumber())
                .beneficiaryEmail(benAccount.getUser().getUsername())
                .beneficiaryName(benAccount.getUser().getName())
                .amount(request.getAmount().getValue())
                .currency(request.getAmount().getCurrency())
                .remainingBalance(srcAccRemainingBalance)
                .remark(request.getRemark())
                .description(request.getDesc())
                .transactionDate(transactionDate)
                .account(srcAccount)
                .type(ETransactionType.DEBIT)
                .build();
        // beneficiary account transaction record
        Transaction benAccountTransaction = Transaction.builder()
                .refNumber(Generator.refNumberGenerator(transactionDate))
                .beneficiaryAccountNumber(srcAccount.getAccountNumber())
                .beneficiaryEmail(srcAccount.getUser().getUsername())
                .beneficiaryName(srcAccount.getUser().getName())
                .amount(request.getAmount().getValue())
                .currency(request.getAmount().getCurrency())
                .remainingBalance(benAccRemainingBalance)
                .remark(request.getRemark())
                .description(request.getDesc())
                .transactionDate(transactionDate)
                .account(benAccount)
                .type(ETransactionType.CREDIT)
                .build();
        this.transactionRepository.saveAll(Arrays.asList(srcAccountTransaction, benAccountTransaction));

        srcAccount.setAvailableBalance(srcAccRemainingBalance);
        benAccount.setAvailableBalance(benAccRemainingBalance);
        this.accountRepository.saveAll(Arrays.asList(srcAccount, benAccount));

        EmailModel emailData = EmailModel.builder()
                .beneficiaryAccount(benAccount.getAccountNumber())
                .beneficiaryName(srcAccount.getUser().getName())
                .amount(request.getAmount())
                .beneficiaryEmail(benAccount.getUser().getUsername())
                .sender(srcAccount.getUser().getName())
                .transactionDate(benAccountTransaction.getTransactionDate())
                .build();

        try {
            emailService.notificationIncomingFunds(emailData);
            LOGGER.info("Email notification sent successfully to {}", benAccount.getUser().getUsername());
        }catch (Exception exception){
            LOGGER.error("Failed to send email notification to {}: {}", benAccount.getUser().getUsername(), exception.getMessage(), exception);
        }

        BlacklistedUserPinToken blacklistedUserPinToken = BlacklistedUserPinToken.builder()
                .user(user)
                .pinToken(request.getPinToken())
                .build();
        blacklistedUserPinTokenRepository.save(blacklistedUserPinToken);

        return TransferResponse.builder()
                .refNumber(srcAccountTransaction.getRefNumber())
                .transactionId(srcAccountTransaction.getId().toString())
                .amount(request.getAmount())
                .transactionDate(transactionDate.toString())
                .remark(srcAccountTransaction.getRemark())
                .desc(srcAccountTransaction.getDescription())
                .beneficiaryAccountNumber(srcAccountTransaction.getBeneficiaryAccountNumber())
                .beneficiaryName(srcAccountTransaction.getBeneficiaryName())
                .sourceAccountNumber(srcAccount.getAccountNumber())
                .sourceName(srcAccount.getUser().getName())
                .build();

    }
}
