package com.mjanicki.spotify.helper;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.mjanicki.spotify.dao.User;
import com.mjanicki.spotify.exception.UserNotFoundException;
import com.mjanicki.spotify.repository.UserRepository;
import com.mjanicki.spotify.service.UserService;
import com.mjanicki.spotify.service.security.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserHelper {

    @NonNull
    private final JwtService jwtService;
    @NonNull
    private final UserService userService;

    @NonNull
    private UserRepository userRepository;

    public UserDetails getUserDetails(HttpServletRequest request) {
        final String header = request.getHeader("Authorization");
        final String jwt = header.substring(7), email = jwtService.extractUsername(jwt);

       return userService.userDetailsService().loadUserByUsername(email);
    }

    public User getUser(HttpServletRequest request) {
        final String header = request.getHeader("Authorization");
        final String jwt = header.substring(7), email = jwtService.extractUsername(jwt);

        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(email));
    }
    
}
