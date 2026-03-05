package com.substring.auth.service;

import com.substring.auth.dto.UserDto;

import java.util.UUID;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUserByEmail(String email);

    UserDto updateUser(UserDto userDto, String userID);

    void deleteUser(String userId);

    UserDto getUserById(String userID);

    Iterable<UserDto> getAllUsers();
}
