package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(
            RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role CreateARoleService(Role r) {

        return this.roleRepository.save(r);
    }

    public Role GetARoleByIdService(long id) {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if (roleOptional.isPresent())
            return roleOptional.get();
        return null;
    }

    public Role UpdateARoleService(Role r) {
        Role roleDB = this.GetARoleByIdService(r.getId());

        roleDB.setName(r.getName());
        roleDB.setDescription(r.getDescription());
        roleDB.setActive(r.isActive());
        roleDB = this.roleRepository.save(roleDB);
        return roleDB;
    }

    public void DeleteARoleService(long id) {
        this.roleRepository.deleteById(id);
    }

    public ResultPaginationDTO GetRolesService(Pageable pageable) {
        Page<Role> pRole = this.roleRepository.findAll(pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pRole.getTotalPages());
        mt.setTotal(pRole.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pRole.getContent());
        return rs;
    }
}
