package com.kelp_6.banking_apps.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SQLDelete(sql = "update accounts set deleted_date = now() where id = ?")
@SQLRestriction(value = "deleted_date is null")
@Table(name = "accounts")
public class Account extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, name = "account_number")
    private String accountNumber; // filled with 10 digit unique number

    @Column(nullable = false, name = "account_card_exp")
    private Date accountExp;

    @Column(nullable = false, name = "available_balance")
    private Double availableBalance;

    @Column(nullable = false, name = "available_balance_currency")
    private String availableBalanceCurr;

    @Column(nullable = false, name = "hold_amount")
    private Double holdAmount;

    @Column(nullable = false, name = "hold_amount_currency")
    private String holdAmountCurr;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "account_type_id", referencedColumnName = "id")
    private AccountType accountType;

    @OneToMany(mappedBy = "account", cascade = {CascadeType.ALL})
    private List<Transaction> transactions;
}
