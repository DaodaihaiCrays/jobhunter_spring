package vn.hoidanit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hoidanit.jobhunter.domain.Permission;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    boolean existsByModuleAndApiPathAndMethod(String module, String apiPath, String method);

    List<Permission> findByIdIn(List<Long> id);

}
