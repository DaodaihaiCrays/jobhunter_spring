package vn.hoidanit.jobhunter.domain.dto;

import lombok.*;
import vn.hoidanit.jobhunter.util.enums.LevelEnum;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResUpdateJobDTO {
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

    private Instant updatedAt;
    private String updatedBy;

    private Instant createdAt;
    private String createdBy;
}

