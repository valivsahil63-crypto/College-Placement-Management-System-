package com.college.placement.service.impl;

import com.college.placement.dto.PlacementDriveDto;
import com.college.placement.entity.Company;
import com.college.placement.entity.PlacementDrive;
import com.college.placement.entity.User;
import com.college.placement.enums.DriveStatus;
import com.college.placement.exception.BusinessValidationException;
import com.college.placement.exception.ResourceNotFoundException;
import com.college.placement.repository.CompanyRepository;
import com.college.placement.repository.PlacementDriveRepository;
import com.college.placement.repository.UserRepository;
import com.college.placement.service.AuditLogService;
import com.college.placement.service.NotificationService;
import com.college.placement.service.PlacementDriveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlacementDriveServiceImpl implements PlacementDriveService {

    private final PlacementDriveRepository placementDriveRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public PlacementDriveServiceImpl(PlacementDriveRepository placementDriveRepository,
                                     CompanyRepository companyRepository,
                                     UserRepository userRepository,
                                     NotificationService notificationService,
                                     AuditLogService auditLogService) {
        this.placementDriveRepository = placementDriveRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public PlacementDrive createDrive(PlacementDriveDto dto, String username) {
        validateDriveDates(dto.getApplicationOpeningDate(), dto.getApplicationClosingDate());

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected company not found: " + dto.getCompanyId()));

        User createdBy = userRepository.findByUsername(username).orElse(null);

        PlacementDrive drive = new PlacementDrive();
        copyDtoToEntity(dto, drive, company);
        drive.setCreatedBy(createdBy);

        PlacementDrive saved = placementDriveRepository.save(drive);

        // If published immediately, notify all students
        if (saved.getStatus() == DriveStatus.PUBLISHED) {
            notificationService.broadcastToAllStudents(
                    "New Placement Drive: " + company.getName(),
                    "New campus recruitment drive announced: " + saved.getTitle() + " (" + saved.getCtcSalary() + " LPA). Check your eligibility now!",
                    "/drives/" + saved.getId()
            );
        }

        auditLogService.log(username, "OFFICER/ADMIN", "CREATE_DRIVE",
                "PlacementDrive", saved.getId().toString(), "Created drive: " + saved.getTitle() + " for " + company.getName(), "N/A");

        return saved;
    }

    @Override
    @Transactional
    public PlacementDrive updateDrive(Long id, PlacementDriveDto dto, String username) {
        PlacementDrive drive = getDriveById(id);
        validateDriveDates(dto.getApplicationOpeningDate(), dto.getApplicationClosingDate());

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected company not found: " + dto.getCompanyId()));

        DriveStatus previousStatus = drive.getStatus();
        copyDtoToEntity(dto, drive, company);

        PlacementDrive updated = placementDriveRepository.save(drive);

        if (previousStatus != DriveStatus.PUBLISHED && updated.getStatus() == DriveStatus.PUBLISHED) {
            notificationService.broadcastToAllStudents(
                    "Placement Drive Published: " + company.getName(),
                    "Recruitment drive " + updated.getTitle() + " is now open for registration! Check your eligibility.",
                    "/drives/" + updated.getId()
            );
        }

        auditLogService.log(username, "OFFICER/ADMIN", "UPDATE_DRIVE",
                "PlacementDrive", id.toString(), "Updated drive: " + updated.getTitle(), "N/A");

        return updated;
    }

    private void validateDriveDates(LocalDateTime opening, LocalDateTime closing) {
        if (opening != null && closing != null && closing.isBefore(opening)) {
            throw new BusinessValidationException("Application closing date cannot be earlier than opening date.");
        }
    }

    private void copyDtoToEntity(PlacementDriveDto dto, PlacementDrive drive, Company company) {
        drive.setTitle(dto.getTitle().trim());
        drive.setDescription(dto.getDescription());
        drive.setCompany(company);
        drive.setJobLocation(dto.getJobLocation().trim());
        drive.setJobType(dto.getJobType());
        drive.setRequiredBranches(dto.getRequiredBranches().trim().toUpperCase());
        drive.setMinCgpa(dto.getMinCgpa());
        drive.setMinTenthPercentage(dto.getMinTenthPercentage());
        drive.setMinTwelfthPercentage(dto.getMinTwelfthPercentage());
        drive.setMaxActiveBacklogs(dto.getMaxActiveBacklogs());
        drive.setEligibleGraduationYear(dto.getEligibleGraduationYear());
        drive.setRequiredSkills(dto.getRequiredSkills());
        drive.setCtcSalary(dto.getCtcSalary());
        drive.setApplicationOpeningDate(dto.getApplicationOpeningDate());
        drive.setApplicationClosingDate(dto.getApplicationClosingDate());
        drive.setVacancies(dto.getVacancies());
        drive.setSelectionProcess(dto.getSelectionProcess());
        drive.setStatus(dto.getStatus());
    }

    @Override
    @Transactional(readOnly = true)
    public PlacementDrive getDriveById(Long id) {
        return placementDriveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placement drive not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlacementDrive> getAllDrives() {
        return placementDriveRepository.findAllOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlacementDrive> getActiveDrives() {
        return placementDriveRepository.findActiveDrives();
    }

    @Override
    @Transactional
    public void updateDriveStatus(Long id, DriveStatus status, String username) {
        PlacementDrive drive = getDriveById(id);
        drive.setStatus(status);
        placementDriveRepository.save(drive);

        auditLogService.log(username, "OFFICER/ADMIN", "UPDATE_DRIVE_STATUS",
                "PlacementDrive", id.toString(), "Changed drive status to: " + status.getDisplayName(), "N/A");
    }

    @Override
    @Transactional
    public void deleteDrive(Long id, String adminUsername) {
        PlacementDrive drive = getDriveById(id);
        placementDriveRepository.delete(drive);

        auditLogService.log(adminUsername, "ROLE_ADMIN", "DELETE_DRIVE",
                "PlacementDrive", id.toString(), "Deleted drive: " + drive.getTitle(), "N/A");
    }
}
