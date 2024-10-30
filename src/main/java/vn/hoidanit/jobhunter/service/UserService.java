package vn.hoidanit.jobhunter.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User CreateUserService(User user) {
        return this.userRepository.save(user);
    }

    public void DeleteUserService(long id) {
        this.userRepository.deleteById(id);
    }

    public User GetUserByIdService(long id) {
        return this.userRepository.findById(id);
    }

    public ResultPaginationDTO GetAllUsersService(Pageable pageable, String email) {
        Page<User> pUser = this.userRepository.findAll(pageable);

        if(!email.equals(""))
            pUser = this.userRepository.findUsersByEmailDomain(email, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pUser.getTotalPages());
        mt.setTotal(pUser.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pUser.getContent());

        return rs;
    }

    public User UpdateUserService(User userUpdate) {

        if(this.GetUserByIdService(userUpdate.getId())!=null) {
            return this.userRepository.save(userUpdate);
        }
        return null;
    }

    public User GetUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }
}
