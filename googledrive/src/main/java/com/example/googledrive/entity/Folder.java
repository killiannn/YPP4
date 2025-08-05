package com.example.googledrive.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Folder")
@Data
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer parentId;
    private Integer ownerId;
    private String name;
    private String path;
    private String status;
}