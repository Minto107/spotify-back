package com.mjanicki.spotify.helper;

import java.util.Arrays;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.mjanicki.spotify.dao.User;
import com.mjanicki.spotify.dao.security.JwtResponse;
import com.mjanicki.spotify.dto.UserDTO;
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
        try {
            final String jwt = getJwt(request);
            final String email = getEmail(jwt);
            return userService.userDetailsService().loadUserByUsername(email);
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    public User getUser(HttpServletRequest request) {
        try {
            final String jwt = getJwt(request);
            final String email = getEmail(jwt);
            return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public JwtResponse getJwtResponse(HttpServletRequest request) {
        try {
            final String jwt = getJwt(request);
            final String email = getEmail(jwt);
            final var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

            final var userDto = UserDTO.builder().id(user.getId()).fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl()).email(user.getEmail()).songs(null).build();

            return new JwtResponse(email, userDto);
        } catch (NullPointerException ignored) {
            return null;
        }
    }
    
    private String getJwt(HttpServletRequest request) throws NullPointerException{
        return Arrays.asList(request.getCookies()).stream()
            .filter(e -> e.getName().equals("accessToken"))
            .findFirst().orElse(null).getValue();
    }

    private String getEmail(String jwt) {
        return jwtService.extractUsername(jwt);
    }
}
