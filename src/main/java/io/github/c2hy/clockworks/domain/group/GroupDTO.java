package io.github.c2hy.clockworks.domain.group;

import lombok.Data;

@Data
public class GroupDTO {
    private String id;
    private String name;
    private String description;
    private int updateIntervalSeconds;
}
