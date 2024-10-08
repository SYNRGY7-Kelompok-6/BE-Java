package com.kelp_6.banking_apps.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@SQLDelete(sql = "update scheduled_transactions set deleted_date = now() where id = ?")
@SQLRestriction("deleted_date is null")
@Table(name = "scheduled_transactions")
public class ScheduledTransaction extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EScheduleStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EScheduleFrequency frequency;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false, name = "beneficiary_account_number")
    private String beneficiaryAccountNumber;

    @Column(nullable = false, name = "beneficiary_account_name")
    private String beneficiaryAccountName;

    private String description;

    @Column(name = "numbers_transactions")
    private Integer numbersTransactions;

    @Column(name = "numbers_succeed_transactions")
    private Integer numbersSucceedTransactions;

    // for once schedule
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "scheduled_date")
    @Temporal(TemporalType.DATE)
    private Date scheduledDate;

    // for weekly schedule
    @Column(name = "scheduled_day")
    private DayOfWeek scheduledDay;

    // for monthly schedule
    @Column(name = "scheduled_date_number")
    private Integer scheduledDateNumber;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "source_account_number", referencedColumnName = "account_number")
    private Account account;
}
