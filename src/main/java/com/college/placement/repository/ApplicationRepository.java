package com.college.placement.repository;

import com.college.placement.entity.Application;
import com.college.placement.entity.PlacementDrive;
import com.college.placement.entity.StudentProfile;
import com.college.placement.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByStudentAndDrive(StudentProfile student, PlacementDrive drive);
    boolean existsByStudentAndDrive(StudentProfile student, PlacementDrive drive);

    List<Application> findByStudentOrderByAppliedAtDesc(StudentProfile student);
    List<Application> findByDriveOrderByAppliedAtDesc(PlacementDrive drive);
    List<Application> findByCurrentStatus(ApplicationStatus status);

    long countByCurrentStatus(ApplicationStatus status);

    @Query("SELECT COUNT(DISTINCT a.student.id) FROM Application a WHERE a.currentStatus = :status")
    long countDistinctStudentByCurrentStatus(@Param("status") ApplicationStatus status);

    @Query("SELECT a.currentStatus, COUNT(a) FROM Application a GROUP BY a.currentStatus")
    List<Object[]> countApplicationsByStatus();

    @Query("SELECT a.drive.company.name, COUNT(a) FROM Application a WHERE a.currentStatus = 'SELECTED' GROUP BY a.drive.company.name")
    List<Object[]> countSelectionsByCompany();

    @Query("SELECT a FROM Application a WHERE a.student.user.username = :username ORDER BY a.appliedAt DESC")
    List<Application> findByStudentUsername(@Param("username") String username);
}
