package com.kelp_6.banking_apps.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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

    @Column(nullable = false)
    private String oppositeAccNumber;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ETransactionType type;

    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_acc_num", referencedColumnName = "accountNumber")
    private User user;
    // with assumption when perform transaction should create 2 new records
    // with 2 diff transaction type and owner (sender-credit, recipient-debit)
}
