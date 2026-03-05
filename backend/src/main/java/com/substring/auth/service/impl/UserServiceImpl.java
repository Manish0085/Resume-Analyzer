package com.substring.auth.service.impl;

import com.substring.auth.dto.*;
import com.substring.auth.entity.*;
import com.substring.auth.exception.*;
import com.substring.auth.helper.*;
import com.substring.auth.repositroy.*;
import com.substring.auth.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        if(userDto.getEmail() == null || userDto.getEmail().isBlank()){
            throw new IllegalArgumentException("Email is required");
        }

        if(userRepository.existsByEmail(userDto.getEmail())){
            throw new IllegalArgumentException("User with this email already exists");
        }

        User user = modelMapper.map(userDto, User.class);
        user.setProvider(userDto.getProvider() != null ? userDto.getProvider(): Provider.LOCAL);
        userRepository.save(user);


        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email id"));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userID) {
        UUID uid = UserHelper.parseUUID(userID);
        User existingUser = userRepository
                .findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("User with this email not Found"));
        if(userDto.getName() != null)
            existingUser.setName(userDto.getName());
        if(userDto.getImage() != null)
            existingUser.setImage(userDto.getImage());
        if(userDto.getProvider() != null)
            existingUser.setProvider(userDto.getProvider());
        if(userDto.getPassword() != null)
            existingUser.setPassword(userDto.getPassword());
        existingUser.setEnable(userDto.isEnable());
        existingUser.setUpdatedAt(Instant.now());
        User updatedUser = userRepository.save(existingUser);

        return modelMapper.map(existingUser, UserDto.class);
    }

    @Override
    public void deleteUser(String userId) {
        UUID uid = UserHelper.parseUUID(userId);
        User user = userRepository
                .findById(uid).orElseThrow(() ->
                        new ResourceNotFoundException("User with this email not found"));
        userRepository.delete(user);

    }

    @Override
    public UserDto getUserById(String userID) {
        User user = userRepository
                .findById(UserHelper.parseUUID(userID))
                .orElseThrow(() -> new ResourceNotFoundException("User with this email not found"));

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public Iterable<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();
    }
}
