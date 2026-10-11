package com.college.placement.service;

import com.college.placement.dto.CompanyDto;
import com.college.placement.entity.Company;

import java.util.List;

public interface CompanyService {
    Company createCompany(CompanyDto dto, String adminUsername);
    Company updateCompany(Long id, CompanyDto dto, String adminUsername);
    Company getCompanyById(Long id);
    List<Company> getAllCompanies();
    List<Company> getActiveCompanies();
    void toggleCompanyStatus(Long id, String adminUsername);
}
