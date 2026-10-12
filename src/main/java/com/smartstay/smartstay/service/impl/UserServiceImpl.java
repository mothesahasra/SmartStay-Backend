package com.smartstay.smartstay.service.impl;

import com.smartstay.smartstay.dto.request.ChangePasswordRequest;
import com.smartstay.smartstay.dto.request.UpdateProfileRequest;
import com.smartstay.smartstay.dto.response.PagedResponse;
import com.smartstay.smartstay.dto.response.UserResponse;
import com.smartstay.smartstay.entity.User;
import com.smartstay.smartstay.exception.BadRequestException;
import com.smartstay.smartstay.exception.ResourceNotFoundException;
import com.smartstay.smartstay.mapper.UserMapper;
import com.smartstay.smartstay.repository.UserRepository;
import com.smartstay.smartstay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getCurrentUser(Long userId) {
        User user = findUserOrThrow(userId);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findUserOrThrow(userId);

        if (StringUtils.hasText(request.getName())) {
            user.setName(request.getName());
        }
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email is already in use");
            }
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getPhone()) && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new BadRequestException("Phone number is already in use");
            }
            user.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getProfileImageUrl())) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = findUserOrThrow(userId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public PagedResponse<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return PagedResponse.<UserResponse>builder()
                .content(page.getContent().stream().map(userMapper::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    public UserResponse getUserById(Long userId) {
        return userMapper.toResponse(findUserOrThrow(userId));
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        User user = findUserOrThrow(userId);
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByEmail(username);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
