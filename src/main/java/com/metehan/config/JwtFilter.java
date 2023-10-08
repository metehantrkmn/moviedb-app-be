package com.metehan.config;

import com.metehan.authentication.jwtToken.Token;
import com.metehan.authentication.jwtToken.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        //check if cookies null and if token cookie empty
        //extract username(email) from token and validate using jwt service
        //authenticate the user if the token valid
        //filterchainn.filter => let filter chain move on tokenString

        //stops executing this filter and continue with other => not authenticated
        if(request.getCookies() == null){
            filterChain.doFilter(request,response);
            return;
        }

        Cookie tokenCookie = Arrays.stream(request.getCookies()).filter(
          cookie -> cookie.getName().equals("token")
        ).findFirst().get();

        //if cookies exists but tokenString cookie not avaliable it stops executing continues with other filters
        if(tokenCookie == null){
            filterChain.doFilter(request,response);
            return;
        }

        String tokenString = tokenCookie.getValue();

        //if no interrupts occured then a cookie with name tokenString is avaliable
        //to start validating tokenString we need to extract username(email) using jwt service

        String userName = jwtService.extractUsername(tokenString);
        System.out.println(tokenString);
        System.out.println("before if blocks in jwt filter !!!");
        if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails user = userDetailsService.loadUserByUsername(userName);
            //NoTokenFindException
            Token token = tokenRepository.findTokenByToken(tokenString).orElseThrow();

            if(jwtService.isTokenValid(tokenString,user) && !token.isRevoked() && user.isEnabled()){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                  user.getUsername(),
                  user.getPassword()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("security context is set succesfully!!!!");
                System.out.println("is user enabled : " + user.isEnabled());
            }

        }
        filterChain.doFilter(request,response);
    }
}
