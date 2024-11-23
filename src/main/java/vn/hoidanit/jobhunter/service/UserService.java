package vn.hoidanit.jobhunter.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompanyService companyService, RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public User CreateUserService(User user) {
        System.out.println(user);
        System.out.println(user.getRole());

        System.out.println("========================");
        if (user.getCompany() != null) {
            Optional<Company> companyOptional = Optional.ofNullable(this.companyService.GetACompanyById(user.getCompany().getId()));
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }
//
//        // check role
        if (user.getRole() != null) {
            Role r = this.roleService.GetARoleByIdService(user.getRole().getId());

            user.setRole(r != null ? r : null);
        }

        user.getRole().setId(user.getRole().getId());

        return this.userRepository.save(user);
    }

    public void DeleteUserService(long id) {
        this.userRepository.deleteById(id);
    }

    public User GetUserByIdService(long id) {
        User user= this.userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with ID " + id + " not found"));
        return user;
    }

    public ResultPaginationDTO GetAllUsersService(Pageable pageable, String email) {
        Page<User> pUser = this.userRepository.findAll(pageable);

        List<ResCreateUserDTO> listResCreateUserDTO = new ArrayList<>();

        for(int i = 0 ; i < pUser.getContent().size(); i++) {
            ResCreateUserDTO resCreateUserDTOTmp = new ResCreateUserDTO();
            ResCreateUserDTO.CompanyUser com = new ResCreateUserDTO.CompanyUser();
            ResCreateUserDTO.RoleUser roleUser = new ResCreateUserDTO.RoleUser();

            resCreateUserDTOTmp.setName(pUser.getContent().get(i).getName());
            resCreateUserDTOTmp.setAddress(pUser.getContent().get(i).getAddress());
            resCreateUserDTOTmp.setId(pUser.getContent().get(i).getId());
            resCreateUserDTOTmp.setGender(pUser.getContent().get(i).getGender());
            resCreateUserDTOTmp.setAge(pUser.getContent().get(i).getAge());
            resCreateUserDTOTmp.setCreatedAt(pUser.getContent().get(i).getCreatedAt());
            resCreateUserDTOTmp.setEmail(pUser.getContent().get(i).getEmail());

            com.setId(pUser.getContent().get(i).getCompany() != null ? pUser.getContent().get(i).getCompany().getId() : 0);
            com.setName(pUser.getContent().get(i).getCompany() != null ? pUser.getContent().get(i).getCompany().getName() : "");
            resCreateUserDTOTmp.setCompany(com);

            roleUser.setName(pUser.getContent().get(i).getRole() != null ? pUser.getContent().get(i).getRole().getName() : "");
            resCreateUserDTOTmp.setRole(roleUser);

            listResCreateUserDTO.add(resCreateUserDTOTmp);
        }

        if(!email.equals(""))
            pUser = this.userRepository.findUsersByEmailDomain(email, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pUser.getTotalPages());
        mt.setTotal(pUser.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(listResCreateUserDTO);

        return rs;
    }

    public User UpdateUserService(User userUpdate) {

        User existingUser = this.GetUserByIdService(userUpdate.getId());

        if (existingUser != null) {
            if (userUpdate.getName() != null) {
                existingUser.setName(userUpdate.getName());
            }
            if (userUpdate.getGender() != null) {
                existingUser.setGender(userUpdate.getGender());
            }
            if (userUpdate.getAge() > 0) {
                existingUser.setAge(userUpdate.getAge());
            }
            if (userUpdate.getAddress() != null) {
                existingUser.setAddress(userUpdate.getAddress());
            }

            // check company
            if (userUpdate.getCompany() != null) {
                Optional<Company> companyOptional = Optional.ofNullable(this.companyService.GetACompanyById(userUpdate.getCompany().getId()));
                existingUser.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
            }

            // check role
            if (userUpdate.getRole() != null) {
                Role r = this.roleService.GetARoleByIdService(userUpdate.getRole().getId());
                existingUser.setRole(r != null ? r : null);
            }

            return this.userRepository.save(existingUser);
        }

        return null;
    }

    public User GetUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }
    public Boolean checkEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public void updateUserToken(String token, String email) {
        User user = this.GetUserByEmail(email);

        if(user!=null) {
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findUserByRefreshTokenAndEmail(token, email);
    }
}
