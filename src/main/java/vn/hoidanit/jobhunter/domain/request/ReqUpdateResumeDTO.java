package vn.hoidanit.jobhunter.domain.request;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import vn.hoidanit.jobhunter.util.enum_package.ResumeStateEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReqUpdateResumeDTO {

    @NotBlank(message = "id is not empty")
    private long id;

    @NotBlank(message = "status is not empty")
    @Enumerated(EnumType.STRING)
    private ResumeStateEnum status;
}
