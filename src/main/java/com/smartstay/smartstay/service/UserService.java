package com.smartstay.smartstay.service;

import com.smartstay.smartstay.dto.request.ChangePasswordRequest;
import com.smartstay.smartstay.dto.request.UpdateProfileRequest;
import com.smartstay.smartstay.dto.response.PagedResponse;
import com.smartstay.smartstay.dto.response.UserResponse;
import com.smartstay.smartstay.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    UserResponse getCurrentUser(Long userId);

    UserResponse updateProfile(Long userId, UpdateProfileRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);

    PagedResponse<UserResponse> getAllUsers(Pageable pageable);

    UserResponse getUserById(Long userId);

    void deactivateUser(Long userId);

    Optional<User> findByUsername(String username);

    User saveUser(User user);
}
