package com.substring.auth.security;

import com.substring.auth.entity.Role;
import com.substring.auth.entity.User;
import com.substring.auth.exception.ResourceNotFoundException;
import com.substring.auth.helper.UserHelper;
import com.substring.auth.repositroy.UserRepository;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            // extract token and validate and then authentication create and then security
            // context ke ander set karunga.
            String token = header.substring(7);
            try {
                Jws<Claims> parse = jwtService.parse(token);
                Claims payload = parse.getBody();

                // Check if it's an access token
                String typ = (String) payload.get("typ");
                if ("access".equals(typ)) {
                    String userId = payload.getSubject();
                    UUID userUUID = UserHelper.parseUUID(userId);

                    userRepository.findById(userUUID)
                            .ifPresent(user -> {
                                if (user.isEnable()) {
                                    List<GrantedAuthority> authorities = user.getRoles() == null ? List.of()
                                            : user.getRoles().stream()
                                                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                                                    .collect(Collectors.toList());

                                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                            user,
                                            null,
                                            authorities);
                                    authentication
                                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                                        SecurityContextHolder.getContext().setAuthentication(authentication);
                                    }
                                }
                            });
                }
            } catch (ExpiredJwtException e) {
                request.setAttribute("error", "Token Expired");
            } catch (JwtException e) {
                request.setAttribute("error", "Invalid Token");
            } catch (Exception e) {
                request.setAttribute("error", "Authentication Error");
            }
        }

        filterChain.doFilter(request, response);
    }

}
