package com.college.placement.entity;

import com.college.placement.enums.OfferStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "placement_offers", indexes = {
    @Index(name = "idx_offer_student", columnList = "student_id"),
    @Index(name = "idx_offer_company", columnList = "company_id"),
    @Index(name = "idx_offer_status", columnList = "status")
})
public class PlacementOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private Application application;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @NotBlank(message = "Job Designation is required")
    @Column(nullable = false, length = 100)
    private String designation;

    @NotNull(message = "Offered CTC is required")
    @DecimalMin(value = "0.0", message = "Package cannot be negative")
    @Column(name = "package_offered", nullable = false)
    private Double packageOffered; // in LPA

    @NotNull(message = "Offer issue date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "offer_date", nullable = false)
    private LocalDate offerDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @NotNull(message = "Offer status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OfferStatus status = OfferStatus.PENDING;

    @Column(name = "offer_details", columnDefinition = "TEXT")
    private String offerDetails;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public PlacementOffer() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public StudentProfile getStudent() {
        return student;
    }

    public void setStudent(StudentProfile student) {
        this.student = student;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Double getPackageOffered() {
        return packageOffered;
    }

    public void setPackageOffered(Double packageOffered) {
        this.packageOffered = packageOffered;
    }

    public LocalDate getOfferDate() {
        return offerDate;
    }

    public void setOfferDate(LocalDate offerDate) {
        this.offerDate = offerDate;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public void setStatus(OfferStatus status) {
        this.status = status;
    }

    public String getOfferDetails() {
        return offerDetails;
    }

    public void setOfferDetails(String offerDetails) {
        this.offerDetails = offerDetails;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
