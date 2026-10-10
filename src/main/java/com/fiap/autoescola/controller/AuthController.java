package com.fiap.autoescola.controller;

import com.fiap.autoescola.dto.auth.LoginRequest;
import com.fiap.autoescola.dto.auth.LoginResponse;
import com.fiap.autoescola.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @io.swagger.v3.oas.annotations.security.SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.senha()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.gerarToken(userDetails);

        return ResponseEntity.ok(new LoginResponse(token));
    }
}
