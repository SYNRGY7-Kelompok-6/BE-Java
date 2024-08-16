package com.kelp_6.banking_apps.repository;

import com.kelp_6.banking_apps.entity.ScheduledTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ScheduledTransactionRepository extends JpaRepository<ScheduledTransaction, UUID> {

    @Modifying
    @Query(value = "delete from scheduled_transactions", nativeQuery = true)
    void hardDeleteAll();
}
