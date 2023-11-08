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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.mjanicki.spotify.dao.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        var cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); //works for localhost as well
        cookie.setMaxAge(60 * 60 * 24);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setAttribute("SameSite", "None");

        response.addCookie(cookie);

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
        return JwtResponse.builder().token(token).user(dto).build();
    }

    @Override
    public JwtResponse getUser(HttpServletRequest request) {
        return userHelper.getJwtResponse(request);
    }
}
