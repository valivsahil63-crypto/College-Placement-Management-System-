package com.college.placement.service;

import com.college.placement.entity.Notification;
import com.college.placement.entity.User;
import java.util.List;

public interface NotificationService {
    void sendNotification(User recipient, String title, String message, String link);
    void sendNotification(String username, String title, String message, String link);
    void broadcastToAllStudents(String title, String message, String link);
    List<Notification> getUserNotifications(String username);
    List<Notification> getUnreadNotifications(String username);
    long getUnreadCount(String username);
    void markAsRead(Long notificationId, String username);
    void markAllAsRead(String username);
}
