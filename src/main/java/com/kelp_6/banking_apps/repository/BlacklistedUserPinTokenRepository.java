package com.kelp_6.banking_apps.repository;

import com.kelp_6.banking_apps.entity.BlacklistedUserPinToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BlacklistedUserPinTokenRepository extends JpaRepository<BlacklistedUserPinToken, Long> {
    boolean existsByUser_IdAndPinToken(UUID userId, String pinToken);
}