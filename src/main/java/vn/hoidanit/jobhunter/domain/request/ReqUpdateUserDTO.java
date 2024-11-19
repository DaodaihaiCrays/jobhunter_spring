package vn.hoidanit.jobhunter.domain.request;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.enums.GenderEnum;

@Getter
@Setter
public class ReqUpdateUserDTO {
    private long id;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
}
