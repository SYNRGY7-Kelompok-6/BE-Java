package com.kelp_6.banking_apps.repository;

import com.kelp_6.banking_apps.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT t FROM Transaction t WHERE t.account.accountNumber = :accountNumber AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findAllByAccount_AccountNumberAndBetween(
            @Param("accountNumber") String accountNumber,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable
            );

    @Modifying
    @Query(value = "delete from transactions", nativeQuery = true)
    void hardDeleteAll();

    @Query(value = "SELECT * FROM transactions WHERE source_account_number = ?1 AND transaction_date BETWEEN ?2 AND ?3",nativeQuery = true)
    List<Transaction> findByAccountAndDate(String accountNumber, LocalDateTime start, LocalDateTime end);
}
