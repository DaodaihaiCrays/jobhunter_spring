package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.dto.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.InvalidException;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping()
    @ApiMessage("Create a job")
    public ResponseEntity<ResCreateJobDTO> CreateAJob(@Valid @RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.jobService.create(job));
    }

    @PutMapping()
    @ApiMessage("Update a job")
    public ResponseEntity<ResUpdateJobDTO> UpdateAJob(@Valid @RequestBody Job job) throws InvalidException {
        Optional<Job> currentJob = this.jobService.GetJobByIdService(job.getId());
        if (!currentJob.isPresent()) {
            throw new InvalidException("job can not find");
        }

        job.setCreatedAt(currentJob.get().getCreatedAt());
        job.setCreatedBy(currentJob.get().getCreatedBy());

        return ResponseEntity.ok()
                .body(this.jobService.UpdateAJobService(job));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a job by id")
    public ResponseEntity<Void> DeleteAJob(@PathVariable("id") long id) throws InvalidException {
        Optional<Job> currentJob = this.jobService.GetJobByIdService(id);
        if (!currentJob.isPresent()) {
            throw new InvalidException("job not found");
        }
        this.jobService.DeleteJobService(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/{id}")
    @ApiMessage("Get a job by id")
    public ResponseEntity<Job> GetJobById(@PathVariable("id") long id) throws InvalidException {
        Optional<Job> currentJob = this.jobService.GetJobByIdService(id);
        if (!currentJob.isPresent()) {
            throw new InvalidException("job can not find");
        }

        return ResponseEntity.ok().body(currentJob.get());
    }

    @GetMapping()
    @ApiMessage("Get job with pagination")
    public ResponseEntity<ResultPaginationDTO> GetAllJob(
            @RequestParam("page") Optional<String> currentOptional,
            @RequestParam("size") Optional<String> pageSizeOptional)
    {
        int currentPage = Integer.parseInt(currentOptional.isPresent() ? currentOptional.get() : "1");
        int pageSize = Integer.parseInt(pageSizeOptional.isPresent() ? pageSizeOptional.get() : "5");
        Pageable pageable = PageRequest.of(currentPage-1, pageSize);

        return ResponseEntity.ok().body(this.jobService.fetchAll(pageable));
    }
}

