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

public class Folder {
    private Integer id;
    private Integer parentId;
    private Integer ownerId;
    private String name;
    private String path;
    private String status;
    private int size;
    private Instant CreatedAt;
    private Instant UpdatedAt;
    
}