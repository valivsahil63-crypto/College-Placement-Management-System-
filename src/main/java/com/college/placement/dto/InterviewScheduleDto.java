package com.college.placement.dto;

import com.college.placement.enums.InterviewMode;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class InterviewScheduleDto {

    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @NotBlank(message = "Round name is required (e.g., Technical Interview 1)")
    private String roundName;

    @NotNull(message = "Interview date and time is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime interviewDate;

    @NotNull(message = "Interview mode is required")
    private InterviewMode mode = InterviewMode.ONLINE;

    @NotBlank(message = "Meeting link or campus venue is required")
    private String meetingLinkOrVenue;

    private String instructions;

    public InterviewScheduleDto() {
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getRoundName() {
        return roundName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public LocalDateTime getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(LocalDateTime interviewDate) {
        this.interviewDate = interviewDate;
    }

    public InterviewMode getMode() {
        return mode;
    }

    public void setMode(InterviewMode mode) {
        this.mode = mode;
    }

    public String getMeetingLinkOrVenue() {
        return meetingLinkOrVenue;
    }

    public void setMeetingLinkOrVenue(String meetingLinkOrVenue) {
        this.meetingLinkOrVenue = meetingLinkOrVenue;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
