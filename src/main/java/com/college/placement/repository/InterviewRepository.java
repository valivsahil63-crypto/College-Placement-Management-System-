package com.college.placement.repository;

import com.college.placement.entity.Application;
import com.college.placement.entity.Interview;
import com.college.placement.enums.InterviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByApplication(Application application);
    List<Interview> findByStatus(InterviewStatus status);

    @Query("SELECT i FROM Interview i WHERE i.application.student.user.username = :username ORDER BY i.interviewDate ASC")
    List<Interview> findUpcomingByStudentUsername(@Param("username") String username);

    @Query("SELECT i FROM Interview i WHERE i.interviewDate >= :from ORDER BY i.interviewDate ASC")
    List<Interview> findUpcomingInterviews(@Param("from") LocalDateTime from);

    @Query("SELECT i FROM Interview i ORDER BY i.interviewDate DESC")
    List<Interview> findAllOrderByInterviewDateDesc();
}
