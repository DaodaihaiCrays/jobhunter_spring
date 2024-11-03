package vn.hoidanit.jobhunter.controller;


import jakarta.validation.Valid;
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
import vn.hoidanit.jobhunter.domain.dto.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.enum_package.GenderEnum;
import vn.hoidanit.jobhunter.util.error.InvalidException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<ResCreateUserDTO> CreateUserController(@Valid  @RequestBody User userRequest) throws InvalidException {

        if(this.userService.checkEmailExist(userRequest.getEmail()))
            throw new InvalidException("Email is existed");

        String hashPassword = this.passwordEncoder.encode(userRequest.getPassword());
        userRequest.setPassword(hashPassword);

        User userNew = this.userService.CreateUserService(userRequest);

        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();

        resCreateUserDTO.setName(userNew.getName());
        resCreateUserDTO.setAddress(userNew.getAddress());
        resCreateUserDTO.setId(userNew.getId());
        resCreateUserDTO.setGender(userNew.getGender());
        resCreateUserDTO.setAge(userNew.getAge());
        resCreateUserDTO.setCreatedAt(userNew.getCreatedAt());
        resCreateUserDTO.setEmail(userNew.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(resCreateUserDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestRespone<String>> DeleteUserController(@PathVariable("id") long id)
            throws InvalidException{
        if (id < 1) throw new InvalidException("Id is not lower than 0");

        this.userService.DeleteUserService(id);

        RestRespone<String> response = new RestRespone<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Delete user successful");
        response.setData("Delete user successful");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResCreateUserDTO> GetUserByIdController(@PathVariable("id") long id) {
        System.out.println(id);
        User user = this.userService.GetUserByIdService(id);
        System.out.println(user);

        if(user!=null) {
            ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();

            resCreateUserDTO.setName(user.getName());
            resCreateUserDTO.setAddress(user.getAddress());
            resCreateUserDTO.setId(user.getId());
            resCreateUserDTO.setGender(user.getGender());
            resCreateUserDTO.setAge(user.getAge());
            resCreateUserDTO.setCreatedAt(user.getCreatedAt());
            resCreateUserDTO.setEmail(user.getEmail());

            return ResponseEntity.status(HttpStatus.OK).body(resCreateUserDTO);
        }

        return ResponseEntity.status(HttpStatus.OK).body(null);

    }

    @GetMapping()
    @ApiMessage("Get a list user")
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
    public ResponseEntity<ResUpdateUserDTO> UpdateUserController(@RequestBody User userRequest) {
        User userUpdate = this.userService.UpdateUserService(userRequest);

        if(userUpdate != null) {
            ResUpdateUserDTO resUpdateUserDTO = new ResUpdateUserDTO();

            resUpdateUserDTO.setId(userUpdate.getId());
            resUpdateUserDTO.setName(userUpdate.getName());
            resUpdateUserDTO.setGender(userUpdate.getGender());
            resUpdateUserDTO.setAge(userUpdate.getAge());
            resUpdateUserDTO.setAddress(userUpdate.getAddress());

            return ResponseEntity.status(HttpStatus.OK).body(resUpdateUserDTO);
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
