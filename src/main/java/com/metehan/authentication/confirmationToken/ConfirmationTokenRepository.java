package com.metehan.authentication.confirmationToken;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken,Long> {
    Optional<ConfirmationToken> findByConfirmationToken(String confirmationToken);
}
