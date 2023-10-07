package com.mjanicki.spotify.service.security;

import com.mjanicki.spotify.dao.security.JwtResponse;
import com.mjanicki.spotify.dao.security.LoginRequest;
import com.mjanicki.spotify.dao.security.RegisterRequest;

public interface AuthenticationService {
    JwtResponse login(LoginRequest login);

    JwtResponse register(RegisterRequest register);
}
