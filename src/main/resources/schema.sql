-- ==============================================================
-- College Placement Management System - Database Schema (MySQL)
-- ==============================================================

CREATE DATABASE IF NOT EXISTS `placement_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `placement_db`;

-- Drop tables if existing in reverse dependency order
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `audit_logs`;
DROP TABLE IF EXISTS `notifications`;
DROP TABLE IF EXISTS `placement_offers`;
DROP TABLE IF EXISTS `interviews`;
DROP TABLE IF EXISTS `application_status_history`;
DROP TABLE IF EXISTS `applications`;
DROP TABLE IF EXISTS `placement_drives`;
DROP TABLE IF EXISTS `companies`;
DROP TABLE IF EXISTS `student_profiles`;
DROP TABLE IF EXISTS `users`;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Users Table
CREATE TABLE `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(120) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `role` VARCHAR(30) NOT NULL,
    `enabled` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NULL,
    INDEX `idx_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Student Profiles Table
CREATE TABLE `student_profiles` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL UNIQUE,
    `usn` VARCHAR(30) NOT NULL UNIQUE,
    `phone` VARCHAR(20) NOT NULL,
    `gender` VARCHAR(15) NULL,
    `branch` VARCHAR(50) NOT NULL,
    `graduation_year` INT NOT NULL,
    `cgpa` DOUBLE NOT NULL,
    `tenth_percentage` DOUBLE NOT NULL,
    `twelfth_percentage` DOUBLE NOT NULL,
    `active_backlogs` INT NOT NULL DEFAULT 0,
    `resume_path` VARCHAR(255) NULL,
    `resume_file_name` VARCHAR(255) NULL,
    `skills` TEXT NULL,
    `address` TEXT NULL,
    `linkedin_url` VARCHAR(255) NULL,
    `github_url` VARCHAR(255) NULL,
    `is_placed` BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_student_usn` (`usn`),
    INDEX `idx_student_branch` (`branch`),
    INDEX `idx_student_grad_year` (`graduation_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Companies Table
CREATE TABLE `companies` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL UNIQUE,
    `description` TEXT NULL,
    `industry` VARCHAR(100) NOT NULL,
    `website` VARCHAR(200) NULL,
    `hr_contact_name` VARCHAR(100) NOT NULL,
    `hr_contact_email` VARCHAR(120) NOT NULL,
    `hr_contact_phone` VARCHAR(20) NULL,
    `logo_url` VARCHAR(255) NULL,
    `location` VARCHAR(100) NOT NULL,
    `active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME NOT NULL,
    INDEX `idx_company_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Placement Drives Table
CREATE TABLE `placement_drives` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(150) NOT NULL,
    `description` TEXT NULL,
    `company_id` BIGINT NOT NULL,
    `job_location` VARCHAR(100) NOT NULL,
    `job_type` VARCHAR(30) NOT NULL,
    `required_branches` VARCHAR(200) NOT NULL,
    `min_cgpa` DOUBLE NOT NULL,
    `min_tenth_percentage` DOUBLE NOT NULL,
    `min_twelfth_percentage` DOUBLE NOT NULL,
    `max_active_backlogs` INT NOT NULL DEFAULT 0,
    `eligible_graduation_year` INT NOT NULL,
    `required_skills` TEXT NULL,
    `ctc_salary` DOUBLE NOT NULL,
    `application_opening_date` DATETIME NOT NULL,
    `application_closing_date` DATETIME NOT NULL,
    `vacancies` INT NOT NULL DEFAULT 1,
    `selection_process` TEXT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    `created_by_user_id` BIGINT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NULL,
    FOREIGN KEY (`company_id`) REFERENCES `companies` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
    INDEX `idx_drive_status` (`status`),
    INDEX `idx_drive_grad_year` (`eligible_graduation_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Applications Table
CREATE TABLE `applications` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL,
    `drive_id` BIGINT NOT NULL,
    `applied_at` DATETIME NOT NULL,
    `current_status` VARCHAR(30) NOT NULL DEFAULT 'APPLIED',
    `officer_remarks` TEXT NULL,
    `student_remarks` TEXT NULL,
    `updated_at` DATETIME NULL,
    CONSTRAINT `uk_student_drive` UNIQUE (`student_id`, `drive_id`),
    FOREIGN KEY (`student_id`) REFERENCES `student_profiles` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`drive_id`) REFERENCES `placement_drives` (`id`) ON DELETE CASCADE,
    INDEX `idx_application_status` (`current_status`),
    INDEX `idx_application_student` (`student_id`),
    INDEX `idx_application_drive` (`drive_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Application Status History Table
CREATE TABLE `application_status_history` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `application_id` BIGINT NOT NULL,
    `from_status` VARCHAR(30) NULL,
    `to_status` VARCHAR(30) NOT NULL,
    `changed_at` DATETIME NOT NULL,
    `changed_by_user_id` BIGINT NULL,
    `remarks` TEXT NULL,
    FOREIGN KEY (`application_id`) REFERENCES `applications` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`changed_by_user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
    INDEX `idx_history_app_id` (`application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Interviews Table
CREATE TABLE `interviews` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `application_id` BIGINT NOT NULL,
    `round_name` VARCHAR(100) NOT NULL,
    `interview_date` DATETIME NOT NULL,
    `mode` VARCHAR(20) NOT NULL,
    `meeting_link_or_venue` VARCHAR(255) NOT NULL,
    `instructions` TEXT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
    `feedback` TEXT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NULL,
    FOREIGN KEY (`application_id`) REFERENCES `applications` (`id`) ON DELETE CASCADE,
    INDEX `idx_interview_status` (`status`),
    INDEX `idx_interview_date` (`interview_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Placement Offers Table
CREATE TABLE `placement_offers` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `application_id` BIGINT NOT NULL UNIQUE,
    `student_id` BIGINT NOT NULL,
    `company_id` BIGINT NOT NULL,
    `designation` VARCHAR(100) NOT NULL,
    `package_offered` DOUBLE NOT NULL,
    `offer_date` DATE NOT NULL,
    `joining_date` DATE NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    `offer_details` TEXT NULL,
    `created_at` DATETIME NOT NULL,
    FOREIGN KEY (`application_id`) REFERENCES `applications` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`student_id`) REFERENCES `student_profiles` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`company_id`) REFERENCES `companies` (`id`) ON DELETE CASCADE,
    INDEX `idx_offer_student` (`student_id`),
    INDEX `idx_offer_company` (`company_id`),
    INDEX `idx_offer_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. Notifications Table
CREATE TABLE `notifications` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `recipient_id` BIGINT NOT NULL,
    `title` VARCHAR(150) NOT NULL,
    `message` TEXT NOT NULL,
    `link` VARCHAR(255) NULL,
    `read_status` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` DATETIME NOT NULL,
    FOREIGN KEY (`recipient_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_notification_user` (`recipient_id`),
    INDEX `idx_notification_read` (`read_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. Audit Logs Table
CREATE TABLE `audit_logs` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(120) NOT NULL,
    `role` VARCHAR(50) NULL,
    `action` VARCHAR(100) NOT NULL,
    `entity_name` VARCHAR(100) NULL,
    `entity_id` VARCHAR(50) NULL,
    `details` TEXT NULL,
    `timestamp` DATETIME NOT NULL,
    `ip_address` VARCHAR(50) NULL,
    INDEX `idx_audit_timestamp` (`timestamp`),
    INDEX `idx_audit_action` (`action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
