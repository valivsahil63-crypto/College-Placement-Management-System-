package com.college.placement.dto;

import jakarta.validation.constraints.NotBlank;

public class AnnouncementDto {

    @NotBlank(message = "Announcement title is required")
    private String title;

    @NotBlank(message = "Announcement message is required")
    private String message;

    private String link;

    public AnnouncementDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
