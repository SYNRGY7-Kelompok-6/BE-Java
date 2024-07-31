package com.kelp_6.banking_apps.repository;

import com.kelp_6.banking_apps.entity.SavedAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SavedAccountsRespository extends JpaRepository<SavedAccounts, UUID> {
    @Query("select s from SavedAccounts s where s.user.id = :id and lower(s.account.user.name) like lower(concat('%', :name, '%') ) ")
    List<SavedAccounts> findAllByUserIdAndLikePattern(
            @Param("id") UUID id,
            @Param("name") String name
    );

    @Query("select s from SavedAccounts s where s.user.id = :id and lower(s.account.user.name) like lower(concat('%', :name, '%') ) and s.favorite = :favorite")
    List<SavedAccounts> findAllByUserIdAndLikePatternAndFavorite(
            @Param("id") UUID id,
            @Param("name") String name,
            @Param("favorite") Boolean favorite
    );

    @Query("select s from SavedAccounts s where s.user.id = :id and s.id = :savedAccountId")
    Optional<SavedAccounts> findByIdAndUser_Id(
            @Param("id") UUID id,
            @Param("savedAccountId") UUID savedAccountId
    );



    @Modifying
    @Query(value = "delete from saved_accounts", nativeQuery = true)
    void hardDeleteAll();
}
