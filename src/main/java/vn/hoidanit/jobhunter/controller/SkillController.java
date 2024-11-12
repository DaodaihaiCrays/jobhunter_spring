package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.InvalidException;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping()
    @ApiMessage("Create a skill")
    public ResponseEntity<Skill> CreateASkill(@Valid @RequestBody Skill s) throws InvalidException {
        // check name
        if (s.getName() != null && this.skillService.isNameExist(s.getName())) {
            throw new InvalidException("skill name = " + s.getName() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.CreateSkillService(s));
    }

    @PutMapping()
    @ApiMessage("Update a skill")
    public ResponseEntity<Skill> UpdateASkill(@Valid @RequestBody Skill s) throws InvalidException {
        // check id
        Skill currentSkill = this.skillService.GetSkillByIdService(s.getId());
        if (currentSkill == null) {
            throw new InvalidException("skill id = " + s.getId() + " can not find");
        }

        // check name
        if (s.getName() != null && this.skillService.isNameExist(s.getName())) {
            throw new InvalidException("skill name = " + s.getName() + " exist");
        }

        currentSkill.setName(s.getName());
        return ResponseEntity.ok().body(this.skillService.UpdateSkillService(currentSkill));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> DeleteASkill(@PathVariable("id") long id) throws InvalidException {
        // check id
        Skill currentSkill = this.skillService.GetSkillByIdService(id);
        if (currentSkill == null) {
            throw new InvalidException("skill id = " + id + " can not find");
        }
        this.skillService.DeleteSkillService(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping()
    @ApiMessage("fetch all skills")
    public ResponseEntity<ResultPaginationDTO> GetAllSkills(
            @RequestParam("page") Optional<String> currentOptional,
            @RequestParam("size") Optional<String> pageSizeOptional
    ) {
        int currentPage = Integer.parseInt(currentOptional.isPresent() ? currentOptional.get() : "1");
        int pageSize = Integer.parseInt(pageSizeOptional.isPresent() ? pageSizeOptional.get() : "5");

        Pageable pageable = PageRequest.of(currentPage-1, pageSize);

        return ResponseEntity.status(HttpStatus.OK).body(
                this.skillService.GetAllSkillsService(pageable));
    }
}
