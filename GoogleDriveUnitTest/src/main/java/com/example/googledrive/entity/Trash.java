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
public class Trash {
    private int Id;
    private int ObjectId;
    private int ObjectTypeId;
    private Instant RemovedDateTime;
    private int UserId;
    private boolean IsPermanent;
}