package com.mjanicki.spotify.service.security.impl;

import com.mjanicki.spotify.dao.security.JwtResponse;
import com.mjanicki.spotify.dao.security.LoginRequest;
import com.mjanicki.spotify.dao.security.RegisterRequest;
import com.mjanicki.spotify.repository.UserRepository;
import com.mjanicki.spotify.service.security.AuthenticationService;
import com.mjanicki.spotify.service.security.JwtService;
import lombok.AllArgsConstructor;
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

    @Override
    public JwtResponse login(LoginRequest login) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword())
        );
        var user = userRepository.findByEmail(login.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("No user registered with email: " + login.getEmail()));
        var token = jwtService.generateToken(user);
        return JwtResponse.builder().token(token).build();
    }

    @Override
    public JwtResponse register(RegisterRequest register) {
        var user = User.builder().fullName(register.getFullName()).email(register.getEmail())
                .password(passwordEncoder.encode(register.getPassword())).build();
        userRepository.save(user);
        var token = jwtService.generateToken(user);
        return JwtResponse.builder().token(token).build();
    }
}
