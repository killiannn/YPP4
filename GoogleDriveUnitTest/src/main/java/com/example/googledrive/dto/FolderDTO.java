package com.example.googledrive.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.Instant;


@Getter
@Setter

@AllArgsConstructor
@NoArgsConstructor
public class FolderDTO {
    private int id;
    private String Name;
    private int parentId;
    private int ownerId;
    private String path;
    private String status;
    private int size;
    private Instant createdAt;
    private Instant updatedAt;
}
