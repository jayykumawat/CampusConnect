package com.jay.campusconnect.service;

import com.jay.campusconnect.dto.request.UserCreateRequest;
import com.jay.campusconnect.dto.request.UserUpdateRequest;
import com.jay.campusconnect.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    List<UserResponse> getUsersByInstitutionId(Long institutionId);
}
