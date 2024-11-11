package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.PermissionService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.InvalidException;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping()
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> CreateAPermissionController(@Valid @RequestBody Permission p) throws InvalidException {
        // check exist
        if (this.permissionService.isPermissionExist(p)) {
            throw new InvalidException("Permission exist.");
        }

        // create new permission
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.CreateAPremissionService(p));
    }

    @PutMapping()
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> UpdateAPermissionController(@Valid @RequestBody Permission p) throws InvalidException {
        // check exist by id
        if (this.permissionService.GetPermissionByIdService(p.getId()) == null) {
            throw new InvalidException("Permission with id = " + p.getId() + " is not exist.");
        }

        // check exist by module, apiPath and method
        if (this.permissionService.isPermissionExist(p)) {
            // check name
            if (this.permissionService.isSameName(p)) {
                throw new InvalidException("Permission đã tồn tại.");
            }

        }

        // update permission
        return ResponseEntity.ok().body(this.permissionService.UpdateAPremissionService(p));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("delete a permission")
    public ResponseEntity<Void> DeleteAPermissionController(@PathVariable("id") long id) throws InvalidException {
        // check exist by id
        if (this.permissionService.GetPermissionByIdService(id) == null) {
            throw new InvalidException("Permission with id = " + id + " is not exist.");
        }
        this.permissionService.DeleteAPremissionService(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping()
    @ApiMessage("Fetch permissions")
    public ResponseEntity<ResultPaginationDTO> GetPermissionsController(
            @RequestParam("page") Optional<String> currentOptional,
            @RequestParam("size") Optional<String> pageSizeOptional
    ) {

        int currentPage = Integer.parseInt(currentOptional.isPresent() ? currentOptional.get() : "1");
        int pageSize = Integer.parseInt(pageSizeOptional.isPresent() ? pageSizeOptional.get() : "5");
        Pageable pageable = PageRequest.of(currentPage-1, pageSize);

        return ResponseEntity.ok().body(this.permissionService.GetAllPermissionsService(pageable));
    }
}
