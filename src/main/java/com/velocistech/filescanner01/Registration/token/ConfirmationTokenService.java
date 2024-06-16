package com.velocistech.filescanner01.Registration.token;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final com.velocistech.filescanner01.Registration.token.ConfirmationTokenRepository confirmationTokenRepository;
    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }
    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }
    public  Optional<ConfirmationToken> getAppUser(Long id){
        return confirmationTokenRepository.findByAppUser(id);
    }
    public int setConfirmedAt(String token) {
        return confirmationTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }

    @Scheduled(cron = "${purge.cron.expression}")
    public void purgeExpired() {
        LocalDateTime now = LocalDateTime.from(Instant.now());
        confirmationTokenRepository.deleteAllExpiredSince(now);
    }

    public void deleteToken(Long userId){
        confirmationTokenRepository.deleteUserToken(userId);
    }

}
