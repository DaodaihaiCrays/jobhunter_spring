package vn.hoidanit.jobhunter.service;


import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.User;
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

    public List<User> GetAllUsersService() {
        return this.userRepository.findAll();
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
