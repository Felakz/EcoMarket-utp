package com.ecomarket.auth;

import java.util.HashSet;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecomarket.auth.dto.AuthRequest;
import com.ecomarket.auth.dto.AuthResponse;
import com.ecomarket.auth.dto.RegisterRequest;
import com.ecomarket.security.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User ? (org.springframework.security.core.userdetails.User) authentication.getPrincipal() : null);

        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        // Try to load full user entity to return id/email if available
        com.ecomarket.auth.User user = userRepository.findByUsername(principal.getUsername()).orElseGet(() -> userRepository.findByEmail(principal.getUsername()).orElse(null));

        AuthResponse resp = new AuthResponse(token, user != null ? user.getId() : null, principal.getUsername(), user != null ? user.getEmail() : null);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body("Username already taken");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        com.ecomarket.auth.User user = new com.ecomarket.auth.User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        java.util.Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER")));
        roles.add(userRole);
        user.setRoles(roles);
        com.ecomarket.auth.User saved = userRepository.save(user);

        String token = jwtService.generateToken(new org.springframework.security.core.userdetails.User(saved.getUsername(), saved.getPassword(), java.util.Collections.emptyList()));

        return ResponseEntity.ok(new AuthResponse(token, saved.getId(), saved.getUsername(), saved.getEmail()));
    }
}
