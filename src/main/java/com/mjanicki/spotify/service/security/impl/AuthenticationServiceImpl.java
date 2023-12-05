package com.mjanicki.spotify.service.security.impl;

import com.mjanicki.spotify.dao.security.JwtResponse;
import com.mjanicki.spotify.dao.security.LoginRequest;
import com.mjanicki.spotify.dao.security.RegisterRequest;
import com.mjanicki.spotify.dto.UserDTO;
import com.mjanicki.spotify.helper.UserHelper;
import com.mjanicki.spotify.repository.UserRepository;
import com.mjanicki.spotify.service.security.AuthenticationService;
import com.mjanicki.spotify.service.security.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.mjanicki.spotify.dao.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Log4j2
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final UserHelper userHelper;

    @Override
    public JwtResponse login(LoginRequest login, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword())
        );
        var user = userRepository.findByEmail(login.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("No user registered with email: " + login.getEmail()));
        var token = jwtService.generateToken(user);
        var dto = UserDTO.builder().id(user.getId()).fullName(user.getFullName())
                    .avatarUrl(user.getAvatarUrl()).email(user.getEmail()).songs(null).build();

        response.addCookie(createJwtCookie(token));

        return JwtResponse.builder().token(token).user(dto).build();
    }

    @Override
    public JwtResponse register(RegisterRequest register, HttpServletResponse response) {
        var user = User.builder().fullName(register.getFullName()).email(register.getEmail())
                .password(passwordEncoder.encode(register.getPassword())).build();
        userRepository.save(user);
        var token = jwtService.generateToken(user);
        var dto = UserDTO.builder().id(user.getId()).fullName(user.getFullName())
            .avatarUrl(user.getAvatarUrl()).email(user.getEmail()).songs(null).build();
        response.addCookie(createJwtCookie(token));
        return JwtResponse.builder().token(token).user(dto).build();
    }

    @Override
    public void logout(HttpServletResponse response) {
        var cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(1);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setAttribute("SameSite", "None");
        
        response.addCookie(cookie);
    }

    @Override
    public JwtResponse getUser(HttpServletRequest request) {
        final var jwtResponse = userHelper.getJwtResponse(request);
        if (jwtResponse == null) {
            log.warn("Request to get user was received, but no token was provided with the request. Returning empty response.");
            return new JwtResponse();
        }
        return jwtResponse;
    }

    private Cookie createJwtCookie(String token) {
        var cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); //works for localhost as well
        cookie.setMaxAge(60 * 60 * 24);
        cookie.setPath("/");
        //TODO change domain to value that will be read from config file
        cookie.setDomain("localhost");
        cookie.setAttribute("SameSite", "None");

        return cookie;
    }
}
