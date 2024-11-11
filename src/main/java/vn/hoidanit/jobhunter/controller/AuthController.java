package vn.hoidanit.jobhunter.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.request.ReqLoginDTO;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.InvalidException;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    @Value("${config.jwt.refresh-token-validity-in-seconds}")
    private long expirationRefreshTokenJwt;
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil,
            UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> Login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        // Nạp input gồm username/password vào Security
        // Đóng gói thông tin đăng nhập của người dùng một cách an toàn
        // trước khi được gửi đến quy trình xác thực của Spring Security.
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

        // Mục đích: Gọi phương thức authenticate từ interface AuthenticationManager
        // để thực hiện xác thực thông tin đăng nhập của người dùng.

        // Chức năng: AuthenticationManager sẽ gọi UserDetailsService
        // (thường triển khai qua phương thức loadUserByUsername)
        // để lấy thông tin người dùng từ cơ sở dữ liệu
        // và xác thực xem thông tin đăng nhập (username, password) có hợp lệ hay không.

        // Xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.GetUserByEmail(loginDTO.getUsername());

        if(currentUserDB!=null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName(),
                    currentUserDB.getRole()
            );

            res.setUser(userLogin);
        }

        // create a token
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);

        res.setAccessToken(access_token);

        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);

        this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

        ResponseCookie responseCookie = ResponseCookie
                .from("cookie", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(this.expirationRefreshTokenJwt)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(res);

    }

    @GetMapping("/account")
    @ApiMessage("Get account via token")
    public ResponseEntity<ResLoginDTO.UserGetAccount> GetAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        User currentUserDB = this.userService.GetUserByEmail(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();

        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
            userLogin.setRole(currentUserDB.getRole());

            userGetAccount.setUser(userLogin);
        }

        return ResponseEntity.ok().body(userGetAccount);

    }

    @GetMapping("/refresh")
    public ResponseEntity<ResLoginDTO> GetRefreshToken(
            @CookieValue(name = "cookie", defaultValue = "refresh_token_value") String cookie
    ) throws InvalidException {

        if(cookie.equals("refresh_token_value"))
            throw new InvalidException("refresh token is not empty at cookie");

        Jwt decodedToken =  this.securityUtil.checkValidRefreshToken(cookie);
        String email = decodedToken.getSubject();

        User user = this.userService.getUserByRefreshTokenAndEmail(cookie, email);

        if(user == null) {
            throw new InvalidException("refresh token is not valid");
        }

        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.GetUserByEmail(email);

        if(currentUserDB!=null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName(),
                    currentUserDB.getRole()
            );

            res.setUser(userLogin);
        }

        // create a token
        String access_token = this.securityUtil.createAccessToken(email, res);

        res.setAccessToken(access_token);

        String newRefreshToken = this.securityUtil.createRefreshToken(email, res);

        this.userService.updateUserToken(newRefreshToken, email);

        ResponseCookie responseCookie = ResponseCookie
                .from("cookie", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(this.expirationRefreshTokenJwt)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(res);
    }

    @GetMapping("/logout")
    @ApiMessage("Logout ")
    public ResponseEntity<ResLoginDTO> Logout() throws InvalidException {

        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        if (email.equals("")) {
            throw new InvalidException("access Token is not valid");
        }

        // update refresh token = null
        this.userService.updateUserToken(null, email);

        // remove refresh token cookie
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("cookie", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(null);

    }

    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User postManUser) throws InvalidException {
        boolean isEmailExist = this.userService.checkEmailExist(postManUser.getEmail());
        if (isEmailExist) {
            throw new InvalidException(
                    "email " + postManUser.getEmail() + "exists");
        }

        String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hashPassword);
        User userNew = this.userService.CreateUserService(postManUser);

        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser com = new ResCreateUserDTO.CompanyUser();

        resCreateUserDTO.setName(userNew.getName());
        resCreateUserDTO.setAddress(userNew.getAddress());
        resCreateUserDTO.setId(userNew.getId());
        resCreateUserDTO.setGender(userNew.getGender());
        resCreateUserDTO.setAge(userNew.getAge());
        resCreateUserDTO.setCreatedAt(userNew.getCreatedAt());
        resCreateUserDTO.setEmail(userNew.getEmail());

        if (userNew.getCompany() != null) {
            com.setId(userNew.getCompany().getId());
            com.setName(userNew.getCompany().getName());
            resCreateUserDTO.setCompany(com);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(resCreateUserDTO);
    }

}
