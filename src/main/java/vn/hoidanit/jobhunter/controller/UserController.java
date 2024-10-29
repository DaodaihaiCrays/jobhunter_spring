package vn.hoidanit.jobhunter.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import vn.hoidanit.jobhunter.domain.RestRespone;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.error.InvalidException;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<User> CreateUserController(@RequestBody User userRequest) {
        String hashPassword = this.passwordEncoder.encode(userRequest.getPassword());
        userRequest.setPassword(hashPassword);

        User userNew = this.userService.CreateUserService(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userNew);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestRespone<String>> DeleteUserController(@PathVariable("id") long id)
            throws InvalidException{
        if (id < 1) throw new InvalidException("Id không được nhỏ hơn 0");

        this.userService.DeleteUserService(id);

        RestRespone<String> response = new RestRespone<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Delete user successful");
        response.setData("Delete user successful");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> GetUserByIdController(@PathVariable("id") long id) {
        User user = this.userService.GetUserByIdService(id);

        if(user!=null) {
            return ResponseEntity.status(HttpStatus.OK).body(user);
        }

        return ResponseEntity.status(HttpStatus.OK).body(null);

    }

    @GetMapping()
    public ResponseEntity<List<User>> GetAllUsersController() {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.GetAllUsersService());
    }

    @PutMapping()
    public ResponseEntity<User> UpdateUserController(@RequestBody User userRequest) {
        User user = this.userService.UpdateUserService(userRequest);
        if(user != null) {
            return ResponseEntity.status(HttpStatus.OK).body(user);
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
