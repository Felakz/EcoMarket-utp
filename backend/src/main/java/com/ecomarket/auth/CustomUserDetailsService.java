package com.ecomarket.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        com.ecomarket.auth.User user = userRepository.findByEmail(usernameOrEmail)
                .or(() -> userRepository.findByUsername(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernameOrEmail));

        java.util.List<GrantedAuthority> authorities = new java.util.ArrayList<>();

        try {
            java.lang.reflect.Method getRoles = user.getClass().getMethod("getRoles");
            Object rolesObj = getRoles.invoke(user);
            if (rolesObj instanceof java.lang.Iterable) {
                for (Object r : (java.lang.Iterable<?>) rolesObj) {
                    String roleName = null;
                    try {
                        java.lang.reflect.Method getName = r.getClass().getMethod("getName");
                        Object nameObj = getName.invoke(r);
                        roleName = nameObj != null ? nameObj.toString() : null;
                    } catch (NoSuchMethodException nsme) {
                        roleName = r != null ? r.toString() : null;
                    }
                    if (roleName != null && !roleName.isBlank()) {
                        authorities.add(new SimpleGrantedAuthority(roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName));
                    }
                }
            }
        } catch (NoSuchMethodException ignored) {
            // fallback: try getRole() single
            try {
                java.lang.reflect.Method getRole = user.getClass().getMethod("getRole");
                Object roleObj = getRole.invoke(user);
                if (roleObj != null) {
                    String roleName = roleObj instanceof String ? (String) roleObj : roleObj.toString();
                    authorities.add(new SimpleGrantedAuthority(roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName));
                }
            } catch (NoSuchMethodException ignored2) {
                // no roles available — leave authorities empty or add default role if you want:
                // authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            } catch (Exception ex) {
                // log for debugging
                System.err.println("Error reading role from user: " + ex.getMessage());
            }
        } catch (Exception ex) {
            System.err.println("Error reading roles via reflection: " + ex.getMessage());
        }

        // resolve password (must exist and be hashed)
        String password = null;
        try {
            java.lang.reflect.Method getPassword = user.getClass().getMethod("getPassword");
            Object pwd = getPassword.invoke(user);
            password = pwd != null ? pwd.toString() : null;
        } catch (Exception e) {
            // log and fail
            System.err.println("No password accessor found or error reading password: " + e.getMessage());
        }

        if (password == null || password.isBlank()) {
            throw new UsernameNotFoundException("User has no password set: " + usernameOrEmail);
        }

        // Build UserDetails: enabled, accountNonExpired, credentialsNonExpired, accountNonLocked = true
        return org.springframework.security.core.userdetails.User.builder()
                .username( (principalFromReflection(user, usernameOrEmail)) )
                .password(password)
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    /**
     * Attempt to determine a principal (username/email) from the user object via reflection.
     * Falls back to the provided fallback value when nothing found.
     */
    private String principalFromReflection(Object user, String fallback) {
        if (user == null) {
            return fallback;
        }
        try {
            java.lang.reflect.Method getUsername = user.getClass().getMethod("getUsername");
            Object val = getUsername.invoke(user);
            if (val != null) {
                String s = val.toString();
                if (!s.isBlank()) return s;
            }
        } catch (Exception ignored) {
        }
        try {
            java.lang.reflect.Method getEmail = user.getClass().getMethod("getEmail");
            Object val = getEmail.invoke(user);
            if (val != null) {
                String s = val.toString();
                if (!s.isBlank()) return s;
            }
        } catch (Exception ignored) {
        }
        try {
            java.lang.reflect.Method getId = user.getClass().getMethod("getId");
            Object val = getId.invoke(user);
            if (val != null) {
                String s = val.toString();
                if (!s.isBlank()) return s;
            }
        } catch (Exception ignored) {
        }
        // Last resort: return the provided identifier
        return fallback;
    }
}
