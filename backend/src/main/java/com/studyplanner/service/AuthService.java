package com.studyplanner.service;

import com.studyplanner.dto.AuthResponse;
import com.studyplanner.dto.LoginRequest;
import com.studyplanner.dto.RegisterRequest;
import com.studyplanner.dto.UserProfileResponse;
import com.studyplanner.exception.BadRequestException;
import com.studyplanner.model.User;
import com.studyplanner.repository.UserRepository;
import com.studyplanner.security.JwtTokenProvider;
import com.studyplanner.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password do not match");
        }

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("This email is already registered.");
        }

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setDailyStudyHours(3.0);
        user.setPreferredStudyTime("18:00");

        User savedUser = userRepository.save(user);

        String token = tokenProvider.generateTokenForUser(savedUser.getId(), savedUser.getEmail(), savedUser.getFullName());
        return new AuthResponse(token, savedUser.getId(), savedUser.getEmail(), savedUser.getFullName());
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = tokenProvider.generateToken(authentication);

        return new AuthResponse(token, userPrincipal.getId(), userPrincipal.getEmail(), userPrincipal.getFullName());
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getDailyStudyHours(),
                user.getPreferredStudyTime(),
                user.getCreatedAt()
        );
    }
}
