package com.kelp_6.banking_apps.repository;

import com.kelp_6.banking_apps.entity.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountTypeRepository extends JpaRepository<AccountType, UUID> {

    @Modifying
    @Query(value = "delete from account_types", nativeQuery = true)
    void hardDeleteAll();
}
