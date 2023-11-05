package com.mjanicki.spotify.service.security;

import com.mjanicki.spotify.dao.security.JwtResponse;
import com.mjanicki.spotify.dao.security.LoginRequest;
import com.mjanicki.spotify.dao.security.RegisterRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    JwtResponse login(LoginRequest login);

    JwtResponse register(RegisterRequest register);

    JwtResponse getUser(HttpServletRequest request);
}
