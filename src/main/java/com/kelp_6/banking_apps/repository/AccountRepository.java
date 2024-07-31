package com.kelp_6.banking_apps.repository;

import com.kelp_6.banking_apps.entity.Account;
import com.kelp_6.banking_apps.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    @Query("select acc from Account acc where acc.user.username = :username")
    Optional<Account> findAccountByAccountNumberAndByUser_Username(
            @Param("username") String username
    );

    Optional<Account> findByAccountNumber(String accountNumber);

    @Modifying
    @Query(value = "delete from accounts", nativeQuery = true)
    void hardDeleteAll();

    @Query(value = "SELECT * FROM accounts WHERE user_id=?1",nativeQuery = true)
    Optional<Account> findByUser(UUID user);
}
