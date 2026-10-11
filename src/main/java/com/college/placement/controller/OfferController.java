package com.college.placement.controller;

import com.college.placement.dto.OfferCreateDto;
import com.college.placement.entity.Application;
import com.college.placement.entity.PlacementOffer;
import com.college.placement.enums.OfferStatus;
import com.college.placement.exception.BusinessValidationException;
import com.college.placement.service.ApplicationService;
import com.college.placement.service.PlacementOfferService;
import com.college.placement.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/officer/offers")
@PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
public class OfferController {

    private final PlacementOfferService placementOfferService;
    private final ApplicationService applicationService;

    public OfferController(PlacementOfferService placementOfferService,
                           ApplicationService applicationService) {
        this.placementOfferService = placementOfferService;
        this.applicationService = applicationService;
    }

    @GetMapping
    public String listOffers(Model model) {
        List<PlacementOffer> offers = placementOfferService.getAllOffers();
        model.addAttribute("offers", offers);
        return "officer/offers";
    }

    @GetMapping("/new")
    public String newOfferForm(@RequestParam("applicationId") Long applicationId, Model model) {
        Application application = applicationService.getApplicationById(applicationId);
        OfferCreateDto dto = new OfferCreateDto();
        dto.setApplicationId(application.getId());
        dto.setDesignation(application.getDrive().getTitle());
        dto.setPackageOffered(application.getDrive().getCtcSalary());
        dto.setOfferDate(LocalDate.now());
        dto.setJoiningDate(LocalDate.now().plusMonths(3));
        dto.setStatus(OfferStatus.PENDING);

        model.addAttribute("offerDto", dto);
        model.addAttribute("application", application);
        return "officer/offer-form";
    }

    @PostMapping("/issue")
    public String issueOffer(@Valid @ModelAttribute("offerDto") OfferCreateDto dto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors()) {
            Application application = applicationService.getApplicationById(dto.getApplicationId());
            model.addAttribute("application", application);
            return "officer/offer-form";
        }

        try {
            String officerUsername = SecurityUtils.getCurrentUsername();
            placementOfferService.createOffer(dto, officerUsername);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Placement offer of " + dto.getPackageOffered() + " LPA issued successfully!");
            return "redirect:/officer/offers";
        } catch (BusinessValidationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/officer/applications";
        }
    }
}
