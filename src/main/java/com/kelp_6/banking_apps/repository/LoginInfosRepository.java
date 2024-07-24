package com.kelp_6.banking_apps.repository;

import com.kelp_6.banking_apps.entity.LoginInfos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoginInfosRepository extends JpaRepository<LoginInfos, UUID> {

    @Modifying
    @Query(value = "delete from login_infos", nativeQuery = true)
    void hardDeleteAll();
}
