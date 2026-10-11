package com.college.placement.service;

import com.college.placement.dto.StudentRegistrationDto;
import com.college.placement.entity.User;

public interface UserService {
    User registerStudent(StudentRegistrationDto dto);
    User findByUsername(String username);
    void updatePassword(String username, String oldPassword, String newPassword);
    void toggleUserStatus(Long userId, String adminUsername);
}
