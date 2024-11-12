package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.request.ReqUpdateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.ResGetResumeDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.InvalidException;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreateResumeDTO> CreateAResumeController(@Valid @RequestBody Resume resume) throws InvalidException {
        // check id exists
        boolean isIdExist = this.resumeService.CheckResumeExistByUserAndJobService(resume);
        if (!isIdExist) {
            throw new InvalidException("user id or job id can not find");
        }

        ResCreateResumeDTO resCreateResumeDTO = this.resumeService.CreateAResumeService(resume);

        if (resCreateResumeDTO==null) {
            throw new InvalidException("email is not exist");
        }

        // create new resume
        return ResponseEntity.status(HttpStatus.CREATED).body(resCreateResumeDTO);
    }

    @PutMapping("")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> UpdateAResumeController(
            @RequestBody Resume resume) throws InvalidException {

        // check id exist
        Optional<Resume> reqResumeOptional = this.resumeService.GetById(resume.getId());
        if (reqResumeOptional.isEmpty()) {
            throw new InvalidException("resume with id = " + resume.getId() + " can not find");
        }

        Resume reqResume = reqResumeOptional.get();
        reqResume.setStatus(resume.getStatus());

        return ResponseEntity.ok().body(this.resumeService.UpdateAResumeService(reqResume));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a resume by id")
    public ResponseEntity<Void> DeleteAResumeController(@PathVariable("id") long id) throws InvalidException {
        Optional<Resume> reqResumeOptional = this.resumeService.GetById(id);
        if (reqResumeOptional.isEmpty()) {
            throw new InvalidException("resume with id = " + id + " can not find");
        }

        this.resumeService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch a resume by id")
    public ResponseEntity<ResGetResumeDTO> GetAResumeControllerById(@PathVariable("id") long id) throws InvalidException {
        Optional<Resume> reqResumeOptional = this.resumeService.GetById(id);

        if (reqResumeOptional.isEmpty()) {
            throw new InvalidException("resume with id = " + id + " can not find");
        }

        return ResponseEntity.ok().body(this.resumeService.GetAResumeService(reqResumeOptional.get()));
    }

    @GetMapping()
    @ApiMessage("Fetch all resume with paginate")
    public ResponseEntity<ResultPaginationDTO> GetAllResumeController(
            @RequestParam("page") Optional<String> currentOptional,
            @RequestParam("size") Optional<String> pageSizeOptional
    ) {

        int currentPage = Integer.parseInt(currentOptional.isPresent() ? currentOptional.get() : "1");
        int pageSize = Integer.parseInt(pageSizeOptional.isPresent() ? pageSizeOptional.get() : "5");

        Pageable pageable = PageRequest.of(currentPage-1, pageSize);
        return ResponseEntity.ok().body(this.resumeService.GetAllResumeService(pageable));
    }
}
