package com.kelp_6.banking_apps.repository;

import com.kelp_6.banking_apps.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Modifying
    @Query(value = "delete from users", nativeQuery = true)
    void hardDeleteAll();

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("select u from User u where u.userID = :userID")
    Optional<User> findByUserID(@Param("userID") String userID);
}
