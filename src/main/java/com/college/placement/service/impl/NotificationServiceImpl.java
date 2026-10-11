package com.college.placement.service.impl;

import com.college.placement.entity.Notification;
import com.college.placement.entity.User;
import com.college.placement.enums.RoleType;
import com.college.placement.repository.NotificationRepository;
import com.college.placement.repository.UserRepository;
import com.college.placement.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void sendNotification(User recipient, String title, String message, String link) {
        if (recipient == null) return;
        Notification notification = new Notification(recipient, title, message, link);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void sendNotification(String username, String title, String message, String link) {
        userRepository.findByUsername(username).ifPresent(user -> {
            Notification notification = new Notification(user, title, message, link);
            notificationRepository.save(notification);
        });
    }

    @Override
    @Transactional
    public void broadcastToAllStudents(String title, String message, String link) {
        List<User> students = userRepository.findByRole(RoleType.ROLE_STUDENT);
        for (User student : students) {
            Notification notification = new Notification(student, title, message, link);
            notificationRepository.save(notification);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(String username) {
        return notificationRepository.findByRecipientUsernameOrderByCreatedAtDesc(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(String username) {
        return notificationRepository.findByRecipientUsernameAndReadStatusFalseOrderByCreatedAtDesc(username);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String username) {
        return notificationRepository.countByRecipientUsernameAndReadStatusFalse(username);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, String username) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            if (notification.getRecipient().getUsername().equals(username)) {
                notification.setReadStatus(true);
                notificationRepository.save(notification);
            }
        });
    }

    @Override
    @Transactional
    public void markAllAsRead(String username) {
        List<Notification> unread = notificationRepository.findByRecipientUsernameAndReadStatusFalseOrderByCreatedAtDesc(username);
        for (Notification n : unread) {
            n.setReadStatus(true);
        }
        notificationRepository.saveAll(unread);
    }
}
