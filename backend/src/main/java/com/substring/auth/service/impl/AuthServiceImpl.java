package com.substring.auth.service.impl;

import com.substring.auth.dto.UserDto;
import com.substring.auth.service.AuthService;
import com.substring.auth.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto registerUser(UserDto userDto) {

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        UserDto userDto1 = userService.createUser(userDto);
        return userDto1;
    }
}
