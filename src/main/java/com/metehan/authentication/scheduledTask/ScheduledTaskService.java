package com.metehan.authentication.scheduledTask;

import com.metehan.authentication.confirmationToken.ConfirmationToken;
import com.metehan.authentication.confirmationToken.ConfirmationTokenRepository;
import com.metehan.authentication.jwtToken.Token;
import com.metehan.authentication.jwtToken.TokenRepository;
import com.metehan.authentication.user.User;
import com.metehan.authentication.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledTaskService {

    private final TokenRepository tokenRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;

    //scheduled tasks run on a different thread in the application
    //unless you configured in the application.properties file it is 1 single thread default
    //it is recommended to allocate more thread than one for applications having lots of scheduled tasks

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void clearRevokedTokens(){
        List<Token> tokens = tokenRepository.getRevokedTokens();
        for (Token t:tokens
             ) {
            System.out.println(t.getId() + " " + t.getToken());
        }
        //tokenRepository.deleteRevokedTokens();
        long count = tokenRepository.removeTokenByRevoked(true);
        System.out.println("revoked tokens deleted " + count);
    }

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void clearExpiredConfirmationTokens(){
        List<ConfirmationToken> confirmationTokens = confirmationTokenRepository.findExpiredConfirmationTokens(new Date());
        for (ConfirmationToken t: confirmationTokens
             ) {
            System.out.println("confirmation tokens to be delete " + t.getTokenId() + " " + t.getConfirmationToken());
        }
        confirmationTokenRepository.deleteExpiredTokens(new Date());
        System.out.println("expired confirmation tokens deleted");
    }

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void clearAllUnVerifiedUsers(){
        List<User> users = userRepository.getAllUnVerifiedUsers();
        for (User u :
                users) {
            System.out.println("unverified users " + u.getEmail());
        }
        userRepository.deleteUnVerifiedUsers();
        System.out.println("unverified users deleted ");
    }

}
