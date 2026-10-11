package com.college.placement.service;

import com.college.placement.dto.PlacementDriveDto;
import com.college.placement.entity.PlacementDrive;
import com.college.placement.enums.DriveStatus;

import java.util.List;

public interface PlacementDriveService {
    PlacementDrive createDrive(PlacementDriveDto dto, String username);
    PlacementDrive updateDrive(Long id, PlacementDriveDto dto, String username);
    PlacementDrive getDriveById(Long id);
    List<PlacementDrive> getAllDrives();
    List<PlacementDrive> getActiveDrives();
    void updateDriveStatus(Long id, DriveStatus status, String username);
    void deleteDrive(Long id, String adminUsername);
}
