package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company CreateCompanyService(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO GetAllCompaniesService(Pageable pageable) {
        Page<Company> pCompany = this.companyRepository.findAll(pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pCompany.getTotalPages());
        mt.setTotal(pCompany.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pCompany.getContent());

        return rs;

    }

    public Company GetACompanyById(long id) {
        return this.companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company with ID " + id + " not found"));
    }

    public void DeleteACompanyService(long id) throws RuntimeException{

            Company company = GetACompanyById(id);

            if(company==null)
                throw new RuntimeException("Delete unsuccessful");

            this.companyRepository.deleteById(id);
    }

    public Company UpdateACompanyService(Company companyReq) {
        Company company = GetACompanyById(companyReq.getId());

        if(company==null)
            throw new RuntimeException("Company with ID " + companyReq.getId() + " not found");

        company.setName(companyReq.getName());
        company.setUpdatedAt(companyReq.getUpdatedAt());
        company.setUpdatedBy(companyReq.getUpdatedBy());
        company.setAddress(companyReq.getAddress());
        company.setDescription(companyReq.getDescription());
        company.setLogo(companyReq.getLogo());

        return this.companyRepository.save(company);
    }

}
