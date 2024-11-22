package vn.hoidanit.jobhunter.domain.request;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.enum_package.GenderEnum;

@Getter
@Setter
public class UserRequestDTO {
    private String name;
    private String email;
    private String password;
    private GenderEnum gender;
    private String address;
    private int age;
    private Long company; // Chỉ nhận ID thay vì đối tượng
    private Long role; // Chỉ nhận ID thay vì đối tượng

    // Getters và setters
}