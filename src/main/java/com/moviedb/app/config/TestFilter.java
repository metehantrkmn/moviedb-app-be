package com.moviedb.app.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TestFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            if(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()){
                System.out.println("this is test filter is user authenticated: " + SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
            }else if(SecurityContextHolder.getContext().getAuthentication().isAuthenticated() == false){
                System.out.println("is authenticated false");
            }else{
                System.out.println("is authenticated is null");
            }
        }catch (Exception ex){
            System.out.println(ex);
        }finally {
            filterChain.doFilter(request,response);
        }

    }
}
