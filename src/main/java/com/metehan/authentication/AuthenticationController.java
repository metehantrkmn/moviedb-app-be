package com.metehan.authentication;

import com.metehan.authentication.dto.LoginDto;
import com.metehan.authentication.dto.RegisterDto;
import com.metehan.authentication.jwtToken.Token;
import com.metehan.authentication.jwtToken.TokenRepository;
import com.metehan.authentication.user.User;
import com.metehan.authentication.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    //for test
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;


    @PostMapping("api/v1/login")
    public ResponseEntity login(@RequestBody LoginDto body){
        String token = authenticationService.login(body);
        return ResponseEntity
                .ok()
                .header("Set-Cookie","token="+token+"; HttpOnly")
                .body(null);
    }

    @PostMapping("api/v1/register")
    public ResponseEntity register(@RequestBody RegisterDto body){
        return ResponseEntity
                .ok()
                .header("Set-Cookie","token="+authenticationService.register(body)+"; HttpOnly")
                .body(null);
    }

    @GetMapping("api/v1/demo")
    public ResponseEntity getDemo(){
        System.out.println(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow();
        System.out.println("this is email of persisted user: " + user.getId());
        if(tokenRepository.getActiveTokens(user.getId()) != null){
            System.out.println("we got the results");
            for (Token t: tokenRepository.getActiveTokens(user.getId())){
                System.out.println(t.getToken() + " " + t.getId());
            }
        }else{
            System.out.println("query failed its null");
        }
        List<Token> tokens = tokenRepository.getActiveTokens(user.getId());
        return ResponseEntity.ok(tokens.size());
    }

    @GetMapping("/api/v1/verify")
    public ResponseEntity verifyEmail(@RequestParam String token){
        emailService.verifyEmail(token);
        return ResponseEntity.ok("Email verified");
    }

}
