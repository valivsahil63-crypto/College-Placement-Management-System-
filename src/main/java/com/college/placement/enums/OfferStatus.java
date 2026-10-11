package com.college.placement.enums;

public enum OfferStatus {
    PENDING("Offer Letter Issued", "warning"),
    ACCEPTED("Offer Accepted", "success"),
    REJECTED("Offer Declined", "danger");

    private final String displayName;
    private final String badgeClass;

    OfferStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
