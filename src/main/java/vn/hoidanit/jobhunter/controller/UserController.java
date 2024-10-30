package vn.hoidanit.jobhunter.controller;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import vn.hoidanit.jobhunter.domain.RestRespone;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.InvalidException;

import java.util.List;
import java.util.Optional;

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
    @ApiMessage("Get users")
    public ResponseEntity<ResultPaginationDTO> GetAllUsersController(
            @RequestParam("current") Optional<String> currentOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional,
            @RequestParam("emailFilter") Optional<String> emailFilterOptional
    ) {
        String email = emailFilterOptional.isPresent() ? emailFilterOptional.get() : "";

        int currentPage = Integer.parseInt(currentOptional.isPresent() ? currentOptional.get() : "1");
        int pageSize = Integer.parseInt(pageSizeOptional.isPresent() ? pageSizeOptional.get() : "3");

        Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.by("email").ascending());
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.GetAllUsersService(pageable, email));
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
