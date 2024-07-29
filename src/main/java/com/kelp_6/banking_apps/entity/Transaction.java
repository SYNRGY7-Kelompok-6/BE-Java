package com.kelp_6.banking_apps.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SQLDelete(sql = "update transactions set deleted_date = now() where id = ?")
@SQLRestriction("deleted_date is null")
@Table(name = "transactions")
public class Transaction extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "beneficiary_account_number")
    private String beneficiaryAccountNumber;

    @Column(nullable = false, name = "beneficiary_email")
    private String beneficiaryEmail;

    @Column(nullable = false, name = "beneficiary_name")
    private String beneficiaryName;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false, name = "remaining_balance")
    private Double remainingBalance;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ETransactionType type;

    private String remark;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false, updatable = false, name = "transaction_date")
    @CreationTimestamp
    private Date transactionDate;

    @ManyToOne
    @JoinColumn(name = "source_account_number", referencedColumnName = "account_number")
    private Account account;
    // with assumption when perform transaction should create 2 new records
    // with 2 diff transaction type and owner (sender-debit, recipient-credit)
}
