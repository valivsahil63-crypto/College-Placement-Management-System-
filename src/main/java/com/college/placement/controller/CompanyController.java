package com.college.placement.controller;

import com.college.placement.dto.CompanyDto;
import com.college.placement.entity.Company;
import com.college.placement.exception.BusinessValidationException;
import com.college.placement.service.CompanyService;
import com.college.placement.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/companies")
    public String listCompanies(Model model) {
        List<Company> companies = companyService.getAllCompanies();
        model.addAttribute("companies", companies);
        return "company/list";
    }

    @GetMapping("/companies/{id}")
    public String viewCompany(@PathVariable("id") Long id, Model model) {
        Company company = companyService.getCompanyById(id);
        model.addAttribute("company", company);
        return "company/detail";
    }

    @GetMapping("/admin/companies")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public String manageCompanies(Model model) {
        List<Company> companies = companyService.getAllCompanies();
        model.addAttribute("companies", companies);
        return "company/manage";
    }

    @GetMapping("/admin/companies/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public String newCompanyForm(Model model) {
        if (!model.containsAttribute("companyDto")) {
            model.addAttribute("companyDto", new CompanyDto());
        }
        return "company/form";
    }

    @PostMapping("/admin/companies/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public String createCompany(@Valid @ModelAttribute("companyDto") CompanyDto dto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            return "company/form";
        }

        try {
            String adminUser = SecurityUtils.getCurrentUsername();
            companyService.createCompany(dto, adminUser);
            redirectAttributes.addFlashAttribute("successMessage", "Company '" + dto.getName() + "' created successfully!");
            return "redirect:/admin/companies";
        } catch (BusinessValidationException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "company/form";
        }
    }

    @GetMapping("/admin/companies/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public String editCompanyForm(@PathVariable("id") Long id, Model model) {
        Company company = companyService.getCompanyById(id);
        CompanyDto dto = new CompanyDto();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setDescription(company.getDescription());
        dto.setIndustry(company.getIndustry());
        dto.setWebsite(company.getWebsite());
        dto.setHrContactName(company.getHrContactName());
        dto.setHrContactEmail(company.getHrContactEmail());
        dto.setHrContactPhone(company.getHrContactPhone());
        dto.setLogoUrl(company.getLogoUrl());
        dto.setLocation(company.getLocation());
        dto.setActive(company.isActive());

        model.addAttribute("companyDto", dto);
        model.addAttribute("isEdit", true);
        return "company/form";
    }

    @PostMapping("/admin/companies/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public String updateCompany(@PathVariable("id") Long id,
                                @Valid @ModelAttribute("companyDto") CompanyDto dto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "company/form";
        }

        try {
            String adminUser = SecurityUtils.getCurrentUsername();
            companyService.updateCompany(id, dto, adminUser);
            redirectAttributes.addFlashAttribute("successMessage", "Company updated successfully!");
            return "redirect:/admin/companies";
        } catch (BusinessValidationException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("isEdit", true);
            return "company/form";
        }
    }

    @PostMapping("/admin/companies/{id}/toggle")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public String toggleCompany(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        String adminUser = SecurityUtils.getCurrentUsername();
        companyService.toggleCompanyStatus(id, adminUser);
        redirectAttributes.addFlashAttribute("successMessage", "Company active status toggled successfully.");
        return "redirect:/admin/companies";
    }
}
