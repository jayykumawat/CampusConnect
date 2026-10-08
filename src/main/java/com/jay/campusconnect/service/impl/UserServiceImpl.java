package com.jay.campusconnect.service.impl;

import com.jay.campusconnect.dto.request.UserCreateRequest;
import com.jay.campusconnect.dto.request.UserUpdateRequest;
import com.jay.campusconnect.dto.response.UserResponse;
import com.jay.campusconnect.entity.Institution;
import com.jay.campusconnect.entity.User;
import com.jay.campusconnect.exception.DuplicateResourceException;
import com.jay.campusconnect.exception.ResourceNotFoundException;
import com.jay.campusconnect.mapper.UserMapper;
import com.jay.campusconnect.repository.InstitutionRepository;
import com.jay.campusconnect.repository.UserRepository;
import com.jay.campusconnect.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            InstitutionRepository institutionRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.institutionRepository = institutionRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "User already exists with email '" + request.email() + "'"
            );
        }

        Institution institution = findInstitution(request.institutionId());
        User user = userMapper.toEntity(request, institution);
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return userMapper.toResponse(findUser(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByInstitutionId(Long institutionId) {
        return userRepository.findByInstitutionId(institutionId).stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = findUser(id);

        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateResourceException(
                    "User already exists with email '" + request.email() + "'"
            );
        }

        Institution institution = findInstitution(request.institutionId());
        userMapper.updateEntity(user, request, institution);

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    private Institution findInstitution(Long id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with id " + id));
    }
}
