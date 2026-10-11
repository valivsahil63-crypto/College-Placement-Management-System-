package com.college.placement.dto;

import java.util.HashMap;
import java.util.Map;

public class DashboardStatsDto {

    private long totalStudents;
    private long totalCompanies;
    private long activeDrives;
    private long totalApplications;
    private long placedStudents;
    private double placementPercentage;
    private double averageCtc;
    private double highestCtc;

    // Charts data
    private Map<String, Long> departmentTotal = new HashMap<>();
    private Map<String, Long> departmentPlaced = new HashMap<>();
    private Map<String, Long> statusBreakdown = new HashMap<>();
    private Map<String, Long> companySelections = new HashMap<>();

    public DashboardStatsDto() {
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalCompanies() {
        return totalCompanies;
    }

    public void setTotalCompanies(long totalCompanies) {
        this.totalCompanies = totalCompanies;
    }

    public long getActiveDrives() {
        return activeDrives;
    }

    public void setActiveDrives(long activeDrives) {
        this.activeDrives = activeDrives;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public long getPlacedStudents() {
        return placedStudents;
    }

    public void setPlacedStudents(long placedStudents) {
        this.placedStudents = placedStudents;
    }

    public double getPlacementPercentage() {
        return placementPercentage;
    }

    public void setPlacementPercentage(double placementPercentage) {
        this.placementPercentage = placementPercentage;
    }

    public double getAverageCtc() {
        return averageCtc;
    }

    public void setAverageCtc(double averageCtc) {
        this.averageCtc = averageCtc;
    }

    public double getHighestCtc() {
        return highestCtc;
    }

    public void setHighestCtc(double highestCtc) {
        this.highestCtc = highestCtc;
    }

    public Map<String, Long> getDepartmentTotal() {
        return departmentTotal;
    }

    public void setDepartmentTotal(Map<String, Long> departmentTotal) {
        this.departmentTotal = departmentTotal;
    }

    public Map<String, Long> getDepartmentPlaced() {
        return departmentPlaced;
    }

    public void setDepartmentPlaced(Map<String, Long> departmentPlaced) {
        this.departmentPlaced = departmentPlaced;
    }

    public Map<String, Long> getStatusBreakdown() {
        return statusBreakdown;
    }

    public void setStatusBreakdown(Map<String, Long> statusBreakdown) {
        this.statusBreakdown = statusBreakdown;
    }

    public Map<String, Long> getCompanySelections() {
        return companySelections;
    }

    public void setCompanySelections(Map<String, Long> companySelections) {
        this.companySelections = companySelections;
    }
}
