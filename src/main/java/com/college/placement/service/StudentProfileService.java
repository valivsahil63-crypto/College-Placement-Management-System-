package com.college.placement.service;

import com.college.placement.dto.StudentProfileUpdateDto;
import com.college.placement.entity.StudentProfile;

import java.util.List;

public interface StudentProfileService {
    StudentProfile getProfileByUsername(String username);
    StudentProfile getProfileById(Long id);
    StudentProfile updateProfile(String username, StudentProfileUpdateDto dto);
    void updateResume(String username, String filePath, String originalFileName);
    List<StudentProfile> getAllStudents();
    List<StudentProfile> searchStudents(String query, String branch, Integer gradYear);
}
