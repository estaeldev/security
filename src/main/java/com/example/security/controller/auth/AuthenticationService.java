package com.example.security.controller.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.security.config.JwtService;
import com.example.security.enums.Role;
import com.example.security.model.User;
import com.example.security.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(final RegisterRequest request) {
        User user = User.builder()
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .email(request.getEmail())
            .password(this.passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();

        this.userRepository.save(user);
        String token = this.jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(token).build();
    }

    public AuthenticationResponse authenticate(final AuthenticationRequest request) {

        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = this.userRepository.findByEmail(request.getEmail()).orElseThrow();
        
        String token = this.jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(token).build();
    }
    
}
