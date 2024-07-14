package com.kelp_6.banking_apps.repository;

import com.kelp_6.banking_apps.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByAccountNumber(String accountNumber);

    @Modifying
    @Query(value = "delete from users", nativeQuery = true)
    void hardDeleteAll();
}
