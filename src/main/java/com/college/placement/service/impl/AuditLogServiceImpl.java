package com.college.placement.service.impl;

import com.college.placement.entity.AuditLog;
import com.college.placement.repository.AuditLogRepository;
import com.college.placement.service.AuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    public void log(String username, String role, String action, String entityName,
                    String entityId, String details, String ipAddress) {
        try {
            AuditLog log = new AuditLog(username, role, action, entityName, entityId, details, ipAddress);
            auditLogRepository.save(log);
        } catch (Exception ignored) {
            // Audit log write failure shouldn't abort the primary business transaction
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop100ByOrderByTimestampDesc();
    }
}
