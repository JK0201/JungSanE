package com.streaming.settlement.user.repository;

import com.streaming.settlement.user.entity.AuthProvider;
import com.streaming.settlement.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {

    @Query("select rt from RefreshToken rt " +
            "join fetch rt.user u " +
            "where u.authProvider = :authProvider " +
            "and u.username = :username " +
            "and rt.refreshToken=:refreshToken")
    Optional<RefreshToken> findByRefreshTokenFetchUser(
            @Param("authProvider") AuthProvider authProvider,
            @Param("username") String username,
            @Param("refreshToken") String refreshToken);

    void deleteByRefreshToken(String refreshToken);
}
