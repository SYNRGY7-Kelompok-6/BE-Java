package com.kelp_6.banking_apps.repository;

import com.kelp_6.banking_apps.entity.LoginInfos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoginInfosRepository extends JpaRepository<LoginInfos, UUID> {
    @Query("select l from LoginInfos l where l.user.userID = :userID and l.isSuccess = true order by l.timestamp desc limit 1")
    Optional<LoginInfos> findLoginSuccessByUser_UserID(@Param("userID") String userID);

    @Query("select l from LoginInfos l where l.user.userID = :userID and l.isSuccess = false order by l.timestamp desc limit 1")
    Optional<LoginInfos> findLoginFailedByUser_UserID(@Param("userID") String userID);

    @Modifying
    @Query(value = "delete from login_infos", nativeQuery = true)
    void hardDeleteAll();
}
