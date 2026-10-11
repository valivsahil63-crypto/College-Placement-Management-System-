package com.college.placement.repository;

import com.college.placement.entity.PlacementDrive;
import com.college.placement.enums.DriveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlacementDriveRepository extends JpaRepository<PlacementDrive, Long> {
    List<PlacementDrive> findByStatus(DriveStatus status);
    List<PlacementDrive> findByCompanyId(Long companyId);
    long countByStatus(DriveStatus status);

    @Query("SELECT d FROM PlacementDrive d ORDER BY d.createdAt DESC")
    List<PlacementDrive> findAllOrderByCreatedAtDesc();

    @Query("SELECT d FROM PlacementDrive d WHERE d.status = 'PUBLISHED' ORDER BY d.applicationClosingDate ASC")
    List<PlacementDrive> findActiveDrives();
}
