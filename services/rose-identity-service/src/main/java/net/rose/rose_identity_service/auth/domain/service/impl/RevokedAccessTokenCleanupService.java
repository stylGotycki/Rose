package net.rose.rose_identity_service.auth.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_identity_service.auth.persistence.repository.api.RevokedAccessTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
class RevokedAccessTokenCleanupService {

    private final RevokedAccessTokenRepository repository;

    @Scheduled(
            cron = "${app.security.revoked-token-cleanup-cron:0 0 * * * *}"
    )
    @Transactional
    public void deleteExpiredTokens() {
        repository.deleteAllByExpiresAtBefore(
                Instant.now()
        );
    }
}