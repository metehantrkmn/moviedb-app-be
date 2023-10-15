package com.moviedb.app.authentication;

import com.moviedb.app.authentication.dto.LoginDto;
import com.moviedb.app.authentication.dto.RegisterDto;
import com.moviedb.app.authentication.jwtToken.Token;
import com.moviedb.app.authentication.jwtToken.TokenRepository;
import com.moviedb.app.authentication.user.User;
import com.moviedb.app.authentication.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
                .header("Authorization","Bearer "+token)
                .body(null);
    }

    @PostMapping("api/v1/register")
    public ResponseEntity register(@RequestBody RegisterDto body){
        return ResponseEntity
                .ok()
                .header("Authorization","Bearer "+authenticationService.register(body))
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
        return ResponseEntity.ok("this is demo endpoint");
    }

    @GetMapping("/api/v1/verify")
    public ModelAndView verifyEmail(@RequestParam String token){
        emailService.verifyEmail(token);
        //return ResponseEntity.ok("Email verified");
        String projectUrl = "https://www.google.com.tr/?hl=tr";
        return new ModelAndView("redirect:" + projectUrl);
    }

}
