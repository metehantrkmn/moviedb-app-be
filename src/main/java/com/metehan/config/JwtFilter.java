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
        //get jwt token from request header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String useremail;
        //check if jwt token exist or not
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            //if jwt doesnt exist do filters but dont go on rest of the method
            filterChain.doFilter(request,response);
            return;
        }

        //if request have a jwt token, block above doesnt work and return then we extract the token below
        //why 7 is about "Bearer " the index coming after from space in the header value
        jwt = authHeader.substring(7);

        //after we get jwt token we should extract useremail and check database if that user really exist
        //to extract useremail we create a service class jwtService
        useremail = jwtService.extractUsername(jwt);

        //if no interrupts occured then a cookie with name tokenString is avaliable
        //to start validating tokenString we need to extract username(email) using jwt service


        System.out.println(jwt);
        System.out.println("before if blocks in jwt filter !!!");
        if(useremail != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails user = userDetailsService.loadUserByUsername(useremail);
            //NoTokenFindException
            Token token = tokenRepository.findTokenByToken(jwt).orElseThrow();

            if(jwtService.isTokenValid(jwt,user) && !token.isRevoked() && user.isEnabled()){
                //if you dont pass authorities parameters then token will not be set authenticated
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                  user.getUsername(),
                  user.getPassword(),
                        null
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
