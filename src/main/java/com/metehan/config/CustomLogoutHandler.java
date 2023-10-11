package com.metehan.config;

import com.metehan.authentication.jwtToken.Token;
import com.metehan.authentication.jwtToken.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        //get the token cookie from request object
        //make request by token string using tokenrepository
        //revoke token object got from database
        //save(update) the token to database

        String tokenString = request.getHeader("Authorization").substring(7);


        Token token = tokenRepository.findTokenByToken(tokenString).orElseThrow();

        token.setRevoked(true);

        tokenRepository.save(token);
        System.out.println("succesfully logout");
    }
}
