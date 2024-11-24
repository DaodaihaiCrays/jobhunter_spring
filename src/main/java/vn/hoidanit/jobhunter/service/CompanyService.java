package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

import org.springframework.data.domain.Pageable;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private JobRepository jobRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository, JobRepository jobRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public Company CreateCompanyService(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO GetAllCompaniesService(Pageable pageable) {
        Page<Company> pCompany = this.companyRepository.findAll(pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

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
                .orElseThrow(() -> new RuntimeException("company with ID " + id + " can not find"));
    }

    public void DeleteACompanyService(long id) throws RuntimeException{

            Company company = GetACompanyById(id);

            if(company==null)
                throw new RuntimeException("delete unsuccessful");

            List<User> users = this.userRepository.findByCompany(company);

            for(int i = 0 ; i < users.size() ; i++) {
                users.get(i).setCompany(null);
                this.userRepository.save(users.get(i));
            }

            List<Job> jobs = this.jobRepository.findByCompany(company);

            for(int i = 0 ; i < jobs.size() ; i++) {
                jobs.get(i).setCompany(null);
                this.jobRepository.save(jobs.get(i));
            }

            this.companyRepository.deleteById(id);
    }

    public Company UpdateACompanyService(Company companyReq) {
        Company company = GetACompanyById(companyReq.getId());

        if(company==null)
            throw new RuntimeException("company with ID " + companyReq.getId() + " can not find");

        company.setName(companyReq.getName());
        company.setUpdatedAt(companyReq.getUpdatedAt());
        company.setUpdatedBy(companyReq.getUpdatedBy());
        company.setAddress(companyReq.getAddress());
        company.setDescription(companyReq.getDescription());
        company.setLogo(companyReq.getLogo());

        return this.companyRepository.save(company);
    }

}
