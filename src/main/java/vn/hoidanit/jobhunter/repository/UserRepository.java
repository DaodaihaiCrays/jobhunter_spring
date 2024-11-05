package vn.hoidanit.jobhunter.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//  public User findById(long id);
  public User findByEmail(String email);

  public Boolean existsByEmail(String email);

  @Query("SELECT u FROM User u WHERE u.email LIKE %:domain ORDER BY u.email ASC")
  Page<User> findUsersByEmailDomain(@Param("domain") String domain, Pageable pageable);

  User findUserByRefreshTokenAndEmail(String token, String email);

  List<User> findByCompany(Company company);
}
