package com.ecommerce.project.security.jwt;

import com.ecommerce.project.security.jwt.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    private Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try{
            String jwt =parseJwt(request);
            if(jwt!=null && jwtUtils.validateJwtToken(jwt)){
                String userName =jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(userName);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                logger.debug("Roles from JWT : {} ",userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        catch(Exception e){
            logger.error("Cannot set user authentication: {} ",e);
        }
        filterChain.doFilter(request,response);
    }

    private String parseJwt(HttpServletRequest request){
        String jwt = jwtUtils.getJwtFromCookies(request);
        logger.debug("AuthTokenFilter.java : {} ",jwt);
        return jwt;
    }
}
