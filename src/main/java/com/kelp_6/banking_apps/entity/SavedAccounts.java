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
@SQLDelete(sql = "update saved_accounts set deleted_date = now() where id = ?")
@SQLRestriction(value = "deleted_date is null")
@Table(name = "saved_accounts")
public class SavedAccounts extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Boolean favorite;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "beneficiary_account_number", referencedColumnName = "account_number")
    private Account account;
}
