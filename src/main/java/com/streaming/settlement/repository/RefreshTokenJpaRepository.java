package com.streaming.settlement.repository;

import com.streaming.settlement.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

    @Query("select rt from RefreshTokenEntity rt " +
            "join fetch rt.userEntity u " +
            "where u.username = :username and rt.refreshToken=:refreshToken")
    Optional<RefreshTokenEntity> findByRefreshTokenAndUsername(
            @Param("username") String username,
            @Param("refreshToken") String refreshToken);
}
