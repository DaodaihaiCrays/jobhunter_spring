package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.InvalidException;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping()
    @ApiMessage("Create a role")
    public ResponseEntity<Role> CreateARoleController(@Valid @RequestBody Role r) throws InvalidException {
        // check name
        if (this.roleService.existByName(r.getName())) {
            throw new InvalidException("role with name = " + r.getName() + " can not find");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.CreateARoleService(r));
    }

    @PutMapping()
    @ApiMessage("Update a role")
    public ResponseEntity<Role> UpdateARoleController(@Valid @RequestBody Role r) throws InvalidException {
        // check id
        if (this.roleService.GetARoleByIdService(r.getId()) == null) {
            throw new InvalidException("role with id = " + r.getId() + " can not find");
        }

        // check name
        if (this.roleService.existByName(r.getName())) {
            throw new InvalidException("role with name = " + r.getName() + " can not find");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.UpdateARoleService(r));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> DeleteARoleController(@PathVariable("id") long id) throws InvalidException {
        // check id
        if (this.roleService.GetARoleByIdService(id) == null) {
            throw new InvalidException("role with id = " + id + " can not find");
        }
        this.roleService.DeleteARoleService(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping()
    @ApiMessage("Fetch roles")
    public ResponseEntity<ResultPaginationDTO> GetAllRolesController(
            @RequestParam("page") Optional<String> currentOptional,
            @RequestParam("size") Optional<String> pageSizeOptional
    ) {
        int currentPage = Integer.parseInt(currentOptional.isPresent() ? currentOptional.get() : "1");
        int pageSize = Integer.parseInt(pageSizeOptional.isPresent() ? pageSizeOptional.get() : "5");
        Pageable pageable = PageRequest.of(currentPage-1, pageSize);

        return ResponseEntity.ok(this.roleService.GetRolesService(pageable));
    }

}
