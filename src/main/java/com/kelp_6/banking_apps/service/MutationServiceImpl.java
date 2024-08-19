package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.entity.Account;
import com.kelp_6.banking_apps.entity.ETransactionType;
import com.kelp_6.banking_apps.entity.Transaction;
import com.kelp_6.banking_apps.entity.User;
import com.kelp_6.banking_apps.mapper.MutationResponseMapper;
import com.kelp_6.banking_apps.model.mutation.*;
import com.kelp_6.banking_apps.repository.AccountRepository;
import com.kelp_6.banking_apps.repository.TransactionRepository;
import com.kelp_6.banking_apps.repository.UserRepository;
import com.kelp_6.banking_apps.utils.UuidUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MutationServiceImpl implements MutationService{
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final MutationResponseMapper mutationResponseMapper;
    private final TransactionTokenService transactionTokenService;
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public MutationResponse getMutation(MutationRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());

        User user = userRepository.findByUserID(request.getUserID()).orElseThrow(() -> new UsernameNotFoundException(
                String.format(" %s doesn't exists", request.getUserID())
        ));
        Account account = accountRepository.findAccountByAccountNumberAndByUser_Username(user.getUsername()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "account number doesn't exists"));
        List<Transaction> transactions = transactionRepository.findAllByAccount_AccountNumberAndBetween(account.getAccountNumber(), request.getFromDate(), request.getToDate(), pageable);

        BalanceDetailsResponse startingBalance = calculateStartingBalance(account.getAvailableBalance(), transactions, user.getCreatedDate());

        String endingDateBalance = formatter.format(new Date());

        if(!transactions.isEmpty()){
            endingDateBalance = formatter.format(transactions.get(0).getTransactionDate());
        }

        return mutationResponseMapper.toDataDTO(account, transactions, startingBalance, endingDateBalance);
    }

    @Override
    public MutationsOnlyResponse getMutationsOnly(MutationRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());

        User user = userRepository.findByUserID(request.getUserID()).orElseThrow(() -> new UsernameNotFoundException(
                String.format(" %s doesn't exists", request.getUserID())
        ));
        Account account = accountRepository.findAccountByAccountNumberAndByUser_Username(user.getUsername()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "account number doesn't exists"));

        if (!this.transactionTokenService.validateTransactionToken(request.getPinToken(), account.getAccountNumber())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid pin credential");
        }

        List<Transaction> transactions = transactionRepository.findAllByAccount_AccountNumberAndBetween(account.getAccountNumber(), request.getFromDate(), request.getToDate(), pageable);

        return mutationResponseMapper.toMutationsDataDTO(transactions);
    }


    private BalanceDetailsResponse calculateStartingBalance(double availableBalance, List<Transaction> transactions, Date startBalanceDate){
        double startBalance = availableBalance;
        Date startDate = startBalanceDate;
        String startCurr = "IDR";
        for (Transaction transaction : transactions){
            if(transaction.getType() == ETransactionType.CREDIT){
                startBalance -= transaction.getAmount();
            }else if(transaction.getType() == ETransactionType.DEBIT){
                startBalance += transaction.getAmount();
            }
            startDate = transaction.getTransactionDate();
            startCurr = transaction.getCurrency();
        }
        return BalanceDetailsResponse.builder()
                .dateTime(formatter.format(startDate))
                .value(startBalance)
                .currency(startCurr)
                .build();
    }

    @Override
    public TransactionDetailResponse getDetailTransaction(TransactionDetailRequest request) {

        User user = userRepository.findByUserID(request.getUserID()).orElseThrow(() -> new UsernameNotFoundException(
                String.format(" %s doesn't exists", request.getUserID())
        ));

        UUID transactionID = UuidUtil.convertStringIntoUUID(request.getTransaction_id());

        Transaction transaction = transactionRepository.findById(transactionID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "transaction id doesn't exists"));

        Account account = accountRepository.findByUser(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "account number doesn't exists"));

//        if (!this.transactionTokenService.validateTransactionToken(request.getPinToken(), account.getAccountNumber())) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid pin credential");
//        }

        if (!account.getAccountNumber().equals(transaction.getAccount().getAccountNumber())){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "transaction id doesn't exists");
        }

        return mutationResponseMapper.toTransactionDetailDTO(user, transaction,account);
    }

    @Override
    public AccountMonthlyResponse getMonthlyMutation(int month, String username) {

        LocalDate now = LocalDate.now();
        YearMonth yearMonth = YearMonth.of(now.getYear(), month);

        LocalDate earlyMonthDate = yearMonth.atDay(1);
        LocalDateTime earlyMonth = earlyMonthDate.atStartOfDay();

        LocalDate endMonthDate = yearMonth.atEndOfMonth();
        LocalDateTime endMonth = endMonthDate.atTime(23, 59, 59, 999999999);

        User user = userRepository.findByUserID(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format(" %s doesn't exists", username)
        ));

        Account account = accountRepository.findByUser(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "account number doesn't exists"));


        List<Transaction> transactions = transactionRepository
                .findByAccountAndDate(account.getAccountNumber(),earlyMonth,endMonth);

        return mutationResponseMapper.toMonthlyMutation(transactions);

    }

    @Override
    public List<SimpleTransactionDetailResponse> getLastTwoCreditTransactions(LatestTransactionsRequest request) {
        User user = userRepository.findByUserID(request.getUserID()).orElseThrow(() -> new UsernameNotFoundException(
                String.format(" %s doesn't exists", request.getUserID())
        ));

        Account account = accountRepository.findAccountByAccountNumberAndByUser_Username(user.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "account number doesn't exists"));

        Pageable pageable = PageRequest.of(0, request.getLimit());

        List<Transaction> transactions = transactionRepository
                .findAllByAccount_AccountNumberOrderByTransactionDateDesc(account.getAccountNumber(), ETransactionType.CREDIT, pageable);

        return mutationResponseMapper.toSimpleTransactionDetailDTOList(transactions,user);
    }
}
