package com.college.placement.service.impl;

import com.college.placement.dto.StudentProfileUpdateDto;
import com.college.placement.entity.StudentProfile;
import com.college.placement.entity.User;
import com.college.placement.exception.ResourceNotFoundException;
import com.college.placement.repository.StudentProfileRepository;
import com.college.placement.repository.UserRepository;
import com.college.placement.service.AuditLogService;
import com.college.placement.service.StudentProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentProfileServiceImpl implements StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public StudentProfileServiceImpl(StudentProfileRepository studentProfileRepository,
                                     UserRepository userRepository,
                                     AuditLogService auditLogService) {
        this.studentProfileRepository = studentProfileRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProfile getProfileByUsername(String username) {
        return studentProfileRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProfile getProfileById(Long id) {
        return studentProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found with id: " + id));
    }

    @Override
    @Transactional
    public StudentProfile updateProfile(String username, StudentProfileUpdateDto dto) {
        StudentProfile profile = getProfileByUsername(username);
        User user = profile.getUser();

        // Update full name in User
        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            user.setFullName(dto.getFullName().trim());
            userRepository.save(user);
        }

        profile.setPhone(dto.getPhone().trim());
        profile.setGender(dto.getGender());
        profile.setBranch(dto.getBranch());
        profile.setGraduationYear(dto.getGraduationYear());
        profile.setCgpa(dto.getCgpa());
        profile.setTenthPercentage(dto.getTenthPercentage());
        profile.setTwelfthPercentage(dto.getTwelfthPercentage());
        profile.setActiveBacklogs(dto.getActiveBacklogs());
        profile.setSkills(dto.getSkills());
        profile.setAddress(dto.getAddress());
        profile.setLinkedinUrl(dto.getLinkedinUrl());
        profile.setGithubUrl(dto.getGithubUrl());

        StudentProfile updated = studentProfileRepository.save(profile);

        auditLogService.log(username, "ROLE_STUDENT", "UPDATE_PROFILE",
                "StudentProfile", profile.getId().toString(), "Updated student academic profile", "N/A");

        return updated;
    }

    @Override
    @Transactional
    public void updateResume(String username, String filePath, String originalFileName) {
        StudentProfile profile = getProfileByUsername(username);
        profile.setResumePath(filePath);
        profile.setResumeFileName(originalFileName);
        studentProfileRepository.save(profile);

        auditLogService.log(username, "ROLE_STUDENT", "RESUME_UPLOAD",
                "StudentProfile", profile.getId().toString(), "Uploaded resume: " + originalFileName, "N/A");
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentProfile> getAllStudents() {
        return studentProfileRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentProfile> searchStudents(String query, String branch, Integer gradYear) {
        return studentProfileRepository.findAll().stream()
                .filter(s -> {
                    if (query != null && !query.isBlank()) {
                        String q = query.toLowerCase().trim();
                        boolean matchesName = s.getUser().getFullName().toLowerCase().contains(q);
                        boolean matchesUsn = s.getUsn().toLowerCase().contains(q);
                        boolean matchesEmail = s.getUser().getUsername().toLowerCase().contains(q);
                        if (!matchesName && !matchesUsn && !matchesEmail) return false;
                    }
                    if (branch != null && !branch.isBlank() && !branch.equalsIgnoreCase("ALL")) {
                        if (!s.getBranch().equalsIgnoreCase(branch.trim())) return false;
                    }
                    if (gradYear != null && gradYear > 0) {
                        if (!s.getGraduationYear().equals(gradYear)) return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
}
