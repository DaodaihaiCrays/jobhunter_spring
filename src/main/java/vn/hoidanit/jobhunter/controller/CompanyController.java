package vn.hoidanit.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping()
    ResponseEntity<Company> CreateCompanyController(@Valid  @RequestBody Company company) {

        Company newCompany = this.companyService.CreateCompanyService(company);

        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

    @GetMapping()
    ResponseEntity<ResultPaginationDTO> GetAllCompaniesController(
            @RequestParam("page") Optional<String> currentOptional,
            @RequestParam("size") Optional<String> pageSizeOptional
    ) {
        int currentPage = Integer.parseInt(currentOptional.isPresent() ? currentOptional.get() : "1");
        int pageSize = Integer.parseInt(pageSizeOptional.isPresent() ? pageSizeOptional.get() : "5");

        Pageable pageable = PageRequest.of(currentPage-1, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(this.companyService.GetAllCompaniesService(pageable));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> DeleteACompanyController(@PathVariable("id") long id) {

        try {
            this.companyService.DeleteACompanyService(id);
            return ResponseEntity.status(HttpStatus.OK).body("delete successful");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.OK).body("delete unsuccessful");
        }
    }

    @PutMapping()
    ResponseEntity<Company> UpdateACompanyController(@RequestBody Company companyReq) {
        Company newCompany = this.companyService.UpdateACompanyService(companyReq);

        return ResponseEntity.status(HttpStatus.OK).body(newCompany);
    }
}
