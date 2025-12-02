package com.ecomarket.application.usecase;

import com.ecomarket.application.dto.request.LoginRequest;
import com.ecomarket.application.dto.request.RegisterRequest;
import com.ecomarket.application.dto.response.AuthResponse;
import com.ecomarket.application.dto.response.UserResponse;
import com.ecomarket.application.mapper.UserMapper;
import com.ecomarket.domain.exception.DuplicateEntityException;
import com.ecomarket.domain.model.Role;
import com.ecomarket.domain.model.User;
import com.ecomarket.domain.repository.RoleRepository;
import com.ecomarket.domain.repository.UserRepository;
import com.ecomarket.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;

/**
 * Servicio de aplicación para autenticación y registro
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());
        
        // Validar duplicados
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateEntityException("Username already exists: " + request.getUsername());
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEntityException("Email already exists: " + request.getEmail());
        }
        
        // Crear usuario
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .isActive(true)
                .roles(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Asignar rol USER
        Role userRole = roleRepository.findByName(Role.USER)
                .orElseThrow(() -> new RuntimeException("Role USER not found"));
        user.addRole(userRole);
        
        // Guardar usuario
        User savedUser = userRepository.save(user);
        
        // Generar token
        String token = jwtService.generateTokenFromUsername(savedUser.getUsername());
        
        UserResponse userResponse = userMapper.toResponse(savedUser);
        
        log.info("User registered successfully: {}", savedUser.getUsername());
        
        return new AuthResponse(token, userResponse);
    }
    
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.getUsername());
        
        // Autenticar
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        // Generar token
        String token = jwtService.generateToken(authentication);
        
        // Obtener usuario
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserResponse userResponse = userMapper.toResponse(user);
        
        log.info("User logged in successfully: {}", request.getUsername());
        
        return new AuthResponse(token, userResponse);
    }
}
