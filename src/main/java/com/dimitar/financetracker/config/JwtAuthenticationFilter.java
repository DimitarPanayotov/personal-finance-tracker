package com.dimitar.financetracker.config;

import com.dimitar.financetracker.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

// This filter reads the Authorization header,
// extracts a JWT if present, validates it,
// loads the user via UserDetailsService,
// and — if valid — sets an authenticated
// Authentication into Spring Security’s SecurityContext
// so the request is treated as authenticated
// for the rest of processing.
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { //ensures doFilterInternal runs at
                                                                    // most once per request
    private static final int BEGIN_INDEX = 7; //For Bearer
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService; // Spring automatically detects
                                                         // CustomUserDetailsService implementation and injects it

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain //object used to continue the chain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); //pass request to next filter
            return;
        }

        jwt = authHeader.substring(BEGIN_INDEX);
        username = jwtUtil.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { //prevents re-authenticating
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, //represent the user
                        null, //(credentials): null — we don't have the password during a JWT-based request
                        userDetails.getAuthorities()
                );
                authToken.setDetails( //Attach request-specific details to the Authentication, such as remote IP and session id
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken); //places the authenticated token into Spring Security’s context for this thread/request
            }
        }
        filterChain.doFilter(request, response); //continue the filter chain after authentication attempt
    }
}
