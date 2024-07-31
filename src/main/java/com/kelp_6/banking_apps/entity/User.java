package com.kelp_6.banking_apps.entity;

import jakarta.persistence.*;
import lombok.*;
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
@SQLDelete(sql = "update users set deleted_date = now() where id = ?")
@SQLRestriction("deleted_date is null")
@Table(name = "users")
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, name = "user_id")
    private String userID;

    private String name;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String pin;

    @Column(nullable = false, name = "pin_expired_date")
    private Date pinExpiredDate;

    @Column(nullable = false, name = "is_verified")
    private boolean isVerified;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Account account;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private List<LoginInfos> loginInfos;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SavedAccounts> savedAccounts;
}
