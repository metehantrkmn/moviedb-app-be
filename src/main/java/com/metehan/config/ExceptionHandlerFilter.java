package com.metehan.config;

import com.metehan.authentication.jwtToken.Token;
import com.metehan.authentication.jwtToken.TokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final TokenRepository tokenRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            //extract calims method of jwt service checks if jwt token expired or not
            //if that method throws expired jwt exception then catch block handle it
            filterChain.doFilter(request,response);
        }catch (ExpiredJwtException ex){
            System.out.println("expired jwt exception occured during filter execution");
            //set token revoked = true if expired
            Token token = tokenRepository.findTokenByToken(
                    request.getHeader("Authorizaiton").substring(7)
            ).get();
            response.setHeader("Authorization","Bearer ");
            response.setStatus(403);
        }
    }
}
