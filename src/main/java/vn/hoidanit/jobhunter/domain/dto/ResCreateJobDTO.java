package vn.hoidanit.jobhunter.domain.dto;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.hoidanit.jobhunter.util.enums.LevelEnum;

@Getter
@Setter
@ToString
public class ResCreateJobDTO {
    private long id;
    private String name;

    private String location;

    private double salary;

    private int quantity;

    private LevelEnum level;

    private Instant startDate;
    private Instant endDate;


    private boolean isActive;

    private List<String> skills;

    private Instant createdAt;
    private String createdBy;
}
