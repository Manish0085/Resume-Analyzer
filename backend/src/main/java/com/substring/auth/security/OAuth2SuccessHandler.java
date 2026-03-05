package com.substring.auth.security;

import com.substring.auth.entity.Provider;
import com.substring.auth.entity.RefreshToken;
import com.substring.auth.entity.User;
import com.substring.auth.repositroy.RefreshTokenRepository;
import com.substring.auth.repositroy.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private Logger logger = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final CookieService cookieService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.auth.frontend.success-redirect}")
    private String frontEndSuccessUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        logger.info("Successful authentication");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String registrationId = "unknown";
        if (authentication instanceof OAuth2AuthenticationToken token) {
            registrationId = token.getAuthorizedClientRegistrationId();
        }

        logger.info("registration ID: " + registrationId);
        logger.info("user: " + oAuth2User.getAttributes().toString());

        User user;

        switch (registrationId) {
            case "google" -> {

                System.out.println("Hiii, Before I am using google to authenticate myself user: ");
                String googleId = oAuth2User.getAttributes().getOrDefault("sub", "").toString();
                String name = oAuth2User.getAttributes().getOrDefault("name", "").toString();
                String picture = oAuth2User.getAttributes().getOrDefault("picture", "").toString();

                String email = (String) oAuth2User.getAttributes().get("email");
                // String email = (String) oAuth2User.getAttributes().get("email");
                if (email == null || email.isBlank()) {
                    email = name + "@google.com";
                }

                System.out.println("Hiii, Middle I am using google to authenticate myself user: ");

                User newUser = new User();
                newUser.setEmail(email);
                newUser.setName(name);
                newUser.setImage(picture);
                newUser.setEnable(true);
                newUser.setProvider(Provider.GOOGLE);
                newUser.setProviderId(googleId);
                newUser.setCreatedAt(Instant.now());
                newUser.setUpdatedAt(Instant.now());
                user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(newUser));

                System.out.println("Hiii, I am using google to authenticate myself user: " + user);
            }

            case "github" -> {
                String name = oAuth2User.getAttributes().getOrDefault("login", "").toString();
                String githubId = oAuth2User.getAttributes().getOrDefault("id", "").toString();
                String picture = oAuth2User.getAttributes().getOrDefault("avatar_url", "").toString();

                String email = (String) oAuth2User.getAttributes().get("email");
                if (email == null) {
                    email = name + "@github.com";
                }
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setName(name);
                newUser.setImage(picture);
                newUser.setEnable(true);
                newUser.setProvider(Provider.GITHUB);
                newUser.setProviderId(githubId);
                newUser.setCreatedAt(Instant.now());
                newUser.setUpdatedAt(Instant.now());
                user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(newUser));
            }

            default -> {
                throw new RuntimeException("Invalid Registration Id");
            }
        }

        String jti = UUID.randomUUID().toString();
        RefreshToken refreshTokenOb = new RefreshToken();
        refreshTokenOb.setJti(jti);
        refreshTokenOb.setUser(user);
        refreshTokenOb.setRevoked(false);
        refreshTokenOb.setCreatedAt(Instant.now());
        refreshTokenOb.setExpiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()));

        refreshTokenRepository.save(refreshTokenOb);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, refreshTokenOb.getJti());

        cookieService.attachRefreshCookie(response, refreshToken, (int) jwtService.getRefreshTtlSeconds());
        response.sendRedirect(frontEndSuccessUrl);

    }
}
