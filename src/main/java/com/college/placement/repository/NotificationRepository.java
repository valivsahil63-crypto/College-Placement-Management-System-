package com.college.placement.repository;

import com.college.placement.entity.Notification;
import com.college.placement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);
    List<Notification> findByRecipientUsernameOrderByCreatedAtDesc(String username);
    List<Notification> findByRecipientUsernameAndReadStatusFalseOrderByCreatedAtDesc(String username);
    long countByRecipientUsernameAndReadStatusFalse(String username);
}
