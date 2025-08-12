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
public class FileVersion {
    private int id;
    private int FileId;
    private int Version;
    private String Path;
    private Instant CreatedAt;
    private int UpdatedBy;
    private boolean IsCurrent;
    private String VersionFile;
    private int Size;
}