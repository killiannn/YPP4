package com.example.googledrive.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SharedUser {
    private Integer id;
    private Integer shareId;
    private Integer userId;
    private Integer permissionId;
}