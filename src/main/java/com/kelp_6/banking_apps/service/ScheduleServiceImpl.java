package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.entity.EScheduleFrequency;
import com.kelp_6.banking_apps.entity.EScheduleStatus;
import com.kelp_6.banking_apps.entity.ScheduledTransaction;
import com.kelp_6.banking_apps.model.transfer.intrabank.Amount;
import com.kelp_6.banking_apps.model.transfer.intrabank.TransferRequest;
import com.kelp_6.banking_apps.repository.ScheduledTransactionRepository;
import com.kelp_6.banking_apps.utils.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService{
    private final ScheduledTransactionRepository scheduledTransactionRepository;
    private final TransactionIntrabankService transactionIntrabankService;

    @Override
    public void scheduleTransferAutomation(Date currentDate) {
        log.info("service - ScheduleServiceImpl - scheduleTransferAutomation - accessed");

        // get list today schedule with status is pending
        List<ScheduledTransaction> allScheduleForToday = scheduledTransactionRepository.findAllScheduleForToday(EScheduleStatus.PENDING, currentDate);

        // perform transfer for each schedule
        for (ScheduledTransaction scheduledTransaction : allScheduleForToday){
            // get available balance and transfer amount
            Double availableBalance = scheduledTransaction.getAccount().getAvailableBalance();
            Double amount = scheduledTransaction.getAmount();

            // checker possibility transfer (amount !>= available balance)
            if(availableBalance - amount < 0) {
                scheduledTransaction.setStatus(EScheduleStatus.CANCELED);
            }else {
                performTransferAutomation(scheduledTransaction);
            }

            // update status
            if(scheduledTransaction.getFrequency().equals(EScheduleFrequency.ONCE)){
                scheduledTransaction.setStatus(EScheduleStatus.SUCCESS);
            }else {
                updateNumbersTransactions(scheduledTransaction);
                if(scheduledTransaction.getStatus().equals(EScheduleStatus.PENDING)){
                    updateIntoNextScheduleDate(currentDate, scheduledTransaction);
                }
            }

            // save updated schedule
            scheduledTransactionRepository.save(scheduledTransaction);
        }
    }

    private void performTransferAutomation(ScheduledTransaction scheduledTransaction){
        log.info("service - ScheduleServiceImpl - performTransferAutomation - accessed");
        Amount amount = Amount.builder()
                .value(scheduledTransaction.getAmount())
                .currency("IDR")
                .build();

        TransferRequest request = TransferRequest.builder()
                .userID(scheduledTransaction.getAccount().getUser().getUserID())
                .amount(amount)
                .desc(scheduledTransaction.getDescription())
                .beneficiaryAccountNumber(scheduledTransaction.getBeneficiaryAccountNumber())
                .remark("Scheduled Transfer")
                .build();

        transactionIntrabankService.transfer(request, true);
    }

    private void updateNumbersTransactions(ScheduledTransaction scheduledTransaction){
        log.info("service - ScheduleServiceImpl - updateNumbersTransactions - accessed");
        Integer numbersSucceedTransactions = scheduledTransaction.getNumbersSucceedTransactions();
        scheduledTransaction.setNumbersSucceedTransactions(numbersSucceedTransactions + 1);

        if(Objects.equals(scheduledTransaction.getNumbersSucceedTransactions(), scheduledTransaction.getNumbersTransactions())){
            scheduledTransaction.setStatus(EScheduleStatus.SUCCESS);
        }
    }

    private void updateIntoNextScheduleDate(Date currentDate, ScheduledTransaction scheduledTransaction){
        log.info("service - ScheduleServiceImpl - updateIntoNextScheduleDate - accessed");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        Date tomorrow = calendar.getTime();

        if(scheduledTransaction.getFrequency().equals(EScheduleFrequency.WEEKLY)){
            Date nextScheduleDay = DateUtil.getNextDay(tomorrow, scheduledTransaction.getScheduledDay());
            scheduledTransaction.setScheduledDate(nextScheduleDay);
        } else if (scheduledTransaction.getFrequency().equals(EScheduleFrequency.MONTHLY)) {
            Date nextScheduleDay = DateUtil.getSpecificDate(tomorrow, scheduledTransaction.getScheduledDateNumber());
            scheduledTransaction.setScheduledDate(nextScheduleDay);
        }

        // checker while next schedule day is getting far from endDate (after)
        if(scheduledTransaction.getScheduledDate().after(scheduledTransaction.getEndDate())){
            scheduledTransaction.setStatus(EScheduleStatus.SUCCESS);
        }
    }
}
