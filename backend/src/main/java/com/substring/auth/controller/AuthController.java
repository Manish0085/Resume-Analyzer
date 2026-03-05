package com.substring.auth.controller;

import com.substring.auth.dto.LoginRequest;
import com.substring.auth.dto.RefreshTokenRequest;
import com.substring.auth.dto.TokenResponse;
import com.substring.auth.dto.UserDto;
import com.substring.auth.entity.RefreshToken;
import com.substring.auth.entity.User;
import com.substring.auth.repositroy.RefreshTokenRepository;
import com.substring.auth.repositroy.UserRepository;
import com.substring.auth.security.CookieService;
import com.substring.auth.security.JWTService;
import com.substring.auth.service.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final ModelMapper mapper;
    private final CookieService cookieService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        // authenticate
        authenticate(loginRequest);
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid username and password"));
        if (!user.isEnable()) {
            throw new DisabledException("User is Disabled");
        }

        String jti = UUID.randomUUID().toString();
        RefreshToken refreshTokenOb = new RefreshToken();
        refreshTokenOb.setJti(jti);
        refreshTokenOb.setUser(user);
        refreshTokenOb.setCreatedAt(Instant.now());
        refreshTokenOb.setExpiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()));
        refreshTokenOb.setRevoked(false);

        refreshTokenRepository.save(refreshTokenOb);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, refreshTokenOb.getJti());

        // use cookie service to attach refresh token in cookie
        cookieService.attachRefreshCookie(response, refreshToken, (int) jwtService.getRefreshTtlSeconds());
        cookieService.addNoStoreHeaders(response);

        TokenResponse tokenResponse = TokenResponse.of(accessToken, refreshToken, jwtService.getAccessTtlSeconds(),
                mapper.map(user, UserDto.class));

        return ResponseEntity.ok(tokenResponse);

    }

    private Authentication authenticate(LoginRequest loginRequest) {

        try {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username and password");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @RequestBody(required = false) RefreshTokenRequest body,
            HttpServletResponse response,
            HttpServletRequest request) {
        String refreshToken = readRefreshTokenFromRequest(body, request)
                .orElseThrow(() -> new BadCredentialsException("Invalid Refresh Token"));

        if (!jwtService.isRefreshToken(refreshToken)) {
            cookieService.clearRefreshCookie(response);
            throw new BadCredentialsException("Invalid Refresh token type!!");
        }

        String jti = jwtService.getJti(refreshToken);
        UUID userId = jwtService.getUserId(refreshToken);

        RefreshToken storedRefreshToken = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> {
                    cookieService.clearRefreshCookie(response);
                    return new BadCredentialsException("Refresh token is not recognized!!!");
                });

        if (storedRefreshToken.isRevoked()) {
            cookieService.clearRefreshCookie(response);
            throw new BadCredentialsException("Refresh Token Revoked");
        }

        if (storedRefreshToken.getExpiresAt().isBefore(Instant.now())) {
            cookieService.clearRefreshCookie(response);
            throw new BadCredentialsException("Refresh Token Expired");
        }

        if (!storedRefreshToken.getUser().getId().equals(userId)) {
            cookieService.clearRefreshCookie(response);
            throw new BadCredentialsException("Refresh Token does not belong to this user");
        }

        // rotate refresh token

        storedRefreshToken.setRevoked(true);
        String newJti = UUID.randomUUID().toString();
        storedRefreshToken.setReplacedByToken(newJti);
        refreshTokenRepository.save(storedRefreshToken);

        User user = storedRefreshToken.getUser();

        var newRefreshTokenOb = new RefreshToken();
        newRefreshTokenOb.setJti(newJti);
        newRefreshTokenOb.setUser(user);
        newRefreshTokenOb.setCreatedAt(Instant.now());
        newRefreshTokenOb.setExpiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()));
        newRefreshTokenOb.setRevoked(false);

        refreshTokenRepository.save(newRefreshTokenOb);
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user, newRefreshTokenOb.getJti());
        cookieService.attachRefreshCookie(response, newRefreshToken, (int) jwtService.getRefreshTtlSeconds());
        cookieService.addNoStoreHeaders(response);
        return ResponseEntity.ok(TokenResponse.of(newAccessToken, newRefreshToken, jwtService.getAccessTtlSeconds(),
                mapper.map(user, UserDto.class)));
    }

    private Optional<String> readRefreshTokenFromRequest(RefreshTokenRequest body, HttpServletRequest request) {
        if (request.getCookies() != null) {
            Optional<String> fromCookie = Arrays.stream(
                    request.getCookies()).filter(c -> cookieService.getRefreshTokenCookieName().equals(c.getName()))
                    .map(Cookie::getValue)
                    .filter(v -> v != null && !v.isBlank()).findFirst();

            if (fromCookie.isPresent()) {
                return fromCookie;
            }
        }

        if (body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
            return Optional.of(body.refreshToken());
        }

        String refreshHeader = request.getHeader("X-Refresh-Token");
        if (refreshHeader != null && !refreshHeader.isBlank()) {
            return Optional.of(refreshHeader.trim());
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer", 0, 6)) {
            String candidate = authHeader.substring(7).trim();
            if (!candidate.isEmpty()) {
                try {
                    if (jwtService.isRefreshToken(candidate)) {
                        return Optional.of(candidate);
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return Optional.empty();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {

        readRefreshTokenFromRequest(null, request)
                .ifPresent(token -> {
                    try {
                        String jti = jwtService.getJti(token);

                        refreshTokenRepository.findByJti(jti)
                                .ifPresent(rt -> {
                                    rt.setRevoked(true);
                                    refreshTokenRepository.save(rt);
                                });

                    } catch (JwtException e) {
                        // Invalid or expired refresh token → safe to ignore on logout
                    }
                });

        cookieService.clearRefreshCookie(response);
        cookieService.addNoStoreHeaders(response);
        SecurityContextHolder.clearContext();

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> registerUser(@RequestBody UserDto userDto, HttpServletResponse response) {
        // 1. Register
        UserDto registeredUser = authService.registerUser(userDto);

        // 2. Automated login after registration
        User user = userRepository.findByEmail(registeredUser.getEmail())
                .orElseThrow(() -> new BadCredentialsException("User not found after registration"));

        if (!user.isEnable()) {
            throw new DisabledException("User is Disabled");
        }

        String jti = UUID.randomUUID().toString();
        RefreshToken refreshTokenOb = new RefreshToken();
        refreshTokenOb.setJti(jti);
        refreshTokenOb.setUser(user);
        refreshTokenOb.setCreatedAt(Instant.now());
        refreshTokenOb.setExpiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()));
        refreshTokenOb.setRevoked(false);
        refreshTokenRepository.save(refreshTokenOb);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, refreshTokenOb.getJti());

        cookieService.attachRefreshCookie(response, refreshToken, (int) jwtService.getRefreshTtlSeconds());
        cookieService.addNoStoreHeaders(response);

        TokenResponse tokenResponse = TokenResponse.of(accessToken, refreshToken, jwtService.getAccessTtlSeconds(),
                mapper.map(user, UserDto.class));

        return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse);
    }

    @GetMapping("/get-me")
    public ResponseEntity<UserDto> getMe(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        return ResponseEntity.ok(mapper.map(user, UserDto.class));
    }
}
