package com.college.placement.exception;

import java.util.Collections;
import java.util.List;

public class IneligibleApplicationException extends RuntimeException {

    private final List<String> reasons;

    public IneligibleApplicationException(String message) {
        super(message);
        this.reasons = Collections.singletonList(message);
    }

    public IneligibleApplicationException(String message, List<String> reasons) {
        super(message);
        this.reasons = reasons != null ? reasons : Collections.emptyList();
    }

    public List<String> getReasons() {
        return reasons;
    }
}
