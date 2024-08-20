package com.kelp_6.banking_apps.repository;

import com.kelp_6.banking_apps.entity.EScheduleStatus;
import com.kelp_6.banking_apps.entity.ScheduledTransaction;
import jakarta.persistence.TemporalType;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduledTransactionRepository extends JpaRepository<ScheduledTransaction, UUID> {
    @Query(value = "select s from ScheduledTransaction s where s.account.user.userID = :userID")
    List<ScheduledTransaction> findAllByAccount_User_UserID(@Param(value = "userID") String userID);

    @Query(value = "SELECT s FROM ScheduledTransaction s WHERE s.status = :status and s.scheduledDate = :currentDate")
    List<ScheduledTransaction> findAllScheduleForToday(
            @Param("status") EScheduleStatus status,
            @Param("currentDate") @Temporal(TemporalType.DATE) Date currentDate
    );

    @Modifying
    @Query(value = "delete from scheduled_transactions", nativeQuery = true)
    void hardDeleteAll();
}
