package com.mjanicki.spotify.config;

import com.mjanicki.spotify.service.UserService;
import com.mjanicki.spotify.service.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final var jwt = getJwtToken(request);
        if (jwt == null || jwt == "") {
            filterChain.doFilter(request, response);
            return ;
        }
        final String email;
        email = jwtService.extractUsername(jwt);

        if (StringUtils.hasLength(email) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(email);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtToken(HttpServletRequest request) {
        final var cookiesArray = request.getCookies();
        if (cookiesArray == null) {
            return null;
        }
        final var cookies = Arrays.asList(request.getCookies());
        final var jwtCookie = cookies.stream()
            .filter(e -> e.getName().equals("accessToken"))
            .findFirst().orElse(null);
        if (jwtCookie == null)
            return null;
        return jwtCookie.getValue();
    }

}
