package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.entity.*;
import com.kelp_6.banking_apps.model.schedule.*;
import com.kelp_6.banking_apps.repository.AccountRepository;
import com.kelp_6.banking_apps.repository.SavedAccountsRespository;
import com.kelp_6.banking_apps.repository.ScheduledTransactionRepository;
import com.kelp_6.banking_apps.repository.UserRepository;
import com.kelp_6.banking_apps.utils.DateUtil;
import com.kelp_6.banking_apps.utils.UuidUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduledTransactionServiceImpl implements ScheduledTransactionService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduledTransactionServiceImpl.class);
    private final ScheduledTransactionRepository scheduledTransactionRepository;
    private final TransactionTokenService transactionTokenService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final SavedAccountsRespository savedAccountsRespository;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");;

    @Override
    @Transactional
    public ScheduledTransactionResponse createSchedule(ScheduledTransactionRequest request) {
        LOGGER.info("accessed");

        // user checker
        User user = userRepository.findByUserID(request.getUserID()).orElseThrow(() -> new UsernameNotFoundException(
                String.format(" %s doesn't exists", request.getUserID())
        ));

        // pin token checker
        if(!transactionTokenService.validateTransactionToken(request.getPinToken(), user.getAccount().getAccountNumber())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid pin credential");
        }

        // oneself transfer checker
        if (request.getBeneficiaryAccountNumber().equals(user.getAccount().getAccountNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "can't transfer to oneself account");
        }

        // get beneficiary account data
        Account benAccount = accountRepository.findByAccountNumber(request.getBeneficiaryAccountNumber()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "beneficiary account number doesn't exists"));

        // unsaved account checker
        if(!savedAccountsRespository.existsByUser_IdAndAccount_AccountNumber(user.getId(), benAccount.getAccountNumber())){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "account number not saved yet");
        }

        // transfer limit exceed checker
        if(request.getAmount() > user.getAccount().getAccountType().getTransferLimit()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "transfer limit exceed");
        }

        // low balance checker
        if(user.getAccount().getAvailableBalance() - request.getAmount() < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "insufficient balance");
        }

        // initialize schedule transaction instance
        ScheduledTransaction scheduledTransaction;

        // fill schedule transaction instance according to frequency
        if(request.getFrequency().equalsIgnoreCase("Sekali")){
            scheduledTransaction = getOnceSchedule(request, user.getAccount());
        } else if (request.getFrequency().equalsIgnoreCase("Mingguan")) {
            scheduledTransaction = getWeeklySchedule(request, user.getAccount());
        } else if (request.getFrequency().equalsIgnoreCase("Bulanan")) {
            scheduledTransaction = getMonthlySchedule(request, user.getAccount());
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unknown frequency");
        }

        // set beneficiary account name
        scheduledTransaction.setBeneficiaryAccountName(benAccount.getUser().getName());

        // save schedule into db
        scheduledTransactionRepository.save(scheduledTransaction);

        return ScheduledTransactionResponse.builder()
                .scheduleId(scheduledTransaction.getId().toString())
                .scheduledDate(simpleDateFormat.format(scheduledTransaction.getScheduledDate()))
                .amount(scheduledTransaction.getAmount())
                .frequency(scheduledTransaction.getFrequency().getDescription())
                .status(scheduledTransaction.getStatus().getDescription())
                .beneficiaryAccountName(scheduledTransaction.getBeneficiaryAccountName())
                .build();
    }

    @Override
    public List<ScheduledTransactionResponse> getAllSchedule(String userID) {
        LOGGER.info("accessed");

        // user checker
        User user = userRepository.findByUserID(userID).orElseThrow(() -> new UsernameNotFoundException(
                String.format(" %s doesn't exists", userID)
        ));

        List<ScheduledTransaction> scheduledTransactions = scheduledTransactionRepository.findAllByAccount_User_UserID(user.getUserID());

        return scheduledTransactions.stream().map((scheduledTransaction -> ScheduledTransactionResponse.builder()
                .scheduleId(scheduledTransaction.getId().toString())
                .scheduledDate(simpleDateFormat.format(scheduledTransaction.getScheduledDate()))
                .amount(scheduledTransaction.getAmount())
                .frequency(scheduledTransaction.getFrequency().getDescription())
                .status(scheduledTransaction.getStatus().getDescription())
                .beneficiaryAccountName(scheduledTransaction.getBeneficiaryAccountName())
                .build())).toList();
    }

    @Override
    public DetailScheduledTransactionResponse getScheduleByScheduleID(String scheduleID, String userID) {
        LOGGER.info("accessed");

        // user checker
        User user = userRepository.findByUserID(userID).orElseThrow(() -> new UsernameNotFoundException(
                String.format(" %s doesn't exists", userID)
        ));

        // UUID conversion
        UUID scheduleUUID = UuidUtil.convertStringIntoUUID(scheduleID);

        // get detail schedule
        ScheduledTransaction scheduledTransaction = scheduledTransactionRepository.findById(scheduleUUID).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "schedule not found"));

        // schedule ownership checker
        if(!scheduledTransaction.getAccount().getUser().getUserID().equalsIgnoreCase(userID))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "schedule not found");

        SourceAccountResponse srcAccountResponse = SourceAccountResponse.builder()
                .accountNumber(user.getAccount().getAccountNumber())
                .availableBalance(user.getAccount().getAvailableBalance())
                .build();

        FrequencyDetail frequencyDetail = getFrequencyDetail(scheduledTransaction);

        return DetailScheduledTransactionResponse.builder()
                .scheduleId(scheduledTransaction.getId().toString())
                .status(scheduledTransaction.getStatus().getDescription())
                .frequency(scheduledTransaction.getFrequency().getDescription())
                .amount(scheduledTransaction.getAmount())
                .source(srcAccountResponse)
                .frequencyDetail(frequencyDetail)
                .build();
    }

    @Override
    @Transactional
    public ScheduledTransactionResponse updateSchedule(UpdateScheduledTransactionRequest request) {
        LOGGER.info("accessed");

        // user checker
        User user = userRepository.findByUserID(request.getUserID()).orElseThrow(() -> new UsernameNotFoundException(
                String.format(" %s doesn't exists", request.getUserID())
        ));

        // pin token checker
        if(!transactionTokenService.validateTransactionToken(request.getPinToken(), user.getAccount().getAccountNumber())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid pin credential");
        }

        // get schedule UUID
        UUID scheduleUUID = UuidUtil.convertStringIntoUUID(request.getScheduleID());

        // get schedule transaction
        ScheduledTransaction scheduledTransaction = scheduledTransactionRepository.findById(scheduleUUID).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "schedule not found"));

        // ownership checker
        if(!user.getAccount().getAccountNumber().equalsIgnoreCase(scheduledTransaction.getAccount().getAccountNumber())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "schedule not found");
        }

        // transfer limit exceed checker
        if(request.getAmount() > user.getAccount().getAccountType().getTransferLimit()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "transfer limit exceed");
        }

        // low balance checker
        if(user.getAccount().getAvailableBalance() - request.getAmount() < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "insufficient balance");
        }

        // fill schedule transaction instance according to frequency
        if(request.getFrequency().equalsIgnoreCase("Sekali")){
            getOnceScheduleUpdate(request, scheduledTransaction);
        } else if (request.getFrequency().equalsIgnoreCase("Mingguan")) {
            getWeeklyScheduleUpdate(request, scheduledTransaction);
        } else if (request.getFrequency().equalsIgnoreCase("Bulanan")) {
            getMonthlyScheduleUpdate(request, scheduledTransaction);
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unknown frequency");
        }

        scheduledTransactionRepository.save(scheduledTransaction);

        return ScheduledTransactionResponse.builder()
                .scheduleId(scheduledTransaction.getId().toString())
                .scheduledDate(simpleDateFormat.format(scheduledTransaction.getScheduledDate()))
                .status(scheduledTransaction.getStatus().getDescription())
                .amount(scheduledTransaction.getAmount())
                .frequency(scheduledTransaction.getFrequency().getDescription())
                .beneficiaryAccountName(scheduledTransaction.getBeneficiaryAccountName())
                .build();
    }

    @Override
    public ScheduledTransactionResponse cancelSchedule(String scheduleID, String userID, String pinToken) {
        LOGGER.info("accessed");

        // user checker
        User user = userRepository.findByUserID(userID).orElseThrow(() -> new UsernameNotFoundException(
                String.format(" %s doesn't exists", userID)
        ));

        // pin token checker
        if(!transactionTokenService.validateTransactionToken(pinToken, user.getAccount().getAccountNumber())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid pin credential");
        }

        // UUID conversion
        UUID scheduleUUID = UuidUtil.convertStringIntoUUID(scheduleID);

        // get detail schedule
        ScheduledTransaction scheduledTransaction = scheduledTransactionRepository.findById(scheduleUUID).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "schedule not found"));

        // schedule ownership checker
        if(!scheduledTransaction.getAccount().getUser().getUserID().equalsIgnoreCase(userID))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "schedule not found");

        // schedule cancellation checker
        if(scheduledTransaction.getStatus().equals(EScheduleStatus.CANCELED))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "schedule has been canceled before");

        // change status
        scheduledTransaction.setStatus(EScheduleStatus.CANCELED);

        // update into db
        scheduledTransactionRepository.save(scheduledTransaction);

        return ScheduledTransactionResponse.builder()
                .scheduleId(scheduledTransaction.getId().toString())
                .status(scheduledTransaction.getStatus().getDescription())
                .amount(scheduledTransaction.getAmount())
                .frequency(scheduledTransaction.getFrequency().getDescription())
                .beneficiaryAccountName(scheduledTransaction.getBeneficiaryAccountName())
                .scheduledDate(simpleDateFormat.format(scheduledTransaction.getScheduledDate()))
                .build();
    }

    private ScheduledTransaction getOnceSchedule(ScheduledTransactionRequest request, Account srcAccount){
        LOGGER.info("accessed");

        // parsing schedule
        Date schedule;
        try{
            schedule = simpleDateFormat.parse(request.getSchedule());
        }catch (ParseException exception){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "schedule should dd-MM-yyyy format");
        }

        return ScheduledTransaction.builder()
                .status(EScheduleStatus.PENDING)
                .frequency(EScheduleFrequency.ONCE)
                .amount(request.getAmount())
                .beneficiaryAccountNumber(request.getBeneficiaryAccountNumber())
                .numbersSucceedTransactions(0)
                .description(request.getDescription())
                .account(srcAccount)
                // once schedule data
                .scheduledDate(schedule)
                .build();

    }

    private ScheduledTransaction getWeeklySchedule(ScheduledTransactionRequest request, Account srcAccount){
        LOGGER.info("accessed");

        weeklyAndMontlyRequestChecker(request);

        DayOfWeek chosenDay = DateUtil.convertToDayOfWeek(request.getSchedule());

        return ScheduledTransaction.builder()
                .status(EScheduleStatus.PENDING)
                .frequency(EScheduleFrequency.WEEKLY)
                .amount(request.getAmount())
                .beneficiaryAccountNumber(request.getBeneficiaryAccountNumber())
                .numbersSucceedTransactions(0)
                .description(request.getDescription())
                .account(srcAccount)
                // weekly schedule data
                .scheduledDay(chosenDay)
                .numbersTransactions(request.getNumbersTransactions())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .scheduledDate(DateUtil.getNextDay(request.getStartDate(), chosenDay))
                .build();
    }

    private ScheduledTransaction getMonthlySchedule(ScheduledTransactionRequest request, Account srcAccount){
        LOGGER.info("accessed");

        weeklyAndMontlyRequestChecker(request);

        // schedule checker (must be a valid number enclosed in a string)
        int dateNumber;
        try {
            dateNumber = Integer.parseInt(request.getSchedule());
        }catch (NumberFormatException exception){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "schedule must be a valid number enclosed in a string");
        }
        // schedule checker (must be inside of range 1-31)
        if(dateNumber <= 0 || dateNumber > 31){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "schedule must be inside of range 1-31");
        }

        return ScheduledTransaction.builder()
                .status(EScheduleStatus.PENDING)
                .frequency(EScheduleFrequency.MONTHLY)
                .amount(request.getAmount())
                .beneficiaryAccountNumber(request.getBeneficiaryAccountNumber())
                .numbersSucceedTransactions(0)
                .description(request.getDescription())
                .account(srcAccount)
                // weekly schedule data
                .scheduledDateNumber(dateNumber)
                .numbersTransactions(request.getNumbersTransactions())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .scheduledDate(DateUtil.getSpecificDate(request.getStartDate(), dateNumber))
                .build();
    }

    private void getOnceScheduleUpdate(UpdateScheduledTransactionRequest request, ScheduledTransaction scheduledTransaction){
        LOGGER.info("accessed");

        // parsing schedule
        Date schedule;
        try{
            schedule = simpleDateFormat.parse(request.getSchedule());
        }catch (ParseException exception){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "schedule should dd-MM-yyyy format");
        }

        scheduledTransaction.setStatus(EScheduleStatus.PENDING);
        scheduledTransaction.setFrequency(EScheduleFrequency.ONCE);
        scheduledTransaction.setAmount(request.getAmount());
        scheduledTransaction.setDescription(request.getDescription());
        scheduledTransaction.setScheduledDate(schedule);
        // update non once schedule attribute to empty value
        scheduledTransaction.setNumbersTransactions(null);
        scheduledTransaction.setNumbersSucceedTransactions(null);
        scheduledTransaction.setStartDate(null);
        scheduledTransaction.setEndDate(null);
        scheduledTransaction.setScheduledDay(null);
        scheduledTransaction.setScheduledDateNumber(null);

    }

    private void getWeeklyScheduleUpdate(UpdateScheduledTransactionRequest request, ScheduledTransaction scheduledTransaction){
        LOGGER.info("accessed");

        weeklyAndMontlyRequestCheckerUpdate(request);

        DayOfWeek chosenDay = DateUtil.convertToDayOfWeek(request.getSchedule());

        scheduledTransaction.setStatus(EScheduleStatus.PENDING);
        scheduledTransaction.setFrequency(EScheduleFrequency.WEEKLY);
        scheduledTransaction.setAmount(request.getAmount());
        scheduledTransaction.setDescription(request.getDescription());
        scheduledTransaction.setScheduledDate(DateUtil.getNextDay(request.getStartDate(), chosenDay));
        scheduledTransaction.setNumbersTransactions(request.getNumbersTransactions());
        scheduledTransaction.setNumbersSucceedTransactions(0);
        scheduledTransaction.setStartDate(request.getStartDate());
        scheduledTransaction.setEndDate(request.getEndDate());
        scheduledTransaction.setScheduledDay(chosenDay);
        // update non weekly schedule attribute to empty value
        scheduledTransaction.setScheduledDateNumber(null);
    }

    private void getMonthlyScheduleUpdate(UpdateScheduledTransactionRequest request, ScheduledTransaction scheduledTransaction){
        LOGGER.info("accessed");

        weeklyAndMontlyRequestCheckerUpdate(request);

        // schedule checker (must be a valid number enclosed in a string)
        int dateNumber;
        try {
            dateNumber = Integer.parseInt(request.getSchedule());
        }catch (NumberFormatException exception){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "schedule must be a valid number enclosed in a string");
        }
        // schedule checker (must be inside of range 1-31)
        if(dateNumber <= 0 || dateNumber > 31){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "schedule must be inside of range 1-31");
        }

        scheduledTransaction.setStatus(EScheduleStatus.PENDING);
        scheduledTransaction.setFrequency(EScheduleFrequency.MONTHLY);
        scheduledTransaction.setAmount(request.getAmount());
        scheduledTransaction.setDescription(request.getDescription());
        scheduledTransaction.setScheduledDate(DateUtil.getSpecificDate(request.getStartDate(), dateNumber));
        scheduledTransaction.setNumbersTransactions(request.getNumbersTransactions());
        scheduledTransaction.setNumbersSucceedTransactions(0);
        scheduledTransaction.setStartDate(request.getStartDate());
        scheduledTransaction.setEndDate(request.getEndDate());
        scheduledTransaction.setScheduledDateNumber(dateNumber);
        // update non weekly schedule attribute to empty value
        scheduledTransaction.setScheduledDay(null);
    }

    private void weeklyAndMontlyRequestChecker(ScheduledTransactionRequest request){
        LOGGER.info("accessed");

        // numbers transactions checker
        if(request.getNumbersTransactions() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "numbers transactions can't be null");
        }
        if(request.getNumbersTransactions() <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "numbers transactions should be greater than 0");
        }

        // start date checker
        if(request.getStartDate() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start date can't be null");
        }
        if(request.getStartDate().before(DateUtil.removeTime(DateUtil.getCurrentDateInJakartaTimeZone()))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start date can't be in the past");
        }

        // end date checker
        if(request.getEndDate() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "end date can't be null");
        }
        if(request.getEndDate().before(DateUtil.removeTime(DateUtil.getCurrentDateInJakartaTimeZone()))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start date can't be in the past");
        }
        if(request.getEndDate().before(request.getStartDate())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "end date can't be before start date");
        }
    }

    private void weeklyAndMontlyRequestCheckerUpdate(UpdateScheduledTransactionRequest request){
        LOGGER.info("accessed");

        // numbers transactions checker
        if(request.getNumbersTransactions() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "numbers transactions can't be null");
        }
        if(request.getNumbersTransactions() <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "numbers transactions should be greater than 0");
        }

        // start date checker
        if(request.getStartDate() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start date can't be null");
        }
        if(request.getStartDate().before(DateUtil.removeTime(DateUtil.getCurrentDateInJakartaTimeZone()))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start date can't be in the past");
        }

        // end date checker
        if(request.getEndDate() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "end date can't be null");
        }
        if(request.getEndDate().before(DateUtil.removeTime(DateUtil.getCurrentDateInJakartaTimeZone()))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start date can't be in the past");
        }
        if(request.getEndDate().before(request.getStartDate())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "end date can't be before start date");
        }
    }

    private FrequencyDetail getFrequencyDetail(ScheduledTransaction scheduledTransaction){
        LOGGER.info("accessed");

        return switch (scheduledTransaction.getFrequency()) {
            case ONCE -> OnceFrequencyDetailResponse.builder()
                    .scheduledDate(simpleDateFormat.format(scheduledTransaction.getScheduledDate()))
                    .build();
            case WEEKLY -> WeeklyFrequencyDetailResponse.builder()
                    .scheduledDate(simpleDateFormat.format(scheduledTransaction.getScheduledDate()))
                    .dayOfWeek(DateUtil.convertToIndonesiaName(scheduledTransaction.getScheduledDay()))
                    .numbersTransactions(scheduledTransaction.getNumbersTransactions())
                    .startDate(simpleDateFormat.format(scheduledTransaction.getStartDate()))
                    .endDate(simpleDateFormat.format(scheduledTransaction.getEndDate()))
                    .build();
            case MONTHLY -> MonthlyFrequencyDetailResponse.builder()
                    .scheduledDate(simpleDateFormat.format(scheduledTransaction.getScheduledDate()))
                    .dayOfMonth(scheduledTransaction.getScheduledDateNumber())
                    .numbersTransactions(scheduledTransaction.getNumbersTransactions())
                    .startDate(simpleDateFormat.format(scheduledTransaction.getStartDate()))
                    .endDate(simpleDateFormat.format(scheduledTransaction.getEndDate()))
                    .build();
        };
    }
}
