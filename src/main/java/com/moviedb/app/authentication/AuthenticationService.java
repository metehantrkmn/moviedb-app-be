package com.moviedb.app.authentication;

import com.moviedb.app.authentication.dto.LoginDto;
import com.moviedb.app.authentication.dto.RegisterDto;
import com.moviedb.app.authentication.exception.NoSuchUserExists;
import com.moviedb.app.authentication.exception.UserAlreadyExists;
import com.moviedb.app.authentication.jwtToken.Token;
import com.moviedb.app.authentication.jwtToken.TokenRepository;
import com.moviedb.app.authentication.user.User;
import com.moviedb.app.authentication.user.UserRepository;
import com.moviedb.app.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    public String login(LoginDto requestBody){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestBody.getEmail(),
                        requestBody.getPassword()
                )
        );

        //if no exception occured then user authenticated succesfully and rest of the operations continues

        User user = userRepository.findUserByEmail(requestBody.getEmail())
                .orElseThrow(() ->
                    new NoSuchUserExists("No such user exists!!!")
                );

        //create a new jwt token using jwt service and return it back to user
        String token = jwtService.generateToken(user);

        //first revoke token in use
        revokeAllActiveTokens(user.getId());
        //the  save the new token
        saveToken(user,token);

        return token;
    }

    public void revokeAllActiveTokens(Integer userId){
        List<Token> tokens = tokenRepository.getActiveTokens(userId);
        for (Token t: tokens){
            t.setRevoked(true);
        }
        tokenRepository.saveAll(tokens);
    }


    public String register(RegisterDto requestBody){

        if(userRepository.existsUserByEmail(requestBody.getEmail()))
            throw new UserAlreadyExists("User already exists!!!");

        User user = User.builder()
                .email(requestBody.getEmail())
                .name(requestBody.getUserName())
                .password(passwordEncoder.encode(requestBody.getPassword()))
                .enabled(false)
                .build();

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        //save token
        saveToken(user,jwtToken);

        emailService.sendEmail(user.getEmail());

        return jwtToken;
    }

    public void saveToken(User user, String jwtToken){
        Token token = Token.builder()
                .user(user)
                .revoked(false)
                .token(jwtToken)
                .build();
        tokenRepository.save(token);
    }

}
