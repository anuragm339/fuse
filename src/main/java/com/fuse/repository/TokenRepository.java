package com.fuse.repository;

import com.fuse.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TokenRepository extends JpaRepository<Token,Long> {
    Token findOneByReferenceKey(String referenceKey);
    Token findOneByUserId(long userId);
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM token WHERE user_id=?1", nativeQuery = true)
    void deleteByUserId(long userId);
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM token WHERE expiry_date>=now()", nativeQuery = true)
    void deleteExpiredTokens();
}
