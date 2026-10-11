package com.college.placement.service.impl;

import com.college.placement.dto.CompanyDto;
import com.college.placement.entity.Company;
import com.college.placement.exception.BusinessValidationException;
import com.college.placement.exception.ResourceNotFoundException;
import com.college.placement.repository.CompanyRepository;
import com.college.placement.service.AuditLogService;
import com.college.placement.service.CompanyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final AuditLogService auditLogService;

    public CompanyServiceImpl(CompanyRepository companyRepository, AuditLogService auditLogService) {
        this.companyRepository = companyRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public Company createCompany(CompanyDto dto, String adminUsername) {
        if (companyRepository.existsByName(dto.getName().trim())) {
            throw new BusinessValidationException("A company with name '" + dto.getName() + "' already exists.");
        }

        Company company = new Company();
        copyDtoToEntity(dto, company);
        company.setActive(true);

        Company saved = companyRepository.save(company);

        auditLogService.log(adminUsername, "ROLE_ADMIN", "CREATE_COMPANY",
                "Company", saved.getId().toString(), "Added new recruiting company: " + saved.getName(), "N/A");

        return saved;
    }

    @Override
    @Transactional
    public Company updateCompany(Long id, CompanyDto dto, String adminUsername) {
        Company company = getCompanyById(id);

        if (!company.getName().equalsIgnoreCase(dto.getName().trim()) &&
                companyRepository.existsByName(dto.getName().trim())) {
            throw new BusinessValidationException("A company with name '" + dto.getName() + "' already exists.");
        }

        copyDtoToEntity(dto, company);
        Company updated = companyRepository.save(company);

        auditLogService.log(adminUsername, "ROLE_ADMIN", "UPDATE_COMPANY",
                "Company", id.toString(), "Updated recruiting company: " + updated.getName(), "N/A");

        return updated;
    }

    private void copyDtoToEntity(CompanyDto dto, Company company) {
        company.setName(dto.getName().trim());
        company.setDescription(dto.getDescription());
        company.setIndustry(dto.getIndustry().trim());
        company.setWebsite(dto.getWebsite());
        company.setHrContactName(dto.getHrContactName().trim());
        company.setHrContactEmail(dto.getHrContactEmail().trim().toLowerCase());
        company.setHrContactPhone(dto.getHrContactPhone());
        company.setLogoUrl(dto.getLogoUrl());
        company.setLocation(dto.getLocation().trim());
    }

    @Override
    @Transactional(readOnly = true)
    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Company> getActiveCompanies() {
        return companyRepository.findByActiveTrue();
    }

    @Override
    @Transactional
    public void toggleCompanyStatus(Long id, String adminUsername) {
        Company company = getCompanyById(id);
        company.setActive(!company.isActive());
        companyRepository.save(company);

        auditLogService.log(adminUsername, "ROLE_ADMIN", "TOGGLE_COMPANY_STATUS",
                "Company", id.toString(), "Company " + company.getName() + " status changed to: " + (company.isActive() ? "Active" : "Inactive"), "N/A");
    }
}
