package com.college.placement.util;

import com.college.placement.exception.BusinessValidationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileUploadUtil {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public static String saveResume(String uploadDir, MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new BusinessValidationException("Please select a valid PDF resume file to upload.");
        }

        if (multipartFile.getSize() > MAX_FILE_SIZE) {
            throw new BusinessValidationException("Resume file size exceeds maximum limit of 5MB.");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new BusinessValidationException("Only PDF resume files (.pdf) are allowed.");
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate sanitized unique filename
        String safeFileName = UUID.randomUUID().toString() + "_" + originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");
        Path filePath = uploadPath.resolve(safeFileName);

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return safeFileName;
    }
}
