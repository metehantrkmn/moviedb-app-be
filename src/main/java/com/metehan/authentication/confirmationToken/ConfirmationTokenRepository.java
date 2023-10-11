package com.metehan.authentication.confirmationToken;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken,Long> {
    Optional<ConfirmationToken> findByConfirmationToken(String confirmationToken);

    @Query(value = """
            select t from ConfirmationToken t where t.expirationDate > :date
            """)
    List<ConfirmationToken> findExpiredConfirmationTokens(Date date);

    @Modifying
    @Query(value = """
            delete from ConfirmationToken t where t.expirationDate < :date
            """)
    void deleteExpiredTokens(Date date);

}
