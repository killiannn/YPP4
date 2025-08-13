package com.example.googledrive.domain;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class File {
    private int id;
    private int FolderId;
    private int OwnerId;
    private int Size;
    private String Name;
    private String Path;
    private int FileTypeId;
    private String Status;
    private Instant CreatedAt;
    private Instant ModifiedDate;
}