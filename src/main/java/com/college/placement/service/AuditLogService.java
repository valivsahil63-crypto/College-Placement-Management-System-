package com.college.placement.service;

import com.college.placement.entity.AuditLog;
import java.util.List;

public interface AuditLogService {
    void log(String username, String role, String action, String entityName, String entityId, String details, String ipAddress);
    List<AuditLog> getRecentLogs();
}
