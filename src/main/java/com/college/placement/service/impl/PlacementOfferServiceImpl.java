package com.college.placement.service.impl;

import com.college.placement.dto.OfferCreateDto;
import com.college.placement.entity.Application;
import com.college.placement.entity.PlacementOffer;
import com.college.placement.enums.ApplicationStatus;
import com.college.placement.enums.OfferStatus;
import com.college.placement.exception.BusinessValidationException;
import com.college.placement.exception.ResourceNotFoundException;
import com.college.placement.repository.ApplicationRepository;
import com.college.placement.repository.PlacementOfferRepository;
import com.college.placement.repository.StudentProfileRepository;
import com.college.placement.service.ApplicationService;
import com.college.placement.service.AuditLogService;
import com.college.placement.service.NotificationService;
import com.college.placement.service.PlacementOfferService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlacementOfferServiceImpl implements PlacementOfferService {

    private final PlacementOfferRepository offerRepository;
    private final ApplicationRepository applicationRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final ApplicationService applicationService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public PlacementOfferServiceImpl(PlacementOfferRepository offerRepository,
                                     ApplicationRepository applicationRepository,
                                     StudentProfileRepository studentProfileRepository,
                                     ApplicationService applicationService,
                                     NotificationService notificationService,
                                     AuditLogService auditLogService) {
        this.offerRepository = offerRepository;
        this.applicationRepository = applicationRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.applicationService = applicationService;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public PlacementOffer createOffer(OfferCreateDto dto, String officerUsername) {
        Application application = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + dto.getApplicationId()));

        if (offerRepository.findByApplication(application).isPresent()) {
            throw new BusinessValidationException("A placement offer has already been issued for this application.");
        }

        // Set status to SELECTED if not yet set
        if (application.getCurrentStatus() != ApplicationStatus.SELECTED) {
            applicationService.updateApplicationStatus(
                    application.getId(),
                    ApplicationStatus.SELECTED,
                    "Selected - Offer letter issued with package " + dto.getPackageOffered() + " LPA",
                    officerUsername
            );
        }

        PlacementOffer offer = new PlacementOffer();
        offer.setApplication(application);
        offer.setStudent(application.getStudent());
        offer.setCompany(application.getDrive().getCompany());
        offer.setDesignation(dto.getDesignation().trim());
        offer.setPackageOffered(dto.getPackageOffered());
        offer.setOfferDate(dto.getOfferDate());
        offer.setJoiningDate(dto.getJoiningDate());
        offer.setStatus(dto.getStatus() != null ? dto.getStatus() : OfferStatus.PENDING);
        offer.setOfferDetails(dto.getOfferDetails());

        PlacementOffer saved = offerRepository.save(offer);

        // Mark student profile as placed
        application.getStudent().setPlaced(true);
        studentProfileRepository.save(application.getStudent());

        // Notify student
        notificationService.sendNotification(
                application.getStudent().getUser(),
                "Congratulations! Job Offer from " + application.getDrive().getCompany().getName(),
                "Congratulations! You have been selected for the role of " + dto.getDesignation() +
                        " with an offered package of " + dto.getPackageOffered() + " LPA.",
                "/student/offers"
        );

        auditLogService.log(officerUsername, "OFFICER/ADMIN", "ISSUE_OFFER",
                "PlacementOffer", saved.getId().toString(), "Issued job offer of " + dto.getPackageOffered() + " LPA to " +
                        application.getStudent().getUser().getFullName(), "N/A");

        return saved;
    }

    @Override
    @Transactional
    public PlacementOffer updateOfferStatus(Long offerId, OfferStatus status, String studentUsername) {
        PlacementOffer offer = getOfferById(offerId);

        if (!offer.getStudent().getUser().getUsername().equals(studentUsername)) {
            throw new BusinessValidationException("You are not authorized to update this offer response.");
        }

        offer.setStatus(status);
        PlacementOffer updated = offerRepository.save(offer);

        auditLogService.log(studentUsername, "ROLE_STUDENT", "RESPOND_OFFER",
                "PlacementOffer", offerId.toString(), "Student updated offer response to: " + status.getDisplayName(), "N/A");

        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlacementOffer> getOffersForStudent(String studentUsername) {
        return offerRepository.findByStudentUserUsername(studentUsername);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlacementOffer> getAllOffers() {
        return offerRepository.findAllOrderByOfferDateDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public PlacementOffer getOfferById(Long id) {
        return offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placement offer not found with id: " + id));
    }
}
