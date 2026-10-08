package com.jay.campusconnect.mapper;

import com.jay.campusconnect.dto.request.UserCreateRequest;
import com.jay.campusconnect.dto.request.UserUpdateRequest;
import com.jay.campusconnect.dto.response.UserResponse;
import com.jay.campusconnect.entity.Institution;
import com.jay.campusconnect.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final InstitutionMapper institutionMapper;

    public UserMapper(InstitutionMapper institutionMapper) {
        this.institutionMapper = institutionMapper;
    }

    public User toEntity(UserCreateRequest request, Institution institution) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setInstitution(institution);
        user.setClassOrYear(request.classOrYear());
        user.setDepartment(request.department());
        return user;
    }

    public void updateEntity(User user, UserUpdateRequest request, Institution institution) {
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setInstitution(institution);
        user.setClassOrYear(request.classOrYear());
        user.setDepartment(request.department());
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                institutionMapper.toResponse(user.getInstitution()),
                user.getClassOrYear(),
                user.getDepartment(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
