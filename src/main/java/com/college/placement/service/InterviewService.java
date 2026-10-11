package com.college.placement.service;

import com.college.placement.dto.InterviewScheduleDto;
import com.college.placement.entity.Interview;
import com.college.placement.enums.InterviewStatus;

import java.util.List;

public interface InterviewService {
    Interview scheduleInterview(InterviewScheduleDto dto, String officerUsername);
    Interview updateInterview(Long id, InterviewStatus status, String feedback, String officerUsername);
    List<Interview> getUpcomingInterviewsForStudent(String studentUsername);
    List<Interview> getAllInterviews();
    List<Interview> getInterviewsByApplication(Long applicationId);
    Interview getInterviewById(Long id);
}
